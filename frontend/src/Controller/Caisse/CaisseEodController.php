<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseEodController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function eod(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'date' => $request->request->get('date', date('Y-m-d')),
                    'force' => $request->request->has('force'),
                ];
                $response = $this->api->post('/caisse/eod/executer', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Clôture de journée effectuée avec succès');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la clôture: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/caisse/eod.html.twig', [
            'result' => $result,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Clôture de journée'],
            ],
        ]);
    }

    public function monitor(Request $request): Response
    {
        try {
            $status = $this->api->get('/caisse/eod/statut')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger le statut EOD: ' . $e->getMessage());
            $status = [];
        }

        return $this->render('backoffice/caisse/eod-monitor.html.twig', [
            'status' => $status,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Suivi EOD'],
            ],
        ]);
    }

    public function history(Request $request): Response
    {
        $params = [
            'page' => $request->query->getInt('page', 0),
            'date_debut' => $request->query->get('date_debut'),
            'date_fin' => $request->query->get('date_fin'),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $history = $this->api->get('/caisse/eod/historique', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de l\'historique: ' . $e->getMessage());
            $history = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
        }

        return $this->render('backoffice/caisse/eod-history.html.twig', [
            'history' => $history['content'] ?? [],
            'total_items' => $history['totalElements'] ?? 0,
            'total_pages' => $history['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Historique EOD'],
            ],
        ]);
    }
}
