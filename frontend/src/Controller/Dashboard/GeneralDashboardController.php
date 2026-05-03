<?php

namespace App\Controller\Dashboard;

use App\Service\ApiClientService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class GeneralDashboardController extends AbstractController
{
    private ApiClientService $api;

    public function __construct(ApiClientService $api)
    {
        $this->api = $api;
    }

    public function index(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/general')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord');
        }

        return $this->render('backoffice/dashboard/general.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    public function siege(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/siege')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord siège');
        }

        return $this->render('backoffice/dashboard/siege.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Siège'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    public function comptable(Request $request): Response
    {
        try {
            $data = $this->api->get('/dashboard/comptable')->toArray();
        } catch (\Exception $e) {
            $data = [];
            $this->addFlash('error', 'Impossible de charger le tableau de bord comptable');
        }

        return $this->render('backoffice/dashboard/comptable.html.twig', [
            'data' => $data,
            'breadcrumbs' => [
                ['label' => 'Comptabilité'],
                ['label' => 'Tableau de bord'],
            ],
        ]);
    }

    public function search(Request $request): Response
    {
        $query = $request->query->get('q', '');
        $results = [];

        if ('' !== $query) {
            try {
                $results = $this->api->get('/search', ['q' => $query])->toArray();
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la recherche');
            }
        }

        return $this->render('backoffice/dashboard/search.html.twig', [
            'query' => $query,
            'results' => $results,
            'breadcrumbs' => [
                ['label' => 'Recherche globale'],
            ],
        ]);
    }

    public function notifications(Request $request): Response
    {
        try {
            $notifications = $this->api->get('/notifications')->toArray();
        } catch (\Exception $e) {
            $notifications = [];
            $this->addFlash('error', 'Impossible de charger les notifications');
        }

        return $this->render('backoffice/dashboard/notifications.html.twig', [
            'notifications' => $notifications,
            'breadcrumbs' => [
                ['label' => 'Notifications'],
            ],
        ]);
    }

    public function notificationDetail(int $id): Response
    {
        try {
            $notification = $this->api->get('/notifications/' . $id)->toArray();
        } catch (\Exception $e) {
            $this->addFlash('error', 'Notification introuvable');
            return $this->redirectToRoute('notifications_list');
        }

        return $this->render('backoffice/dashboard/notification-detail.html.twig', [
            'notification' => $notification,
            'breadcrumbs' => [
                ['label' => 'Notifications', 'url' => $this->generateUrl('notifications_list')],
                ['label' => 'Détail'],
            ],
        ]);
    }

    public function profile(Request $request): Response
    {
        $formData = [];

        if ($request->isMethod('POST')) {
            $formData = [
                'nom' => $request->request->get('nom'),
                'prenom' => $request->request->get('prenom'),
                'email' => $request->request->get('email'),
                'telephone' => $request->request->get('telephone'),
                'fonction' => $request->request->get('fonction'),
            ];

            try {
                $this->api->put('/users/profile', $formData);
                $this->addFlash('success', 'Profil mis à jour avec succès');
                return $this->redirectToRoute('profile');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de la mise à jour du profil');
            }
        }

        try {
            $user = $this->api->get('/users/profile')->toArray();
        } catch (\Exception $e) {
            $user = [];
            $this->addFlash('error', 'Impossible de charger le profil');
        }

        return $this->render('backoffice/dashboard/profile.html.twig', [
            'user' => $user,
            'formData' => $formData,
            'breadcrumbs' => [
                ['label' => 'Mon profil'],
            ],
        ]);
    }

    public function preferences(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'langue' => $request->request->get('langue'),
                'notifications_email' => $request->request->has('notifications_email'),
                'notifications_push' => $request->request->has('notifications_push'),
                'theme' => $request->request->get('theme'),
                'page_size' => $request->request->getInt('page_size', 20),
            ];

            try {
                $this->api->put('/users/preferences', $data);
                $this->addFlash('success', 'Préférences enregistrées');
                return $this->redirectToRoute('preferences');
            } catch (\Exception $e) {
                $this->addFlash('error', 'Erreur lors de l\'enregistrement des préférences');
            }
        }

        try {
            $preferences = $this->api->get('/users/preferences')->toArray();
        } catch (\Exception $e) {
            $preferences = [];
        }

        return $this->render('backoffice/dashboard/preferences.html.twig', [
            'preferences' => $preferences,
            'breadcrumbs' => [
                ['label' => 'Préférences'],
            ],
        ]);
    }

    public function help(Request $request): Response
    {
        try {
            $faq = $this->api->get('/help/faq')->toArray();
        } catch (\Exception $e) {
            $faq = [];
        }

        return $this->render('backoffice/dashboard/help.html.twig', [
            'faq' => $faq,
            'breadcrumbs' => [
                ['label' => 'Aide'],
            ],
        ]);
    }
}
