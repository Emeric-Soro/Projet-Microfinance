<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditDisbursementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function contract(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/contract', $request->request->all());
                $this->addFlash('success', 'Contrat enregistré avec succès.');
                return $this->redirectToRoute('credit_contract', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $contrat = $this->api->get('/credit/' . $id . '/contract')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/contract.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'contrat' => $contrat,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Contrat'],
            ],
        ]);
    }

    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/disbursement', $request->request->all());
                $this->addFlash('success', 'Décaissement effectué avec succès.');
                return $this->redirectToRoute('credit_disbursement', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du décaissement : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $decaissement = $this->api->get('/credit/' . $id . '/disbursement')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/disbursement.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'decaissement' => $decaissement,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Décaissement'],
            ],
        ]);
    }
}
