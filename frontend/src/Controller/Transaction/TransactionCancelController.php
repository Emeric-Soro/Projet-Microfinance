<?php

namespace App\Controller\Transaction;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class TransactionCancelController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    #[Route('/operations/{ref}/cancel', name: 'operation_cancel', methods: ['GET', 'POST'])]
    public function cancel(string $ref, Request $request): Response
    {
        try {
            $transaction = $this->api->get('/transactions/' . $ref)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Opération introuvable');
            return $this->redirectToRoute('operation_history');
        }

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'motif' => $request->request->get('motif'),
                    'autorisation' => $request->request->get('autorisation'),
                ];
                $this->api->post('/transactions/' . $ref . '/cancel', $data);
                $this->addFlash('success', 'Annulation effectuée pour ' . $ref);
                return $this->redirectToRoute('operation_detail', ['ref' => $ref]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'annulation: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/operations/annulation.html.twig', [
            'transaction' => $transaction,
            'mode' => 'cancel',
            'breadcrumbs' => [
                ['label' => 'Opérations', 'url' => $this->generateUrl('operation_history')],
                ['label' => 'Annulation #' . $ref],
            ],
        ]);
    }

    #[Route('/operations/{ref}/extourne', name: 'operation_extourne', methods: ['GET', 'POST'])]
    public function extourne(string $ref, Request $request): Response
    {
        try {
            $transaction = $this->api->get('/transactions/' . $ref)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Opération introuvable');
            return $this->redirectToRoute('operation_history');
        }

        if ($request->isMethod('POST')) {
            try {
                $data = [
                    'motif' => $request->request->get('motif'),
                    'autorisation' => $request->request->get('autorisation'),
                ];
                $this->api->post('/transactions/' . $ref . '/extourne', $data);
                $this->addFlash('success', 'Extourne effectuée pour ' . $ref);
                return $this->redirectToRoute('operation_detail', ['ref' => $ref]);
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'extourne: ' . $e->getMessage());
            }
        }

        return $this->render('backoffice/operations/annulation.html.twig', [
            'transaction' => $transaction,
            'mode' => 'extourne',
            'breadcrumbs' => [
                ['label' => 'Opérations', 'url' => $this->generateUrl('operation_history')],
                ['label' => 'Extourne #' . $ref],
            ],
        ]);
    }
}
