<?php

namespace App\Service;

class ReferenceDataService
{
    private ApiClientService $apiClient;

    public function __construct(ApiClientService $apiClient)
    {
        $this->apiClient = $apiClient;
    }

    public function getAgencies(): array
    {
        $response = $this->apiClient->get('/api/reference/agencies');
        return $response->toArray();
    }

    public function getProducts(string $type = null): array
    {
        $params = $type ? ['type' => $type] : [];
        $response = $this->apiClient->get('/api/reference/products', $params);
        return $response->toArray();
    }

    public function getRegions(): array
    {
        $response = $this->apiClient->get('/api/reference/regions');
        return $response->toArray();
    }

    public function getEmployees(): array
    {
        $response = $this->apiClient->get('/api/reference/employees');
        return $response->toArray();
    }

    public function getNationalities(): array
    {
        return [
            'CI' => 'Ivoirienne',
            'BF' => 'Burkinabè',
            'ML' => 'Malienne',
            'SN' => 'Sénégalaise',
            'BJ' => 'Béninoise',
            'TG' => 'Togolaise',
            'NE' => 'Nigérienne',
            'GN' => 'Guinéenne',
            'FR' => 'Française',
            'OTHER' => 'Autre',
        ];
    }

    public function getCivilities(): array
    {
        return [
            'MR' => 'M.',
            'MME' => 'Mme',
            'MLLE' => 'Mlle',
            'DR' => 'Dr',
            'MAITRE' => 'Maître',
        ];
    }

    public function getFamilySituations(): array
    {
        return [
            'CELIBATAIRE' => 'Célibataire',
            'MARIE' => 'Marié(e)',
            'DIVORCE' => 'Divorcé(e)',
            'VEUF' => 'Veuf/Veuve',
        ];
    }

    public function getDocumentTypes(): array
    {
        return [
            'CNI' => 'Carte Nationale d\'Identité',
            'PASSEPORT' => 'Passeport',
            'PERMIS' => 'Permis de Conduire',
            'ATTESTATION' => 'Attestation d\'Identité',
        ];
    }
}
