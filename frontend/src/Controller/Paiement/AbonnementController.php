<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/abonnements')]
class AbonnementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_abonnements', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        $abonnements = $this->api->get('/paiements/abonnements', $params)->toArray();

        return $this->render('backoffice/paiement/abonnements.html.twig', [
            'current_menu' => 'paiements_abonnements',
            'abonnements' => $abonnements,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Abonnements'],
            ],
        ]);
    }

    #[Route('/manage/{id}', name: 'paiements_abonnements_manage', methods: ['GET', 'POST'])]
    public function manage(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/paiements/abonnements/' . $id, $request->request->all());
                $this->addFlash('success', 'Abonnement mis à jour avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_abonnements');
        }

        $abonnement = $this->api->get('/paiements/abonnements/' . $id)->toArray();

        return $this->render('backoffice/paiement/abonnements.html.twig', [
            'current_menu' => 'paiements_abonnements',
            'abonnement' => $abonnement,
            'manage_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Abonnements', 'url' => $this->generateUrl('paiements_abonnements')],
                ['label' => 'Gérer'],
            ],
        ]);
    }
}
