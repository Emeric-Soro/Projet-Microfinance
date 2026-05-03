<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class AgencySelectionController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $agencyId = $request->request->get('agency_id', '');

            if (empty($agencyId)) {
                $this->addFlash('error', 'Veuillez sélectionner une agence.');
                return $this->redirectToRoute('select_agency');
            }

            try {
                $this->apiClient->post('/auth/select-agency', [
                    'agencyId' => $agencyId,
                ]);

                $request->getSession()->set('agency_id', $agencyId);

                return $this->redirectToRoute('dashboard_general');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la sélection de l\'agence. Veuillez réessayer.');
                return $this->redirectToRoute('select_agency');
            }
        }

        try {
            $agencies = $this->apiClient->get('/auth/agencies')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Impossible de charger la liste des agences.');
            $agencies = [];
        }

        return $this->render('auth/select-agency.html.twig', [
            'agencies' => $agencies['data'] ?? $agencies,
        ]);
    }
}
