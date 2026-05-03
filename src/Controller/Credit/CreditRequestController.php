<?php
namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditRequestController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Simulation de crédit
    public function simulation(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/requests/simulation')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Échec de la simulation crédit');
        }
        return $this->render('backoffice/credit/simulation.html.twig', $data);
    }

    // Liste des demandes
    public function list(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/requests')->toArray();
        } catch (\Exception $e) {
            $data = ['data' => []];
        }
        return $this->render('backoffice/credit/requests.html.twig', $data);
    }

    // Création d'une nouvelle demande (multi-step)
    public function create(Request $request): Response
    {
        $payload = $request->request->all();
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $data = $this->apiClient->post('/credit/requests', $payload)->toArray();
                $this->addFlash('success', 'Demande de crédit créée avec succès');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de la création de la demande');
            }
        } else {
            try {
                $data = $this->apiClient->get('/credit/requests/new')->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/request-create.html.twig', $data);
    }

    // Documents d'une demande
    public function documents(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->files->all();
                $data = $this->apiClient->post("/credit/requests/{$id}/documents", $payload)->toArray();
                $this->addFlash('success', 'Documents téléversés avec succès');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec du téléversement des documents');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/requests/{$id}/documents")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/request-documents.html.twig', $data);
    }

    // Garanties d'une demande
    public function guarantees(int $id, Request $request): Response
    {
        if ("POST" === strtoupper($request->getMethod())) {
            try {
                $payload = $request->request->all();
                $data = $this->apiClient->post("/credit/requests/{$id}/guarantees", $payload)->toArray();
                $this->addFlash('success', 'Bonne garantie ajoutée');
            } catch (\Exception $e) {
                $data = [];
                $this->addFlash('error', 'Échec de l’ajout de la garantie');
            }
        } else {
            try {
                $data = $this->apiClient->get("/credit/requests/{$id}/guarantees")->toArray();
            } catch (\Exception $e) {
                $data = [];
            }
        }
        return $this->render('backoffice/credit/guarantees.html.twig', $data);
    }

    // Groupements de solidarité (group lending)
    public function solidarityGroups(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/credit/solidarity-groups')->toArray();
        } catch (\Exception $e) {
            $data = ['data' => []];
        }
        return $this->render('backoffice/credit/solidarity-groups.html.twig', $data);
    }

    // Historique client crédit
    public function clientHistory(int $clientId): Response
    {
        try {
            $data = $this->apiClient->get("/credit/client/{$clientId}/history")->toArray();
        } catch (\Exception $e) {
            $data = [];
        }
        return $this->render('backoffice/credit/client-history.html.twig', $data);
    }
}
