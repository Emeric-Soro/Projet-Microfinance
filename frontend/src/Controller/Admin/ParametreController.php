<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ParametreController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/parametres', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des paramètres');
            $data = ['data' => [], 'total' => 0];
        }
        return $this->render('backoffice/admin/parametres.html.twig', [
            'items' => $data['data'] ?? [],
            'mode' => 'list',
        ]);
    }

    public function edit(string $key, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $item = $this->apiClient->get('/admin/parametres/'.$key)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Paramètre introuvable');
                return $this->redirectToRoute('admin_parametres');
            }
            return $this->render('backoffice/admin/parametres.html.twig', [
                'item' => $item, 'mode' => 'edit',
            ]);
        }
        try {
            $this->apiClient->put('/admin/parametres/'.$key, $request->request->all())->toArray();
            $this->addFlash('success', 'Paramètre mis à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de mise à jour');
        }
        return $this->redirectToRoute('admin_parametres');
    }
}
