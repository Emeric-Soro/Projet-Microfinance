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
        if ($request->isMethod('GET')) {
            return $this->renderForm('auth/change-expired-password.html.twig');
        }

        $current = (string) $request->request->get('current_password');
        $new = (string) $request->request->get('new_password');
        $confirm = (string) $request->request->get('confirm_password');

        if ('' === $current || '' === $new || '' === $confirm) {
            $this->addFlash('error', 'Veuillez renseigner tous les champs.');
            return $this->renderForm('auth/change-expired-password.html.twig');
        }

        try {
            $this->apiClient->post('/auth/password-change-expired', [
                'current_password' => $current,
                'new_password' => $new,
                'confirm_password' => $confirm,
            ]);
            $this->addFlash('success', 'Mot de passe changé avec succès.');
            return $this->redirectToRoute('login');
        } catch (\Throwable $e) {
            $this->addFlash('error', 'Erreur lors du changement: '.$e->getMessage());
            return $this->renderForm('auth/change-expired-password.html.twig');
        }
    }

    public function accountLocked(): Response
    {
        return $this->render('auth/account-locked.html.twig');
    }
}
