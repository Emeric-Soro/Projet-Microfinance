<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ClientDetailController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function show(int $id, Request $request): Response
    {
        try {
            $client = $this->apiClient->get('/clients/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger le client');
            $client = [];
        }
        return $this->render('backoffice/client/show.html.twig', [
            'client' => $client,
        ]);
    }

    public function edit(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $client = $this->apiClient->get('/clients/'.$id)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Impossible de charger le client');
                $client = [];
            }
            return $this->render('backoffice/client/edit.html.twig', ['client' => $client]);
        }

        $payload = $request->request->all();
        try {
            $this->apiClient->put('/clients/'.$id, $payload)->toArray();
            $this->addFlash('success', 'Client mis à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de la mise à jour');
        }
        return $this->redirect('/clients/'.$id);
    }

    public function beneficiaires(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/clients/'.$id.'/beneficiaires')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des bénéficiaires');
            $data = [];
        }
        return $this->render('backoffice/client/beneficiaires.html.twig', ['beneficiaires' => $data]);
    }

    public function mandataires(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/clients/'.$id.'/mandataires')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur des mandataires');
            $data = [];
        }
        return $this->render('backoffice/client/mandataires.html.twig', ['mandataires' => $data]);
    }

    public function contacts(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/clients/'.$id.'/contacts')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur des contacts');
            $data = [];
        }
        return $this->render('backoffice/client/contacts.html.twig', ['contacts' => $data]);
    }

    public function riskEvaluation(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/risk.html.twig', ['risk' => []]);
        }
        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients/'.$id.'/risk', $payload)->toArray();
            $this->addFlash('success', 'Évaluation de risque enregistrée');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de l’enregistrement du risque');
        }
        return $this->redirect('/clients/'.$id);
    }

    public function screening(int $id, Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/clients/'.$id.'/screening')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec du screening');
            $data = [];
        }
        return $this->render('backoffice/client/screening.html.twig', ['screening' => $data]);
    }

    public function history(int $id, Request $request): Response
    {
        try {
            $timeline = $this->apiClient->get('/clients/'.$id.'/history')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger l’historique');
            $timeline = [];
        }
        return $this->render('backoffice/client/history.html.twig', ['timeline' => $timeline]);
    }

    public function suspend(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/suspend.html.twig', ['id' => $id]);
        }
        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients/'.$id.'/suspend', $payload)->toArray();
            $this->addFlash('success', 'Client suspendu');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de la suspension');
        }
        return $this->redirect('/clients/'.$id);
    }

    public function reactivate(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/reactivate.html.twig', ['id' => $id]);
        }
        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients/'.$id.'/reactivate', $payload)->toArray();
            $this->addFlash('success', 'Client réactivé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de la réactivation');
        }
        return $this->redirect('/clients/'.$id);
    }

    public function archive(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/archive.html.twig', ['id' => $id]);
        }
        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients/'.$id.'/archive', $payload)->toArray();
            $this->addFlash('success', 'Client archivé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de l’archivage');
        }
        return $this->redirect('/clients');
    }
}
