<?php

namespace App\Controller\Comptabilite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class BalanceController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/comptabilite/balance', name: 'comptabilite_balance', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $dateDebut = $request->query->get('date_debut', date('Y-01-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-d'));
        $agenceId = $request->query->get('agence_id', '');
        $classe = $request->query->get('classe', '');
        $page = $request->query->getInt('page', 0);

        $params = array_filter(compact('dateDebut', 'dateFin', 'agenceId', 'classe', 'page'));
        $data = $this->api->get('/comptabilite/balance', $params)->toArray();

        $agences = $this->api->get('/organisation/agences')->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/balance.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Balance'],
            ],
            'accounts' => $data['data'] ?? [],
            'total_debit' => $data['totalDebit'] ?? 0,
            'total_credit' => $data['totalCredit'] ?? 0,
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 50,
            'total_pages' => $data['totalPages'] ?? 1,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
            'agence_id' => $agenceId,
            'classe' => $classe,
            'agences' => $agences,
        ]);
    }

    #[Route('/comptabilite/balance-agee', name: 'comptabilite_aged_balance', methods: ['GET'])]
    public function aged(Request $request): Response
    {
        $dateArrete = $request->query->get('date_arrete', date('Y-m-d'));
        $tranche = $request->query->get('tranche', '');

        $params = array_filter(compact('dateArrete', 'tranche'));
        $data = $this->api->get('/comptabilite/balance-agee', $params)->toArray();

        return $this->render('backoffice/comptabilite/balance-agee.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Balance âgée'],
            ],
            'entries' => $data['data'] ?? [],
            'date_arrete' => $dateArrete,
            'tranche' => $tranche,
            'totaux' => $data['totaux'] ?? [],
        ]);
    }

    #[Route('/comptabilite/resultat', name: 'comptabilite_pnl', methods: ['GET'])]
    public function pnl(Request $request): Response
    {
        $exerciceId = $request->query->get('exercice_id', '');

        $exercices = $this->api->get('/comptabilite/exercices')->toArray()['data'] ?? [];
        $params = $exerciceId ? ['exercice_id' => $exerciceId] : [];
        $data = $this->api->get('/comptabilite/resultat', $params)->toArray();

        return $this->render('backoffice/comptabilite/resultat.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Résultat'],
            ],
            'resultat' => $data,
            'exercices' => $exercices,
            'exercice_id' => $exerciceId,
        ]);
    }

    #[Route('/comptabilite/flux-tresorerie', name: 'comptabilite_cash_flow', methods: ['GET'])]
    public function cashFlow(Request $request): Response
    {
        $dateDebut = $request->query->get('date_debut', date('Y-01-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-d'));

        $data = $this->api->get('/comptabilite/flux-tresorerie', compact('dateDebut', 'dateFin'))->toArray();

        return $this->render('backoffice/comptabilite/flux-tresorerie.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Flux de trésorerie'],
            ],
            'flux' => $data['data'] ?? [],
            'totaux' => $data['totaux'] ?? [],
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
        ]);
    }

    #[Route('/comptabilite/controles', name: 'comptabilite_controls', methods: ['GET'])]
    public function controls(Request $request): Response
    {
        $type = $request->query->get('type', '');
        $params = array_filter(compact('type'));
        $data = $this->api->get('/comptabilite/controles', $params)->toArray();

        return $this->render('backoffice/comptabilite/controles.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Contrôles'],
            ],
            'controles' => $data['data'] ?? [],
            'type' => $type,
            'anomalies' => $data['anomalies'] ?? [],
            'stats' => $data['stats'] ?? [],
        ]);
    }

    #[Route('/comptabilite/reporting', name: 'comptabilite_reports', methods: ['GET'])]
    public function reports(Request $request): Response
    {
        $type = $request->query->get('type', 'synthese');
        $dateDebut = $request->query->get('date_debut', date('Y-01-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-d'));

        $params = compact('type', 'dateDebut', 'dateFin');
        $data = $this->api->get('/comptabilite/reporting', $params)->toArray();

        return $this->render('backoffice/comptabilite/reporting.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Reporting'],
            ],
            'report' => $data,
            'type' => $type,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
        ]);
    }

    #[Route('/comptabilite/hors-bilan', name: 'comptabilite_off_balance', methods: ['GET'])]
    public function offBalance(Request $request): Response
    {
        $page = $request->query->getInt('page', 0);
        $data = $this->api->get('/comptabilite/hors-bilan', ['page' => $page])->toArray();

        return $this->render('backoffice/comptabilite/hors-bilan.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Hors bilan'],
            ],
            'engagements' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
        ]);
    }
}
