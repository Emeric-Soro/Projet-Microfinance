<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientMergeController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients/merge', name: 'client_merge', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $sourceResults = [];
        $targetResults = [];

        if ($request->isMethod('POST')) {
            try {
                $sourceId = $request->request->get('source_id');
                $targetId = $request->request->get('target_id');

                if (!$sourceId || !$targetId) {
                    $this->addFlash('error', 'Veuillez sélectionner un client source et un client cible.');
                } elseif ($sourceId === $targetId) {
                    $this->addFlash('error', 'Les clients source et cible doivent être différents.');
                } else {
                    $this->api->post('/clients/merge', [
                        'source_id' => (int) $sourceId,
                        'target_id' => (int) $targetId,
                    ]);
                    $this->addFlash('success', 'Fusion effectuée avec succès.');
                    return $this->redirectToRoute('client_detail', ['id' => $targetId]);
                }
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la fusion : ' . $e->getMessage());
            }
        }

        $sourceQuery = $request->query->get('source_q', '');
        $targetQuery = $request->query->get('target_q', '');

        if (!empty($sourceQuery)) {
            try {
                $data = $this->api->get('/clients/search', ['q' => $sourceQuery, 'size' => 10])->toArray();
                $sourceResults = $data['content'] ?? [];
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la recherche du client source.');
            }
        }

        if (!empty($targetQuery)) {
            try {
                $data = $this->api->get('/clients/search', ['q' => $targetQuery, 'size' => 10])->toArray();
                $targetResults = $data['content'] ?? [];
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la recherche du client cible.');
            }
        }

        return $this->render('backoffice/client/merge.html.twig', [
            'current_menu' => 'client_list',
            'sourceQuery' => $sourceQuery,
            'targetQuery' => $targetQuery,
            'sourceResults' => $sourceResults,
            'targetResults' => $targetResults,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => 'Fusion de clients'],
            ],
        ]);
    }
}
