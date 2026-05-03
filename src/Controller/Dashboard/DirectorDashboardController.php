<?php
namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class DirectorDashboardController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/director')->toArray();
        } catch (\\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Direction', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/direction.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }
}
