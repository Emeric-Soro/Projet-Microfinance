<?php

namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AgencyDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(int $id, Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/agence/' . $id)->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord de l\'agence');
        }

        return $this->render('backoffice/dashboard/agency.html.twig', [
            'data' => $data,
            'agence_id' => $id,
            'breadcrumbs' => [
                ['label' => 'Agences', 'url' => $this->generateUrl('organisation_agencies')],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }
}
