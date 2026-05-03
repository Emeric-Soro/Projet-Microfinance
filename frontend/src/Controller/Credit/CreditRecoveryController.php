<?php

namespace App\Controller\Credit;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CreditRecoveryController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function arrears(Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        if ($request->query->get('q')) {
            $params['q'] = $request->query->get('q');
        }
        if ($request->query->get('tranche')) {
            $params['tranche'] = $request->query->get('tranche');
        }

        try {
            $data = $this->api->get('/credit/arrears', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['content' => [], 'totalElements' => 0, 'totalPages' => 0];
            $this->addFlash('error', 'Erreur lors du chargement des impayés.');
        }

        return $this->render('backoffice/credit/arrears.html.twig', [
            'current_menu' => 'credit_arrears',
            'impayes' => $data['content'] ?? [],
            'total_items' => $data['totalElements'] ?? 0,
            'total_pages' => $data['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'search_query' => $request->query->get('q', ''),
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Impayés'],
            ],
        ]);
    }

    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/recovery', $request->request->all());
                $this->addFlash('success', 'Action de recouvrement enregistrée avec succès.');
                return $this->redirectToRoute('credit_recovery', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $recouvrement = $this->api->get('/credit/' . $id . '/recovery')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_arrears');
        }

        return $this->render('backoffice/credit/recovery.html.twig', [
            'current_menu' => 'credit_arrears',
            'credit' => $credit,
            'recouvrement' => $recouvrement,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Impayés', 'url' => $this->generateUrl('credit_arrears')],
                ['label' => 'N°' . $id . ' - Recouvrement'],
            ],
        ]);
    }

    public function litigation(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/litigation', $request->request->all());
                $this->addFlash('success', 'Action contentieuse enregistrée avec succès.');
                return $this->redirectToRoute('credit_litigation', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $contentieux = $this->api->get('/credit/' . $id . '/litigation')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_arrears');
        }

        return $this->render('backoffice/credit/litigation.html.twig', [
            'current_menu' => 'credit_arrears',
            'credit' => $credit,
            'contentieux' => $contentieux,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Impayés', 'url' => $this->generateUrl('credit_arrears')],
                ['label' => 'N°' . $id . ' - Contentieux'],
            ],
        ]);
    }

    public function provisions(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/provisions', $request->request->all());
                $this->addFlash('success', 'Provision enregistrée avec succès.');
                return $this->redirectToRoute('credit_provisions');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $data = $this->api->get('/credit/provisions', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $data = ['content' => []];
            $this->addFlash('error', 'Erreur lors du chargement des provisions.');
        }

        return $this->render('backoffice/credit/provisions.html.twig', [
            'current_menu' => 'credit_provisions',
            'provisions' => $data['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Provisions'],
            ],
        ]);
    }

    public function chargeOff(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/credit/' . $id . '/charge-off', $request->request->all());
                $this->addFlash('success', 'Passage en perte enregistré avec succès.');
                return $this->redirectToRoute('credit_arrears');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du passage en perte : ' . $e->getMessage());
            }
        }

        try {
            $credit = $this->api->get('/credit/' . $id)->toArray();
            $provisions = $this->api->get('/credit/' . $id . '/provisions')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Crédit introuvable.');
            return $this->redirectToRoute('credit_arrears');
        }

        return $this->render('backoffice/credit/charge-off.html.twig', [
            'current_menu' => 'credit_arrears',
            'credit' => $credit,
            'provisions' => $provisions,
            'breadcrumbs' => [
                ['label' => 'Crédit', 'url' => $this->generateUrl('credit_dashboard')],
                ['label' => 'Impayés', 'url' => $this->generateUrl('credit_arrears')],
                ['label' => 'N°' . $id . ' - Passage en perte'],
            ],
        ]);
    }
}
