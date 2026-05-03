<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientCreateController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients/create', name: 'client_create', methods: ['GET', 'POST'])]
    public function createPhysical(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $files = $request->files->all();

                if (!empty($files)) {
                    $data['files'] = $files;
                }

                $this->api->post('/clients/physiques', $data);
                $this->addFlash('success', 'Client physique créé avec succès.');
                return $this->redirectToRoute('client_list');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création : ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/client/create-physical.html.twig', [
            'current_menu' => 'client_create',
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => 'Nouveau client physique'],
            ],
        ]);
    }

    #[Route('/clients/create-moral', name: 'client_create_moral', methods: ['GET', 'POST'])]
    public function createMoral(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $files = $request->files->all();

                if (!empty($files)) {
                    $data['files'] = $files;
                }

                $this->api->post('/clients/moraux', $data);
                $this->addFlash('success', 'Personne morale créée avec succès.');
                return $this->redirectToRoute('client_list');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la création : ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/client/create-moral.html.twig', [
            'current_menu' => 'client_create_moral',
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => 'Nouvelle personne morale'],
            ],
        ]);
    }
}
