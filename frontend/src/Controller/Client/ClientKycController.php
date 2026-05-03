<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientKycController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients/{id}/kyc', name: 'client_kyc', methods: ['GET', 'POST'])]
    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/kyc', $request->request->all());
                $this->addFlash('success', 'KYC mis à jour avec succès.');
                return $this->redirectToRoute('client_kyc', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour KYC : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $kyc = $this->api->get('/clients/' . $id . '/kyc')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/kyc.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'kyc' => $kyc,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'KYC'],
            ],
        ]);
    }
}
