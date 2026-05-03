<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseVarianceController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        $params = [
            'date' => $request->query->get('date'),
            'guichet' => $request->query->get('guichet'),
            'page' => $request->query->getInt('page', 0),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $variances = $this->api->get('/caisse/variances', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des écarts: ' . $e->getMessage());
            $variances = ['content' => [], 'totalElements' => 0];
        }

        return $this->render('backoffice/caisse/variances.html.twig', [
            'variances' => $variances['content'] ?? [],
            'total_items' => $variances['totalElements'] ?? 0,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Écarts'],
            ],
        ]);
    }

    public function validateVariance(int $id, Request $request): Response
    {
        try {
            $variance = $this->api->get('/caisse/variances/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Écart introuvable');
            return $this->redirectToRoute('caisse_variances');
        }

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'action' => $request->request->get('action', 'APPROVE'),
                    'commentaire' => $request->request->get('commentaire'),
                ];
                $this->api->post('/caisse/variances/' . $id . '/validate', $data);
                $this->addFlash('success', 'Écart traité avec succès');
                return $this->redirectToRoute('caisse_variances');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du traitement: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/caisse/variance-validate.html.twig', [
            'variance' => $variance,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Validation écart'],
            ],
        ]);
    }
}
