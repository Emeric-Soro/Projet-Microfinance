<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditAnalysisController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/requests/' . $id . '/analysis', $request->request->all());
                $this->addFlash('success', 'Analyse enregistrée avec succès.');
                return $this->redirectToRoute('credit_analysis', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $demande = $this->api->get('/credit/requests/' . $id)->toArray();
            $analyse = $this->api->get('/credit/requests/' . $id . '/analysis')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Demande introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/analysis.html.twig', [
            'current_menu' => 'credit_requests',
            'demande' => $demande,
            'analyse' => $analyse,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'N°' . $id . ' - Analyse'],
            ],
        ]);
    }

    public function scoring(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/requests/' . $id . '/scoring', $request->request->all());
                $this->addFlash('success', 'Scoring enregistré avec succès.');
                return $this->redirectToRoute('credit_scoring', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $demande = $this->api->get('/credit/requests/' . $id)->toArray();
            $scoring = $this->api->get('/credit/requests/' . $id . '/scoring')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Demande introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/scoring.html.twig', [
            'current_menu' => 'credit_requests',
            'demande' => $demande,
            'scoring' => $scoring,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'N°' . $id . ' - Scoring'],
            ],
        ]);
    }
}
