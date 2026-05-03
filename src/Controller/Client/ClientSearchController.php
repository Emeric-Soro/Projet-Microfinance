<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ClientSearchController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        // GET shows the form, POST processes the search
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/search.html.twig', [
                'results' => [],
                'criteria' => [],
            ]);
        }

        $criteria = $request->request->all();
        try {
            $data = $this->apiClient->post('/clients/search', $criteria)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Recherche échouée');
            $data = ['data' => []];
        }

        return $this->render('backoffice/client/search.html.twig', [
            'results' => $data['data'] ?? [],
            'criteria' => $criteria,
        ]);
    }
}
