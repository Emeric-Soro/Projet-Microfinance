<?php

namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ClientSearchController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/clients/search', name: 'client_search', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        $results = [];
        $searched = false;

        if ($request->isMethod('POST')) {
            $params = [];
            $searchData = $request->request->all();

            if (!empty($searchData['nom'])) {
                $params['nom'] = $searchData['nom'];
            }
            if (!empty($searchData['prenom'])) {
                $params['prenom'] = $searchData['prenom'];
            }
            if (!empty($searchData['numeroIdentite'])) {
                $params['numeroIdentite'] = $searchData['numeroIdentite'];
            }
            if (!empty($searchData['telephone'])) {
                $params['telephone'] = $searchData['telephone'];
            }
            if (!empty($searchData['email'])) {
                $params['email'] = $searchData['email'];
            }
            if (!empty($searchData['dateNaissanceDeb'])) {
                $params['dateNaissanceDeb'] = $searchData['dateNaissanceDeb'];
            }
            if (!empty($searchData['dateNaissanceFin'])) {
                $params['dateNaissanceFin'] = $searchData['dateNaissanceFin'];
            }
            if (!empty($searchData['statut'])) {
                $params['statut'] = $searchData['statut'];
            }
            if (!empty($searchData['type'])) {
                $params['type'] = $searchData['type'];
            }
            if (!empty($searchData['agence_id'])) {
                $params['agence_id'] = $searchData['agence_id'];
            }

            if (!empty($params)) {
                $searched = true;
                try {
                    $data = $this->api->get('/clients/search', $params)->toArray();
                    $results = $data['content'] ?? $data;
                } catch (\Exception $e) {
                    $this->addFlash('error', 'Erreur lors de la recherche avancée.');
                }
            } else {
                $this->addFlash('warning', 'Veuillez remplir au moins un critère de recherche.');
            }
        }

        return $this->render('backoffice/client/search.html.twig', [
            'current_menu' => 'client_search',
            'results' => $results,
            'searched' => $searched,
            'breadcrumbs' => [
                ['label' => 'Clients', 'url' => $this->generateUrl('client_list')],
                ['label' => 'Recherche avancée'],
            ],
        ]);
    }
}
