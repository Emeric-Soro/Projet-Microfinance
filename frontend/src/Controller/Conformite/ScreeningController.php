<?php

namespace App\Controller\Conformite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ScreeningController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/screening', $request->request->all());
                $this->addFlash('success', 'Screening effectué avec succès.');
                return $this->redirectToRoute('conformite_screening');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du screening : ' . $e->getMessage());
            }
        }

        try {
            $resultats = $this->api->get('/conformite/screening', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des résultats.');
            $resultats = [];
        }

        return $this->render('backoffice/conformite/screening.html.twig', [
            'resultats' => $resultats,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Screening'],
            ],
        ]);
    }

    public function pep(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/pep', $request->request->all());
                $this->addFlash('success', 'Personne politiquement exposée enregistrée.');
                return $this->redirectToRoute('conformite_pep');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $peps = $this->api->get('/conformite/pep', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des PEP.');
            $peps = [];
        }

        return $this->render('backoffice/conformite/pep.html.twig', [
            'peps' => $peps,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Personnes politiquement exposées'],
            ],
        ]);
    }

    public function bic(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/bic', $request->request->all());
                $this->addFlash('success', 'BIC/IC enregistré.');
                return $this->redirectToRoute('conformite_bic');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $bics = $this->api->get('/conformite/bic', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des BIC/IC.');
            $bics = [];
        }

        return $this->render('backoffice/conformite/bic.html.twig', [
            'bics' => $bics,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Bénéficiaires effectifs'],
            ],
        ]);
    }

    public function correspondents(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/correspondants', $request->request->all());
                $this->addFlash('success', 'Correspondant enregistré.');
                return $this->redirectToRoute('conformite_correspondents');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $correspondants = $this->api->get('/conformite/correspondants', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des correspondants.');
            $correspondants = [];
        }

        return $this->render('backoffice/conformite/correspondents.html.twig', [
            'correspondants' => $correspondants,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Correspondants bancaires'],
            ],
        ]);
    }
}
