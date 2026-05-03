<?php

namespace App\Service;

use Psr\Log\LoggerInterface;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Contracts\Cache\CacheInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Symfony\Contracts\HttpClient\ResponseInterface;

class ApiClientService
{
    private HttpClientInterface $httpClient;
    private ?CacheInterface $cache;
    private LoggerInterface $logger;
    private RequestStack $requestStack;
    private ?SessionInterface $session = null;
    private string $baseUrl;

    private const CACHE_TTL = 300;
    private const MAX_RETRIES = 3;

    public function __construct(
        HttpClientInterface $httpClient,
        ?CacheInterface $cache,
        LoggerInterface $logger,
        RequestStack $requestStack,
        string $apiBaseUrl = ''
    ) {
        $this->httpClient = $httpClient;
        $this->cache = $cache;
        $this->logger = $logger;
        $this->requestStack = $requestStack;
        $this->baseUrl = $apiBaseUrl ?: ($_SERVER['API_BASE_URL'] ?? ($_ENV['API_BASE_URL'] ?? 'http://localhost:8080/api'));
    }

    private function getSession(): ?SessionInterface
    {
        if ($this->session === null) {
            $this->session = $this->requestStack->getSession();
        }
        return $this->session;
    }

    private function getAuthHeaders(): array
    {
        $headers = [
            'Accept' => 'application/json',
            'Content-Type' => 'application/json',
            'X-Correlation-Id' => uniqid('mfn-', true),
        ];

        $session = $this->getSession();
        if ($session && $session->has('jwt_token')) {
            $headers['Authorization'] = 'Bearer ' . $session->get('jwt_token');
        }

        return $headers;
    }

    public function get(string $path, array $params = []): ResponseInterface
    {
        $url = $this->buildUrl($path, $params);
        return $this->request('GET', $url, [
            'headers' => $this->getAuthHeaders(),
        ]);
    }

    public function post(string $path, array $data = []): ResponseInterface
    {
        return $this->request('POST', $path, [
            'json' => $data,
            'headers' => array_merge($this->getAuthHeaders(), [
                'X-Idempotency-Key' => uniqid('idem-', true),
            ]),
        ]);
    }

    public function put(string $path, array $data = []): ResponseInterface
    {
        return $this->request('PUT', $path, [
            'json' => $data,
            'headers' => $this->getAuthHeaders(),
        ]);
    }

    public function delete(string $path): ResponseInterface
    {
        return $this->request('DELETE', $path, [
            'headers' => $this->getAuthHeaders(),
        ]);
    }

    public function login(string $username, string $password): array
    {
        $response = $this->request('POST', $this->baseUrl . '/utilisateurs/login', [
            'json' => [
                'login' => $username,
                'motDePasse' => $password,
            ],
            'headers' => [
                'Accept' => 'application/json',
                'Content-Type' => 'application/json',
                'X-Correlation-Id' => uniqid('mfn-', true),
            ],
        ]);

        $data = $this->parseJsonResponse($response);
        if (($response->getStatusCode() ?? 500) < 400) {
            $this->persistAuthentication($data);
        }

        return $data;
    }

    public function loginOtp(string $username, string $challenge, string $otp): array
    {
        $response = $this->request('POST', $this->baseUrl . '/utilisateurs/login/otp', [
            'json' => [
                'login' => $username,
                'challengeId' => $challenge,
                'codeOtp' => $otp,
            ],
            'headers' => [
                'Accept' => 'application/json',
                'Content-Type' => 'application/json',
                'X-Correlation-Id' => uniqid('mfn-', true),
            ],
        ]);

        $data = $this->parseJsonResponse($response);
        if (($response->getStatusCode() ?? 500) < 400) {
            $this->persistAuthentication($data);
        }

        return $data;
    }

