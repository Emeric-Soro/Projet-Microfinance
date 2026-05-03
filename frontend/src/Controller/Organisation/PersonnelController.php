<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class PersonnelController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/personnel', name: 'organisation_personnel', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        if ($request->query->get('agence_id')) {
            $params['agence_id'] = $request->query->get('agence_id');
        }

        try {
            $data = $this->api->get('/organisation/personnel', $params)->toArray();
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $agences = [];
            $this->addFlash('error', 'Erreur lors du chargement du personnel');
        }

        return $this->render('backoffice/organisation/personnel.html.twig', [
            'current_menu' => 'organisation_personnel',
            'personnel' => $data['content'] ?? [],
            'agences' => $agences['content'] ?? $agences,
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Personnel'],
            ],
        ]);
    }

    #[Route('/organisation/personnel/affecter', name: 'organisation_personnel_affecter', methods: ['POST'])]
    public function affecter(Request $request): Response
    {
        try {
            $this->api->post('/organisation/personnel/affecter', $request->request->all());
            $this->addFlash('success', 'Affectation effectuée avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'affectation: ' . $e->getMessage());
        }

        return $this->redirectToRoute('organisation_personnel');
    }
}
