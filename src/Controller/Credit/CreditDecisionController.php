<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditDecisionController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Réunion du comité de crédit
    public function committee(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}/committee")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/committee.html.twig', $data);
    }

    // Formulaire de décision
    public function index(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/decision", $payload)->toArray();
                $this->addFlash('success', 'Décision enregistrée');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de l’enregistrement de la décision');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/decision")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/decision.html.twig', $data);
    }
}
