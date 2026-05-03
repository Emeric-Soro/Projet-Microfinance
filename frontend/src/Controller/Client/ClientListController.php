<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientListController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients', name: 'client_list', methods: ['GET'])]
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
            $data = $this->api->get('/clients', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement de la liste des clients.');
        }

        return $this->render('backoffice/client/index.html.twig', [
            'current_menu' => 'client_list',
            'clients' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'search_query' => $request->query->get('q', ''),
            'breadcrumbs' => [
                ['label' => 'Clients'],
                ['label' => 'Liste'],
            ],
        ]);
    }

    #[Route('/clients/export', name: 'client_export', methods: ['GET'])]
    public function export(Request $request): Response
    {
        $format = $request->query->get('format', 'csv');
        $params = [];

        if ($request->query->get('q')) {
            $params['q'] = $request->query->get('q');
        }
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        try {
            $data = $this->api->get('/clients/export', array_merge($params, ['format' => $format]))->toArray();
            $content = $data['content'] ?? '';
            $filename = 'clients_' . date('Y-m-d') . '.' . $format;

            return new Response(
                $content,
                Response::HTTP_OK,
                [
                    'Content-Type' => $format === 'csv' ? 'text/csv' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                    'Content-Disposition' => 'attachment; filename="' . $filename . '"',
                ]
            );
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'exportation des clients.');
            return $this->redirectToRoute('client_list');
        }
    }

    #[Route('/clients/stats', name: 'client_stats', methods: ['GET'])]
    public function stats(Request $request): Response
    {
        try {
            $data = $this->api->get('/clients/stats')->toArray();
        } catch (\Exception $e) {
            $data = [
                'total' => 0, 'actifs' => 0, 'suspendus' => 0,
                'physiques' => 0, 'moraux' => 0,
                'nouveaux_mois' => 0, 'nouveaux_semaine' => 0,
                'par_agence' => [], 'par_statut' => [],
            ];
            $this->addFlash('error', 'Erreur lors du chargement des statistiques.');
        }

        return $this->render('backoffice/client/stats.html.twig', [
            'current_menu' => 'client_stats',
            'stats' => $data,
            'breadcrumbs' => [
                ['label' => 'Clients'],
                ['label' => 'Statistiques'],
            ],
        ]);
    }
}
