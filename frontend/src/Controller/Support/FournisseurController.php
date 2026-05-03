<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class FournisseurController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/fournisseurs', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des fournisseurs');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/fournisseurs.html.twig', [
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
            return $this->render('backoffice/support/fournisseurs.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/fournisseurs', $request->request->all())->toArray();
            $this->addFlash('success', 'Fournisseur créé avec succès');
            return $this->redirectToRoute('support_fournisseurs');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/support/fournisseurs.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/fournisseurs/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Fournisseur introuvable');
            return $this->redirectToRoute('support_fournisseurs');
        }
        return $this->render('backoffice/support/fournisseurs.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }
}
