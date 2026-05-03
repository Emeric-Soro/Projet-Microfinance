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
        if ($request->isMethod('GET')) {
            // Fetch setup data for QR code and manual code
            $data = $this->apiClient->get('/auth/2fa/setup');
            $qr = $data['qrCode'] ?? '';
            $manual = $data['manualCode'] ?? '';
            return $this->renderForm('auth/setup-2fa.html.twig', ['qrCode' => $qr, 'manualCode' => $manual]);
        }

        // POST: verify TOT P and enable 2FA
        $code = (string) $request->request->get('totp', '');
        if ('' === $code) {
            $this->addFlash('error', 'Veuillez saisir le code TOT P.');
            return $this->renderForm('auth/setup-2fa.html.twig');
        }

        try {
            $response = $this->apiClient->post('/auth/2fa/activate', ['code' => $code]);
            // After enabling, redirect to login or dashboard as appropriate
            $this->addFlash('success', '2FA activée avec succès.');
            return $this->redirectToRoute('login');
        } catch (\Throwable $e) {
            $this->addFlash('error', 'Erreur lors de l’activation 2FA: '.$e->getMessage());
            return $this->renderForm('auth/setup-2fa.html.twig');
        }
    }
}
