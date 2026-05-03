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
        // GET: render login form
        if ($request->isMethod('GET')) {
            return $this->renderForm('auth/login.html.twig');
        }

        // POST: process login
        $username = (string) $request->request->get('username');
        $password = (string) $request->request->get('password');

        if ('' === $username || '' === $password) {
            $this->addFlash('error', 'Veuillez entrer votre identifiant et votre mot de passe.');
            return $this->renderForm('auth/login.html.twig');
        }

        try {
            $response = $this->apiClient->login($username, $password);
            $session = $request->getSession();

            if (isset($response['jwt_token'])) {
                $session->set('jwt_token', $response['jwt_token']);
            }
            if (isset($response['refresh_token'])) {
                $session->set('refresh_token', $response['refresh_token']);
            }
            if (isset($response['user'])) {
                $session->set('user', $response['user']);
            }

            // Handle possible next steps
            if (!empty($response['requiresOtp']) && $response['requiresOtp'] === true) {
                if (isset($response['otp_challenge'])) {
                    $session->set('otp_challenge', $response['otp_challenge']);
                }
                $session->set('login_username', $username);
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
            $this->addFlash('error', 'Erreur lors de l’authentification: '.$e->getMessage());
            return $this->renderForm('auth/login.html.twig');
        }
    }
}
