<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CourrierController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/courrier', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement du courrier');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/courrier.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function create(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/support/courrier.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/courrier', $request->request->all())->toArray();
            $this->addFlash('success', 'Courrier enregistré');
            return $this->redirectToRoute('support_courrier');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'enregistrement');
            return $this->render('backoffice/support/courrier.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/courrier/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Courrier introuvable');
            return $this->redirectToRoute('support_courrier');
        }
        return $this->render('backoffice/support/courrier.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }
}
