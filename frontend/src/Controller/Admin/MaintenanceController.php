<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class MaintenanceController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $status = $this->apiClient->get('/admin/maintenance')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement');
            $status = [];
        }
        return $this->render('backoffice/admin/maintenance.html.twig', [
            'status' => $status, 'mode' => 'index',
        ]);
    }

    public function enable(): Response
    {
        try {
            $this->apiClient->post('/admin/maintenance/enable')->toArray();
            $this->addFlash('success', 'Mode maintenance activé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur d\'activation');
        }
        return $this->redirectToRoute('admin_maintenance');
    }

    public function disable(): Response
    {
        try {
            $this->apiClient->post('/admin/maintenance/disable')->toArray();
            $this->addFlash('success', 'Mode maintenance désactivé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de désactivation');
        }
        return $this->redirectToRoute('admin_maintenance');
    }

    public function message(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->redirectToRoute('admin_maintenance');
        }
        try {
            $this->apiClient->put('/admin/maintenance/message', $request->request->all())->toArray();
            $this->addFlash('success', 'Message de maintenance mis à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de mise à jour');
        }
        return $this->redirectToRoute('admin_maintenance');
    }
}
