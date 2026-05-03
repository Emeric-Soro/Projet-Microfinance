<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/remboursements')]
class RemboursementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_remboursements', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        $remboursements = $this->api->get('/paiements/remboursements', $params)->toArray();

        return $this->render('backoffice/paiement/remboursement.html.twig', [
            'current_menu' => 'paiements_remboursements',
            'remboursements' => $remboursements,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Remboursements'],
            ],
        ]);
    }

    #[Route('/process/{id}', name: 'paiements_remboursements_process', methods: ['POST'])]
    public function process(int $id): Response
    {
        try {
            $this->api->post('/paiements/remboursements/' . $id . '/process');
            $this->addFlash('success', 'Remboursement traité avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement: ' . $e->getMessage());
        }

        return $this->redirectToRoute('paiements_remboursements');
    }
}
