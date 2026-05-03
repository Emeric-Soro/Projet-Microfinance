<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class LogController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/logs', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des logs');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/admin/logs.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/admin/logs/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Log introuvable');
            return $this->redirectToRoute('admin_logs');
        }
        return $this->render('backoffice/admin/logs.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }
}
