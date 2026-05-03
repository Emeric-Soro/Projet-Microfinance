<?php

namespace App\Controller\Comptabilite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class GrandLivreController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/comptabilite/grand-livre', name: 'comptabilite_general_ledger', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $compteId = $request->query->get('compte_id', '');
        $dateDebut = $request->query->get('date_debut', date('Y-01-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-d'));
        $page = $request->query->getInt('page', 0);

        $params = array_filter(compact('compteId', 'dateDebut', 'dateFin', 'page'));
        $data = $compteId ? $this->api->get('/comptabilite/grand-livre', $params)->toArray() : ['data' => [], 'total' => 0];

        $accounts = $this->api->get('/comptabilite/plan-comptable', ['pageSize' => 500])->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/grand-livre.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Grand livre'],
            ],
            'entries' => $data['data'] ?? [],
            'accounts' => $accounts,
            'compte_id' => $compteId,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 50,
            'total_pages' => $data['totalPages'] ?? 1,
            'solde' => $data['solde'] ?? 0,
            'total_debit' => $data['totalDebit'] ?? 0,
            'total_credit' => $data['totalCredit'] ?? 0,
        ]);
    }
}
