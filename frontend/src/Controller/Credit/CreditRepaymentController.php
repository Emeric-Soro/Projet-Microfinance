<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditRepaymentController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function activeList(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        if ($request->query->get('q')) {
            $params['q'] = $request->query->get('q');
        }
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        try {
            $data = $this->api->get('/credit/active', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des crédits actifs.');
        }

        return $this->render('backoffice/credit/active.html.twig', [
            'current_menu' => 'credit_active',
            'credits' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'search_query' => $request->query->get('q', ''),
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs'],
            ],
        ]);
    }

    public function detail(int $id): Response
    {
        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $echeances = $this->api->get('/credit/' . $id . '/schedule')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/detail.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'echeances' => $echeances['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id],
            ],
        ]);
    }

    public function schedule(int $id): Response
    {
        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $echeances = $this->api->get('/credit/' . $id . '/schedule')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/schedule.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'echeances' => $echeances['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Échéancier'],
            ],
        ]);
    }

    public function repayment(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/repayment', $request->request->all());
                $this->addFlash('success', 'Remboursement enregistré avec succès.');
                return $this->redirectToRoute('credit_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du remboursement : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $echeances = $this->api->get('/credit/' . $id . '/schedule')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/repayment.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'echeances' => $echeances['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Remboursement'],
            ],
        ]);
    }

    public function prepayment(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/prepayment', $request->request->all());
                $this->addFlash('success', 'Remboursement anticipé enregistré avec succès.');
                return $this->redirectToRoute('credit_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du remboursement anticipé : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $simulation = $this->api->get('/credit/' . $id . '/prepayment/simulation')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/prepayment.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'simulation' => $simulation,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Remb. anticipé'],
            ],
        ]);
    }

    public function postponement(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/postponement', $request->request->all());
                $this->addFlash('success', 'Report d\'échéance enregistré avec succès.');
                return $this->redirectToRoute('credit_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du report : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $echeances = $this->api->get('/credit/' . $id . '/schedule')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/postponement.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'echeances' => $echeances['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Report d\'échéance'],
            ],
        ]);
    }

    public function restructuring(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/restructuring', $request->request->all());
                $this->addFlash('success', 'Restructuration enregistrée avec succès.');
                return $this->redirectToRoute('credit_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la restructuration : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $simulation = $this->api->get('/credit/' . $id . '/restructuring/simulation')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/restructuring.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'simulation' => $simulation,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Restructuration'],
            ],
        ]);
    }

    public function groupCollection(int $groupId, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/group-collection/' . $groupId, $request->request->all());
                $this->addFlash('success', 'Collecte de groupe enregistrée avec succès.');
                return $this->redirectToRoute('credit_group_collection', ['groupId' => $groupId]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la collecte : ' . $e->getMessage());
            }
        }

        try {
            $groupe = $this->api->get('/credit/solidarity-groups/' . $groupId)->toArray();
            $membres = $this->api->get('/credit/solidarity-groups/' . $groupId . '/members')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Groupe introuvable.');
            return $this->redirectToRoute('credit_solidarity_groups');
        }

        return $this->render('backoffice/credit/group-collection.html.twig', [
            'current_menu' => 'credit_solidarity_groups',
            'groupe' => $groupe,
            'membres' => $membres['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Groupes solidaires', 'url' => $this->generateUrl('credit_solidarity_groups')],
                ['label' => $groupe['nom'] ?? 'Groupe N°' . $groupId . ' - Collecte'],
            ],
        ]);
    }

    public function fullFile(int $id): Response
    {
        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $dossier = $this->api->get('/credit/' . $id . '/file')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_active');
        }

        return $this->render('backoffice/credit/file.html.twig', [
            'current_menu' => 'credit_active',
            'credit' => $credit,
            'dossier' => $dossier,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Crédits actifs', 'url' => $this->generateUrl('credit_active')],
                ['label' => 'N°' . $id . ' - Dossier complet'],
            ],
        ]);
    }
}
