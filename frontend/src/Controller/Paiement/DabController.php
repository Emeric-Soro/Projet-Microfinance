<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/dab')]
class DabController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_dab', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }

        $operations = $this->api->get('/paiements/dab', $params)->toArray();
        $stats = $this->api->get('/paiements/dab/stats')->toArray();

        return $this->render('backoffice/paiement/dab.html.twig', [
            'current_menu' => 'paiements_dab',
            'operations' => $operations,
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'DAB'],
            ],
        ]);
    }
}
