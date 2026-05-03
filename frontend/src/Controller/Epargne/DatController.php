<?php

namespace App\Controller\Epargne;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class DatController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/epargne/dat', name: 'epargne_dat', methods: ['GET', 'POST'])]
    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'client' => $request->request->get('client'),
                    'montant' => $request->request->get('montant'),
                    'duree_jours' => $request->request->get('duree_jours'),
                    'taux' => $request->request->get('taux'),
                    'date_souscription' => $request->request->get('date_souscription'),
                    'capitalisation' => $request->request->has('capitalisation'),
                ];
                $this->api->post('/epargne/dat', $data);
                $this->addFlash('success', 'DAT créé avec succès');
                return $this->redirectToRoute('epargne_dat');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur: ' . $e->getMessage());
            }
        }

        try {
            $dats = $this->api->get('/epargne/dat')->toArray();
            $products = $this->api->get('/epargne/produits')->toArray();
        } catch (\Exception $e) {
            $dats = [];
            $products = [];
        }

        return $this->render('backoffice/epargne/dat.html.twig', [
            'dats' => $dats,
            'products' => $products,
            'breadcrumbs' => [
                ['label' => 'Épargne'],
                ['label' => 'Dépôt à Terme'],
            ],
        ]);
    }

    #[Route('/epargne/dat/{id}', name: 'epargne_dat_detail', methods: ['GET'])]
    public function detail(string $id): Response
    {
        try {
            $dat = $this->api->get('/epargne/dat/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'DAT introuvable');
            return $this->redirectToRoute('epargne_dat');
        }

        return $this->render('backoffice/epargne/dat_detail.html.twig', [
            'dat' => $dat,
            'breadcrumbs' => [
                ['label' => 'Épargne', 'url' => $this->generateUrl('epargne_products')],
                ['label' => 'DAT', 'url' => $this->generateUrl('epargne_dat')],
                ['label' => 'Détail'],
            ],
        ]);
    }
}
