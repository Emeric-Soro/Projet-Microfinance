<?php
namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;

class AccountOpenController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient)
    {}

    public function index(Request $request, SessionInterface $session): Response
    {
        $step = (int) $request->query->get('step', 1);
        $payload = [];

        // Simple multi-step flow persisted in session for continuity
        if ($request->isMethod('POST')) {
            $stepPosted = (int) $request->get('step', $step);
            $session->set('compte_open_step', $stepPosted);
            $step = $stepPosted;

            // Persist partial data as needed (best-effort)
            $clientId = $request->request->get('client_id');
            if ($clientId) {
                $session->set('compte_open_client_id', $clientId);
            }
            // Capture product/deposit data if provided
            if ($request->request->has('product_id')) {
                $session->set('compte_open_product_id', $request->request->get('product_id'));
            }
            if ($request->request->has('initial_deposit')) {
                $session->set('compte_open_initial_deposit', $request->request->get('initial_deposit'));
            }
        }

        // Load data for UI if needed
        try {
            $clients = $this->apiClient->get('/clients', [])->toArray();
            $products = $this->apiClient->get('/produits/comptes', [])->toArray();
        } catch (\Exception $e) {
            $clients = [];
            $products = [];
        }

        $payload['step'] = $step;
        $payload['clients'] = $clients['data'] ?? [];
        $payload['products'] = $products['data'] ?? [];
        
        return $this->render('backoffice/compte/open.html.twig', $payload);
    }
}
