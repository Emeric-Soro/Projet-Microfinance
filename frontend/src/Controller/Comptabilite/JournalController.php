<?php

namespace App\Controller\Comptabilite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class JournalController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/comptabilite/journaux', name: 'comptabilite_journals', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $page = $request->query->getInt('page', 0);
        $data = $this->api->get('/comptabilite/journaux', ['page' => $page])->toArray();

        return $this->render('backoffice/comptabilite/journaux.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Journaux'],
            ],
            'journals' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
        ]);
    }

    #[Route('/comptabilite/journal/{code}', name: 'comptabilite_ledger', methods: ['GET'])]
    public function ledger(Request $request, string $code): Response
    {
        $dateDebut = $request->query->get('date_debut', date('Y-m-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-t'));
        $page = $request->query->getInt('page', 0);

        $params = compact('dateDebut', 'dateFin', 'page');
        $data = $this->api->get('/comptabilite/journaux/' . $code, $params)->toArray();

        return $this->render('backoffice/comptabilite/brouillard.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Journaux', 'url' => $this->generateUrl('comptabilite_journals')],
                ['label' => 'Journal ' . $code],
            ],
            'journal_code' => $code,
            'entries' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
            'solde' => $data['solde'] ?? 0,
            'total_debit' => $data['totalDebit'] ?? 0,
            'total_credit' => $data['totalCredit'] ?? 0,
        ]);
    }
}
