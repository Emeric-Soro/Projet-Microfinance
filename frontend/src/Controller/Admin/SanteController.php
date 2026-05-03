<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SanteController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $health = $this->apiClient->get('/admin/sante')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement de l\'état du système');
            $health = [];
        }
        return $this->render('backoffice/admin/sante.html.twig', [
            'health' => $health,
        ]);
    }
}
