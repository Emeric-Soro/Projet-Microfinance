<?php

namespace App\Controller\Transaction;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class DepositController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/operations/depot', name: 'operation_deposit', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $accounts = [];
        $result = null;

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'compte' => $request->request->get('compte'),
                    'montant' => $request->request->get('montant'),
                    'devise' => $request->request->get('devise', 'XOF'),
                    'type_operation' => $request->request->get('type_operation', 'ESPECES'),
                    'description' => $request->request->get('description'),
                    'guichet' => $request->request->get('guichet'),
                ];
                $response = $this->api->post('/transactions/depot', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Dépôt effectué avec succès. Réf: ' . ($result['reference'] ?? ''));
                return $this->redirectToRoute('operation_receipt', ['ref' => $result['reference'] ?? '']);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du dépôt: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/comptes/actifs')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('warning', 'Impossible de charger les comptes');
        }

        return $this->render('backoffice/operations/depot.html.twig', [
            'accounts' => $accounts,
            'result' => $result,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Dépôt'],
            ],
        ]);
    }

    #[Route('/operations/depot/history', name: 'operation_deposit_history', methods: ['GET'])]
    public function history(Request $request): Response
    {
        $params = [];
        $dateDebut = $request->query->get('date_debut', date('Y-m-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-d'));
        $params['date_debut'] = $dateDebut;
        $params['date_fin'] = $dateFin;
        $params['page'] = $request->query->getInt('page', 0);

        try {
            $data = $this->api->get('/transactions/depots', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement de l\'historique');
        }

        return $this->render('backoffice/operations/history.html.twig', [
            'transactions' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => 20,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Historique des dépôts'],
            ],
        ]);
    }

    #[Route('/operations/{ref}/receipt', name: 'operation_receipt', methods: ['GET'])]
    public function receipt(string $ref): Response
    {
        try {
            $transaction = $this->api->get('/transactions/' . $ref)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Transaction introuvable');
            return $this->redirectToRoute('operation_history');
        }

        return $this->render('backoffice/operations/receipt.html.twig', [
            'transaction' => $transaction,
            'breadcrumbs' => [
                ['label' => 'Opérations', 'url' => $this->generateUrl('operation_history')],
                ['label' => 'Reçu #' . $ref],
            ],
        ]);
    }
}
