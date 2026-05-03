<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/compensation')]
class ChequeCompensationController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_compensation', methods: ['GET'])]
    public function index(): Response
    {
        $compensations = $this->api->get('/paiements/compensation')->toArray();

        return $this->render('backoffice/paiement/compensation.html.twig', [
            'current_menu' => 'paiements_compensation',
            'compensations' => $compensations,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Compensation'],
            ],
        ]);
    }

    #[Route('/process/{id}', name: 'paiements_compensation_process', methods: ['POST'])]
    public function process(int $id): Response
    {
        try {
            $this->api->post('/paiements/compensation/' . $id . '/process');
            $this->addFlash('success', 'Compensation traitée avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement: ' . $e->getMessage());
        }

        return $this->redirectToRoute('paiements_compensation');
    }
}
