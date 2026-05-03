<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/momo-historique')]
class MomoHistoryController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_momo_historique', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('operateur')) {
            $params['operateur'] = $request->query->get('operateur');
        }
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }
        if ($request->query->get('page')) {
            $params['page'] = $request->query->get('page');
        }

        $historique = $this->api->get('/paiements/momo-historique', $params)->toArray();

        return $this->render('backoffice/paiement/momo-historique.html.twig', [
            'current_menu' => 'paiements_momo_historique',
            'historique' => $historique,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Historique Mobile Money'],
            ],
        ]);
    }
}
