<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditRequestController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function simulation(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            try {
                $result = $this->api->post('/credit/simulation', $request->request->all())->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la simulation : ' . $e->getMessage());
            }
        }

        try {
            $produits = $this->api->get('/credit/products')->toArray();
        } catch (\Exception $e) {
            $produits = ['content' => []];
        }

        return $this->render('backoffice/credit/simulation.html.twig', [
            'current_menu' => 'credit_simulation',
            'produits' => $produits['content'] ?? [],
            'result' => $result,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Simulation'],
            ],
        ]);
    }

    public function list(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }
        if ($request->query->get('q')) {
            $params['q'] = $request->query->get('q');
        }

        try {
            $data = $this->api->get('/credit/requests', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des demandes de crédit.');
        }

        return $this->render('backoffice/credit/requests.html.twig', [
            'current_menu' => 'credit_requests',
            'requests' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'search_query' => $request->query->get('q', ''),
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes'],
            ],
        ]);
    }

    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/requests', $request->request->all());
                $this->addFlash('success', 'Demande de crédit créée avec succès.');
                return $this->redirectToRoute('credit_requests');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création : ' . $e->getMessage());
            }
        }

        try {
            $clients = $this->api->get('/clients', ['size' => 100])->toArray();
            $produits = $this->api->get('/credit/products')->toArray();
        } catch (\Exception $e) {
            $clients = ['content' => []];
            $produits = ['content' => []];
        }

        return $this->render('backoffice/credit/request-create.html.twig', [
            'current_menu' => 'credit_request_create',
            'clients' => $clients['content'] ?? [],
            'produits' => $produits['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'Nouvelle demande'],
            ],
        ]);
    }

    public function documents(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $files = $request->files->all();
                if (!empty($files)) {
                    $data['files'] = $files;
                }
                $this->api->post('/credit/requests/' . $id . '/documents', $data);
                $this->addFlash('success', 'Document ajouté avec succès.');
                return $this->redirectToRoute('credit_request_documents', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ajout du document : ' . $e->getMessage());
            }
        }

        try {
            $demande = $this->api->get('/credit/requests/' . $id)->toArray();
            $documents = $this->api->get('/credit/requests/' . $id . '/documents')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Demande introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/request-documents.html.twig', [
            'current_menu' => 'credit_requests',
            'demande' => $demande,
            'documents' => $documents['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'N°' . $id . ' - Documents'],
            ],
        ]);
    }

    public function guarantees(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/requests/' . $id . '/guarantees', $request->request->all());
                $this->addFlash('success', 'Garantie ajoutée avec succès.');
                return $this->redirectToRoute('credit_guarantees', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ajout de la garantie : ' . $e->getMessage());
            }
        }

        try {
            $demande = $this->api->get('/credit/requests/' . $id)->toArray();
            $garanties = $this->api->get('/credit/requests/' . $id . '/guarantees')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Demande introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/guarantees.html.twig', [
            'current_menu' => 'credit_requests',
            'demande' => $demande,
            'garanties' => $garanties['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Demandes', 'url' => $this->generateUrl('credit_requests')],
                ['label' => 'N°' . $id . ' - Garanties'],
            ],
        ]);
    }

    public function solidarityGroups(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/solidarity-groups', $request->request->all());
                $this->addFlash('success', 'Groupe solidaire créé avec succès.');
                return $this->redirectToRoute('credit_solidarity_groups');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création : ' . $e->getMessage());
            }
        }

        try {
            $data = $this->api->get('/credit/solidarity-groups', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['content' => []];
            $this->addFlash('error', 'Erreur lors du chargement des groupes solidaires.');
        }

        return $this->render('backoffice/credit/solidarity-groups.html.twig', [
            'current_menu' => 'credit_solidarity_groups',
            'groups' => $data['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Groupes solidaires'],
            ],
        ]);
    }

    public function clientHistory(int $clientId): Response
    {
        try {
            $client = $this->api->get('/clients/' . $clientId)->toArray();
            $historique = $this->api->get('/credit/client/' . $clientId . '/history')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('credit_requests');
        }

        return $this->render('backoffice/credit/client-history.html.twig', [
            'current_menu' => 'credit_requests',
            'client' => $client,
            'historique' => $historique['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Historique crédit - ' . ($client['nom'] ?? $client['raisonSociale'] ?? 'Client')],
            ],
        ]);
    }
}
