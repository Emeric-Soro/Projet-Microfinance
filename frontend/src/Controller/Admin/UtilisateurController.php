<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class UtilisateurController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/utilisateurs', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des utilisateurs');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/admin/utilisateurs.html.twig', [
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
            return $this->render('backoffice/admin/utilisateurs.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/admin/utilisateurs', $request->request->all())->toArray();
            $this->addFlash('success', 'Utilisateur créé avec succès');
            return $this->redirectToRoute('admin_utilisateurs');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/admin/utilisateurs.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/admin/utilisateurs/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Utilisateur introuvable');
            return $this->redirectToRoute('admin_utilisateurs');
        }
        return $this->render('backoffice/admin/utilisateurs.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }

    public function edit(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $item = $this->apiClient->get('/admin/utilisateurs/'.$id)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Utilisateur introuvable');
                return $this->redirectToRoute('admin_utilisateurs');
            }
            return $this->render('backoffice/admin/utilisateurs.html.twig', [
                'item' => $item, 'mode' => 'edit',
            ]);
        }
        try {
            $this->apiClient->put('/admin/utilisateurs/'.$id, $request->request->all())->toArray();
            $this->addFlash('success', 'Utilisateur mis à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de mise à jour');
        }
        return $this->redirectToRoute('admin_utilisateurs_show', ['id' => $id]);
    }

    public function suspend(int $id): Response
    {
        try {
            $this->apiClient->post('/admin/utilisateurs/'.$id.'/suspend')->toArray();
            $this->addFlash('success', 'Utilisateur suspendu');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la suspension');
        }
        return $this->redirectToRoute('admin_utilisateurs');
    }
}
