<?php
namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class GeneralDashboardController extends AbstractController
{
    public function __construct(private ApiClientService $apiClient) {}

    public function index(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/general')->toArray();
        } catch (\\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }

        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Général', 'url' => '']
        ];

        return $this->render('backoffice/dashboard/general.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function siege(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/siege')->toArray();
        } catch (\\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Siège', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/siege.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function comptable(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/comptable')->toArray();
        } catch (\\Exception $e) {
            $data = ['stats' => [], 'recent' => [], 'alerts' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Comptable', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/comptable.html.twig', [
            'stats' => $data['stats'] ?? [],
            'recent' => $data['recent'] ?? [],
            'alerts' => $data['alerts'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function search(Request $request): Response
    {
        $query = $request->query->get('q', '');
        try {
            $data = $this->apiClient->get('/dashboard/search', ['q' => $query])->toArray();
        } catch (\\Exception $e) {
            $data = ['headers' => [], 'rows' => [], 'empty_title' => 'Aucun résultat', 'empty_message' => 'Aucun élément trouvé'];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Recherche', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/search.html.twig', [
            'query' => $query,
            'headers' => $data['headers'] ?? [],
            'rows' => $data['rows'] ?? [],
            'empty_title' => $data['empty_title'] ?? '',
            'empty_message' => $data['empty_message'] ?? '',
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function notifications(): Response
    {
        try {
            $data = $this->apiClient->get('/dashboard/notifications')->toArray();
        } catch (\\Exception $e) {
            $data = ['notifications' => [], 'headers' => [], 'rows' => []];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Notifications', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/notifications.html.twig', [
            'headers' => $data['headers'] ?? [],
            'rows' => $data['rows'] ?? [],
            'notifications' => $data['notifications'] ?? [],
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function notificationDetail(int $id): Response
    {
        try {
            $notification = $this->apiClient->get('/dashboard/notifications/'.$id)->toArray();
        } catch (\\Exception $e) {
            $notification = [];
        }
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Notifications', 'url' => '/backoffice/dashboard/notifications'],
            ['label' => 'Détail', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/notification-detail.html.twig', [
            'notification' => $notification,
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function profile(Request $request): Response
    {
        // Prefill profile data
        try {
            $profile = $this->apiClient->get('/user/profile')->toArray();
        } catch (\\Exception $e) {
            $profile = [];
        }

        if ($request->isMethod('POST')) {
            $payload = $request->request->all();
            try {
                $this->apiClient->post('/user/profile', $payload);
                $this->addFlash('success', 'Profil enregistré avec succès');
            } catch (\\Exception $e) {
                $this->addFlash('error', 'Échec de l’enregistrement du profil');
            }
            return $this->redirect('/backoffice/dashboard/profile');
        }

        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Profil', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/profile.html.twig', [
            'profile' => $profile,
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function preferences(Request $request): Response
    {
        try {
            $prefs = $this->apiClient->get('/user/preferences')->toArray();
        } catch (\\Exception $e) {
            $prefs = [];
        }

        if ($request->isMethod('POST')) {
            $payload = $request->request->all();
            try {
                $this->apiClient->post('/user/preferences', $payload);
                $this->addFlash('success', 'Préférences enregistrées avec succès');
            } catch (\\Exception $e) {
                $this->addFlash('error', 'Échec de l’enregistrement des préférences');
            }
            return $this->redirect('/backoffice/dashboard/preferences');
        }

        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Préférences', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/preferences.html.twig', [
            'preferences' => $prefs,
            'breadcrumbs' => $breadcrumbs,
        ]);
    }

    public function help(): Response
    {
        $breadcrumbs = [
            ['label' => 'Accueil', 'url' => '/backoffice'],
            ['label' => 'Aide', 'url' => '']
        ];
        return $this->render('backoffice/dashboard/help.html.twig', [
            'breadcrumbs' => $breadcrumbs,
        ]);
    }
}
