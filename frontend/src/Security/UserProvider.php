<?php

namespace App\Security;

use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\Security\Core\Exception\UserNotFoundException;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

class UserProvider implements UserProviderInterface
{
    private RequestStack $requestStack;

    public function __construct(RequestStack $requestStack)
    {
        $this->requestStack = $requestStack;
    }

    public static function fromApiData(array $userData): UserInterface
    {
        $roles = [];
        foreach ($userData['roles'] ?? [] as $role) {
            $normalizedRole = strtoupper((string) $role);
            if (!str_starts_with($normalizedRole, 'ROLE_')) {
                $normalizedRole = 'ROLE_' . $normalizedRole;
            }
            $roles[] = $normalizedRole;
        }

        $roles[] = 'ROLE_USER';
        $userData['roles'] = array_values(array_unique($roles));

        return new ApiUser($userData);
    }

    public function loadUserByIdentifier(string $identifier): UserInterface
    {
        $session = $this->requestStack->getSession();
        $userData = $session->get('user');

        if (!$userData) {
            throw new UserNotFoundException('User not found in session');
        }

        return self::fromApiData($userData);
    }

    public function refreshUser(UserInterface $user): UserInterface
    {
        return $this->loadUserByIdentifier($user->getUserIdentifier());
    }

    public function supportsClass(string $class): bool
    {
        return $class === ApiUser::class || is_subclass_of($class, ApiUser::class);
    }
}
