<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/partenaires')]
class PartenaireController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_partenaires', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        $partenaires = $this->api->get('/paiements/partenaires', $params)->toArray();

        return $this->render('backoffice/paiement/partenaires.html.twig', [
            'current_menu' => 'paiements_partenaires',
            'partenaires' => $partenaires,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Partenaires'],
            ],
        ]);
    }

    #[Route('/create', name: 'paiements_partenaires_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/paiements/partenaires', $request->request->all());
                $this->addFlash('success', 'Partenaire créé avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_partenaires');
        }

        return $this->render('backoffice/paiement/partenaires.html.twig', [
            'current_menu' => 'paiements_partenaires',
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Partenaires', 'url' => $this->generateUrl('paiements_partenaires')],
                ['label' => 'Nouveau partenaire'],
            ],
        ]);
    }

    #[Route('/edit/{id}', name: 'paiements_partenaires_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/paiements/partenaires/' . $id, $request->request->all());
                $this->addFlash('success', 'Partenaire mis à jour avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
            return $this->redirectToRoute('paiements_partenaires');
        }

        $partenaire = $this->api->get('/paiements/partenaires/' . $id)->toArray();

        return $this->render('backoffice/paiement/partenaires.html.twig', [
            'current_menu' => 'paiements_partenaires',
            'partenaire' => $partenaire,
            'edit_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Partenaires', 'url' => $this->generateUrl('paiements_partenaires')],
                ['label' => 'Modifier'],
            ],
        ]);
    }
}
