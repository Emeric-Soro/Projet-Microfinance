<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SauvegardeController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/sauvegardes', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des sauvegardes');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/admin/sauvegardes.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function create(): Response
    {
        try {
            $this->apiClient->post('/admin/sauvegardes')->toArray();
            $this->addFlash('success', 'Sauvegarde déclenchée');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la sauvegarde');
        }
        return $this->redirectToRoute('admin_sauvegardes');
    }

    public function restore(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $item = $this->apiClient->get('/admin/sauvegardes/'.$id)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Sauvegarde introuvable');
                return $this->redirectToRoute('admin_sauvegardes');
            }
            return $this->render('backoffice/admin/sauvegardes.html.twig', [
                'item' => $item, 'mode' => 'restore',
            ]);
        }
        try {
            $this->apiClient->post('/admin/sauvegardes/'.$id.'/restore', $request->request->all())->toArray();
            $this->addFlash('success', 'Restauration effectuée');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la restauration');
        }
        return $this->redirectToRoute('admin_sauvegardes');
    }
}
