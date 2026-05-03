<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SecurityController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function changeExpiredPassword(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $currentPassword = $request->request->get('current_password', '');
            $newPassword = $request->request->get('new_password', '');
            $confirmPassword = $request->request->get('confirm_password', '');

            if (empty($currentPassword) || empty($newPassword) || empty($confirmPassword)) {
                $this->addFlash('error', 'Veuillez remplir tous les champs.');
                return $this->render('auth/change-expired-password.html.twig');
            }

            if ($newPassword !== $confirmPassword) {
                $this->addFlash('error', 'Les nouveaux mots de passe ne correspondent pas.');
                return $this->render('auth/change-expired-password.html.twig');
            }

            if (strlen($newPassword) < 8) {
                $this->addFlash('error', 'Le nouveau mot de passe doit contenir au moins 8 caractères.');
                return $this->render('auth/change-expired-password.html.twig');
            }

            try {
                $this->apiClient->post('/auth/change-password', [
                    'currentPassword' => $currentPassword,
                    'newPassword' => $newPassword,
                ]);

                $this->addFlash('success', 'Mot de passe modifié avec succès. Veuillez vous connecter.');
                return $this->redirectToRoute('login');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Échec du changement de mot de passe. Vérifiez votre mot de passe actuel.');
                return $this->render('auth/change-expired-password.html.twig');
            }
        }

        return $this->render('auth/change-expired-password.html.twig');
    }

    public function accountLocked(): Response
    {
        return $this->render('auth/account-locked.html.twig');
    }
}
