<?php

namespace App\Service;

use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;

class AuthService
{
    private ApiClientService $apiClient;
    private RequestStack $requestStack;

    public function __construct(ApiClientService $apiClient, RequestStack $requestStack)
    {
        $this->apiClient = $apiClient;
        $this->requestStack = $requestStack;
    }

    public function authenticate(string $username, string $password): array
    {
        $result = $this->apiClient->login($username, $password);
        
        if (!isset($result['token'])) {
            throw new AuthenticationException($result['message'] ?? 'Authentication failed');
        }

        return $result;
    }

    public function verifyOtp(string $username, string $challenge, string $otp): array
    {
        return $this->apiClient->loginOtp($username, $challenge, $otp);
    }

    public function getCurrentUser(): ?array
    {
        $session = $this->requestStack->getSession();
        return $session->get('user');
    }

    public function hasRole(string $role): bool
    {
        $user = $this->getCurrentUser();
        if (!$user || !isset($user['roles'])) {
            return false;
        }
        return in_array($role, $user['roles']);
    }

    public function hasPermission(string $permission): bool
    {
        $user = $this->getCurrentUser();
        if (!$user || !isset($user['permissions'])) {
            return false;
        }
        return in_array($permission, $user['permissions']);
    }

    public function isAuthenticated(): bool
    {
        $session = $this->requestStack->getSession();
        return $session->has('jwt_token');
    }

    public function getSelectedAgency(): ?array
    {
        $session = $this->requestStack->getSession();
        return $session->get('selected_agency');
    }

    public function selectAgency(int $agencyId): void
    {
        $session = $this->requestStack->getSession();
        $session->set('selected_agency', ['id' => $agencyId]);
    }

    public function logout(): void
    {
        $this->apiClient->logout();
    }

    public function requestPasswordReset(string $identifier): array
    {
        return $this->apiClient->post('/api/auth/forgot-password', [
            'identifier' => $identifier,
        ])->toArray();
    }

    public function resetPassword(string $token, string $newPassword): array
    {
        return $this->apiClient->post('/api/auth/reset-password', [
            'token' => $token,
            'password' => $newPassword,
        ])->toArray();
    }

    public function changeExpiredPassword(string $oldPassword, string $newPassword): array
    {
        return $this->apiClient->post('/api/auth/change-password', [
            'oldPassword' => $oldPassword,
            'newPassword' => $newPassword,
        ])->toArray();
    }
}
