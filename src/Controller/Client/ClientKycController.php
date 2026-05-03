<?php
namespace App\Controller\Client;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class ClientKycController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(int $id, Request $request): Response
    {
        try {
            $kyc = $this->apiClient->get('/clients/'.$id.'/kyc')->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors du chargement KYC');
            $kyc = [];
        }
        return $this->render('backoffice/client/kyc.html.twig', ['kyc' => $kyc]);
    }
}
