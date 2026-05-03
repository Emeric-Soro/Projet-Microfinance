<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditDisbursementController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Contrat de crédit – aperçu/édition
    public function contract(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}/contract")->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Échec du chargement du contrat');
        }
        return $this->render('backoffice/credit/contract.html.twig', $data);
    }

    // Formulaire de déblocage (avancement)
    public function index(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/disbursement", $payload)->toArray();
                $this->addFlash('success', 'Déblocage enregistré');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec du déblocage');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/disbursement")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/disbursement.html.twig', $data);
    }
}
