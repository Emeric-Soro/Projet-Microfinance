<?php

namespace App\Controller\Risque;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RiskController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/risques', name: 'risque_dashboard', methods: ['GET'])]
    public function dashboard(): Response
    {
        $stats = $this->api->get('/risques/dashboard')->toArray();

        return $this->render('backoffice/risques/dashboard.html.twig', [
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    #[Route('/risques/credit', name: 'risque_credit', methods: ['GET'])]
    public function credit(): Response
    {
        $data = $this->api->get('/risques/credit')->toArray();

        return $this->render('backoffice/risques/credit.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Risque de Crédit'],
            ],
        ]);
    }

    #[Route('/risques/provisions', name: 'risque_provisions', methods: ['GET'])]
    public function provisions(): Response
    {
        $data = $this->api->get('/risques/provisions')->toArray();

        return $this->render('backoffice/risques/provisions.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Analyse des Provisions'],
            ],
        ]);
    }

    #[Route('/risques/liquidite', name: 'risque_liquidity', methods: ['GET'])]
    public function liquidity(): Response
    {
        $data = $this->api->get('/risques/liquidite')->toArray();

        return $this->render('backoffice/risques/liquidite.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Risque de Liquidité'],
            ],
        ]);
    }

    #[Route('/risques/cartographie', name: 'risque_risk_map', methods: ['GET'])]
    public function riskMap(): Response
    {
        $data = $this->api->get('/risques/cartographie')->toArray();

        return $this->render('backoffice/risques/cartographie.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Cartographie des Risques'],
            ],
        ]);
    }

    #[Route('/risques/alm', name: 'risque_alm', methods: ['GET'])]
    public function alm(): Response
    {
        $data = $this->api->get('/risques/alm')->toArray();

        return $this->render('backoffice/risques/alm.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'ALM'],
            ],
        ]);
    }

    #[Route('/risques/pca', name: 'risque_pca', methods: ['GET'])]
    public function pca(): Response
    {
        $data = $this->api->get('/risques/pca')->toArray();

        return $this->render('backoffice/risques/pca.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Plan de Continuité d\'Activité'],
            ],
        ]);
    }

    #[Route('/risques/pra', name: 'risque_pra', methods: ['GET'])]
    public function pra(): Response
    {
        $data = $this->api->get('/risques/pra')->toArray();

        return $this->render('backoffice/risques/pra.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Plan de Reprise d\'Activité'],
            ],
        ]);
    }

    #[Route('/risques/taux', name: 'risque_interest_rate', methods: ['GET'])]
    public function interestRate(): Response
    {
        $data = $this->api->get('/risques/taux')->toArray();

        return $this->render('backoffice/risques/taux.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Risque de Taux'],
            ],
        ]);
    }
}
