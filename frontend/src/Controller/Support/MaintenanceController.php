<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class MaintenanceController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/maintenance', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des maintenances');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/maintenance.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function create(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/support/maintenance.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/maintenance', $request->request->all())->toArray();
            $this->addFlash('success', 'Maintenance enregistrée');
            return $this->redirectToRoute('support_maintenance');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'enregistrement');
            return $this->render('backoffice/support/maintenance.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }
}
