<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class LoginController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $username = $request->request->get('username', '');
            $password = $request->request->get('password', '');

            if (empty($username) || empty($password)) {
                $this->addFlash('error', 'Veuillez saisir votre identifiant et votre mot de passe.');
                return $this->render('auth/login.html.twig');
            }

            try {
                $result = $this->apiClient->login($username, $password);

                if (($result['otpRequis'] ?? false) === true) {
                    $request->getSession()->set('otp_challenge', $result['challengeId'] ?? '');
                    $request->getSession()->set('otp_username', $username);
                    return $this->redirectToRoute('login_otp');
                }

                return $this->redirectToRoute('dashboard_general');
            } catch (\Exception $e) {
                $message = $e->getMessage();
                if (str_contains($message, 'locked')) {
                    $this->addFlash('error', 'Votre compte est verrouillé. Contactez l\'administrateur.');
                    return $this->redirectToRoute('account_locked');
                }
                if (str_contains($message, 'credentials') || str_contains($message, 'identifiants')) {
                    $this->addFlash('error', 'Identifiant ou mot de passe incorrect.');
                } else {
                    $this->addFlash('error', 'Erreur de connexion. Veuillez réessayer.');
                }
                return $this->render('auth/login.html.twig');
            }
        }

        if ($this->getUser()) {
            return $this->redirectToRoute('dashboard_general');
        }

        return $this->render('auth/login.html.twig');
    }
}
