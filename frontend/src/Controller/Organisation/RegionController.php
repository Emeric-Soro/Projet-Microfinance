<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RegionController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/regions', name: 'organisation_regions', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;

        try {
            $data = $this->api->get('/organisation/regions', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des régions');
        }

        return $this->render('backoffice/organisation/regions.html.twig', [
            'current_menu' => 'organisation_regions',
            'regions' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Régions'],
            ],
        ]);
    }

    #[Route('/organisation/regions/create', name: 'organisation_regions_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/regions', $request->request->all());
                $this->addFlash('success', 'Région créée avec succès.');
                return $this->redirectToRoute('organisation_regions');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/organisation/regions.html.twig', [
            'current_menu' => 'organisation_regions',
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_regions')],
                ['label' => 'Régions', 'url' => $this->generateUrl('organisation_regions')],
                ['label' => 'Nouvelle région'],
            ],
        ]);
    }

    #[Route('/organisation/regions/{id}', name: 'organisation_regions_show', methods: ['GET'])]
    public function show(int $id): Response
    {
        try {
            $region = $this->api->get('/organisation/regions/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Région introuvable');
            return $this->redirectToRoute('organisation_regions');
        }

        return $this->render('backoffice/organisation/regions.html.twig', [
            'current_menu' => 'organisation_regions',
            'region' => $region,
            'show_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_regions')],
                ['label' => 'Régions', 'url' => $this->generateUrl('organisation_regions')],
                ['label' => $region['libelle'] ?? 'Détail'],
            ],
        ]);
    }

    #[Route('/organisation/regions/{id}/edit', name: 'organisation_regions_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/organisation/regions/' . $id, $request->request->all());
                $this->addFlash('success', 'Région mise à jour avec succès.');
                return $this->redirectToRoute('organisation_regions');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
        }

        try {
            $region = $this->api->get('/organisation/regions/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Région introuvable');
            return $this->redirectToRoute('organisation_regions');
        }

        return $this->render('backoffice/organisation/regions.html.twig', [
            'current_menu' => 'organisation_regions',
            'region' => $region,
            'edit_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_regions')],
                ['label' => 'Régions', 'url' => $this->generateUrl('organisation_regions')],
                ['label' => 'Modifier'],
            ],
        ]);
    }
}
