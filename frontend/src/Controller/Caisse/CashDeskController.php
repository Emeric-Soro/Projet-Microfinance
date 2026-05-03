<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CashDeskController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/caisse', name: 'caisse_dashboard', methods: ['GET'])]
    public function dashboard(): Response
    {
        try {
            $data = $this->api->get('/caisse/dashboard')->toArray();
        } catch (\Exception $e) {
            $data = [];
        }

        return $this->render('backoffice/caisse/dashboard.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    #[Route('/caisse/list', name: 'caisse_list', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [
            'page' => $request->query->getInt('page', 0),
            'agence' => $request->query->get('agence'),
            'statut' => $request->query->get('statut'),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $caisses = $this->api->get('/caisse', $params)->toArray();
        } catch (\Exception $e) {
            $caisses = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
        }

        return $this->render('backoffice/caisse/list.html.twig', [
            'caisses' => $caisses['content'] ?? [],
            'total_items' => $caisses['totalElements'] ?? 0,
            'total_pages' => $caisses['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => 20,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Liste'],
            ],
        ]);
    }

    #[Route('/caisse/billetage/{id}', name: 'caisse_billetage', methods: ['GET', 'POST'])]
    public function billetage(string $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'coupures' => $request->request->all('coupures'),
                    'guichet' => $request->request->get('guichet'),
                ];
                $this->api->post('/caisse/' . $id . '/billetage', $data);
                $this->addFlash('success', 'Billetage enregistré');
                return $this->redirectToRoute('caisse_session', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $session = $this->api->get('/caisse/sessions/' . $id)->toArray();
            $denominations = $this->api->get('/caisse/denominations')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Session introuvable');
            return $this->redirectToRoute('caisse_list');
        }

        return $this->render('backoffice/caisse/billetage.html.twig', [
            'session' => $session,
            'denominations' => $denominations,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Billetage'],
            ],
        ]);
    }

    #[Route('/caisse/variances', name: 'caisse_variances', methods: ['GET'])]
    public function variances(Request $request): Response
    {
        $params = [
            'date' => $request->query->get('date'),
            'guichet' => $request->query->get('guichet'),
            'page' => $request->query->getInt('page', 0),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $variances = $this->api->get('/caisse/variances', $params)->toArray();
        } catch (\Exception $e) {
            $variances = ['content' => [], 'totalElements' => 0];
        }

        return $this->render('backoffice/caisse/variances.html.twig', [
            'variances' => $variances['content'] ?? [],
            'total_items' => $variances['totalElements'] ?? 0,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Écarts'],
            ],
        ]);
    }

    #[Route('/caisse/variances/{id}/validate', name: 'caisse_variance_validate', methods: ['GET', 'POST'])]
    public function varianceValidate(string $id, Request $request): Response
    {
        try {
            $variance = $this->api->get('/caisse/variances/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Écart introuvable');
            return $this->redirectToRoute('caisse_variances');
        }

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'action' => $request->request->get('action', 'APPROVE'),
                    'commentaire' => $request->request->get('commentaire'),
                ];
                $this->api->post('/caisse/variances/' . $id . '/validate', $data);
                $this->addFlash('success', 'Écart traité');
                return $this->redirectToRoute('caisse_variances');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/caisse/variance-validate.html.twig', [
            'variance' => $variance,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Validation écart'],
            ],
        ]);
    }

    #[Route('/caisse/provisioning', name: 'caisse_provisioning', methods: ['GET', 'POST'])]
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
                $this->addFlash('success', 'Provisionnement effectué');
                return $this->redirectToRoute('caisse_provisioning');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
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

    #[Route('/caisse/unloading', name: 'caisse_unloading', methods: ['GET', 'POST'])]
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
                $this->addFlash('success', 'Déchargement effectué');
                return $this->redirectToRoute('caisse_unloading');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
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

    #[Route('/caisse/transfer', name: 'caisse_transfer', methods: ['GET', 'POST'])]
    public function interCaisseTransfer(Request $request): Response
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
                $this->addFlash('success', 'Transfert effectué');
                return $this->redirectToRoute('caisse_transfer');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
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

    #[Route('/caisse/params', name: 'caisse_params', methods: ['GET', 'POST'])]
    public function params(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $this->api->put('/caisse/params', $data);
                $this->addFlash('success', 'Paramètres mis à jour');
                return $this->redirectToRoute('caisse_params');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $params = $this->api->get('/caisse/params')->toArray();
        } catch (\Exception $e) {
            $params = [];
        }

        return $this->render('backoffice/caisse/params.html.twig', [
            'params' => $params,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Paramètres'],
            ],
        ]);
    }
}
