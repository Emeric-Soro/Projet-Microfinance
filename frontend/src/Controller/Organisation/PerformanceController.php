<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class PerformanceController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/performance', name: 'organisation_performance', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }
        if ($request->query->get('agence_id')) {
            $params['agenceId'] = $request->query->get('agence_id');
        }

        try {
            $stats = $this->api->get('/organisation/performance', $params)->toArray();
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $stats = [];
            $agences = [];
            $this->addFlash('error', 'Erreur lors du chargement des performances');
        }

        return $this->render('backoffice/organisation/performance.html.twig', [
            'current_menu' => 'organisation_performance',
            'stats' => $stats,
            'agences' => $agences['content'] ?? $agences,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Performance des Agences'],
            ],
        ]);
    }
}
