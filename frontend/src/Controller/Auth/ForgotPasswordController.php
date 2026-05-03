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
        if ($request->isMethod('POST')) {
            $email = $request->request->get('email', '');

            if (empty($email)) {
                $this->addFlash('error', 'Veuillez saisir votre adresse email.');
                return $this->render('auth/forgot-password.html.twig');
            }

            try {
                $this->apiClient->post('/auth/forgot-password', [
                    'email' => $email,
                ]);

                $this->addFlash('success', 'Si cette adresse email existe, un lien de réinitialisation vous a été envoyé.');
                return $this->redirectToRoute('login');
            } catch (\Exception $e) {
                $this->addFlash('success', 'Si cette adresse email existe, un lien de réinitialisation vous a été envoyé.');
                return $this->redirectToRoute('login');
            }
        }

        return $this->render('auth/forgot-password.html.twig');
    }
}
