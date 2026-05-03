<?php

namespace App\Controller\Epargne;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class SavingsReportController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/epargne/interest-history', name: 'epargne_interest_history', methods: ['GET'])]
    public function interestHistory(Request $request): Response
    {
        $params = [
            'produit' => $request->query->get('produit'),
            'date_debut' => $request->query->get('date_debut'),
            'date_fin' => $request->query->get('date_fin'),
            'page' => $request->query->getInt('page', 0),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $history = $this->api->get('/epargne/interets/historique', $params)->toArray();
            $products = $this->api->get('/epargne/produits')->toArray();
        } catch (\Exception $e) {
            $history = ['content' => [], 'totalElements' => 0];
            $products = [];
        }

        return $this->render('backoffice/epargne/interest-history.html.twig', [
            'history' => $history['content'] ?? [],
            'total_items' => $history['totalElements'] ?? 0,
            'current_page' => $params['page'],
            'products' => $products,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Historique des intérêts'],
            ],
        ]);
    }

    #[Route('/epargne/movements/{id}', name: 'epargne_movements', methods: ['GET'])]
    public function movements(string $id, Request $request): Response
    {
        $params = [
            'page' => $request->query->getInt('page', 0),
            'size' => 20,
            'date_debut' => $request->query->get('date_debut'),
            'date_fin' => $request->query->get('date_fin'),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $account = $this->api->get('/epargne/comptes/' . $id)->toArray();
            $movements = $this->api->get('/epargne/comptes/' . $id . '/mouvements', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable');
            return $this->redirectToRoute('epargne_reporting');
        }

        return $this->render('backoffice/epargne/movements.html.twig', [
            'account' => $account,
            'movements' => $movements['content'] ?? [],
            'total_items' => $movements['totalElements'] ?? 0,
            'total_pages' => $movements['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Mouvements'],
            ],
        ]);
    }

    #[Route('/epargne/reporting', name: 'epargne_reporting', methods: ['GET'])]
    public function index(Request $request): Response
    {
        try {
            $stats = $this->api->get('/epargne/reporting/stats')->toArray();
            $products = $this->api->get('/epargne/reporting/produits')->toArray();
        } catch (\Exception $e) {
            $stats = [];
            $products = [];
        }

        return $this->render('backoffice/epargne/reporting.html.twig', [
            'stats' => $stats,
            'products' => $products,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Reporting'],
            ],
        ]);
    }
}
