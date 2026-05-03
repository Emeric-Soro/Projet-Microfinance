<?php

namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AccountDetailController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/comptes/{num}', name: 'account_detail', methods: ['GET'])]
    public function show(string $num): Response
    {
        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
            $mouvements = $this->api->get('/comptes/' . $num . '/mouvements', ['page' => 0, 'size' => 10])->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/show.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'mouvements' => $mouvements['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? 'Compte'],
            ],
        ]);
    }

    #[Route('/comptes/{num}/history', name: 'account_history', methods: ['GET'])]
    public function history(string $num, Request $request): Response
    {
        $params = [];
        $page = $request->query->getInt('page', 0);
        $params['page'] = $page;
        $params['size'] = 20;

        if ($request->query->get('date_debut')) {
            $params['date_debut'] = $request->query->get('date_debut');
        }
        if ($request->query->get('date_fin')) {
            $params['date_fin'] = $request->query->get('date_fin');
        }
        if ($request->query->get('type')) {
            $params['type'] = $request->query->get('type');
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
            $history = $this->api->get('/comptes/' . $num . '/history', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/history.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'mouvements' => $history['content'] ?? [],
            'total_items' => $history['totalElements'] ?? 0,
            'total_pages' => $history['totalPages'] ?? 0,
            'current_page' => $page,
            'page_size' => 20,
            'date_debut' => $request->query->get('date_debut', ''),
            'date_fin' => $request->query->get('date_fin', ''),
            'filter_type' => $request->query->get('type', ''),
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Historique'],
            ],
        ]);
    }

    #[Route('/comptes/{num}/product', name: 'account_product_change', methods: ['GET', 'POST'])]
    public function changeProduct(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/comptes/' . $num . '/change-product', $request->request->all());
                $this->addFlash('success', 'Produit modifié avec succès.');
                return $this->redirectToRoute('account_detail', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du changement de produit : ' . $e->getMessage());
            }
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
            $products = $this->api->get('/products/comptes')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/product-change.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'products' => $products['content'] ?? $products,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Changer de produit'],
            ],
        ]);
    }

    #[Route('/comptes/{num}/settings', name: 'account_settings', methods: ['GET', 'POST'])]
    public function settings(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->put('/comptes/' . $num . '/settings', $request->request->all());
                $this->addFlash('success', 'Paramètres du compte mis à jour avec succès.');
                return $this->redirectToRoute('account_detail', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour des paramètres : ' . $e->getMessage());
            }
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
            $settings = $this->api->get('/comptes/' . $num . '/settings')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/settings.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'settings' => $settings,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Paramètres'],
            ],
        ]);
    }
}
