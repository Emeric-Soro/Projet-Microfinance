<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/api')]
class ApiPaiementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_api', methods: ['GET'])]
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

        $transactions = $this->api->get('/paiements/api', $params)->toArray();
        $stats = $this->api->get('/paiements/api/stats')->toArray();

        return $this->render('backoffice/paiement/api.html.twig', [
            'current_menu' => 'paiements_api',
            'transactions' => $transactions,
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'API Paiements'],
            ],
        ]);
    }
}
