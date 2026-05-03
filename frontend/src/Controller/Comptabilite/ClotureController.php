<?php

namespace App\Controller\Comptabilite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClotureController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/comptabilite/cloture', name: 'comptabilite_closing', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            $action = $request->request->get('action');
            $exerciceId = $request->request->get('exercice_id');
            $data = ['action' => $action, 'exercice_id' => $exerciceId];

            try {
                $response = $this->api->post('/comptabilite/cloture', $data)->toArray();
                $this->addFlash('success', 'Opération de clôture exécutée avec succès.');
                $result = $response;
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $exercices = $this->api->get('/comptabilite/exercices')->toArray()['data'] ?? [];
        $statut = $this->api->get('/comptabilite/cloture/statut')->toArray();

        return $this->render('backoffice/comptabilite/cloture.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Clôture'],
            ],
            'exercices' => $exercices,
            'statut' => $statut,
            'result' => $result,
        ]);
    }

    #[Route('/comptabilite/exercices', name: 'comptabilite_fiscal_years', methods: ['GET', 'POST'])]
    public function fiscalYears(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'code' => $request->request->get('code'),
                'libelle' => $request->request->get('libelle'),
                'date_debut' => $request->request->get('date_debut'),
                'date_fin' => $request->request->get('date_fin'),
                'statut' => $request->request->get('statut', 'OUVERT'),
            ];

            try {
                $this->api->post('/comptabilite/exercices', $data)->toArray();
                $this->addFlash('success', 'Exercice créé avec succès.');
                return $this->redirectToRoute('comptabilite_fiscal_years');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $page = $request->query->getInt('page', 0);
        $data = $this->api->get('/comptabilite/exercices', ['page' => $page])->toArray();

        return $this->render('backoffice/comptabilite/exercices.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Exercices'],
            ],
            'exercices' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
        ]);
    }

    #[Route('/comptabilite/fiscal', name: 'comptabilite_tax', methods: ['GET', 'POST'])]
    public function tax(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'type_declaration' => $request->request->get('type_declaration'),
                'exercice_id' => $request->request->get('exercice_id'),
                'period' => $request->request->get('period'),
            ];

            try {
                $response = $this->api->post('/comptabilite/fiscal/calculer', $data)->toArray();
                $this->addFlash('success', 'Déclaration fiscale générée.');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $exercices = $this->api->get('/comptabilite/exercices')->toArray()['data'] ?? [];
        $declarations = $this->api->get('/comptabilite/fiscal/declarations')->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/fiscal.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Fiscal'],
            ],
            'exercices' => $exercices,
            'declarations' => $declarations,
        ]);
    }

    #[Route('/comptabilite/immobilisations', name: 'comptabilite_fixed_assets', methods: ['GET', 'POST'])]
    public function fixedAssets(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'designation' => $request->request->get('designation'),
                'categorie' => $request->request->get('categorie'),
                'valeur_acquisition' => $request->request->get('valeur_acquisition'),
                'date_acquisition' => $request->request->get('date_acquisition'),
                'duree_amortissement' => $request->request->get('duree_amortissement'),
                'mode_amortissement' => $request->request->get('mode_amortissement'),
            ];

            try {
                $this->api->post('/comptabilite/immobilisations', $data)->toArray();
                $this->addFlash('success', 'Immobilisation enregistrée avec succès.');
                return $this->redirectToRoute('comptabilite_fixed_assets');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $page = $request->query->getInt('page', 0);
        $categorie = $request->query->get('categorie', '');
        $params = array_filter(compact('page', 'categorie'));
        $data = $this->api->get('/comptabilite/immobilisations', $params)->toArray();

        $categories = $this->api->get('/comptabilite/immobilisations/categories')->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/immobilisations.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Immobilisations'],
            ],
            'immobilisations' => $data['data'] ?? [],
            'categories' => $categories,
            'categorie' => $categorie,
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
        ]);
    }
}
