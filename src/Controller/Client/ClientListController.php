<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\RedirectResponse;

class ClientListController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        $params = [
            'page' => $request->query->getInt('page', 1),
            'search' => $request->query->get('search', ''),
            'type' => $request->query->get('type', ''),
            'status' => $request->query->get('status', ''),
        ];

        try {
            $data = $this->apiClient->get('/clients', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des clients');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }

        return $this->render('backoffice/client/index.html.twig', [
            'clients' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'filters' => [
                'type' => $params['type'],
                'status' => $params['status'],
            ],
        ]);
    }

    public function export(Request $request): Response
    {
        $params = [
            'format' => $request->query->get('format', 'csv'),
            'page' => $request->query->getInt('page', 1),
            'search' => $request->query->get('search', ''),
            'type' => $request->query->get('type', ''),
            'status' => $request->query->get('status', ''),
        ];

        try {
            $response = $this->apiClient->get('/clients/export', $params);
            $content = $response->getContent();
            $format = strtolower($params['format'] ?? 'csv');
            $filename = 'clients.' . ($format === 'excel' ? 'xlsx' : 'csv');
            return new Response($content, 200, [
                'Content-Type' => 'text/' . ($format === 'excel' ? 'xlsx' : 'csv'),
                'Content-Disposition' => 'attachment; filename="' . $filename . '"',
            ]);
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de l’export des clients');
            return $this->redirect($request->headers->get('referer', '/clients'));
        }
    }

    public function stats(): Response
    {
        try {
            $stats = $this->apiClient->get('/clients/stats')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger les statistiques');
            $stats = [];
        }

        return $this->render('backoffice/client/stats.html.twig', [
            'stats' => $stats,
        ]);
    }
}
