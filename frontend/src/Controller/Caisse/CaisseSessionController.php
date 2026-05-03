<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseSessionController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function list(Request $request): Response
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
            $this->addFlash('error', 'Erreur lors du chargement de la liste: ' . $e->getMessage());
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

    public function open(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'guichet' => $request->request->get('guichet'),
                    'solde_initial' => $request->request->get('solde_initial'),
                    'devise' => $request->request->get('devise', 'XOF'),
                    'observations' => $request->request->get('observations'),
                ];
                $response = $this->api->post('/caisse/sessions/open', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Session ouverte avec succès');
                return $this->redirectToRoute('caisse_session', ['id' => $result['id'] ?? '']);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ouverture: ' . $e->getMessage());
            }
        }

        try {
            $guichets = $this->api->get('/caisse/guichets')->toArray();
        } catch (\Exception $e) {
            $guichets = [];
        }

        return $this->render('backoffice/caisse/open.html.twig', [
            'guichets' => $guichets,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Ouverture'],
            ],
        ]);
    }

    public function session(int $id, Request $request): Response
    {
        try {
            $session = $this->api->get('/caisse/sessions/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Session introuvable');
            return $this->redirectToRoute('caisse_list');
        }

        return $this->render('backoffice/caisse/session.html.twig', [
            'session' => $session,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Session'],
            ],
        ]);
    }

    public function close(int $id, Request $request): Response
    {
        try {
            $session = $this->api->get('/caisse/sessions/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Session introuvable');
            return $this->redirectToRoute('caisse_list');
        }

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'solde_final' => $request->request->get('solde_final'),
                    'observations' => $request->request->get('observations'),
                ];
                $this->api->post('/caisse/sessions/' . $id . '/close', $data);
                $this->addFlash('success', 'Session fermée avec succès');
                return $this->redirectToRoute('caisse_session', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la fermeture: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/caisse/close.html.twig', [
            'session' => $session,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Fermeture'],
            ],
        ]);
    }
}
