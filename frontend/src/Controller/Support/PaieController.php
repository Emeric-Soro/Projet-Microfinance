<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class PaieController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/paie', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement de la paie');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/paie.html.twig', [
            'items' => $data['data'] ?? [],
            'total' => $data['total'] ?? 0,
            'page' => $data['page'] ?? 1,
            'pages' => $data['pages'] ?? 1,
            'mode' => 'list',
        ]);
    }

    public function process(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/support/paie.html.twig', [
                'mode' => 'process',
            ]);
        }
        try {
            $result = $this->apiClient->post('/paie/process', $request->request->all())->toArray();
            $this->addFlash('success', 'Paie traitée avec succès');
            return $this->redirectToRoute('support_paie');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du traitement');
            return $this->render('backoffice/support/paie.html.twig', [
                'mode' => 'process',
            ]);
        }
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/paie/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Fiche de paie introuvable');
            return $this->redirectToRoute('support_paie');
        }
        return $this->render('backoffice/support/paie.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }

    public function bulletins(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/paie/bulletins', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des bulletins');
            $data = ['data' => [], 'total' => 0];
        }
        return $this->render('backoffice/support/paie.html.twig', [
            'items' => $data['data'] ?? [],
            'mode' => 'bulletins',
        ]);
    }
}
