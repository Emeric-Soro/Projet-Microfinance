<?php

namespace App\Controller\Caisse;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CaisseBilletageController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(int $id, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'coupures' => $request->request->all('coupures'),
                    'guichet' => $request->request->get('guichet'),
                ];
                $this->api->post('/caisse/' . $id . '/billetage', $data);
                $this->addFlash('success', 'Billetage enregistré avec succès');
                return $this->redirectToRoute('caisse_session', ['id' => $id]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors du billetage: ' . $e->getMessage());
            }
        }

        try {
            $session = $this->api->get('/caisse/sessions/' . $id)->toArray();
            $denominations = $this->api->get('/caisse/denominations')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Session introuvable');
            return $this->redirectToRoute('caisse_list');
        }

        return $this->render('backoffice/caisse/billetage.html.twig', [
            'session' => $session,
            'denominations' => $denominations,
            'breadcrumbs' => [
                ['label' => 'Caisse'],
                ['label' => 'Billetage'],
            ],
        ]);
    }
}
