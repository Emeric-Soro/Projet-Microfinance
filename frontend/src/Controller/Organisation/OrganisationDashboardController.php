<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class OrganisationDashboardController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/dashboard', name: 'organisation_dashboard', methods: ['GET'])]
    public function index(Request $request): Response
    {
        try {
            $stats = $this->api->get('/organisation/dashboard')->toArray();
        } catch (\Exception $e) {
            $stats = [];
            $this->addFlash('error', 'Erreur lors du chargement du tableau de bord');
        }

        return $this->render('backoffice/organisation/dashboard.html.twig', [
            'current_menu' => 'organisation_dashboard',
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }
}
