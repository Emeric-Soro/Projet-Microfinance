<?php

namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AccountListController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/comptes', name: 'account_list', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        if ($request->query->get('q')) {
            $params['q'] = $request->query->get('q');
        }
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }
        if ($request->query->get('agence_id')) {
            $params['agence_id'] = $request->query->get('agence_id');
        }

        try {
            $data = $this->api->get('/comptes', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement de la liste des comptes.');
        }

        return $this->render('backoffice/compte/index.html.twig', [
            'current_menu' => 'account_list',
            'comptes' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'search_query' => $request->query->get('q', ''),
            'breadcrumbs' => [
                ['label' => 'Comptes'],
                ['label' => 'Liste'],
            ],
        ]);
    }

    #[Route('/comptes/dormants', name: 'account_dormant', methods: ['GET'])]
    public function dormant(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;
        $params['statut'] = 'DORMANT';

        try {
            $data = $this->api->get('/comptes/dormants', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des comptes dormants.');
        }

        return $this->render('backoffice/compte/dormant.html.twig', [
            'current_menu' => 'account_dormant',
            'comptes' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => 'Comptes dormants'],
            ],
        ]);
    }

    #[Route('/comptes/pending', name: 'account_pending', methods: ['GET'])]
    public function pending(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        try {
            $data = $this->api->get('/comptes/pending', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des comptes en attente.');
        }

        return $this->render('backoffice/compte/pending.html.twig', [
            'current_menu' => 'account_pending',
            'comptes' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => 'En attente'],
            ],
        ]);
    }

    #[Route('/comptes/agence/{id}', name: 'account_by_agency', methods: ['GET'])]
    public function byAgency(int $id, Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;
        $params['agence_id'] = $id;

        try {
            $data = $this->api->get('/comptes/by-agency/' . $id, $params)->toArray();
            $agence = $this->api->get('/agences/' . $id)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $agence = ['nom' => 'Agence #' . $id];
            $this->addFlash('error', 'Erreur lors du chargement des comptes de l\'agence.');
        }

        return $this->render('backoffice/compte/agency.html.twig', [
            'current_menu' => 'account_by_agency',
            'comptes' => $data['content'] ?? [],
            'agence' => $agence,
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $agence['nom'] ?? 'Agence'],
            ],
        ]);
    }
}
