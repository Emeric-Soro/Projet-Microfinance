<?php

namespace App\Controller\Transaction;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class WithdrawalController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/operations/retrait', name: 'operation_withdrawal', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $accounts = [];

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'compte' => $request->request->get('compte'),
                    'montant' => $request->request->get('montant'),
                    'devise' => $request->request->get('devise', 'XOF'),
                    'type_operation' => $request->request->get('type_operation', 'ESPECES'),
                    'description' => $request->request->get('description'),
                    'guichet' => $request->request->get('guichet'),
                    'piece_identite' => $request->request->get('piece_identite'),
                ];
                $response = $this->api->post('/transactions/retrait', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Retrait effectué avec succès. Réf: ' . ($result['reference'] ?? ''));
                return $this->redirectToRoute('operation_receipt', ['ref' => $result['reference'] ?? '']);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du retrait: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/comptes/actifs')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('warning', 'Impossible de charger les comptes');
        }

        return $this->render('backoffice/operations/retrait.html.twig', [
            'accounts' => $accounts,
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Retrait'],
            ],
        ]);
    }
}
