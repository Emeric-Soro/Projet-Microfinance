<?php

namespace App\Twig;

use App\Service\AuthService;
use Twig\Extension\AbstractExtension;
use Twig\TwigFilter;
use Twig\TwigFunction;

class AppExtension extends AbstractExtension
{
    private AuthService $authService;

    public function __construct(AuthService $authService)
    {
        $this->authService = $authService;
    }

    public function getFilters(): array
    {
        return [
            new TwigFilter('montant', [$this, 'formatMontant']),
            new TwigFilter('telephone', [$this, 'formatTelephone']),
            new TwigFilter('date_fr', [$this, 'formatDateFr']),
            new TwigFilter('status_class', [$this, 'getStatusClass']),
            new TwigFilter('pourcentage', [$this, 'formatPourcentage']),
            new TwigFilter('taux', [$this, 'formatTaux']),
            new TwigFilter('numerocompte', [$this, 'formatNumeroCompte']),
            new TwigFilter('age', [$this, 'calculateAge']),
            new TwigFilter('tronque', [$this, 'truncateText'], ['needs_context' => false]),
        ];
    }

    public function getFunctions(): array
    {
        return [
            new TwigFunction('has_role', [$this->authService, 'hasRole']),
            new TwigFunction('has_permission', [$this->authService, 'hasPermission']),
            new TwigFunction('current_user', [$this->authService, 'getCurrentUser']),
            new TwigFunction('current_agency', [$this->authService, 'getSelectedAgency']),
            new TwigFunction('percent_of', [$this, 'percentOf']),
            new TwigFunction('bool_icon', [$this, 'boolIcon']),
        ];
    }

    public function formatMontant($montant): string
    {
        if ($montant === null) return '-';
        return number_format((float) $montant, 0, ',', ' ') . ' FCFA';
    }

    public function formatTelephone(?string $telephone): string
    {
        if (!$telephone) return '-';
        $cleaned = preg_replace('/[^0-9]/', '', $telephone);
        if (strlen($cleaned) === 12 && str_starts_with($cleaned, '225')) {
            $cleaned = substr($cleaned, 3);
        }
        $chunks = str_split($cleaned, 2);
        return implode(' ', $chunks);
    }

    public function formatDateFr($date): string
    {
        if (!$date) return '-';
        if ($date instanceof \DateTimeInterface) {
            return $date->format('d/m/Y');
        }
        if (is_string($date)) {
            return date('d/m/Y', strtotime($date)) ?: $date;
        }
        return (string) $date;
    }

    public function getStatusClass(string $status): string
    {
        $map = [
            'ACTIF' => 'mf-badge-success',
            'ACTIVE' => 'mf-badge-success',
            'APPROVED' => 'mf-badge-success',
            'VALIDE' => 'mf-badge-success',
            'COMPLETED' => 'mf-badge-success',
            'EXECUTEE' => 'mf-badge-success',
            'PENDING' => 'mf-badge-pending',
            'EN_ATTENTE' => 'mf-badge-pending',
            'PENDING_VALIDATION' => 'mf-badge-pending',
            'SUSPENDU' => 'mf-badge-pending',
            'BLOQUE' => 'mf-badge-error',
            'BLOQUÉ' => 'mf-badge-error',
            'REJECTED' => 'mf-badge-error',
            'REJETE' => 'mf-badge-error',
            'FERME' => 'mf-badge-error',
            'CLOTURE' => 'mf-badge-error',
            'INACTIF' => 'mf-badge',
            'ARCHIVED' => 'mf-badge',
            'RADIE' => 'mf-badge-error',
        ];
        return $map[strtoupper($status)] ?? 'mf-badge';
    }

    public function formatPourcentage(float $value, int $decimals = 2): string
    {
        return number_format($value, $decimals, ',', ' ') . ' %';
    }

    public function formatTaux($value): string
    {
        if ($value === null) return '-';
        return number_format((float) $value, 2, ',', ' ') . ' %';
    }

    public function formatNumeroCompte(?string $numero): string
    {
        if (!$numero) return '-';
        $chunks = str_split(str_replace(' ', '', $numero), 4);
        return implode(' ', $chunks);
    }

    public function calculateAge($dateNaissance): int
    {
        if (!$dateNaissance) return 0;
        if (is_string($dateNaissance)) {
            $dateNaissance = new \DateTime($dateNaissance);
        }
        if ($dateNaissance instanceof \DateTimeInterface) {
            return (int) $dateNaissance->diff(new \DateTime())->y;
        }
        return 0;
    }

    public function truncateText(string $text, int $length = 100): string
    {
        if (mb_strlen($text) <= $length) return $text;
        return mb_substr($text, 0, $length) . '...';
    }

    public function percentOf(float $value, float $total): float
    {
        if ($total == 0) return 0;
        return round(($value / $total) * 100, 2);
    }

    public function boolIcon(bool $value): string
    {
        return $value ? '✓' : '✗';
    }
}
