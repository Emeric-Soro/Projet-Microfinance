<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class SessionController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/caisse/open', name: 'caisse_open', methods: ['GET', 'POST'])]
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
                $this->addFlash('success', 'Session ouverte');
                return $this->redirectToRoute('caisse_session', ['id' => $result['id'] ?? '']);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
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

    #[Route('/caisse/session/{id}', name: 'caisse_session', methods: ['GET'])]
    public function show(string $id): Response
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

    #[Route('/caisse/close/{id}', name: 'caisse_close', methods: ['GET', 'POST'])]
    public function close(string $id, Request $request): Response
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
                $this->addFlash('success', 'Session fermée');
                return $this->redirectToRoute('caisse_session', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
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

    #[Route('/caisse/teller-statement/{id}', name: 'caisse_teller_statement', methods: ['GET'])]
    public function tellerStatement(string $id, Request $request): Response
    {
        $params = [
            'date_debut' => $request->query->get('date_debut'),
            'date_fin' => $request->query->get('date_fin'),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $guichet = $this->api->get('/caisse/guichets/' . $id)->toArray();
            $statement = $this->api->get('/caisse/guichets/' . $id . '/releve', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Guichet introuvable');
            return $this->redirectToRoute('caisse_list');
        }

        return $this->render('backoffice/caisse/teller-statement.html.twig', [
            'guichet' => $guichet,
            'statement' => $statement,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Relevé guichetier'],
            ],
        ]);
    }
}
