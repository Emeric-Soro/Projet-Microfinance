<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ResetPasswordController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(string $token, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $newPassword = $request->request->get('new_password', '');
            $confirmPassword = $request->request->get('confirm_password', '');

            if (empty($newPassword) || empty($confirmPassword)) {
                $this->addFlash('error', 'Veuillez remplir tous les champs.');
                return $this->render('auth/reset-password.html.twig', ['token' => $token]);
            }

            if ($newPassword !== $confirmPassword) {
                $this->addFlash('error', 'Les mots de passe ne correspondent pas.');
                return $this->render('auth/reset-password.html.twig', ['token' => $token]);
            }

            if (strlen($newPassword) < 8) {
                $this->addFlash('error', 'Le mot de passe doit contenir au moins 8 caractères.');
                return $this->render('auth/reset-password.html.twig', ['token' => $token]);
            }

            try {
                $this->apiClient->post('/auth/reset-password', [
                    'token' => $token,
                    'newPassword' => $newPassword,
                ]);

                $this->addFlash('success', 'Mot de passe réinitialisé avec succès. Vous pouvez maintenant vous connecter.');
                return $this->redirectToRoute('login');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Le lien de réinitialisation est invalide ou a expiré.');
                return $this->redirectToRoute('forgot_password');
            }
        }

        return $this->render('auth/reset-password.html.twig', ['token' => $token]);
    }
}
