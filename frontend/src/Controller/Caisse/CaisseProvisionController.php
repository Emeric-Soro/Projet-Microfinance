<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseProvisionController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function provisioning(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'guichet' => $request->request->get('guichet'),
                    'montant' => $request->request->get('montant'),
                    'type' => $request->request->get('type', 'ESPECES'),
                ];
                $this->api->post('/caisse/provisioning', $data);
                $this->addFlash('success', 'Provisionnement effectué avec succès');
                return $this->redirectToRoute('caisse_provisioning');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du provisionnement: ' . $e->getMessage());
            }
        }

        try {
            $history = $this->api->get('/caisse/provisioning')->toArray();
        } catch (\Exception $e) {
            $history = ['content' => []];
        }

        return $this->render('backoffice/caisse/provisioning.html.twig', [
            'history' => $history['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Provisionnement'],
            ],
        ]);
    }

    public function unloading(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'guichet' => $request->request->get('guichet'),
                    'montant' => $request->request->get('montant'),
                    'motif' => $request->request->get('motif'),
                ];
                $this->api->post('/caisse/dechargement', $data);
                $this->addFlash('success', 'Déchargement effectué avec succès');
                return $this->redirectToRoute('caisse_unloading');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du déchargement: ' . $e->getMessage());
            }
        }

        try {
            $history = $this->api->get('/caisse/dechargement')->toArray();
        } catch (\Exception $e) {
            $history = ['content' => []];
        }

        return $this->render('backoffice/caisse/unloading.html.twig', [
            'history' => $history['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Déchargement'],
            ],
        ]);
    }
}
