<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class PermissionController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/permissions', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des permissions');
            $data = ['data' => [], 'total' => 0];
        }
        return $this->render('backoffice/admin/permissions.html.twig', [
            'items' => $data['data'] ?? [],
            'mode' => 'list',
        ]);
    }

    public function matrix(): Response
    {
        try {
            $matrix = $this->apiClient->get('/admin/permissions/matrix')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement de la matrice');
            $matrix = [];
        }
        return $this->render('backoffice/admin/permissions.html.twig', [
            'matrix' => $matrix, 'mode' => 'matrix',
        ]);
    }
}
