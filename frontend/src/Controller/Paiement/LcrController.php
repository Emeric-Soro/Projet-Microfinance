<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/lcr')]
class LcrController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_lcr', methods: ['GET'])]
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

        $lcrs = $this->api->get('/paiements/lcr', $params)->toArray();

        return $this->render('backoffice/paiement/lcr.html.twig', [
            'current_menu' => 'paiements_lcr',
            'lcrs' => $lcrs,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'LCR'],
            ],
        ]);
    }

    #[Route('/manage/{id}', name: 'paiements_lcr_manage', methods: ['GET', 'POST'])]
    public function manage(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/paiements/lcr/' . $id, $request->request->all());
                $this->addFlash('success', 'LCR mise à jour avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_lcr');
        }

        $lcr = $this->api->get('/paiements/lcr/' . $id)->toArray();

        return $this->render('backoffice/paiement/lcr.html.twig', [
            'current_menu' => 'paiements_lcr',
            'lcr' => $lcr,
            'manage_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'LCR', 'url' => $this->generateUrl('paiements_lcr')],
                ['label' => 'Gérer'],
            ],
        ]);
    }
}
