<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CacheController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $status = $this->apiClient->get('/admin/cache')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement du cache');
            $status = [];
        }
        return $this->render('backoffice/admin/cache.html.twig', [
            'status' => $status, 'mode' => 'index',
        ]);
    }

    public function clear(string $prefix): Response
    {
        try {
            $this->apiClient->post('/admin/cache/clear/'.$prefix)->toArray();
            $this->addFlash('success', 'Cache vidé pour : '.$prefix);
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du vidage du cache');
        }
        return $this->redirectToRoute('admin_cache');
    }

    public function clearAll(): Response
    {
        try {
            $this->apiClient->post('/admin/cache/clear-all')->toArray();
            $this->addFlash('success', 'Cache intégralement vidé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du vidage du cache');
        }
        return $this->redirectToRoute('admin_cache');
    }
}
