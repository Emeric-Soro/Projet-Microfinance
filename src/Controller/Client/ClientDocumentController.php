<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ClientDocumentController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(int $id, Request $request): Response
    {
        try {
            $docs = $this->apiClient->get('/clients/'.$id.'/documents')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger les documents');
            $docs = [];
        }
        // Handle upload if POST
        if ($request->isMethod('POST')) {
            // Minimal placeholder: forward to API as multipart if available
            $file = $request->files->get('document');
            try {
                $payload = ['document' => $file ? fopen($file->getRealPath(), 'r') : null];
                $this->apiClient->post('/clients/'.$id.'/documents', $payload)->toArray();
                $this->addFlash('success', 'Document téléchargé');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Échec du téléchargement du document');
            }
            return $this->redirect('/clients/'.$id.'/documents');
        }
        return $this->render('backoffice/client/documents.html.twig', ['documents' => $docs]);
    }
}
