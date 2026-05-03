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
        $session = $request->getSession();

        // GET: list agencies
        if ($request->isMethod('GET')) {
            $agencies = $session->get('agencies', null);
            if (null === $agencies) {
                $data = $this->apiClient->get('/auth/agencies');
                $agencies = $data[0]['agencies'] ?? [];
                $session->set('agencies', $agencies);
            }
            return $this->renderForm('auth/select-agency.html.twig', ['agencies' => $agencies]);
        }

        // POST: set selected agency
        $selected = (string) $request->request->get('agency');
        if ('' === $selected) {
            $this->addFlash('error', 'Veuillez sélectionner une agence.');
            return $this->renderForm('auth/select-agency.html.twig', ['agencies' => $session->get('agencies', [])]);
        }
        $session->set('selected_agency', $selected);
        return $this->redirectToRoute('dashboard_general');
    }
}
