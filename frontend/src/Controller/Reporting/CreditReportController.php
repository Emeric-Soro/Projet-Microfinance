<?php

namespace App\Controller\Reporting;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CreditReportController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/reporting/credit', name: 'reporting_credit', methods: ['GET'])]
    public function index(): Response
    {
        $data = $this->api->get('/reporting/credit')->toArray();

        return $this->render('backoffice/reporting/credit.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting'],
                ['label' => 'Reporting Crédit'],
            ],
        ]);
    }

    #[Route('/reporting/epargne', name: 'reporting_savings', methods: ['GET'])]
    public function savings(): Response
    {
        $data = $this->api->get('/reporting/epargne')->toArray();

        return $this->render('backoffice/reporting/epargne.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Reporting', 'url' => $this->generateUrl('reporting_credit')],
                ['label' => 'Reporting Épargne'],
            ],
        ]);
    }
}
