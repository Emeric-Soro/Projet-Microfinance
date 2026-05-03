<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class InventaireController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/inventaire', name: 'organisation_inventaire', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        if ($request->query->get('agence_id')) {
            $params['agence_id'] = $request->query->get('agence_id');
        }

        try {
            $data = $this->api->get('/organisation/inventaire', $params)->toArray();
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $agences = [];
            $this->addFlash('error', 'Erreur lors du chargement des inventaires');
        }

        return $this->render('backoffice/organisation/inventaire.html.twig', [
            'current_menu' => 'organisation_inventaire',
            'inventaires' => $data['content'] ?? [],
            'agences' => $agences['content'] ?? $agences,
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Inventaire'],
            ],
        ]);
    }

    #[Route('/organisation/inventaire/create', name: 'organisation_inventaire_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/inventaire', $request->request->all());
                $this->addFlash('success', 'Inventaire créé avec succès.');
                return $this->redirectToRoute('organisation_inventaire');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
        }

        try {
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $agences = ['content' => []];
        }

        return $this->render('backoffice/organisation/inventaire.html.twig', [
            'current_menu' => 'organisation_inventaire',
            'agences' => $agences['content'] ?? $agences,
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_inventaire')],
                ['label' => 'Inventaire', 'url' => $this->generateUrl('organisation_inventaire')],
                ['label' => 'Nouvel inventaire'],
            ],
        ]);
    }

    #[Route('/organisation/inventaire/{id}/validate', name: 'organisation_inventaire_validate', methods: ['GET'])]
    public function validate(int $id): Response
    {
        try {
            $this->api->post('/organisation/inventaire/' . $id . '/validate');
            $this->addFlash('success', 'Inventaire validé avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la validation: ' . $e->getMessage());
        }

        return $this->redirectToRoute('organisation_inventaire');
    }
}
