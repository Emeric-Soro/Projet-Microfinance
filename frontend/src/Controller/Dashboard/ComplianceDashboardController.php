<?php

namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ComplianceDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/compliance')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord conformité');
        }

        return $this->render('backoffice/dashboard/compliance.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Conformité'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }
}
