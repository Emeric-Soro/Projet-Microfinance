<?php

namespace App\Controller\Transaction;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class TransactionHistoryController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/operations/history', name: 'operation_history', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [
            'page' => $request->query->getInt('page', 0),
            'size' => 20,
            'type' => $request->query->get('type'),
            'statut' => $request->query->get('statut'),
            'date_debut' => $request->query->get('date_debut'),
            'date_fin' => $request->query->get('date_fin'),
            'compte' => $request->query->get('compte'),
            'guichet' => $request->query->get('guichet'),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $data = $this->api->get('/transactions', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des opérations');
        }

        return $this->render('backoffice/operations/operations.html.twig', [
            'transactions' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Historique'],
            ],
        ]);
    }

    #[Route('/operations/{ref}', name: 'operation_detail', methods: ['GET'])]
    public function detail(string $ref): Response
    {
        try {
            $transaction = $this->api->get('/transactions/' . $ref)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Opération introuvable');
            return $this->redirectToRoute('operation_history');
        }

        return $this->render('backoffice/operations/detail.html.twig', [
            'transaction' => $transaction,
            'breadcrumbs' => [
                ['label' => 'Opérations', 'url' => $this->generateUrl('operation_history')],
                ['label' => 'Détail #' . $ref],
            ],
        ]);
    }

    #[Route('/operations/{ref}/receipt', name: 'operation_receipt', methods: ['GET'])]
    public function receipt(string $ref): Response
    {
        try {
            $transaction = $this->api->get('/transactions/' . $ref)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Opération introuvable');
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

    #[Route('/operations/validation', name: 'operation_validate', methods: ['GET', 'POST'])]
    public function validate(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $ref = $request->request->get('reference');
            $action = $request->request->get('action');

            try {
                if ($action === 'approve') {
                    $this->api->post('/transactions/' . $ref . '/validate');
                    $this->addFlash('success', 'Opération ' . $ref . ' validée');
                } else {
                    $motif = $request->request->get('motif', 'Rejet');
                    $this->api->post('/transactions/' . $ref . '/reject', ['motif' => $motif]);
                    $this->addFlash('success', 'Opération ' . $ref . ' rejetée');
                }
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }

            return $this->redirectToRoute('operation_validate');
        }

        try {
            $data = $this->api->get('/transactions/en-attente')->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement');
        }

        return $this->render('backoffice/operations/validation.html.twig', [
            'transactions' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Validation'],
            ],
        ]);
    }

    #[Route('/operations/forcage', name: 'operation_supervisor_force', methods: ['GET', 'POST'])]
    public function supervisorForce(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'reference' => $request->request->get('reference'),
                    'motif' => $request->request->get('motif'),
                    'code_superviseur' => $request->request->get('code_superviseur'),
                    'montant' => $request->request->get('montant'),
                ];
                $this->api->post('/transactions/forcage', $data);
                $this->addFlash('success', 'Forçage effectué pour ' . ($data['reference'] ?? ''));
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }

            return $this->redirectToRoute('operation_supervisor_force');
        }

        return $this->render('backoffice/operations/forcage.html.twig', [
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Forçage superviseur'],
            ],
        ]);
    }
}
