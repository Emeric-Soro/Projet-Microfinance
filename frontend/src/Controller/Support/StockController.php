<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class StockController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/stocks', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des stocks');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/stocks.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function movement(int $id, Request $request): Response
    {
        $params = $request->query->all();
        try {
            $item = $this->apiClient->get('/stocks/'.$id)->toArray();
            $movements = $this->apiClient->get('/stocks/'.$id.'/movements', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des mouvements');
            return $this->redirectToRoute('support_stocks');
        }
        return $this->render('backoffice/support/stocks.html.twig', [
            'item' => $item,
            'movements' => $movements['data'] ?? [],
            'mode' => 'movement',
        ]);
    }
}
