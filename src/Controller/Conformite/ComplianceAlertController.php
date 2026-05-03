<?php
namespace App\Controller\Conformite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ComplianceAlertController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function dashboard(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/dashboard', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement du tableau de bord de conformité.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/dashboard.html.twig', $data);
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/alerts', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des alertes de conformité.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/alerts.html.twig', $data);
    }

    public function investigation(int $id, Request $request): Response
    {
        try {
            $alert = $this->apiClient->get('/compliance/alerts/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l’investigation de l’alerte.');
            $alert = [];
        }
        return $this->render('backoffice/conformite/investigation.html.twig', ['alert' => $alert]);
    }

    public function riskProfile(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/risk-profile', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement du profil de risque.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/risk-profile.html.twig', $data);
    }

    public function paymentIncidents(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/payment-incidents', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des incidents de paiement.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/payment-incidents.html.twig', $data);
    }

    public function tax(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/tax', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des taxes.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/tax.html.twig', $data);
    }

    public function inspector(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/inspectors', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des inspecteurs.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/inspector.html.twig', $data);
    }

    public function training(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/training', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de la formation conformité.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/training.html.twig', $data);
    }

    public function riskMap(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/risk-map', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de la cartographie des risques.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/risk-map.html.twig', $data);
    }

    public function internalControl(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/internal-control', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement du contrôle interne.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/internal-control.html.twig', $data);
    }
}