    public function refreshToken(): ?array
    {
        $session = $this->getSession();
        if (!$session || !$session->has('refresh_token')) {
            return null;
        }

        try {
            $response = $this->request('POST', $this->baseUrl . '/auth/refresh', [
                'json' => ['refreshToken' => $session->get('refresh_token')],
                'headers' => [
                    'Accept' => 'application/json',
                    'Content-Type' => 'application/json',
                ],
            ]);

            $data = $response->toArray();
            
            if (isset($data['token'])) {
                $session->set('jwt_token', $data['token']);
                if (isset($data['refreshToken'])) {
                    $session->set('refresh_token', $data['refreshToken']);
                }
            }

            return $data;
        } catch (\Exception $e) {
            $this->logger->warning('Token refresh failed: ' . $e->getMessage());
            return null;
        }
    }

    public function logout(): void
    {
        $session = $this->getSession();
        if ($session) {
            try {
                $this->request('POST', $this->baseUrl . '/utilisateurs/logout', [
                    'headers' => $this->getAuthHeaders(),
                ]);
            } catch (\Exception $e) {
                $this->logger->warning('Logout API call failed: ' . $e->getMessage());
            }
            $session->clear();
        }
    }

    public function getCached(string $path, array $params = [], int $ttl = self::CACHE_TTL): array
    {
        if (!$this->cache) {
            return $this->get($path, $params)->toArray();
        }

        $cacheKey = 'api_' . md5($path . serialize($params));

        return $this->cache->get($cacheKey, function () use ($path, $params) {
            return $this->get($path, $params)->toArray();
        }, $ttl);
    }

    public function invalidateCache(string $pathPrefix = ''): void
    {
        $this->logger->info('Cache invalidation requested for: ' . ($pathPrefix ?: 'all'));
    }

    private function request(string $method, string $url, array $options = []): ResponseInterface
    {
        if (!str_starts_with($url, 'http')) {
            $url = $this->baseUrl . $url;
        }

        $retries = 0;
        $lastException = null;

        while ($retries < self::MAX_RETRIES) {
            try {
                $response = $this->httpClient->request($method, $url, $options);
                
                $statusCode = $response->getStatusCode();
                
                if ($statusCode === 401 && $method !== 'POST' && !str_contains($url, '/auth/')) {
                    $refreshResult = $this->refreshToken();
                    if ($refreshResult) {
                        $options['headers']['Authorization'] = 'Bearer ' . ($this->getSession() ? $this->getSession()->get('jwt_token') : '');
                        $response = $this->httpClient->request($method, $url, $options);
                    }
                }

                return $response;
            } catch (\Exception $e) {
                $lastException = $e;
                $retries++;
                if ($retries < self::MAX_RETRIES) {
                    usleep(100000 * $retries);
                }
            }
        }

        throw $lastException ?? new \RuntimeException('API request failed after ' . self::MAX_RETRIES . ' retries');
    }

    private function buildUrl(string $path, array $params = []): string
    {
        $url = $path;
        if (!empty($params)) {
            $query = http_build_query($params);
            $url .= (str_contains($path, '?') ? '&' : '?') . $query;
        }
        return $url;
    }

    private function parseJsonResponse(ResponseInterface $response): array
    {
        $content = $response->getContent(false);
        if ($content === '') {
            return [];
        }

        try {
            $data = json_decode($content, true, 512, JSON_THROW_ON_ERROR);
        } catch (\JsonException $exception) {
            $this->logger->warning('API response is not valid JSON.', [
                'status' => $response->getStatusCode(),
                'body' => $content,
                'error' => $exception->getMessage(),
            ]);

            return [
                'message' => 'Réponse API invalide.',
            ];
        }

        return is_array($data) ? $data : [];
    }

    private function persistAuthentication(array $data): void
    {
        if (!isset($data['token'])) {
            return;
        }

        $session = $this->getSession();
        if (!$session) {
            return;
        }

        $session->set('jwt_token', $data['token']);
        $session->remove('refresh_token');

        $userData = [];
        if (isset($data['utilisateur']) && is_array($data['utilisateur'])) {
            $userData = $this->normalizeUserData($data['utilisateur']);
        }

        $session->set('user', $userData);
    }

    private function normalizeUserData(array $userData): array
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
        $userData['username'] = $userData['login'] ?? ($userData['username'] ?? '');

        if (!isset($userData['nom']) && isset($userData['login'])) {
            $userData['nom'] = $userData['login'];
        }

        return $userData;
    }
}
