<?php

namespace App\Controller\Comptabilite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class EcritureController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/comptabilite/operations-diverses', name: 'comptabilite_diverse', methods: ['GET', 'POST'])]
    public function diverse(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'libelle' => $request->request->get('libelle'),
                'date_operation' => $request->request->get('date_operation'),
                'journal_code' => $request->request->get('journal_code'),
                'lignes' => $request->request->all('lignes'),
            ];

            try {
                $this->api->post('/comptabilite/operations-diverses', $data)->toArray();
                $this->addFlash('success', 'Opération diverse enregistrée avec succès.');
                return $this->redirectToRoute('comptabilite_diverse');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $journaux = $this->api->get('/comptabilite/journaux')->toArray()['data'] ?? [];
        $accounts = $this->api->get('/comptabilite/plan-comptable', ['pageSize' => 500])->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/operations-diverses.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Opérations diverses'],
            ],
            'journaux' => $journaux,
            'accounts' => $accounts,
        ]);
    }

    #[Route('/comptabilite/brouillard', name: 'comptabilite_draft', methods: ['GET'])]
    public function draft(Request $request): Response
    {
        $dateDebut = $request->query->get('date_debut', date('Y-m-01'));
        $dateFin = $request->query->get('date_fin', date('Y-m-t'));
        $journalCode = $request->query->get('journal_code', '');
        $page = $request->query->getInt('page', 0);

        $params = array_filter(compact('dateDebut', 'dateFin', 'journalCode', 'page'));
        $data = $this->api->get('/comptabilite/brouillard', $params)->toArray();

        $journaux = $this->api->get('/comptabilite/journaux')->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/brouillard.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Brouillard'],
            ],
            'entries' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
            'journal_code' => $journalCode,
            'journaux' => $journaux,
            'solde' => $data['solde'] ?? 0,
            'total_debit' => $data['totalDebit'] ?? 0,
            'total_credit' => $data['totalCredit'] ?? 0,
        ]);
    }

    #[Route('/comptabilite/lettrage', name: 'comptabilite_lettering', methods: ['GET', 'POST'])]
    public function lettering(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'compte_id' => $request->request->get('compte_id'),
                'ecriture_ids' => $request->request->all('ecriture_ids'),
                'date_lettrage' => $request->request->get('date_lettrage', date('Y-m-d')),
            ];

            try {
                $this->api->post('/comptabilite/lettrage', $data)->toArray();
                $this->addFlash('success', 'Lettrage effectué avec succès.');
                return $this->redirectToRoute('comptabilite_lettering');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $compteId = $request->query->get('compte_id');
        $nonLettrees = [];
        if ($compteId) {
            $nonLettrees = $this->api->get('/comptabilite/lettrage/non-lettrees', ['compte_id' => $compteId])->toArray()['data'] ?? [];
        }

        $accounts = $this->api->get('/comptabilite/plan-comptable', ['pageSize' => 500])->toArray()['data'] ?? [];

        return $this->render('backoffice/comptabilite/lettrage.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Lettrage'],
            ],
            'accounts' => $accounts,
            'non_lettrees' => $nonLettrees,
            'compte_id' => $compteId,
        ]);
    }

    #[Route('/comptabilite/contre-passation', name: 'comptabilite_reversal', methods: ['GET', 'POST'])]
    public function reversal(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'ecriture_id' => $request->request->get('ecriture_id'),
                'date_extourne' => $request->request->get('date_extourne', date('Y-m-d')),
                'motif' => $request->request->get('motif'),
            ];

            try {
                $this->api->post('/comptabilite/contre-passation', $data)->toArray();
                $this->addFlash('success', 'Contre-passation effectuée avec succès.');
                return $this->redirectToRoute('comptabilite_reversal');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        $page = $request->query->getInt('page', 0);
        $data = $this->api->get('/comptabilite/contre-passation', ['page' => $page])->toArray();

        return $this->render('backoffice/comptabilite/contre-passation.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Contre-passation'],
            ],
            'ecritures' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
        ]);
    }

    #[Route('/comptabilite/pieces-comptables', name: 'comptabilite_documents', methods: ['GET', 'POST'])]
    public function documents(Request $request): Response
    {
        $page = $request->query->getInt('page', 0);
        $search = $request->query->get('search', '');
        $dateDebut = $request->query->get('date_debut', '');
        $dateFin = $request->query->get('date_fin', '');

        $params = array_filter(compact('page', 'search', 'dateDebut', 'dateFin'));
        $data = $this->api->get('/comptabilite/pieces-comptables', $params)->toArray();

        return $this->render('backoffice/comptabilite/pieces-comptables.html.twig', [
            'current_menu' => 'comptabilite',
            'breadcrumbs' => [
                ['label' => 'Comptabilité', 'url' => ''],
                ['label' => 'Pièces comptables'],
            ],
            'pieces' => $data['data'] ?? [],
            'total_items' => $data['total'] ?? 0,
            'current_page' => $page,
            'page_size' => $data['pageSize'] ?? 20,
            'total_pages' => $data['totalPages'] ?? 1,
            'search' => $search,
            'date_debut' => $dateDebut,
            'date_fin' => $dateFin,
        ]);
    }
}
