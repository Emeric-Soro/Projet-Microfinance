<?php
namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class CashierDashboardController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/cashier')->toArray();
        } catch (\\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Caissier', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/cashier.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }
}
