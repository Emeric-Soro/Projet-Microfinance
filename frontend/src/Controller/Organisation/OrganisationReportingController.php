<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class OrganisationReportingController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/reporting', name: 'organisation_reporting', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }

        try {
            $data = $this->api->get('/organisation/reporting', $params)->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Erreur lors du chargement du reporting');
        }

        return $this->render('backoffice/organisation/reporting.html.twig', [
            'current_menu' => 'organisation_reporting',
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Reporting'],
            ],
        ]);
    }
}
