<?php

namespace App\Controller\Comptabilite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ChartController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/comptabilite/plan-comptable', name: 'comptabilite_chart', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $search = $request->query->get('search', '');
        $type = $request->query->get('type', '');
        $page = $request->query->getInt('page', 0);
        $params = array_filter(compact('search', 'type', 'page'));

        $data = $this->api->get('/comptabilite/plan-comptable', $params)->toArray();

        return $this->render('backoffice/comptabilite/plan-comptable.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Plan comptable'],
            ],
            'accounts' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
            'search' => $search,
            'type' => $type,
            'classes' => $this->api->get('/comptabilite/plan-comptable/classes')->toArray()['data'] ?? [],
        ]);
    }

    #[Route('/comptabilite/schemas', name: 'comptabilite_schemas', methods: ['GET'])]
    public function schemas(Request $request): Response
    {
        $page = $request->query->getInt('page', 0);
        $data = $this->api->get('/comptabilite/schemas', ['page' => $page])->toArray();

        return $this->render('backoffice/comptabilite/schemas.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Schémas comptables'],
            ],
            'schemas' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
        ]);
    }

    #[Route('/comptabilite/schemas/test', name: 'comptabilite_schema_test', methods: ['GET', 'POST'])]
    public function schemaTest(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            $schemaId = $request->request->get('schema_id');
            $montant = $request->request->get('montant');
            $data = ['schema_id' => $schemaId, 'montant' => $montant];

            try {
                $response = $this->api->post('/comptabilite/schemas/test', $data)->toArray();
                $this->addFlash('success', 'Test exécuté avec succès.');
                $result = $response['result'] ?? null;
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $schemas = $this->api->get('/comptabilite/schemas')->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/schema-test.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Schémas comptables', 'url' => $this->generateUrl('comptabilite_schemas')],
                ['label' => 'Test'],
            ],
            'schemas' => $schemas,
            'result' => $result,
        ]);
    }
}
