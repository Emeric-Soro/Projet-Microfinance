<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class EmployeController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/employes', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des employés');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/employes.html.twig', [
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
            return $this->render('backoffice/support/employes.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/employes', $request->request->all())->toArray();
            $this->addFlash('success', 'Employé créé avec succès');
            return $this->redirectToRoute('support_employes');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/support/employes.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function show(int $id): Response
    {
        try {
            $item = $this->apiClient->get('/employes/'.$id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Employé introuvable');
            return $this->redirectToRoute('support_employes');
        }
        return $this->render('backoffice/support/employes.html.twig', [
            'item' => $item, 'mode' => 'show',
        ]);
    }

    public function edit(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            try {
                $item = $this->apiClient->get('/employes/'.$id)->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Employé introuvable');
                return $this->redirectToRoute('support_employes');
            }
            return $this->render('backoffice/support/employes.html.twig', [
                'item' => $item, 'mode' => 'edit',
            ]);
        }
        try {
            $this->apiClient->put('/employes/'.$id, $request->request->all())->toArray();
            $this->addFlash('success', 'Employé mis à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de mise à jour');
        }
        return $this->redirectToRoute('support_employes_show', ['id' => $id]);
    }
}
