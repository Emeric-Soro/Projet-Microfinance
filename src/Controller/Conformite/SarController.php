<?php
namespace App\Controller\Conformite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SarController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/sar', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des déclarations SAR.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/sar.html.twig', $data);
    }

    public function reports(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/sar/reports', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des rapports SAR.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/reports.html.twig', $data);
    }
}
