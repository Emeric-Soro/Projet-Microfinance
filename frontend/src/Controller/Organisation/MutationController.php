<?php

namespace App\Controller\Organisation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class MutationController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/organisation/mutations', name: 'organisation_mutations', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;

        try {
            $data = $this->api->get('/organisation/mutations', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des mutations');
        }

        return $this->render('backoffice/organisation/mutations.html.twig', [
            'current_menu' => 'organisation_mutations',
            'mutations' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Organisation'],
                ['label' => 'Mutations'],
            ],
        ]);
    }

    #[Route('/organisation/mutations/create', name: 'organisation_mutations_create', methods: ['GET', 'POST'])]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/organisation/mutations', $request->request->all());
                $this->addFlash('success', 'Mutation créée avec succès.');
                return $this->redirectToRoute('organisation_mutations');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création: ' . $e->getMessage());
            }
        }

        try {
            $personnel = $this->api->get('/organisation/personnel')->toArray();
            $agences = $this->api->get('/organisation/agences')->toArray();
        } catch (\Exception $e) {
            $personnel = ['content' => []];
            $agences = ['content' => []];
        }

        return $this->render('backoffice/organisation/mutations.html.twig', [
            'current_menu' => 'organisation_mutations',
            'personnel' => $personnel['content'] ?? $personnel,
            'agences' => $agences['content'] ?? $agences,
            'create_mode' => true,
            'breadcrumbs' => [
                ['label' => 'Organisation', 'url' => $this->generateUrl('organisation_mutations')],
                ['label' => 'Mutations', 'url' => $this->generateUrl('organisation_mutations')],
                ['label' => 'Nouvelle mutation'],
            ],
        ]);
    }
}
