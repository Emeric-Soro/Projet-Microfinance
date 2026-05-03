<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ChangelogController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $data = $this->apiClient->get('/admin/changelog')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement du changelog');
            $data = ['data' => []];
        }
        return $this->render('backoffice/admin/changelog.html.twig', [
            'items' => $data['data'] ?? [],
        ]);
    }
}
