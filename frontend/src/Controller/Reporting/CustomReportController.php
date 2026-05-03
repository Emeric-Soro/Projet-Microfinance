<?php

namespace App\Controller\Reporting;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CustomReportController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/reporting/personnalise', name: 'reporting_custom', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $config = $request->request->all('report');
            $this->api->post('/reporting/personnalise', $config)->toArray();
            $this->addFlash('success', 'Rapport personnalisé créé avec succès.');

            return $this->redirectToRoute('reporting_custom');
        }

        $reports = $this->api->get('/reporting/personnalise')->toArray();

        return $this->render('backoffice/reporting/personnalise.html.twig', [
            'reports' => $reports,
            'breadcrumbs' => [
                ['label' => 'Reporting'],
                ['label' => 'Rapports Personnalisés'],
            ],
        ]);
    }

    #[Route('/reporting/programme', name: 'reporting_scheduled', methods: ['GET', 'POST'])]
    public function scheduled(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $program = $request->request->all('program');
            $this->api->post('/reporting/programme', $program)->toArray();
            $this->addFlash('success', 'Programmation de rapport enregistrée.');

            return $this->redirectToRoute('reporting_scheduled');
        }

        $programs = $this->api->get('/reporting/programme')->toArray();

        return $this->render('backoffice/reporting/programme.html.twig', [
            'programs' => $programs,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_custom')],
                ['label' => 'Rapports Programmés'],
            ],
        ]);
    }
}
