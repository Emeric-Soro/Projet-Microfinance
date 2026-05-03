<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class DepartController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/departs', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des départs');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/depart.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function process(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->redirectToRoute('support_depart');
        }
        try {
            $this->apiClient->post('/departs/'.$id.'/process', $request->request->all())->toArray();
            $this->addFlash('success', 'Départ traité avec succès');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement');
        }
        return $this->redirectToRoute('support_depart');
    }
}
