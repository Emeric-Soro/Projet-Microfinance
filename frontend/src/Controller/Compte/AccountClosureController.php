<?php

namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AccountClosureController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/comptes/{num}/closure', name: 'account_closure', methods: ['GET', 'POST'])]
    public function index(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/comptes/' . $num . '/closure', $request->request->all());
                $this->addFlash('success', 'Compte clôturé avec succès.');
                return $this->redirectToRoute('account_list');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la clôture : ' . $e->getMessage());
            }
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/closure.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'motifs' => [
                'INITIATIVE_CLIENT' => 'Initiative du client',
                'TRANSFERT' => 'Transfert vers un autre produit',
                'FUSION' => 'Fusion de comptes',
                'DECES' => 'Décès du titulaire',
                'RADIATION' => 'Radiation',
                'AUTRE' => 'Autre motif',
            ],
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Clôture'],
            ],
        ]);
    }
}
