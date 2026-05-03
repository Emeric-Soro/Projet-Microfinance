<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ClientMergeController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $clients = $this->apiClient->get('/clients/merge')->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du chargement des clients');
                $clients = [];
            }
            return $this->render('backoffice/client/merge.html.twig', ['clients' => $clients]);
        }

        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients/merge', $payload)->toArray();
            $this->addFlash('success', 'Fusion des clients réalisée');
            return $this->redirect('/clients');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Échec de la fusion');
            return $this->redirect('/clients/merge');
        }
    }
}
