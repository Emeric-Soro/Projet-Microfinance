<?php

namespace App\Controller\Compte;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Routing\Annotation\Route;

class AccountOpenController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('/comptes/open', name: 'account_open', methods: ['GET', 'POST'])]
    public function index(Request $request, SessionInterface $session): Response
    {
        $step = $request->query->getInt('step', 1);
        $sessionKey = 'account_open';

        if ($request->isMethod('POST')) {
            $step = $request->request->getInt('step', 1);

            try {
                if ($step === 1) {
                    $clientId = $request->request->get('client_id');
                    if (!$clientId) {
                        $this->addFlash('error', 'Veuillez sélectionner un client.');
                    } else {
                        $session->set($sessionKey . '_client_id', $clientId);
                        return $this->redirectToRoute('account_open', ['step' => 2]);
                    }
                }

                if ($step === 2) {
                    $productId = $request->request->get('product_id');
                    if (!$productId) {
                        $this->addFlash('error', 'Veuillez sélectionner un produit.');
                    } else {
                        $session->set($sessionKey . '_product_id', $productId);
                        return $this->redirectToRoute('account_open', ['step' => 3]);
                    }
                }

                if ($step === 3) {
                    $session->set($sessionKey . '_initial_deposit', $request->request->get('montant_initial', 0));
                    return $this->redirectToRoute('account_open', ['step' => 4]);
                }

                if ($step === 4) {
                    $data = [
                        'client_id' => $session->get($sessionKey . '_client_id'),
                        'product_id' => $session->get($sessionKey . '_product_id'),
                        'montant_initial' => $session->get($sessionKey . '_initial_deposit', 0),
                        'observation' => $request->request->get('observation', ''),
                    ];

                    $this->api->post('/comptes/open', $data);
                    $session->remove($sessionKey . '_client_id');
                    $session->remove($sessionKey . '_product_id');
                    $session->remove($sessionKey . '_initial_deposit');
                    $this->addFlash('success', 'Compte ouvert avec succès.');
                    return $this->redirectToRoute('account_list');
                }
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'ouverture : ' . $e->getMessage());
            }
        }

        $data = [
            'step' => $step,
            'client_id' => $session->get($sessionKey . '_client_id'),
            'product_id' => $session->get($sessionKey . '_product_id'),
            'montant_initial' => $session->get($sessionKey . '_initial_deposit', 0),
        ];

        if ($step > 1 && !$data['client_id']) {
            $this->addFlash('warning', 'Session expirée. Veuillez recommencer.');
            return $this->redirectToRoute('account_open', ['step' => 1]);
        }

        if ($step === 2 || empty($data['products'])) {
            try {
                $data['products'] = $this->api->get('/products/comptes')->toArray();
            } catch (\Exception $e) {
                $data['products'] = [];
            }
        }

        if ($step === 2) {
            try {
                $client = $this->api->get('/clients/' . $data['client_id'])->toArray();
                $data['client_nom'] = $client['nom'] ?? $client['raisonSociale'] ?? '';
            } catch (\Exception $e) {
                $data['client_nom'] = '';
            }
        }

        return $this->render('backoffice/compte/open.html.twig', [
            'current_menu' => 'account_open',
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Comptes', 'url' => $this->generateUrl('account_list')],
                ['label' => 'Ouverture de compte'],
            ],
        ]);
    }
}
