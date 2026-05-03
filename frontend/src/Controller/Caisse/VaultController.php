<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class VaultController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/caisse/vault', name: 'caisse_vault', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'type' => $request->request->get('type'),
                    'montant' => $request->request->get('montant'),
                    'motif' => $request->request->get('motif'),
                ];
                $this->api->post('/caisse/coffre', $data);
                $this->addFlash('success', 'Opération coffre effectuée');
                return $this->redirectToRoute('caisse_vault');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $vault = $this->api->get('/caisse/coffre')->toArray();
        } catch (\Exception $e) {
            $vault = [];
        }

        return $this->render('backoffice/caisse/vault.html.twig', [
            'vault' => $vault,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Coffre'],
            ],
        ]);
    }

    #[Route('/caisse/in-transit', name: 'caisse_in_transit', methods: ['GET', 'POST'])]
    public function inTransit(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'guichet_source' => $request->request->get('guichet_source'),
                    'guichet_destination' => $request->request->get('guichet_destination'),
                    'montant' => $request->request->get('montant'),
                    'date_envoi' => $request->request->get('date_envoi'),
                    'reference' => $request->request->get('reference'),
                ];
                $this->api->post('/caisse/en-transit', $data);
                $this->addFlash('success', 'Fonds en transit enregistrés');
                return $this->redirectToRoute('caisse_in_transit');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $inTransit = $this->api->get('/caisse/en-transit')->toArray();
        } catch (\Exception $e) {
            $inTransit = ['content' => []];
        }

        return $this->render('backoffice/caisse/in-transit.html.twig', [
            'inTransit' => $inTransit['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'En transit'],
            ],
        ]);
    }
}
