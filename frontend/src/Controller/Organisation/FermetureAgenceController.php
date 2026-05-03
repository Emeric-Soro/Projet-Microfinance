<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class FermetureAgenceController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/fermeture/{id}', name: 'organisation_fermeture', methods: ['GET', 'POST'])]
    public function close(Request $request, int $id): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/agences/' . $id . '/fermer', $request->request->all());
                $this->addFlash('success', 'Agence fermée avec succès.');
                return $this->redirectToRoute('organisation_agences');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la fermeture: ' . $e->getMessage());
            }
        }

        try {
            $agence = $this->api->get('/organisation/agences/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Agence introuvable');
            return $this->redirectToRoute('organisation_agences');
        }

        return $this->render('backoffice/organisation/fermeture.html.twig', [
            'current_menu' => 'organisation_agences',
            'agence' => $agence,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Agences', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => $agence['libelle'] ?? '', 'url' => $this->generateUrl('organisation_agences_show', ['id' => $id])],
                ['label' => 'Fermeture'],
            ],
        ]);
    }
}
