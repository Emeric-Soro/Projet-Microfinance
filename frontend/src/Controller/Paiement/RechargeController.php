<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/recharges')]
class RechargeController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_recharges', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('operateur')) {
            $params['operateur'] = $request->query->get('operateur');
        }
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $recharges = $this->api->get('/paiements/recharges', $params)->toArray();
        $stats = $this->api->get('/paiements/recharges/stats')->toArray();

        return $this->render('backoffice/paiement/recharges.html.twig', [
            'current_menu' => 'paiements_recharges',
            'recharges' => $recharges,
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Recharges'],
            ],
        ]);
    }

    #[Route('/process/{id}', name: 'paiements_recharges_process', methods: ['POST'])]
    public function process(int $id): Response
    {
        try {
            $this->api->post('/paiements/recharges/' . $id . '/process');
            $this->addFlash('success', 'Recharge traitée avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement: ' . $e->getMessage());
        }

        return $this->redirectToRoute('paiements_recharges');
    }
}
