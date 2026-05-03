<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditRecoveryController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Arriérés
    public function arrears(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/arrears')->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/arrears.html.twig', $data);
    }

    // Action de récupération pour un crédit
    public function index(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/recovery", $payload)->toArray();
                $this->addFlash('success', 'Action de récupération enregistrée');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de l’action de récupération');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/recovery")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/recovery.html.twig', $data);
    }

    // Gestion des litiges
    public function litigation(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/litigation", $payload)->toArray();
                $this->addFlash('success', 'Litige enregistré');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de l’enregistrement du litige');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/litigation")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/litigation.html.twig', $data);
    }

    // Provisions
    public function provisions(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/provisions')->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/provisions.html.twig', $data);
    }

    // Charge-off
    public function chargeOff(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/charge-off", $payload)->toArray();
                $this->addFlash('success', 'Charge-off confirmé');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec du write-off');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/charge-off")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/charge-off.html.twig', $data);
    }
}
