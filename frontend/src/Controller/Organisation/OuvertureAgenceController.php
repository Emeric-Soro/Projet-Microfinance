<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class OuvertureAgenceController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/ouverture', name: 'organisation_ouverture', methods: ['GET', 'POST'])]
    public function open(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/agences/ouvrir', $request->request->all());
                $this->addFlash('success', 'Agence ouverte avec succès.');
                return $this->redirectToRoute('organisation_agences');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ouverture: ' . $e->getMessage());
            }
        }

        try {
            $regions = $this->api->get('/organisation/regions')->toArray();
        } catch (\Exception $e) {
            $regions = ['content' => []];
        }

        return $this->render('backoffice/organisation/ouverture.html.twig', [
            'current_menu' => 'organisation_agences',
            'regions' => $regions['content'] ?? $regions,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_agences')],
                ['label' => 'Ouverture d\'Agence'],
            ],
        ]);
    }
}
