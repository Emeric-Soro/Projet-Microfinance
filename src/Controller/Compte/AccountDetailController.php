<?php
namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AccountDetailController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient)
    {}

    public function show(string $num, Request $request): Response
    {
        try {
            $info = $this->apiClient->get('/comptes/'. urlencode($num), [])->toArray();
        } catch (\Exception $e) {
            $info = ['data' => []];
            $this->addFlash('error', 'Impossible de charger le compte');
        }

        return $this->render('backoffice/compte/show.html.twig', [
            'num' => $num,
            'info' => $info['data'] ?? [],
        ]);
    }

    public function history(string $num, Request $request): Response
    {
        try {
            $params = $request->query->all();
            $data = $this->apiClient->get('/comptes/'. urlencode($num) . '/history', $params)->toArray();
        } catch (\Exception $e) {
            $data = ['data' => []];
            $this->addFlash('error', 'Erreur lors de la récupération des mouvements');
        }
        return $this->render('backoffice/compte/history.html.twig', [
            'num' => $num,
            'data' => $data['data'] ?? [],
        ]);
    }

    public function changeProduct(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $payload = $request->request->all();
                $this->apiClient->post('/comptes/'. urlencode($num) . '/product', $payload);
                $this->addFlash('success', 'Produit mis à jour');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Impossible de changer le produit');
            }
        }
        return $this->redirectToRoute('app_compte_show', ['num' => $num]);
    }

    public function settings(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $payload = $request->request->all();
                $this->apiClient->post('/comptes/'. urlencode($num) . '/settings', $payload);
                $this->addFlash('success', 'Paramètres enregistrés');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Échec de l’enregistrement des paramètres');
            }
        }
        return $this->render('backoffice/compte/settings.html.twig', ['num' => $num]);
    }
}
