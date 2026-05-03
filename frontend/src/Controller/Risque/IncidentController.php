<?php

namespace App\Controller\Risque;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class IncidentController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/risques/incidents', name: 'risque_operational', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = $request->request->all('incident');
            $this->api->post('/risques/incidents', $data)->toArray();
            $this->addFlash('success', 'Incident opérationnel enregistré.');

            return $this->redirectToRoute('risque_operational');
        }

        $incidents = $this->api->get('/risques/incidents')->toArray();

        return $this->render('backoffice/risques/incidents.html.twig', [
            'incidents' => $incidents,
            'breadcrumbs' => [
                ['label' => 'Gestion des Risques', 'url' => $this->generateUrl('risque_dashboard')],
                ['label' => 'Incidents Opérationnels'],
            ],
        ]);
    }
}
