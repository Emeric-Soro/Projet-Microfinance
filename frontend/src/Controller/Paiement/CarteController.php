<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/cartes')]
class CarteController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_cartes', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $cartes = $this->api->get('/paiements/cartes', $params)->toArray();
        $stats = $this->api->get('/paiements/cartes/stats')->toArray();

        return $this->render('backoffice/paiement/cartes.html.twig', [
            'current_menu' => 'paiements_cartes',
            'cartes' => $cartes,
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Cartes'],
            ],
        ]);
    }

    #[Route('/litiges', name: 'paiements_cartes_litiges', methods: ['GET'])]
    public function litiges(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $litiges = $this->api->get('/paiements/cartes/litiges', $params)->toArray();

        return $this->render('backoffice/paiement/cartes-litiges.html.twig', [
            'current_menu' => 'paiements_cartes',
            'litiges' => $litiges,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Cartes', 'url' => $this->generateUrl('paiements_cartes')],
                ['label' => 'Litiges'],
            ],
        ]);
    }

    #[Route('/rapprochement', name: 'paiements_cartes_rapprochement', methods: ['GET'])]
    public function rapprochement(): Response
    {
        $rapprochement = $this->api->get('/paiements/cartes/rapprochement')->toArray();

        return $this->render('backoffice/paiement/cartes-rapprochement.html.twig', [
            'current_menu' => 'paiements_cartes',
            'rapprochement' => $rapprochement,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Cartes', 'url' => $this->generateUrl('paiements_cartes')],
                ['label' => 'Rapprochement'],
            ],
        ]);
    }
}
