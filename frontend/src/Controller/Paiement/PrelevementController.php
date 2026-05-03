<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/prelevements')]
class PrelevementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_prelevements', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $prelevements = $this->api->get('/paiements/prelevements', $params)->toArray();

        return $this->render('backoffice/paiement/prelevement.html.twig', [
            'current_menu' => 'paiements_prelevements',
            'prelevements' => $prelevements,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Prélèvements'],
            ],
        ]);
    }

    #[Route('/manage/{id}', name: 'paiements_prelevements_manage', methods: ['GET', 'POST'])]
    public function manage(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/paiements/prelevements/' . $id, $request->request->all());
                $this->addFlash('success', 'Prélèvement mis à jour avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_prelevements');
        }

        $prelevement = $this->api->get('/paiements/prelevements/' . $id)->toArray();

        return $this->render('backoffice/paiement/prelevement.html.twig', [
            'current_menu' => 'paiements_prelevements',
            'prelevement' => $prelevement,
            'manage_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Prélèvements', 'url' => $this->generateUrl('paiements_prelevements')],
                ['label' => 'Gérer'],
            ],
        ]);
    }
}
