<?php
namespace App\Controller\Admin;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SecuriteController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        try {
            $policy = $this->apiClient->get('/admin/securite')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de chargement de la politique');
            $policy = [];
        }
        return $this->render('backoffice/admin/securite.html.twig', [
            'policy' => $policy, 'mode' => 'index',
        ]);
    }

    public function edit(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->redirectToRoute('admin_securite');
        }
        try {
            $this->apiClient->put('/admin/securite', $request->request->all())->toArray();
            $this->addFlash('success', 'Politique de sécurité mise à jour');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur de mise à jour');
        }
        return $this->redirectToRoute('admin_securite');
    }
}
