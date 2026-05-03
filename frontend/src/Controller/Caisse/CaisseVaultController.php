<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseVaultController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function vault(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'type' => $request->request->get('type'),
                    'montant' => $request->request->get('montant'),
                    'motif' => $request->request->get('motif'),
                ];
                $this->api->post('/caisse/coffre', $data);
                $this->addFlash('success', 'Opération sur le coffre effectuée avec succès');
                return $this->redirectToRoute('caisse_vault');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'opération: ' . $e->getMessage());
            }
        }

        try {
            $vault = $this->api->get('/caisse/coffre')->toArray();
        } catch (\Exception $e) {
            $vault = [];
        }

        return $this->render('backoffice/caisse/vault.html.twig', [
            'vault' => $vault,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Coffre'],
            ],
        ]);
    }
}
