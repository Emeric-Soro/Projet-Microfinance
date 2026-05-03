<?php

namespace App\Controller\Reporting;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RegulatoryReportController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/reporting/reglementaire', name: 'reporting_regulatory', methods: ['GET'])]
    public function index(): Response
    {
        $data = $this->api->get('/reporting/reglementaire')->toArray();

        return $this->render('backoffice/reporting/reglementaire.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting'],
                ['label' => 'Reporting Réglementaire'],
            ],
        ]);
    }

    #[Route('/reporting/conformite', name: 'reporting_compliance', methods: ['GET'])]
    public function compliance(): Response
    {
        $data = $this->api->get('/reporting/conformite')->toArray();

        return $this->render('backoffice/reporting/conformite.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_regulatory')],
                ['label' => 'Reporting Conformité'],
            ],
        ]);
    }

    #[Route('/reporting/rh', name: 'reporting_hr', methods: ['GET'])]
    public function hr(): Response
    {
        $data = $this->api->get('/reporting/rh')->toArray();

        return $this->render('backoffice/reporting/rh.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_regulatory')],
                ['label' => 'Reporting RH'],
            ],
        ]);
    }
}
