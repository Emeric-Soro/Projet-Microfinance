<?php
namespace App\Controller\Support;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CongeController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function list(Request $request): Response
    {
        $params = $request->query->all();
        try {
            $data = $this->apiClient->get('/conges', $params)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement des congés');
            $data = ['data' => [], 'total' => 0, 'page' => 1, 'pages' => 1];
        }
        return $this->render('backoffice/support/conges.html.twig', [
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
            return $this->render('backoffice/support/conges.html.twig', [
                'item' => [], 'mode' => 'create',
            ]);
        }
        try {
            $this->apiClient->post('/conges', $request->request->all())->toArray();
            $this->addFlash('success', 'Congé créé avec succès');
            return $this->redirectToRoute('support_conges');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création');
            return $this->render('backoffice/support/conges.html.twig', [
                'item' => $request->request->all(), 'mode' => 'create',
            ]);
        }
    }

    public function approve(int $id, Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->redirectToRoute('support_conges');
        }
        try {
            $this->apiClient->post('/conges/'.$id.'/approve', $request->request->all())->toArray();
            $this->addFlash('success', 'Congé approuvé');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'approbation');
        }
        return $this->redirectToRoute('support_conges');
    }
}
