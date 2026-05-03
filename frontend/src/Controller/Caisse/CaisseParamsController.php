<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseParamsController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function params(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = $request->request->all();
                $this->api->put('/caisse/params', $data);
                $this->addFlash('success', 'Paramètres mis à jour avec succès');
                return $this->redirectToRoute('caisse_params');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
            }
        }

        try {
            $params = $this->api->get('/caisse/params')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger les paramètres: ' . $e->getMessage());
            $params = [];
        }

        return $this->render('backoffice/caisse/params.html.twig', [
            'params' => $params,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Paramètres'],
            ],
        ]);
    }
}
