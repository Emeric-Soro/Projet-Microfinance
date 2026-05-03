<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/factures')]
class FactureController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_factures', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $factures = $this->api->get('/paiements/factures', $params)->toArray();
        $stats = $this->api->get('/paiements/factures/stats')->toArray();

        return $this->render('backoffice/paiement/factures.html.twig', [
            'current_menu' => 'paiements_factures',
            'factures' => $factures,
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Factures'],
            ],
        ]);
    }

    #[Route('/payer/{id}', name: 'paiements_factures_payer', methods: ['GET', 'POST'])]
    public function payer(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/paiements/factures/' . $id . '/payer', $request->request->all());
                $this->addFlash('success', 'Facture payée avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du paiement: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_factures');
        }

        $facture = $this->api->get('/paiements/factures/' . $id)->toArray();
        $comptes = $this->api->get('/paiements/comptes')->toArray();

        return $this->render('backoffice/paiement/factures.html.twig', [
            'current_menu' => 'paiements_factures',
            'facture' => $facture,
            'comptes' => $comptes,
            'paiement_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Factures', 'url' => $this->generateUrl('paiements_factures')],
                ['label' => 'Payer'],
            ],
        ]);
    }
}
