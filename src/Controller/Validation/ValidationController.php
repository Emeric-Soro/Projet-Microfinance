<?php
namespace App\Controller\Validation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ValidationController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    // Validation queue list with filters
    public function queue(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/validations', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de la file d\'attente des validations.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/validation/queue.html.twig', $data);
    }

    // Detail view for a validation request
    public function detail(int $id): Response
    {
        try {
            $detail = $this->apiClient->get('/validations/'.$id)->toArray();
            $audit  = $this->apiClient->get('/validations/'.$id.'/audit')->toArray();
            $payload = ['validation' => $detail, 'auditTimeline' => $audit];
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de la validation.');
            $payload = ['validation' => [], 'auditTimeline' => []];
        }
        return $this->render('backoffice/validation/detail.html.twig', $payload);
    }

    // Maker-Checker actions routed through the validation workflow
    public function approve(Request $request, int $id): Response
    {
        try {
            $payload = $request->request->all();
            $this->apiClient->post('/validations/'.$id.'/approve', $payload);
            $this->addFlash('success', 'Validation approuvée avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de l’approbation de la validation.');
        }
        return $this->redirect('/validations');
    }

    public function reject(Request $request, int $id): Response
    {
        try {
            $payload = $request->request->all();
            $this->apiClient->post('/validations/'.$id.'/reject', $payload);
            $this->addFlash('success', 'Validation rejetée.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec du rejet de la validation.');
        }
        return $this->redirect('/validations');
    }

    public function revision(Request $request, int $id): Response
    {
        try {
            $payload = $request->request->all();
            $this->apiClient->post('/validations/'.$id.'/revision', $payload);
            $this->addFlash('success', 'Révision demandée envoyée.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de l’envoi de la révision.');
        }
        return $this->redirect('/validations');
    }

    // History of validations
    public function history(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/validations/history', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de l’historique des validations.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/validation/history.html.twig', $data);
    }

    // Maker-Checker configuration and rules
    public function limits(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/validations/limits', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des limites.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/validation/limits.html.twig', $data);
    }

    public function rules(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/validations/rules', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des règles.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/validation/rules.html.twig', $data);
    }

    public function forced(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/validations/forced', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des validations forcées.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/validation/forced.html.twig', $data);
    }

    public function myActions(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/validations/my-actions', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de vos actions.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/validation/my-actions.html.twig', $data);
    }
}
