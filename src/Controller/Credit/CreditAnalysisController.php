<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditAnalysisController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Formulaire d'analyse crédit
    public function index(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}/analysis")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/analysis.html.twig', $data);
    }

    // Page de scoring
    public function scoring(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}/scoring")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/scoring.html.twig', $data);
    }
}
