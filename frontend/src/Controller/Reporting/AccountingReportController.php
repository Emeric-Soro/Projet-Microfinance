<?php

namespace App\Controller\Reporting;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AccountingReportController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/reporting/comptable', name: 'reporting_accounting', methods: ['GET'])]
    public function index(): Response
    {
        $data = $this->api->get('/reporting/comptable')->toArray();

        return $this->render('backoffice/reporting/comptable.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting'],
                ['label' => 'Reporting Comptable'],
            ],
        ]);
    }

    #[Route('/reporting/ratios', name: 'reporting_ratios', methods: ['GET'])]
    public function ratios(): Response
    {
        $data = $this->api->get('/reporting/ratios')->toArray();

        return $this->render('backoffice/reporting/ratios.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_accounting')],
                ['label' => 'Ratios'],
            ],
        ]);
    }

    #[Route('/reporting/caisse', name: 'reporting_cash', methods: ['GET'])]
    public function cash(): Response
    {
        $data = $this->api->get('/reporting/caisse')->toArray();

        return $this->render('backoffice/reporting/caisse.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_accounting')],
                ['label' => 'Reporting Caisse'],
            ],
        ]);
    }

    #[Route('/reporting/agences', name: 'reporting_agency', methods: ['GET'])]
    public function agency(): Response
    {
        $data = $this->api->get('/reporting/agences')->toArray();

        return $this->render('backoffice/reporting/agences.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_accounting')],
                ['label' => 'Reporting Agences'],
            ],
        ]);
    }

    #[Route('/reporting/abonnements', name: 'reporting_subscriptions', methods: ['GET', 'POST'])]
    public function subscriptions(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = $request->request->all('subscription');
            $this->api->post('/reporting/abonnements', $data)->toArray();
            $this->addFlash('success', 'Abonnement au reporting enregistré.');

            return $this->redirectToRoute('reporting_subscriptions');
        }

        $subscriptions = $this->api->get('/reporting/abonnements')->toArray();

        return $this->render('backoffice/reporting/abonnements.html.twig', [
            'subscriptions' => $subscriptions,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_accounting')],
                ['label' => 'Abonnements'],
            ],
        ]);
    }
}
