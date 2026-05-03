<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class VersionController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(): Response
    {
        try {
            $data = $this->apiClient->get('/admin/versions')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des versions');
            $data = ['data' => []];
        }
        return $this->render('backoffice/admin/versions.html.twig', [
            'items' => $data['data'] ?? [],
        ]);
    }
}
