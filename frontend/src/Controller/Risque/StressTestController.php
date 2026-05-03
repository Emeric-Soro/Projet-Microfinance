<?php

namespace App\Controller\Risque;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class StressTestController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/risques/stress-tests', name: 'risque_stress_tests', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $scenario = $request->request->all('scenario');
            $result = $this->api->post('/risques/stress-tests/simulate', $scenario)->toArray();
            $this->addFlash('success', 'Simulation de stress test exécutée avec succès.');

            return $this->redirectToRoute('risque_stress_tests', ['result' => $result]);
        }

        $data = $this->api->get('/risques/stress-tests')->toArray();

        return $this->render('backoffice/risques/stress-tests.html.twig', [
            'data' => $data,
            'simulation_result' => $request->query->all('result'),
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Tests de Résistance'],
            ],
        ]);
    }

    #[Route('/risques/reporting', name: 'risque_reporting', methods: ['GET'])]
    public function reporting(): Response
    {
        $data = $this->api->get('/risques/reporting')->toArray();

        return $this->render('backoffice/risques/reporting.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Reporting Risques'],
            ],
        ]);
    }
}
