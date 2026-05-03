<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/virements/rtgs')]
class VirementControllerRtgs extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_virements_rtgs', methods: ['GET'])]
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

        $virements = $this->api->get('/paiements/virements/rtgs', $params)->toArray();

        return $this->render('backoffice/paiement/rtgs.html.twig', [
            'current_menu' => 'paiements_virements_rtgs',
            'virements' => $virements,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Virements RTGS'],
            ],
        ]);
    }

    #[Route('/process/{id}', name: 'paiements_virements_rtgs_process', methods: ['POST'])]
    public function process(int $id): Response
    {
        try {
            $this->api->post('/paiements/virements/rtgs/' . $id . '/process');
            $this->addFlash('success', 'Virement RTGS traité avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement: ' . $e->getMessage());
        }

        return $this->redirectToRoute('paiements_virements_rtgs');
    }
}
