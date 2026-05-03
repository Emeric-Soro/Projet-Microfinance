<?php
namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AccountBlockController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient)
    {}

    public function block(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $payload = $request->request->all();
                $this->apiClient->post('/comptes/'. urlencode($num) . '/block', $payload);
                $this->addFlash('success', 'Compte bloqué');
                return $this->redirectToRoute('app_compte_show', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Impossible de bloquer le compte');
            }
        }
        return $this->render('backoffice/compte/block.html.twig', ['num' => $num]);
    }

    public function unblock(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $payload = $request->request->all();
                $this->apiClient->post('/comptes/'. urlencode($num) . '/unblock', $payload);
                $this->addFlash('success', 'Compte débloqué');
                return $this->redirectToRoute('app_compte_show', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Impossible de débloquer le compte');
            }
        }
        return $this->render('backoffice/compte/unblock.html.twig', ['num' => $num]);
    }

    public function oppose(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $payload = $request->request->all();
                $this->apiClient->post('/comptes/'. urlencode($num) . '/oppose', $payload);
                $this->addFlash('success', 'Opposition enregistrée');
                return $this->redirectToRoute('app_compte_show', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Impossible d’enregistrer l’opposition');
            }
        }
        return $this->render('backoffice/compte/oppose.html.twig', ['num' => $num]);
    }
}
