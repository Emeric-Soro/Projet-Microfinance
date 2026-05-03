<?php
namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AccountListController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}
    
    public function index(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/comptes', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['data' => [], 'total' => 0];
            $this->addFlash('error', 'Erreur de chargement des comptes');
        }

        return $this->render('backoffice/compte/index.html.twig', $data);
    }

    public function dormant(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/comptes/dormants', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['data' => [], 'total' => 0];
            $this->addFlash('error', 'Erreur de chargement des comptes dormants');
        }
        return $this->render('backoffice/compte/dormant.html.twig', $data);
    }

    public function pending(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/comptes/pending', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['data' => [], 'total' => 0];
            $this->addFlash('error', 'Erreur de chargement des comptes en attente');
        }
        return $this->render('backoffice/compte/pending.html.twig', $data);
    }

    public function byAgency(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/comptes/agence/'. $id, $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['data' => [], 'total' => 0];
            $this->addFlash('error', 'Erreur de chargement des comptes agence');
        }
        return $this->render('backoffice/compte/agency.html.twig', $data);
    }
}
