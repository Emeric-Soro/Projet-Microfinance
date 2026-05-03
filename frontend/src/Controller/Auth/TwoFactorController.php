<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class TwoFactorController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function setup(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $code = $request->request->get('verification_code', '');

            if (empty($code)) {
                $this->addFlash('error', 'Veuillez saisir le code de vérification.');
                return $this->render('auth/setup-2fa.html.twig');
            }

            try {
                $this->apiClient->post('/auth/2fa/verify', [
                    'code' => $code,
                ]);

                $this->addFlash('success', 'Authentification à deux facteurs activée avec succès.');
                return $this->redirectToRoute('select_agency');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Code de vérification invalide. Veuillez réessayer.');
                return $this->render('auth/setup-2fa.html.twig');
            }
        }

        try {
            $setupData = $this->apiClient->post('/auth/2fa/setup')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible d\'initialiser la configuration 2FA.');
            $setupData = [];
        }

        return $this->render('auth/setup-2fa.html.twig', [
            'qr_code' => $setupData['qr_code'] ?? '',
            'secret' => $setupData['secret'] ?? '',
            'manual_key' => $setupData['manual_key'] ?? '',
        ]);
    }
}
