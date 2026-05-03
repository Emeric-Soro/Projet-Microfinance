<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseTransferController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function transfer(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'guichet_source' => $request->request->get('guichet_source'),
                    'guichet_destination' => $request->request->get('guichet_destination'),
                    'montant' => $request->request->get('montant'),
                    'motif' => $request->request->get('motif'),
                ];
                $this->api->post('/caisse/transfert', $data);
                $this->addFlash('success', 'Transfert inter-caisse effectué avec succès');
                return $this->redirectToRoute('caisse_transfer');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du transfert: ' . $e->getMessage());
            }
        }

        try {
            $transfers = $this->api->get('/caisse/transfert')->toArray();
            $guichets = $this->api->get('/caisse/guichets')->toArray();
        } catch (\Exception $e) {
            $transfers = ['content' => []];
            $guichets = [];
        }

        return $this->render('backoffice/caisse/transfer.html.twig', [
            'transfers' => $transfers['content'] ?? [],
            'guichets' => $guichets,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Transfert'],
            ],
        ]);
    }
}
