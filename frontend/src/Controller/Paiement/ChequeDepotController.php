<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/cheques')]
class ChequeDepotController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/depot', name: 'paiements_cheques_depot', methods: ['GET', 'POST'])]
    public function depot(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/paiements/cheques/depot', $request->request->all());
                $this->addFlash('success', 'Chèque déposé avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du dépôt du chèque: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_cheques_depot');
        }

        $comptes = $this->api->get('/paiements/cheques/comptes')->toArray();

        return $this->render('backoffice/paiement/cheque-depot.html.twig', [
            'current_menu' => 'paiements_cheques',
            'comptes' => $comptes,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Dépôt de chèque'],
            ],
        ]);
    }

    #[Route('/carnets', name: 'paiements_cheques_carnets', methods: ['GET'])]
    public function carnets(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $carnets = $this->api->get('/paiements/cheques/carnets', $params)->toArray();

        return $this->render('backoffice/paiement/chequebooks.html.twig', [
            'current_menu' => 'paiements_cheques',
            'carnets' => $carnets,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Carnets de chèques'],
            ],
        ]);
    }

    #[Route('/carnets/create', name: 'paiements_cheques_carnets_create', methods: ['GET', 'POST'])]
    public function createCarnet(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/paiements/cheques/carnets', $request->request->all());
                $this->addFlash('success', 'Carnet de chèques créé avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création du carnet: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_cheques_carnets');
        }

        $comptes = $this->api->get('/paiements/cheques/comptes')->toArray();

        return $this->render('backoffice/paiement/chequebooks.html.twig', [
            'current_menu' => 'paiements_cheques',
            'comptes' => $comptes,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Nouveau carnet'],
            ],
        ]);
    }

    #[Route('/oppose', name: 'paiements_cheques_oppose', methods: ['GET', 'POST'])]
    public function oppose(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/paiements/cheques/opposition', $request->request->all());
                $this->addFlash('success', 'Opposition enregistrée avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'opposition: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_cheques_oppose');
        }

        $oppositions = $this->api->get('/paiements/cheques/opposition')->toArray();

        return $this->render('backoffice/paiement/cheque-oppose.html.twig', [
            'current_menu' => 'paiements_cheques',
            'oppositions' => $oppositions,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Opposition chèques'],
            ],
        ]);
    }

    #[Route('/incidents', name: 'paiements_cheques_incidents', methods: ['GET'])]
    public function incidents(): Response
    {
        $incidents = $this->api->get('/paiements/cheques/incidents')->toArray();

        return $this->render('backoffice/paiement/cheque-incidents.html.twig', [
            'current_menu' => 'paiements_cheques',
            'incidents' => $incidents,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Incidents chèques'],
            ],
        ]);
    }
}
