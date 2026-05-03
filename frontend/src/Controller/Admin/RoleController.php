<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class RoleController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/roles', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des rôles');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/admin/roles.html.twig', [
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
            return $this->render('backoffice/admin/roles.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/admin/roles', $request->request->all())->toArray();
            $this->addFlash('success', 'Rôle créé avec succès');
            return $this->redirectToRoute('admin_roles');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/admin/roles.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function edit(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $item = $this->apiClient->get('/admin/roles/'.$id)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Rôle introuvable');
                return $this->redirectToRoute('admin_roles');
            }
            return $this->render('backoffice/admin/roles.html.twig', [
                'item' => $item, 'mode' => 'edit',
            ]);
        }
        try {
            $this->apiClient->put('/admin/roles/'.$id, $request->request->all())->toArray();
            $this->addFlash('success', 'Rôle mis à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de mise à jour');
        }
        return $this->redirectToRoute('admin_roles');
    }

    public function permissions(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/admin/roles/'.$id)->toArray();
            $permissions = $this->apiClient->get('/admin/roles/'.$id.'/permissions')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des permissions');
            return $this->redirectToRoute('admin_roles');
        }
        return $this->render('backoffice/admin/roles.html.twig', [
            'item' => $item, 'permissions' => $permissions, 'mode' => 'permissions',
        ]);
    }
}
