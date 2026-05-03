<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AgenceController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/agences', name: 'organisation_agences', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        if ($request->query->get('region_id')) {
            $params['region_id'] = $request->query->get('region_id');
        }

        try {
            $data = $this->api->get('/organisation/agences', $params)->toArray();
            $regions = $this->api->get('/organisation/regions')->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $regions = [];
            $this->addFlash('error', 'Erreur lors du chargement des agences');
        }

        return $this->render('backoffice/organisation/agences.html.twig', [
            'current_menu' => 'organisation_agences',
            'agences' => $data['content'] ?? [],
            'regions' => $regions['content'] ?? $regions,
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Agences'],
            ],
        ]);
    }

    #[Route('/organisation/agences/create', name: 'organisation_agences_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/agences', $request->request->all());
                $this->addFlash('success', 'Agence créée avec succès.');
                return $this->redirectToRoute('organisation_agences');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
        }

        try {
            $regions = $this->api->get('/organisation/regions')->toArray();
        } catch (\Exception $e) {
            $regions = ['content' => []];
        }

        return $this->render('backoffice/organisation/agences.html.twig', [
            'current_menu' => 'organisation_agences',
            'regions' => $regions['content'] ?? $regions,
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Agences', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Nouvelle agence'],
            ],
        ]);
    }

    #[Route('/organisation/agences/{id}', name: 'organisation_agences_show', methods: ['GET'])]
    public function show(int $id): Response
    {
        try {
            $agence = $this->api->get('/organisation/agences/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Agence introuvable');
            return $this->redirectToRoute('organisation_agences');
        }

        return $this->render('backoffice/organisation/agences.html.twig', [
            'current_menu' => 'organisation_agences',
            'agence' => $agence,
            'show_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Agences', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => $agence['libelle'] ?? 'Détail'],
            ],
        ]);
    }

    #[Route('/organisation/agences/{id}/edit', name: 'organisation_agences_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/organisation/agences/' . $id, $request->request->all());
                $this->addFlash('success', 'Agence mise à jour avec succès.');
                return $this->redirectToRoute('organisation_agences');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
        }

        try {
            $agence = $this->api->get('/organisation/agences/' . $id)->toArray();
            $regions = $this->api->get('/organisation/regions')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Agence introuvable');
            return $this->redirectToRoute('organisation_agences');
        }

        return $this->render('backoffice/organisation/agences.html.twig', [
            'current_menu' => 'organisation_agences',
            'agence' => $agence,
            'regions' => $regions['content'] ?? $regions,
            'edit_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Agences', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Modifier'],
            ],
        ]);
    }

    #[Route('/organisation/agences/{id}/params', name: 'organisation_agences_params', methods: ['GET', 'POST'])]
    public function params(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/organisation/agences/' . $id . '/params', $request->request->all());
                $this->addFlash('success', 'Paramètres mis à jour avec succès.');
                return $this->redirectToRoute('organisation_agences_show', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
        }

        try {
            $agence = $this->api->get('/organisation/agences/' . $id)->toArray();
            $params = $this->api->get('/organisation/agences/' . $id . '/params')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Agence introuvable');
            return $this->redirectToRoute('organisation_agences');
        }

        return $this->render('backoffice/organisation/agences.html.twig', [
            'current_menu' => 'organisation_agences',
            'agence' => $agence,
            'params' => $params,
            'params_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Agences', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => $agence['libelle'] ?? '', 'url' => $this->generateUrl('organisation_agences_show', ['id' => $id])],
                ['label' => 'Paramètres'],
            ],
        ]);
    }
}
