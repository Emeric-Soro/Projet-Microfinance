<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/commissions')]
class CommissionController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_commissions', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }
        if ($request->query->get('date_debut')) {
            $params['dateDebut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['dateFin'] = $request->query->get('date_fin');
        }

        $commissions = $this->api->get('/paiements/commissions', $params)->toArray();

        return $this->render('backoffice/paiement/commissions.html.twig', [
            'current_menu' => 'paiements_commissions',
            'commissions' => $commissions,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Commissions'],
            ],
        ]);
    }

    #[Route('/calculer', name: 'paiements_commissions_calculer', methods: ['POST'])]
    public function calculate(Request $request): Response
    {
        try {
            $result = $this->api->post('/paiements/commissions/calculer', $request->request->all())->toArray();
            $this->addFlash('success', 'Calcul des commissions effectué.');
            return $this->render('backoffice/paiement/commissions.html.twig', [
                'current_menu' => 'paiements_commissions',
                'commissions' => $this->api->get('/paiements/commissions')->toArray(),
                'calculation_result' => $result,
                'breadcrumbs' => [
                    ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                    ['label' => 'Paiements'],
                    ['label' => 'Commissions'],
                ],
            ]);
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du calcul: ' . $e->getMessage());
        }

        return $this->redirectToRoute('paiements_commissions');
    }
}
