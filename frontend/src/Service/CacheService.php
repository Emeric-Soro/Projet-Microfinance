<?php

namespace App\Service;

use Symfony\Contracts\Cache\CacheInterface;

class CacheService
{
    private CacheInterface $cache;

    public function __construct(CacheInterface $cache)
    {
        $this->cache = $cache;
    }

    public function get(string $key, callable $callback, int $ttl = 3600): mixed
    {
        return $this->cache->get($key, $callback, $ttl);
    }

    public function delete(string $key): bool
    {
        return $this->cache->delete($key);
    }

    public function clear(): bool
    {
        return $this->cache->clear();
    }
}
