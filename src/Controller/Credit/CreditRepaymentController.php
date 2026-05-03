<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditRepaymentController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Liste des crédits actifs
    public function activeList(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/active')->toArray();
        } catch (\Exception $e) {
            $data = ['data' => []];
        }
        return $this->render('backoffice/credit/active.html.twig', $data);
    }

    // Détail d’un crédit avec onglets
    public function detail(int $id): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/detail.html.twig', $data);
    }

    // Schedule amortissement
    public function schedule(int $id): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}/schedule")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/schedule.html.twig', $data);
    }

    // Effectuer un remboursement
    public function repayment(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/repayment", $payload)->toArray();
                $this->addFlash('success', 'Remboursement enregistré');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec du remboursement');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/repayment")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/repayment.html.twig', $data);
    }

    // Prépaiement
    public function prepayment(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/prepayment", $payload)->toArray();
                $this->addFlash('success', 'Prépaiement enregistré');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec du prépaiement');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/prepayment")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/prepayment.html.twig', $data);
    }

    // Report postponement
    public function postponement(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/postponement", $payload)->toArray();
                $this->addFlash('success', 'Report de paiement enregistré');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec du report de paiement');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/postponement")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/postponement.html.twig', $data);
    }

    // Restructuration
    public function restructuring(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/{$id}/restructuring", $payload)->toArray();
                $this->addFlash('success', 'Restructuration enregistrée');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de la restructuration');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/{$id}/restructuring")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/restructuring.html.twig', $data);
    }

    // Groupe de collecte (group collection)
    public function groupCollection(int $groupId, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/group-collection/{$groupId}", $payload)->toArray();
                $this->addFlash('success', 'Collecte de groupe enregistrée');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de la collecte de groupe');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/group-collection/{$groupId}")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/group-collection.html.twig', $data);
    }

    // Fichier complet du dossier crédit
    public function fullFile(int $id): Response
    {
        try {
            $data = $this->apiClient->get("/credit/{$id}/file")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/file.html.twig', $data);
    }
}
