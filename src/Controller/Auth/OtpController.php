<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class OtpController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        $session = $request->getSession();

        // GET: show OTP form
        if ($request->isMethod('GET')) {
            $username = (string) $session->get('login_username', '');
            return $this->renderForm('auth/otp.html.twig', ['username' => $username]);
        }

        // POST: submit OTP
        $username = (string) $session->get('login_username', '');
        $challenge = (string) $session->get('otp_challenge', '');
        $otp = (string) $request->request->get('otp', '');

        if ('' === $username || '' === $otp) {
            $this->addFlash('error', 'Veuillez entrer le code OTP.');
            return $this->renderForm('auth/otp.html.twig', ['username' => $username]);
        }

        try {
            $response = $this->apiClient->loginOtp($username, $challenge, $otp);

            if (isset($response['jwt_token'])) {
                $session->set('jwt_token', $response['jwt_token']);
            }
            if (isset($response['refresh_token'])) {
                $session->set('refresh_token', $response['refresh_token']);
            }
            if (isset($response['user'])) {
                $session->set('user', $response['user']);
            }

            // Decide next step
            if (!empty($response['requiresOtp']) && $response['requiresOtp'] === true) {
                return $this->redirectToRoute('login_otp');
            }
            if (!empty($response['requires2fa']) && $response['requires2fa'] === true) {
                return $this->redirectToRoute('setup_2fa');
            }
            if (!empty($response['requiresAgency']) && $response['requiresAgency'] === true) {
                return $this->redirectToRoute('select_agency');
            }

            return $this->redirectToRoute('dashboard_general');
        } catch (\Throwable $e) {
            $this->addFlash('error', 'Code OTP invalide: '.$e->getMessage());
            return $this->renderForm('auth/otp.html.twig', ['username' => $username]);
        }
    }
}
