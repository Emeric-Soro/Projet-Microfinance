<?php

namespace App\Controller\Epargne;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class SavingsProductController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/epargne/products', name: 'epargne_products', methods: ['GET'])]
    public function list(Request $request): Response
    {
        try {
            $products = $this->api->get('/epargne/produits')->toArray();
        } catch (\Exception $e) {
            $products = [];
            $this->addFlash('error', 'Erreur lors du chargement des produits');
        }

        return $this->render('backoffice/epargne/products.html.twig', [
            'products' => $products,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Produits'],
            ],
        ]);
    }

    #[Route('/epargne/rates', name: 'epargne_rates', methods: ['GET', 'POST'])]
    public function rates(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'produit' => $request->request->get('produit'),
                    'taux' => $request->request->get('taux'),
                    'date_effet' => $request->request->get('date_effet'),
                ];
                $this->api->post('/epargne/taux', $data);
                $this->addFlash('success', 'Taux mis à jour');
                return $this->redirectToRoute('epargne_rates');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $products = $this->api->get('/epargne/produits')->toArray();
            $rates = $this->api->get('/epargne/taux')->toArray();
        } catch (\Exception $e) {
            $products = [];
            $rates = [];
            $this->addFlash('error', 'Erreur lors du chargement');
        }

        return $this->render('backoffice/epargne/rates.html.twig', [
            'products' => $products,
            'rates' => $rates,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Taux'],
            ],
        ]);
    }

    #[Route('/epargne/interest', name: 'epargne_interest', methods: ['GET', 'POST'])]
    public function calculateInterest(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'produit' => $request->request->get('produit'),
                    'date_debut' => $request->request->get('date_debut'),
                    'date_fin' => $request->request->get('date_fin'),
                ];
                $response = $this->api->post('/epargne/interets/calculer', $data);
                $result = $response->toArray();
                $this->addFlash('success', 'Calcul des intérêts effectué');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $products = $this->api->get('/epargne/produits')->toArray();
        } catch (\Exception $e) {
            $products = [];
        }

        return $this->render('backoffice/epargne/interest.html.twig', [
            'products' => $products,
            'result' => $result,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Calcul intérêts'],
            ],
        ]);
    }

    #[Route('/epargne/programmed', name: 'epargne_programmed', methods: ['GET', 'POST'])]
    public function programmed(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'client' => $request->request->get('client'),
                    'montant' => $request->request->get('montant'),
                    'frequence' => $request->request->get('frequence'),
                    'duree' => $request->request->get('duree'),
                    'produit' => $request->request->get('produit'),
                ];
                $this->api->post('/epargne/programme', $data);
                $this->addFlash('success', 'Compte épargne programmé créé');
                return $this->redirectToRoute('epargne_programmed');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/epargne/programme/comptes')->toArray();
            $products = $this->api->get('/epargne/produits')->toArray();
        } catch (\Exception $e) {
            $accounts = [];
            $products = [];
        }

        return $this->render('backoffice/epargne/programmed.html.twig', [
            'accounts' => $accounts,
            'products' => $products,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Épargne programmée'],
            ],
        ]);
    }

    #[Route('/epargne/mandatory', name: 'epargne_mandatory', methods: ['GET', 'POST'])]
    public function mandatory(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'client' => $request->request->get('client'),
                    'montant' => $request->request->get('montant'),
                    'produit' => $request->request->get('produit'),
                ];
                $this->api->post('/epargne/obligatoire', $data);
                $this->addFlash('success', 'Compte épargne obligatoire créé');
                return $this->redirectToRoute('epargne_mandatory');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/epargne/obligatoire/comptes')->toArray();
            $products = $this->api->get('/epargne/produits')->toArray();
        } catch (\Exception $e) {
            $accounts = [];
            $products = [];
        }

        return $this->render('backoffice/epargne/mandatory.html.twig', [
            'accounts' => $accounts,
            'products' => $products,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Épargne obligatoire'],
            ],
        ]);
    }

    #[Route('/epargne/tontines', name: 'epargne_tontines', methods: ['GET', 'POST'])]
    public function tontines(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'nom' => $request->request->get('nom'),
                    'client_responsable' => $request->request->get('client_responsable'),
                    'montant_cycle' => $request->request->get('montant_cycle'),
                    'nombre_participants' => $request->request->get('nombre_participants'),
                    'date_debut' => $request->request->get('date_debut'),
                    'frequence' => $request->request->get('frequence', 'MENSUELLE'),
                ];
                $this->api->post('/epargne/tontines', $data);
                $this->addFlash('success', 'Tontine créée');
                return $this->redirectToRoute('epargne_tontines');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $tontines = $this->api->get('/epargne/tontines')->toArray();
        } catch (\Exception $e) {
            $tontines = [];
        }

        return $this->render('backoffice/epargne/tontines.html.twig', [
            'tontines' => $tontines,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Tontines'],
            ],
        ]);
    }

    #[Route('/epargne/tontines/{id}/contributions', name: 'epargne_tontine_contributions', methods: ['GET', 'POST'])]
    public function tontineContributions(string $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'membre' => $request->request->get('membre'),
                    'montant' => $request->request->get('montant'),
                    'date' => $request->request->get('date'),
                ];
                $this->api->post('/epargne/tontines/' . $id . '/contributions', $data);
                $this->addFlash('success', 'Contribution enregistrée');
                return $this->redirectToRoute('epargne_tontine_contributions', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $tontine = $this->api->get('/epargne/tontines/' . $id)->toArray();
            $contributions = $this->api->get('/epargne/tontines/' . $id . '/contributions')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Tontine introuvable');
            return $this->redirectToRoute('epargne_tontines');
        }

        return $this->render('backoffice/epargne/tontine-contributions.html.twig', [
            'tontine' => $tontine,
            'contributions' => $contributions,
            'breadcrumbs' => [
                ['label' => 'Épargne', 'url' => $this->generateUrl('epargne_products')],
                ['label' => 'Tontines', 'url' => $this->generateUrl('epargne_tontines')],
                ['label' => $tontine['nom'] ?? 'Contributions'],
            ],
        ]);
    }

    #[Route('/epargne/withdrawal', name: 'epargne_withdrawal', methods: ['GET', 'POST'])]
    public function withdrawal(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'compte' => $request->request->get('compte'),
                    'montant' => $request->request->get('montant'),
                    'motif' => $request->request->get('motif'),
                ];
                $this->api->post('/epargne/retrait', $data);
                $this->addFlash('success', 'Retrait épargne effectué');
                return $this->redirectToRoute('epargne_withdrawal');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $accounts = $this->api->get('/epargne/comptes')->toArray();
        } catch (\Exception $e) {
            $accounts = [];
        }

        return $this->render('backoffice/epargne/withdrawal.html.twig', [
            'accounts' => $accounts,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Retrait'],
            ],
        ]);
    }

    #[Route('/epargne/close/{id}', name: 'epargne_close', methods: ['GET', 'POST'])]
    public function close(string $id, Request $request): Response
    {
        try {
            $account = $this->api->get('/epargne/comptes/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Compte introuvable');
            return $this->redirectToRoute('epargne_products');
        }

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'motif' => $request->request->get('motif'),
                    'destination_fonds' => $request->request->get('destination_fonds'),
                ];
                $this->api->post('/epargne/comptes/' . $id . '/cloturer', $data);
                $this->addFlash('success', 'Compte clôturé');
                return $this->redirectToRoute('epargne_products');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/epargne/close.html.twig', [
            'account' => $account,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Clôture'],
            ],
        ]);
    }

    #[Route('/epargne/projects', name: 'epargne_projects', methods: ['GET', 'POST'])]
    public function projects(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'nom' => $request->request->get('nom'),
                    'montant_objectif' => $request->request->get('montant_objectif'),
                    'client' => $request->request->get('client'),
                    'date_fin_prevue' => $request->request->get('date_fin_prevue'),
                ];
                $this->api->post('/epargne/projets', $data);
                $this->addFlash('success', 'Projet épargne créé');
                return $this->redirectToRoute('epargne_projects');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $projects = $this->api->get('/epargne/projets')->toArray();
        } catch (\Exception $e) {
            $projects = [];
        }

        return $this->render('backoffice/epargne/projects.html.twig', [
            'projects' => $projects,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Projets'],
            ],
        ]);
    }

    #[Route('/epargne/auto-transfer', name: 'epargne_auto_transfer', methods: ['GET', 'POST'])]
    public function autoTransfer(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'compte_source' => $request->request->get('compte_source'),
                    'compte_destination' => $request->request->get('compte_destination'),
                    'montant' => $request->request->get('montant'),
                    'frequence' => $request->request->get('frequence', 'MENSUELLE'),
                    'jour_execution' => $request->request->get('jour_execution'),
                    'date_debut' => $request->request->get('date_debut'),
                    'actif' => true,
                ];
                $this->api->post('/epargne/virement-automatique', $data);
                $this->addFlash('success', 'Virement automatique configuré');
                return $this->redirectToRoute('epargne_auto_transfer');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $transfers = $this->api->get('/epargne/virement-automatique')->toArray();
            $accounts = $this->api->get('/comptes/actifs')->toArray();
        } catch (\Exception $e) {
            $transfers = [];
            $accounts = [];
        }

        return $this->render('backoffice/epargne/auto-transfer.html.twig', [
            'transfers' => $transfers,
            'accounts' => $accounts,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Virement automatique'],
            ],
        ]);
    }

    #[Route('/epargne/simulation', name: 'epargne_simulation', methods: ['GET', 'POST'])]
    public function simulation(Request $request): Response
    {
        $result = null;

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'montant_initial' => $request->request->get('montant_initial'),
                    'versement_mensuel' => $request->request->get('versement_mensuel'),
                    'duree_mois' => $request->request->get('duree_mois'),
                    'taux_annuel' => $request->request->get('taux_annuel'),
                ];
                $response = $this->api->post('/epargne/simulation', $data);
                $result = $response->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/epargne/simulation.html.twig', [
            'result' => $result,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Simulation'],
            ],
        ]);
    }

    #[Route('/epargne/certificates', name: 'epargne_certificates', methods: ['GET', 'POST'])]
    public function certificates(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $compte = $request->request->get('compte');
            return $this->redirectToRoute('epargne_certificates', ['compte' => $compte]);
        }

        $compte = $request->query->get('compte');
        $certificate = null;

        try {
            $accounts = $this->api->get('/epargne/comptes')->toArray();
            if ($compte) {
                $certificate = $this->api->get('/epargne/certificats/' . $compte)->toArray();
            }
        } catch (\Exception $e) {
            $accounts = [];
        }

        return $this->render('backoffice/epargne/certificates.html.twig', [
            'accounts' => $accounts,
            'certificate' => $certificate,
            'selected_compte' => $compte,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Certificats'],
            ],
        ]);
    }
}
