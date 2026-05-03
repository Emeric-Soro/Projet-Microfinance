<?php

namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CashierDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/caissier')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord caissier');
        }

        return $this->render('backoffice/dashboard/cashier.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Tableau de bord caissier'],
            ],
        ]);
    }
}
