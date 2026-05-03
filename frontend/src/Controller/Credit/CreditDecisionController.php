<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditDecisionController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function committee(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/requests/' . $id . '/committee', $request->request->all());
                $this->addFlash('success', 'Avis du comité enregistré avec succès.');
                return $this->redirectToRoute('credit_committee', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $demande = $this->api->get('/credit/requests/' . $id)->toArray();
            $comite = $this->api->get('/credit/requests/' . $id . '/committee')->toArray();
            $membres = $this->api->get('/credit/requests/' . $id . '/committee/members')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Demande introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/committee.html.twig', [
            'current_menu' => 'credit_requests',
            'demande' => $demande,
            'comite' => $comite,
            'membres' => $membres['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'N°' . $id . ' - Comité'],
            ],
        ]);
    }

    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/requests/' . $id . '/decision', $request->request->all());
                $this->addFlash('success', 'Décision enregistrée avec succès.');
                return $this->redirectToRoute('credit_decision', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $demande = $this->api->get('/credit/requests/' . $id)->toArray();
            $decision = $this->api->get('/credit/requests/' . $id . '/decision')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Demande introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/decision.html.twig', [
            'current_menu' => 'credit_requests',
            'demande' => $demande,
            'decision' => $decision,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'N°' . $id . ' - Décision'],
            ],
        ]);
    }
}
