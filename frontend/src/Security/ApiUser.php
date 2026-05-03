<?php

namespace App\Security;

use Symfony\Component\Security\Core\User\UserInterface;

class ApiUser implements UserInterface
{
    public function __construct(private array $data)
    {
    }

    public function getRoles(): array
    {
        $roles = $this->data['roles'] ?? [];
        $roles[] = 'ROLE_USER';

        return array_values(array_unique($roles));
    }

    public function getPassword(): ?string
    {
        return null;
    }

    public function eraseCredentials(): void
    {
    }

    public function getUserIdentifier(): string
    {
        return $this->data['login'] ?? $this->data['username'] ?? $this->data['email'] ?? 'unknown';
    }

    public function getData(): array
    {
        return $this->data;
    }
}
