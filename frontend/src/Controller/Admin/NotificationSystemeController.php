<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class NotificationSystemeController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/admin/notifications', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des notifications');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/admin/notifications-systeme.html.twig', [
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
            return $this->render('backoffice/admin/notifications-systeme.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/admin/notifications', $request->request->all())->toArray();
            $this->addFlash('success', 'Notification créée');
            return $this->redirectToRoute('admin_notifications_systeme');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/admin/notifications-systeme.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function send(int $id): Response
    {
        try {
            $this->apiClient->post('/admin/notifications/'.$id.'/send')->toArray();
            $this->addFlash('success', 'Notification envoyée');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'envoi');
        }
        return $this->redirectToRoute('admin_notifications_systeme');
    }
}
