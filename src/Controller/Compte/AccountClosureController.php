<?php
namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AccountClosureController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient)
    {}

    public function index(string $num, Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $payload = $request->request->all();
                $this->apiClient->post('/comptes/'. urlencode($num) . '/closure', $payload);
                $this->addFlash('success', 'Compte clôturé');
                return $this->redirectToRoute('app_compte_show', ['num' => $num]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Impossible de clôturer le compte');
            }
        }
        return $this->render('backoffice/compte/closure.html.twig', ['num' => $num]);
    }
}
