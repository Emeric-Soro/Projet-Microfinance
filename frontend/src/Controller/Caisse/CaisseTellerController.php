<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseTellerController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function tellerStatement(int $id, Request $request): Response
    {
        $params = [
            'date_debut' => $request->query->get('date_debut'),
            'date_fin' => $request->query->get('date_fin'),
        ];
        $params = array_filter($params, fn($v) => $v !== null && $v !== '');

        try {
            $guichet = $this->api->get('/caisse/guichets/' . $id)->toArray();
            $statement = $this->api->get('/caisse/guichets/' . $id . '/releve', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Guichet introuvable');
            return $this->redirectToRoute('caisse_list');
        }

        return $this->render('backoffice/caisse/teller-statement.html.twig', [
            'guichet' => $guichet,
            'statement' => $statement,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Relevé guichetier'],
            ],
        ]);
    }
}
