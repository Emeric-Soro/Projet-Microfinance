<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseInTransitController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function inTransit(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'guichet_source' => $request->request->get('guichet_source'),
                    'guichet_destination' => $request->request->get('guichet_destination'),
                    'montant' => $request->request->get('montant'),
                    'date_envoi' => $request->request->get('date_envoi'),
                    'reference' => $request->request->get('reference'),
                ];
                $this->api->post('/caisse/en-transit', $data);
                $this->addFlash('success', 'Fonds en transit enregistrés avec succès');
                return $this->redirectToRoute('caisse_in_transit');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement: ' . $e->getMessage());
            }
        }

        try {
            $inTransit = $this->api->get('/caisse/en-transit')->toArray();
        } catch (\Exception $e) {
            $inTransit = ['content' => []];
        }

        return $this->render('backoffice/caisse/in-transit.html.twig', [
            'inTransit' => $inTransit['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'En transit'],
            ],
        ]);
    }
}
