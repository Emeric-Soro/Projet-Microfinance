<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditProductController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Tableau de bord Crédit
    public function dashboard(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/dashboard')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Échec du chargement du tableau de bord Crédit');
        }
        return $this->render('backoffice/credit/dashboard.html.twig', $data);
    }

    // Liste des produits de crédit
    public function list(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/products')->toArray();
        } catch (\Exception $e) {
            $data = ['data' => []];
        }
        return $this->render('backoffice/credit/products.html.twig', $data);
    }

    // Reporting crédits
    public function reporting(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/reporting')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Échec du chargement des rapports Crédit');
        }
        return $this->render('backoffice/credit/reporting.html.twig', $data);
    }

    // Alertes Crédit
    public function alerts(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/alerts')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Échec du chargement des alertes Crédit');
        }
        return $this->render('backoffice/credit/alerts.html.twig', $data);
    }
}
