<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditProductController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function dashboard(Request $request): Response
    {
        try {
            $data = $this->api->get('/credit/dashboard')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Erreur lors du chargement du tableau de bord crédit.');
        }

        return $this->render('backoffice/credit/dashboard.html.twig', [
            'current_menu' => 'credit_dashboard',
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Crédit'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    public function list(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        try {
            $data = $this->api->get('/credit/products', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des produits de crédit.');
        }

        return $this->render('backoffice/credit/products.html.twig', [
            'current_menu' => 'credit_products',
            'products' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Produits'],
            ],
        ]);
    }

    public function reporting(Request $request): Response
    {
        try {
            $data = $this->api->get('/credit/reporting', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Erreur lors du chargement du reporting crédit.');
        }

        return $this->render('backoffice/credit/reporting.html.twig', [
            'current_menu' => 'credit_reporting',
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Reporting'],
            ],
        ]);
    }

    public function alerts(Request $request): Response
    {
        try {
            $data = $this->api->get('/credit/alerts', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['content' => []];
            $this->addFlash('error', 'Erreur lors du chargement des alertes crédit.');
        }

        return $this->render('backoffice/credit/alerts.html.twig', [
            'current_menu' => 'credit_alerts',
            'alerts' => $data['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Alertes'],
            ],
        ]);
    }
}
