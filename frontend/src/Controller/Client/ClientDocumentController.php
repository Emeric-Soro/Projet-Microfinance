<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientDocumentController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients/{id}/documents', name: 'client_documents', methods: ['GET', 'POST'])]
    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $files = $request->files->all();
                if (!empty($files)) {
                    $data['files'] = $files;
                }
                $this->api->post('/clients/' . $id . '/documents', $data);
                $this->addFlash('success', 'Document ajouté avec succès.');
                return $this->redirectToRoute('client_documents', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ajout du document : ' . $e->getMessage());
            }
        }

        try {
            $client = $this->api->get('/clients/' . $id)->toArray();
            $documents = $this->api->get('/clients/' . $id . '/documents')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Client introuvable.');
            return $this->redirectToRoute('client_list');
        }

        return $this->render('backoffice/client/documents.html.twig', [
            'current_menu' => 'client_list',
            'client' => $client,
            'documents' => $documents['content'] ?? [],
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => $client['nom'] ?? $client['raisonSociale'] ?? 'Client', 'url' => $this->generateUrl('client_detail', ['id' => $id])],
                ['label' => 'Documents'],
            ],
        ]);
    }
}
