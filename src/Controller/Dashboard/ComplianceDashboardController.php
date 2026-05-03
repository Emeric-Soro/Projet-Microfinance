<?php
namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class ComplianceDashboardController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/compliance')->toArray();
        } catch (\\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Conformité', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/compliance.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }
}
