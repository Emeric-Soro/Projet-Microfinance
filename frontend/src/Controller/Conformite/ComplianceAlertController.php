<?php

namespace App\Controller\Conformite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ComplianceAlertController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function dashboard(): Response
    {
        try {
            $stats = $this->api->get('/conformite/dashboard')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement du tableau de bord.');
            $stats = [];
        }

        return $this->render('backoffice/conformite/dashboard.html.twig', [
            'stats' => $stats,
            'breadcrumbs' => [
                ['label' => 'Conformité'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    public function index(Request $request): Response
    {
        try {
            $alertes = $this->api->get('/conformite/alertes', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des alertes.');
            $alertes = [];
        }

        return $this->render('backoffice/conformite/alerts.html.twig', [
            'alertes' => $alertes,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Alertes'],
            ],
        ]);
    }

    public function investigation(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/alertes/' . $id . '/investigation', $request->request->all());
                $this->addFlash('success', 'Enquête enregistrée.');
                return $this->redirectToRoute('conformite_investigation', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $alerte = $this->api->get('/conformite/alertes/' . $id)->toArray();
            $investigation = $this->api->get('/conformite/alertes/' . $id . '/investigation')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Alerte introuvable.');
            return $this->redirectToRoute('conformite_alerts');
        }

        return $this->render('backoffice/conformite/investigation.html.twig', [
            'alerte' => $alerte,
            'investigation' => $investigation,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Alertes', 'url' => $this->generateUrl('conformite_alerts')],
                ['label' => 'Enquête #' . $id],
            ],
        ]);
    }

    public function riskProfile(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/risk-profile', $request->request->all());
                $this->addFlash('success', 'Profil de risque mis à jour.');
                return $this->redirectToRoute('conformite_risk_profile');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour : ' . $e->getMessage());
            }
        }

        try {
            $profils = $this->api->get('/conformite/risk-profile', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des profils.');
            $profils = [];
        }

        return $this->render('backoffice/conformite/risk-profile.html.twig', [
            'profils' => $profils,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Profil de risque'],
            ],
        ]);
    }

    public function paymentIncidents(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/incidents-paiement', $request->request->all());
                $this->addFlash('success', 'Incident de paiement enregistré.');
                return $this->redirectToRoute('conformite_payment_incidents');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $incidents = $this->api->get('/conformite/incidents-paiement', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des incidents.');
            $incidents = [];
        }

        return $this->render('backoffice/conformite/payment-incidents.html.twig', [
            'incidents' => $incidents,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Incidents de paiement'],
            ],
        ]);
    }

    public function tax(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/taxe', $request->request->all());
                $this->addFlash('success', 'Déclaration fiscale enregistrée.');
                return $this->redirectToRoute('conformite_tax');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $taxes = $this->api->get('/conformite/taxe', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des données fiscales.');
            $taxes = [];
        }

        return $this->render('backoffice/conformite/tax.html.twig', [
            'taxes' => $taxes,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Conformité fiscale'],
            ],
        ]);
    }

    public function inspector(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/inspection', $request->request->all());
                $this->addFlash('success', 'Rapport d\'inspection enregistré.');
                return $this->redirectToRoute('conformite_inspector');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $inspections = $this->api->get('/conformite/inspection', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des inspections.');
            $inspections = [];
        }

        return $this->render('backoffice/conformite/inspector.html.twig', [
            'inspections' => $inspections,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Inspection'],
            ],
        ]);
    }

    public function training(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/formation', $request->request->all());
                $this->addFlash('success', 'Session de formation enregistrée.');
                return $this->redirectToRoute('conformite_training');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $formations = $this->api->get('/conformite/formation', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des formations.');
            $formations = [];
        }

        return $this->render('backoffice/conformite/training.html.twig', [
            'formations' => $formations,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Formation'],
            ],
        ]);
    }

    public function riskMap(): Response
    {
        try {
            $data = $this->api->get('/conformite/cartographie-risques')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de la cartographie.');
            $data = [];
        }

        return $this->render('backoffice/conformite/risk-map.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Cartographie des risques'],
            ],
        ]);
    }

    public function internalControl(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/controle-interne', $request->request->all());
                $this->addFlash('success', 'Contrôle interne enregistré.');
                return $this->redirectToRoute('conformite_internal_control');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $controles = $this->api->get('/conformite/controle-interne', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des contrôles.');
            $controles = [];
        }

        return $this->render('backoffice/conformite/internal-control.html.twig', [
            'controles' => $controles,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Contrôle interne'],
            ],
        ]);
    }
}
