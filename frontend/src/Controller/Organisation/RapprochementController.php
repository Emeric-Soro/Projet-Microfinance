<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RapprochementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/rapprochements', name: 'organisation_rapprochements', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'date_debut' => $request->request->get('date_debut', date('Y-m-01')),
                    'date_fin' => $request->request->get('date_fin', date('Y-m-d')),
                    'type' => $request->request->get('type'),
                ];
                $result = $this->api->post('/organisation/rapprochements/executer', $data)->toArray();
                $this->addFlash('success', 'Rapprochement effectué avec succès.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du rapprochement: ' . $e->getMessage());
            }
        }

        try {
            $historique = $this->api->get('/organisation/rapprochements')->toArray();
        } catch (\Exception $e) {
            $historique = ['content' => []];
        }

        return $this->render('backoffice/organisation/rapprochements.html.twig', [
            'current_menu' => 'organisation_rapprochements',
            'historique' => $historique['content'] ?? $historique,
            'result' => $result,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Rapprochements'],
            ],
        ]);
    }
}
