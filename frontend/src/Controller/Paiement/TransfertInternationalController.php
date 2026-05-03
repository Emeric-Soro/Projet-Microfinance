<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/transferts-internationaux')]
class TransfertInternationalController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_transferts_internationaux', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }

        $transferts = $this->api->get('/paiements/transferts-internationaux', $params)->toArray();
        $stats = $this->api->get('/paiements/transferts-internationaux/stats')->toArray();

        return $this->render('backoffice/paiement/transfert-international.html.twig', [
            'current_menu' => 'paiements_transferts_internationaux',
            'transferts' => $transferts,
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Transferts internationaux'],
            ],
        ]);
    }

    #[Route('/create', name: 'paiements_transferts_internationaux_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/paiements/transferts-internationaux', $request->request->all());
                $this->addFlash('success', 'Transfert international créé avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_transferts_internationaux');
        }

        $comptes = $this->api->get('/paiements/comptes')->toArray();
        $devises = $this->api->get('/paiements/devises')->toArray();

        return $this->render('backoffice/paiement/transfert-international.html.twig', [
            'current_menu' => 'paiements_transferts_internationaux',
            'comptes' => $comptes,
            'devises' => $devises,
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Transferts internationaux', 'url' => $this->generateUrl('paiements_transferts_internationaux')],
                ['label' => 'Nouveau transfert'],
            ],
        ]);
    }
}
