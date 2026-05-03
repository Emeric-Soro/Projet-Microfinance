<?php
namespace App\Controller\Auth;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class OtpController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(Request $request): Response
    {
        $session = $request->getSession();
        $username = $session->get('otp_username', '');
        $challenge = $session->get('otp_challenge', '');

        if (empty($username) || empty($challenge)) {
            return $this->redirectToRoute('login');
        }

        if ($request->isMethod('POST')) {
            $otp = $request->request->get('otp', '');

            if (empty($otp)) {
                $this->addFlash('error', 'Veuillez saisir le code OTP reçu.');
                return $this->render('auth/otp.html.twig');
            }

            try {
                $result = $this->apiClient->loginOtp($username, $challenge, $otp);

                $session->remove('otp_username');
                $session->remove('otp_challenge');

                return $this->redirectToRoute('dashboard_general');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Code OTP invalide ou expiré. Veuillez réessayer.');
                return $this->render('auth/otp.html.twig');
            }
        }

        return $this->render('auth/otp.html.twig');
    }
}
