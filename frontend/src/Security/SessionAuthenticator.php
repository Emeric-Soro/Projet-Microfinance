<?php

namespace App\Security;

use App\Service\ApiClientService;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\CustomUserMessageAuthenticationException;
use Symfony\Component\Security\Http\Authenticator\AbstractAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;
use Symfony\Component\Security\Http\EntryPoint\AuthenticationEntryPointInterface;

class SessionAuthenticator extends AbstractAuthenticator implements AuthenticationEntryPointInterface
{
    private const OTP_REQUIRED_FLAG = '__OTP_REQUIRED__';

    public function __construct(
        private ApiClientService $apiClient,
        private UrlGeneratorInterface $urlGenerator,
    ) {
    }

    public function supports(Request $request): ?bool
    {
        if (!$request->isMethod('POST')) {
            return false;
        }

        return in_array($request->attributes->get('_route'), ['login', 'login_otp'], true);
    }

    public function authenticate(Request $request): Passport
    {
        $route = $request->attributes->get('_route');

        if ($route === 'login_otp') {
            return $this->authenticateOtp($request);
        }

        $username = trim((string) $request->request->get('username', ''));
        $password = (string) $request->request->get('password', '');

        if ($username === '' || $password === '') {
            throw new CustomUserMessageAuthenticationException('Identifiants requis.');
        }

        $data = $this->apiClient->login($username, $password);

        if (($data['otpRequis'] ?? false) === true) {
            $request->getSession()->set('otp_username', $username);
            $request->getSession()->set('otp_challenge', $data['challengeId'] ?? '');
            throw new CustomUserMessageAuthenticationException(self::OTP_REQUIRED_FLAG);
        }

        return $this->createPassport($data, $username);
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        $request->getSession()->remove('otp_username');
        $request->getSession()->remove('otp_challenge');

        return new RedirectResponse($this->urlGenerator->generate('dashboard_general'));
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception): ?Response
    {
        if ($exception->getMessageKey() === self::OTP_REQUIRED_FLAG) {
            return new RedirectResponse($this->urlGenerator->generate('login_otp'));
        }

        $request->getSession()->getFlashBag()->add('error', $exception->getMessageKey());

        $targetRoute = $request->attributes->get('_route') === 'login_otp' ? 'login_otp' : 'login';

        return new RedirectResponse($this->urlGenerator->generate($targetRoute));
    }

    public function start(Request $request, ?AuthenticationException $authException = null): Response
    {
        return new RedirectResponse($this->urlGenerator->generate('login'));
    }

    private function authenticateOtp(Request $request): Passport
    {
        $session = $request->getSession();
        $username = (string) $session->get('otp_username', '');
        $challenge = (string) $session->get('otp_challenge', '');
        $otp = preg_replace('/\s+/', '', (string) $request->request->get('otp', ''));

        if ($username === '' || $challenge === '') {
            throw new CustomUserMessageAuthenticationException('Session OTP expirée. Veuillez vous reconnecter.');
        }

        if ($otp === '') {
            throw new CustomUserMessageAuthenticationException('Veuillez saisir le code OTP reçu.');
        }

        $data = $this->apiClient->loginOtp($username, $challenge, $otp);

        return $this->createPassport($data, $username);
    }

    private function createPassport(array $data, string $fallbackIdentifier): Passport
    {
        if (!isset($data['token']) || !isset($data['utilisateur']) || !is_array($data['utilisateur'])) {
            throw new CustomUserMessageAuthenticationException(
                $data['message'] ?? 'Échec de l\'authentification.'
            );
        }

        $userData = $data['utilisateur'];
        $userIdentifier = $userData['login'] ?? $userData['username'] ?? $fallbackIdentifier;

        return new SelfValidatingPassport(
            new UserBadge($userIdentifier, function () use ($data) {
                return UserProvider::fromApiData($data['utilisateur'] ?? []);
            })
        );
    }
}
