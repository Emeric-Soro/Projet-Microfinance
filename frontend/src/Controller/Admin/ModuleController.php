<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ModuleController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/modules', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des modules');
            $data = ['data' => []];
        }
        return $this->render('backoffice/admin/modules.html.twig', [
            'items' => $data['data'] ?? [],
            'mode' => 'list',
        ]);
    }

    public function toggle(int $id): Response
    {
        try {
            $this->apiClient->post('/admin/modules/'.$id.'/toggle')->toArray();
            $this->addFlash('success', 'Module basculé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du basculement');
        }
        return $this->redirectToRoute('admin_modules');
    }

    public function configure(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $item = $this->apiClient->get('/admin/modules/'.$id)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Module introuvable');
                return $this->redirectToRoute('admin_modules');
            }
            return $this->render('backoffice/admin/modules.html.twig', [
                'item' => $item, 'mode' => 'configure',
            ]);
        }
        try {
            $this->apiClient->put('/admin/modules/'.$id, $request->request->all())->toArray();
            $this->addFlash('success', 'Module configuré');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de configuration');
        }
        return $this->redirectToRoute('admin_modules');
    }
}
