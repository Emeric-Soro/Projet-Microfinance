<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ForgotPasswordController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        if ($request->isMethod('GET')) {
            return $this->renderForm('auth/forgot-password.html.twig');
        }

        $email = (string) $request->request->get('email');
        if ('' === $email) {
            $this->addFlash('error', 'Veuillez saisir votre adresse e-mail.');
            return $this->renderForm('auth/forgot-password.html.twig');
        }

        try {
            $this->apiClient->post('/auth/password-reset/request', ['email' => $email]);
            $this->addFlash('success', 'Lien de réinitialisation envoyé par e-mail.');
            return $this->redirectToRoute('login');
        } catch (\Throwable $e) {
            $this->addFlash('error', 'Erreur lors de la demande de réinitialisation: '.$e->getMessage());
            return $this->renderForm('auth/forgot-password.html.twig');
        }
    }
}
