<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ResetPasswordController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request, string $token): Response
    {
        if ($request->isMethod('GET')) {
            return $this->renderForm('auth/reset-password.html.twig', ['token' => $token]);
        }

        $password = (string) $request->request->get('password');
        $confirm = (string) $request->request->get('confirmPassword');

        if ('' === $password || '' === $confirm) {
            $this->addFlash('error', 'Veuillez saisir un nouveau mot de passe et sa confirmation.');
            return $this->renderForm('auth/reset-password.html.twig', ['token' => $token]);
        }

        try {
            $this->apiClient->post('/auth/password-reset', [
                'token' => $token,
                'password' => $password,
                'password_confirmation' => $confirm,
            ]);
            $this->addFlash('success', 'Mot de passe réinitialisé avec succès.');
            return $this->redirectToRoute('login');
        } catch (\Throwable $e) {
            $this->addFlash('error', 'Erreur lors de la réinitialisation: '.$e->getMessage());
            return $this->renderForm('auth/reset-password.html.twig', ['token' => $token]);
        }
    }
}
