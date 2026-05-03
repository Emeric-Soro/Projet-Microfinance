<?php

namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class AccountStatementController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/comptes/{num}/statement', name: 'account_statement', methods: ['GET', 'POST'])]
    public function index(string $num, Request $request): Response
    {
        $params = [];
        $dateDebut = $request->query->get('date_debut', date('Y-m-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-d'));
        $params['date_debut'] = $dateDebut;
        $params['date_fin'] = $dateFin;
        $params['page'] = $request->query->getInt('page', 0);
        $params['size'] = 50;

        if ($request->isMethod('POST')) {
            $dateDebut = $request->request->get('date_debut', $dateDebut);
            $dateFin = $request->request->get('date_fin', $dateFin);
            return $this->redirectToRoute('account_statement', [
                'num' => $num,
                'date_debut' => $dateDebut,
                'date_fin' => $dateFin,
            ]);
        }

        try {
            $compte = $this->api->get('/comptes/' . $num)->toArray();
            $mouvements = $this->api->get('/comptes/' . $num . '/statement', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable.');
            return $this->redirectToRoute('account_list');
        }

        return $this->render('backoffice/compte/statement.html.twig', [
            'current_menu' => 'account_list',
            'compte' => $compte,
            'mouvements' => $mouvements['content'] ?? [],
            'total_items' => $mouvements['totalElements'] ?? 0,
            'total_pages' => $mouvements['totalPages'] ?? 0,
            'current_page' => $params['page'],
            'page_size' => $params['size'],
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
            'solde_debut' => $mouvements['soldeDebut'] ?? $compte['solde'] ?? 0,
            'solde_fin' => $mouvements['soldeFin'] ?? $compte['solde'] ?? 0,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => $compte['numero'] ?? $num, 'url' => $this->generateUrl('account_detail', ['num' => $num])],
                ['label' => 'Relevé'],
            ],
        ]);
    }
}
