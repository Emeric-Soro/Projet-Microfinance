<?php

namespace App\Controller\Paiement;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/backoffice/paiements/virements-masse')]
class VirementMasseController extends AbstractController
{
    public function __construct(
        private readonly ApiClientService $api,
    ) {
    }

    #[Route('', name: 'paiements_virements_masse', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $params = [];
        if ($request->query->get('statut')) {
            $params['statut'] = $request->query->get('statut');
        }

        $virements = $this->api->get('/paiements/virements-masse', $params)->toArray();

        return $this->render('backoffice/paiement/virement-masse.html.twig', [
            'current_menu' => 'paiements_virements_masse',
            'virements' => $virements,
            'breadcrumbs' => [
                ['label' => 'Accueil', 'url' => $this->generateUrl('dashboard_general')],
                ['label' => 'Paiements'],
                ['label' => 'Virements de masse'],
            ],
        ]);
    }

    #[Route('/execute/{id}', name: 'paiements_virements_masse_execute', methods: ['POST'])]
    public function execute(int $id): Response
    {
        try {
            $this->api->post('/paiements/virements-masse/' . $id . '/execute');
            $this->addFlash('success', 'Virement de masse exécuté avec succès.');
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'exécution: ' . $e->getMessage());
        }

        return $this->redirectToRoute('paiements_virements_masse');
    }
}
