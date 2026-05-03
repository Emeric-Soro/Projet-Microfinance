<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class BudgetController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/budget', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement du budget');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/budget.html.twig', [
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
            return $this->render('backoffice/support/budget.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/budget', $request->request->all())->toArray();
            $this->addFlash('success', 'Budget créé avec succès');
            return $this->redirectToRoute('support_budget');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/support/budget.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/budget/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Budget introuvable');
            return $this->redirectToRoute('support_budget');
        }
        return $this->render('backoffice/support/budget.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }

    public function execution(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/budget/'.$id)->toArray();
            $execution = $this->apiClient->get('/budget/'.$id.'/execution')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement de l\'exécution');
            return $this->redirectToRoute('support_budget');
        }
        return $this->render('backoffice/support/budget.html.twig', [
            'item' => $item, 'execution' => $execution, 'mode' => 'execution',
        ]);
    }
}
