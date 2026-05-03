<?php

namespace App\Controller\Validation;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ValidationController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function queue(Request $request): Response
    {
        try {
            $validations = $this->api->get('/validations/queue', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de la file d\'attente.');
            $validations = [];
        }

        return $this->render('backoffice/validation/queue.html.twig', [
            'validations' => $validations,
            'breadcrumbs' => [
                ['label' => 'Validations'],
                ['label' => 'File d\'attente'],
            ],
        ]);
    }

    public function detail(int $id): Response
    {
        try {
            $validation = $this->api->get('/validations/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Validation introuvable.');
            return $this->redirectToRoute('validation_queue');
        }

        return $this->render('backoffice/validation/detail.html.twig', [
            'validation' => $validation,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Détail #' . $id],
            ],
        ]);
    }

    public function approve(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/validations/' . $id . '/approve', $request->request->all());
                $this->addFlash('success', 'Validation approuvée avec succès.');
                return $this->redirectToRoute('validation_queue');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'approbation : ' . $e->getMessage());
            }
        }

        try {
            $validation = $this->api->get('/validations/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Validation introuvable.');
            return $this->redirectToRoute('validation_queue');
        }

        return $this->render('backoffice/validation/approve.html.twig', [
            'validation' => $validation,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Approuver #' . $id],
            ],
        ]);
    }

    public function reject(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/validations/' . $id . '/reject', $request->request->all());
                $this->addFlash('success', 'Validation rejetée.');
                return $this->redirectToRoute('validation_queue');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du rejet : ' . $e->getMessage());
            }
        }

        try {
            $validation = $this->api->get('/validations/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Validation introuvable.');
            return $this->redirectToRoute('validation_queue');
        }

        return $this->render('backoffice/validation/reject.html.twig', [
            'validation' => $validation,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Rejeter #' . $id],
            ],
        ]);
    }

    public function revision(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/validations/' . $id . '/revision', $request->request->all());
                $this->addFlash('success', 'Demande de révision envoyée.');
                return $this->redirectToRoute('validation_queue');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la demande de révision : ' . $e->getMessage());
            }
        }

        try {
            $validation = $this->api->get('/validations/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Validation introuvable.');
            return $this->redirectToRoute('validation_queue');
        }

        return $this->render('backoffice/validation/revision.html.twig', [
            'validation' => $validation,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Révision #' . $id],
            ],
        ]);
    }

    public function history(Request $request): Response
    {
        try {
            $historique = $this->api->get('/validations/history', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de l\'historique.');
            $historique = [];
        }

        return $this->render('backoffice/validation/history.html.twig', [
            'historique' => $historique,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Historique'],
            ],
        ]);
    }

    public function limits(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/validations/limits', $request->request->all());
                $this->addFlash('success', 'Limites de validation mises à jour.');
                return $this->redirectToRoute('validation_limits');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour : ' . $e->getMessage());
            }
        }

        try {
            $limites = $this->api->get('/validations/limits')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des limites.');
            $limites = [];
        }

        return $this->render('backoffice/validation/limits.html.twig', [
            'limites' => $limites,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Limites'],
            ],
        ]);
    }

    public function rules(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/validations/rules', $request->request->all());
                $this->addFlash('success', 'Règles de validation mises à jour.');
                return $this->redirectToRoute('validation_rules');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour : ' . $e->getMessage());
            }
        }

        try {
            $regles = $this->api->get('/validations/rules')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des règles.');
            $regles = [];
        }

        return $this->render('backoffice/validation/rules.html.twig', [
            'regles' => $regles,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Règles'],
            ],
        ]);
    }

    public function forced(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/validations/forced', $request->request->all());
                $this->addFlash('success', 'Validation forcée effectuée.');
                return $this->redirectToRoute('validation_forced');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la validation forcée : ' . $e->getMessage());
            }
        }

        try {
            $forced = $this->api->get('/validations/forced')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement.');
            $forced = [];
        }

        return $this->render('backoffice/validation/forced.html.twig', [
            'forced' => $forced,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Validations forcées'],
            ],
        ]);
    }

    public function myActions(Request $request): Response
    {
        try {
            $actions = $this->api->get('/validations/my-actions', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement de vos actions.');
            $actions = [];
        }

        return $this->render('backoffice/validation/my-actions.html.twig', [
            'actions' => $actions,
            'breadcrumbs' => [
                ['label' => 'Validations', 'url' => $this->generateUrl('validation_queue')],
                ['label' => 'Mes actions'],
            ],
        ]);
    }
}
