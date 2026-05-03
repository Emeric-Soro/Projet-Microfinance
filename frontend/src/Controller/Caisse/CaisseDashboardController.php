<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->api->get('/caisse/dashboard')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger le tableau de bord: ' . $e->getMessage());
            $data = [];
        }

        return $this->render('backoffice/caisse/dashboard.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }
}
