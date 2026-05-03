<?php

namespace App\Controller\Transaction;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class TellerJournalController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/operations/teller-journal', name: 'operation_teller_journal', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [
            'date' => $request->query->get('date', date('Y-m-d')),
            'guichet' => $request->query->get('guichet'),
            'page' => $request->query->getInt('page', 0),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $data = $this->api->get('/transactions/journal-guichet', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement du journal');
        }

        try {
            $summary = $this->api->get('/transactions/journal-guichet/synthese', $params)->toArray();
        } catch (\Exception $e) {
            $summary = [];
        }

        return $this->render('backoffice/operations/teller-journal.html.twig', [
            'transactions' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => 20,
            'summary' => $summary,
            'selected_date' => $params['date'],
            'breadcrumbs' => [
                ['label' => 'Opérations'],
                ['label' => 'Journal de guichet'],
            ],
        ]);
    }
}
