<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/plafonds')]
class PlafondController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_plafonds', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        $plafonds = $this->api->get('/paiements/plafonds', $params)->toArray();

        return $this->render('backoffice/paiement/plafonds.html.twig', [
            'current_menu' => 'paiements_plafonds',
            'plafonds' => $plafonds,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Plafonds'],
            ],
        ]);
    }

    #[Route('/edit/{id}', name: 'paiements_plafonds_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/paiements/plafonds/' . $id, $request->request->all());
                $this->addFlash('success', 'Plafond mis à jour avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_plafonds');
        }

        $plafond = $this->api->get('/paiements/plafonds/' . $id)->toArray();

        return $this->render('backoffice/paiement/plafonds.html.twig', [
            'current_menu' => 'paiements_plafonds',
            'plafond' => $plafond,
            'edit_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Plafonds', 'url' => $this->generateUrl('paiements_plafonds')],
                ['label' => 'Modifier'],
            ],
        ]);
    }
}
