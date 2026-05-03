<?php
namespace App\Controller\Conformite;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ScreeningController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/screening', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des résultats de screening.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/screening.html.twig', $data);
    }

    public function pep(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/screening/pep', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des PEP.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/pep.html.twig', $data);
    }

    public function bic(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/screening/bic', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement du registre BIC.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/bic.html.twig', $data);
    }

    public function correspondents(Request $request): Response
    {
        try {
            $data = $this->apiClient->get('/compliance/screening/correspondents', $request->query->all())->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement des correspondants bancaires.');
            $data = ['data' => []];
        }
        return $this->render('backoffice/conformite/correspondents.html.twig', $data);
    }
}
