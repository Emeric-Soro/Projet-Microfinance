<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientDetailController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients/{id}', name: 'client_detail', methods: ['GET'])]
    public function show(int $id): Response
    {
        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $comptes = $this->api->get('/clients/' . $id . '/comptes')->toArray();
            $credits = $this->api->get('/clients/' . $id . '/credits')->toArray();
            $epargne = $this->api->get('/clients/' . $id . '/epargne')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/show.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'comptes' => $comptes['content'] ?? [],
            'credits' => $credits['content'] ?? [],
            'epargne' => $epargne['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Détail'],
            ],
        ]);
    }

    #[Route('/clients/{id}/edit', name: 'client_edit', methods: ['GET', 'POST'])]
    public function edit(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $files = $request->files->all();
                if (!empty($files)) {
                    $data['files'] = $files;
                }
                $this->api->put('/clients/' . $id, $data);
                $this->addFlash('success', 'Client mis à jour avec succès.');
                return $this->redirectToRoute('client_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/edit.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Modifier'],
            ],
        ]);
    }

    #[Route('/clients/{id}/beneficiaires', name: 'client_beneficiaires', methods: ['GET', 'POST'])]
    public function beneficiaires(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/beneficiaires', $request->request->all());
                $this->addFlash('success', 'Bénéficiaire effectif ajouté avec succès.');
                return $this->redirectToRoute('client_beneficiaires', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ajout : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $beneficiaires = $this->api->get('/clients/' . $id . '/beneficiaires')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/beneficiaires.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'beneficiaires' => $beneficiaires['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Bénéficiaires effectifs'],
            ],
        ]);
    }

    #[Route('/clients/{id}/mandataires', name: 'client_mandataires', methods: ['GET', 'POST'])]
    public function mandataires(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/mandataires', $request->request->all());
                $this->addFlash('success', 'Mandataire ajouté avec succès.');
                return $this->redirectToRoute('client_mandataires', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ajout : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $mandataires = $this->api->get('/clients/' . $id . '/mandataires')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/mandataires.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'mandataires' => $mandataires['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Mandataires'],
            ],
        ]);
    }

    #[Route('/clients/{id}/contacts', name: 'client_contacts', methods: ['GET', 'POST'])]
    public function contacts(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/contacts', $request->request->all());
                $this->addFlash('success', 'Contact ajouté avec succès.');
                return $this->redirectToRoute('client_contacts', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ajout : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $contacts = $this->api->get('/clients/' . $id . '/contacts')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/contacts.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'contacts' => $contacts['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Contacts'],
            ],
        ]);
    }

    #[Route('/clients/{id}/risk', name: 'client_risk', methods: ['GET', 'POST'])]
    public function riskEvaluation(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/risk-evaluation', $request->request->all());
                $this->addFlash('success', 'Évaluation des risques mise à jour avec succès.');
                return $this->redirectToRoute('client_risk', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'évaluation : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $risk = $this->api->get('/clients/' . $id . '/risk-evaluation')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/risk.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'risk' => $risk,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Évaluation des risques'],
            ],
        ]);
    }

    #[Route('/clients/{id}/screening', name: 'client_screening', methods: ['GET'])]
    public function screening(int $id): Response
    {
        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $screening = $this->api->get('/clients/' . $id . '/screening')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/screening.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'screening' => $screening,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Screening AML'],
            ],
        ]);
    }

    #[Route('/clients/{id}/history', name: 'client_history', methods: ['GET'])]
    public function history(int $id): Response
    {
        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $history = $this->api->get('/clients/' . $id . '/history')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/history.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'history' => $history['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Historique'],
            ],
        ]);
    }

    #[Route('/clients/{id}/suspend', name: 'client_suspend', methods: ['GET', 'POST'])]
    public function suspend(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/suspend', $request->request->all());
                $this->addFlash('success', 'Client suspendu avec succès.');
                return $this->redirectToRoute('client_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la suspension : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/suspend.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Suspendre'],
            ],
        ]);
    }

    #[Route('/clients/{id}/reactivate', name: 'client_reactivate', methods: ['GET', 'POST'])]
    public function reactivate(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/reactivate', $request->request->all());
                $this->addFlash('success', 'Client réactivé avec succès.');
                return $this->redirectToRoute('client_detail', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la réactivation : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/reactivate.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Réactiver'],
            ],
        ]);
    }

    #[Route('/clients/{id}/archive', name: 'client_archive', methods: ['GET', 'POST'])]
    public function archive(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/clients/' . $id . '/archive', $request->request->all());
                $this->addFlash('success', 'Client archivé avec succès.');
                return $this->redirectToRoute('client_list');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'archivage : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/archive.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Archiver'],
            ],
        ]);
    }
}
