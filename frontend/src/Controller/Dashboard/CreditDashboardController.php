<?php

namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/credit')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord crédit');
        }

        return $this->render('backoffice/dashboard/credit.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Crédit'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }
}
