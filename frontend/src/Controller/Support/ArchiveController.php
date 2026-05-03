<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ArchiveController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/archives', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des archives');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/archives.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function search(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/archives/search', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de recherche');
            $data = ['data' => [], 'total' => 0];
        }
        return $this->render('backoffice/support/archives.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'query' => $params['q'] ?? '',
            'mode' => 'search',
        ]);
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/archives/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Archive introuvable');
            return $this->redirectToRoute('support_archives');
        }
        return $this->render('backoffice/support/archives.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }
}
