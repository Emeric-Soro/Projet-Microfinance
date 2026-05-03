<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class GuichetController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/guichets', name: 'organisation_guichets', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        if ($request->query->get('agence_id')) {
            $params['agence_id'] = $request->query->get('agence_id');
        }

        try {
            $data = $this->api->get('/organisation/guichets', $params)->toArray();
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $agences = [];
            $this->addFlash('error', 'Erreur lors du chargement des guichets');
        }

        return $this->render('backoffice/organisation/guichets.html.twig', [
            'current_menu' => 'organisation_guichets',
            'guichets' => $data['content'] ?? [],
            'agences' => $agences['content'] ?? $agences,
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Guichets'],
            ],
        ]);
    }

    #[Route('/organisation/guichets/create', name: 'organisation_guichets_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/guichets', $request->request->all());
                $this->addFlash('success', 'Guichet créé avec succès.');
                return $this->redirectToRoute('organisation_guichets');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
        }

        try {
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $agences = ['content' => []];
        }

        return $this->render('backoffice/organisation/guichets.html.twig', [
            'current_menu' => 'organisation_guichets',
            'agences' => $agences['content'] ?? $agences,
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_guichets')],
                ['label' => 'Guichets', 'url' => $this->generateUrl('organisation_guichets')],
                ['label' => 'Nouveau guichet'],
            ],
        ]);
    }
}
