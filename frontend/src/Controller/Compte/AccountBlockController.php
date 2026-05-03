<?php

namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AccountBlockController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/comptes/{num}/block', name: 'account_block', methods: ['GET', 'POST'])]
    public function block(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/comptes/' . $num . '/block', $request->request->all());
                $this->addFlash('success', 'Compte bloqué avec succès.');
                return $this->redirectToRoute('account_detail', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du blocage : ' . $e->getMessage());
            }
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/block.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Bloquer'],
            ],
        ]);
    }

    #[Route('/comptes/{num}/unblock', name: 'account_unblock', methods: ['GET', 'POST'])]
    public function unblock(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/comptes/' . $num . '/unblock', $request->request->all());
                $this->addFlash('success', 'Compte débloqué avec succès.');
                return $this->redirectToRoute('account_detail', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du déblocage : ' . $e->getMessage());
            }
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/unblock.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Débloquer'],
            ],
        ]);
    }

    #[Route('/comptes/{num}/oppose', name: 'account_oppose', methods: ['GET', 'POST'])]
    public function oppose(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/comptes/' . $num . '/oppose', $request->request->all());
                $this->addFlash('success', 'Opposition enregistrée avec succès.');
                return $this->redirectToRoute('account_detail', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'opposition : ' . $e->getMessage());
            }
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/oppose.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Opposition'],
            ],
        ]);
    }
}
