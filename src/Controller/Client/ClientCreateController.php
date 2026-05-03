<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\RedirectResponse;

class ClientCreateController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function createPhysical(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/create-physical.html.twig', [
                'data' => [],
            ]);
        }

        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients', $payload)->toArray();
            $this->addFlash('success', 'Client physique créé avec succès');
            return $this->redirect('/clients');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création du client');
            return $this->render('backoffice/client/create-physical.html.twig', [
                'data' => $payload,
            ]);
        }
    }

    public function createMoral(Request $request): Response
    {
        if (!$request->isMethod('POST')) {
            return $this->render('backoffice/client/create-moral.html.twig', [
                'data' => [],
            ]);
        }

        $payload = $request->request->all();
        try {
            $this->apiClient->post('/clients', $payload)->toArray();
            $this->addFlash('success', 'Client moral créé avec succès');
            return $this->redirect('/clients');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la création du client');
            return $this->render('backoffice/client/create-moral.html.twig', [
                'data' => $payload,
            ]);
        }
    }
}
