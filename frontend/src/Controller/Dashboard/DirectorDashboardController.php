<?php

namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class DirectorDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/direction')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord direction');
        }

        return $this->render('backoffice/dashboard/direction.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Direction'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }
}
