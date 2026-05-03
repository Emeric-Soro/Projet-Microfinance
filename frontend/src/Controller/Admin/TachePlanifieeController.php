<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class TachePlanifieeController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/taches-planifiees', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des tâches');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/admin/taches.html.twig', [
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
            return $this->render('backoffice/admin/taches.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/admin/taches-planifiees', $request->request->all())->toArray();
            $this->addFlash('success', 'Tâche planifiée créée');
            return $this->redirectToRoute('admin_taches_planifiees');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/admin/taches.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function run(int $id): Response
    {
        try {
            $this->apiClient->post('/admin/taches-planifiees/'.$id.'/run')->toArray();
            $this->addFlash('success', 'Tâche exécutée');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'exécution');
        }
        return $this->redirectToRoute('admin_taches_planifiees');
    }
}
