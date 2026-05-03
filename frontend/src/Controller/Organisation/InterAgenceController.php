<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class InterAgenceController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/inter-agences', name: 'organisation_inter_agences', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }

        try {
            $data = $this->api->get('/organisation/inter-agences', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des opérations inter-agences');
        }

        return $this->render('backoffice/organisation/inter-agences.html.twig', [
            'current_menu' => 'organisation_inter_agences',
            'operations' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Opérations Inter-Agences'],
            ],
        ]);
    }
}
