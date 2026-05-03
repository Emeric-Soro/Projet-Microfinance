<?php
namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class AgencyDashboardController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(int $id): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/agency/'.$id)->toArray();
        } catch (\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Agence', 'url' => ''],
            ['label' => 'Vue Agence', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/agency.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }
}
