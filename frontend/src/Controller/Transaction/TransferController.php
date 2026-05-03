<?php

namespace App\Controller\Transaction;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class TransferController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/operations/virement', name: 'operation_transfer', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $accounts = [];

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'compte_source' => $request->request->get('compte_source'),
                    'compte_destination' => $request->request->get('compte_destination'),
                    'montant' => $request->request->get('montant'),
                    'devise' => $request->request->get('devise', 'XOF'),
                    'description' => $request->request->get('description'),
                    'guichet' => $request->request->get('guichet'),
                ];
                $response = $this->api->post('/transactions/virement', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Virement effectué avec succès. Réf: ' . ($result['reference'] ?? ''));
                return $this->redirectToRoute('operation_receipt', ['ref' => $result['reference'] ?? '']);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du virement: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/comptes/actifs')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('warning', 'Impossible de charger les comptes');
        }

        return $this->render('backoffice/operations/virement.html.twig', [
            'accounts' => $accounts,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Virement'],
            ],
        ]);
    }

    #[Route('/operations/deplacee', name: 'operation_displaced', methods: ['GET', 'POST'])]
    public function displaced(Request $request): Response
    {
        $accounts = [];

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'compte_source' => $request->request->get('compte_source'),
                    'compte_destination' => $request->request->get('compte_destination'),
                    'montant' => $request->request->get('montant'),
                    'devise' => $request->request->get('devise', 'XOF'),
                    'date_operation' => $request->request->get('date_operation'),
                    'description' => $request->request->get('description'),
                    'guichet' => $request->request->get('guichet'),
                ];
                $response = $this->api->post('/transactions/deplacee', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Opération déplacée enregistrée. Réf: ' . ($result['reference'] ?? ''));
                return $this->redirectToRoute('operation_receipt', ['ref' => $result['reference'] ?? '']);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/comptes/actifs')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('warning', 'Impossible de charger les comptes');
        }

        return $this->render('backoffice/operations/deplacee.html.twig', [
            'accounts' => $accounts,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Opération déplacée'],
            ],
        ]);
    }

    #[Route('/operations/virement/lot', name: 'operation_transfer_batch', methods: ['GET', 'POST'])]
    public function batch(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $virements = $request->request->all('virements');
                $response = $this->api->post('/transactions/virement/lot', ['virements' => $virements]);
                $result = $response->toArray();
                $this->addFlash('success', 'Virement de masse effectué (' . count($virements) . ' opérations)');
                return $this->redirectToRoute('operation_history');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du virement de masse: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/operations/virement.html.twig', [
            'batch' => true,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Virement de masse'],
            ],
        ]);
    }
}
