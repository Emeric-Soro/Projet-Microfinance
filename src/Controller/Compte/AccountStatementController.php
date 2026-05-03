<?php
namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AccountStatementController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient)
    {}

    public function index(string $num, Request $request): Response
    {
        try {
            $range = $request->query->all();
            $data = $this->apiClient->get('/comptes/'. urlencode($num) . '/statement', $range)->toArray();
        } catch (\Exception $e) {
            $data = ['data' => [], 'range' => []];
            $this->addFlash('error', 'Impossible de générer l’État');
        }
        return $this->render('backoffice/compte/statement.html.twig', [
            'num' => $num,
            'data' => $data['data'] ?? [],
            'range' => $data['range'] ?? [],
        ]);
    }
}
