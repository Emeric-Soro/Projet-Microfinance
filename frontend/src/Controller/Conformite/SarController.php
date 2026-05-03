<?php

namespace App\Controller\Conformite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class SarController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            try {
                $this->api->post('/conformite/sar', $request->request->all());
                $this->addFlash('success', 'Déclaration SAR enregistrée.');
                return $this->redirectToRoute('conformite_sar');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement : ' . $e->getMessage());
            }
        }

        try {
            $declarations = $this->api->get('/conformite/sar', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des déclarations SAR.');
            $declarations = [];
        }

        return $this->render('backoffice/conformite/sar.html.twig', [
            'declarations' => $declarations,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Déclarations SAR'],
            ],
        ]);
    }

    public function reports(Request $request): Response
    {
        try {
            $rapports = $this->api->get('/conformite/sar/rapports', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des rapports SAR.');
            $rapports = [];
        }

        return $this->render('backoffice/conformite/reports.html.twig', [
            'rapports' => $rapports,
            'breadcrumbs' => [
                ['label' => 'Conformité', 'url' => $this->generateUrl('conformite_dashboard')],
                ['label' => 'Déclarations SAR', 'url' => $this->generateUrl('conformite_sar')],
                ['label' => 'Rapports'],
            ],
        ]);
    }
}
