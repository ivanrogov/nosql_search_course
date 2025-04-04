package com.epam.jmp.redislab.configuration;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import redis.clients.jedis.HostAndPort;
//import redis.clients.jedis.JedisCluster;

import io.github.bucket4j.BucketConfiguration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisConfiguration {

    /*@Bean
    // https://www.baeldung.com/jedis-java-redis-client-library
    // https://stackoverflow.com/questions/30078034/redis-cluster-in-multiple-threads
    public JedisCluster jedisCluster() {
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", 30000));
        JedisCluster jedis = new JedisCluster(jedisClusterNodes);
        return jedis;
    }*/

    @Bean
    public Config config() {
        Config config = new Config();
        config.useClusterServers()
                .addNodeAddress("redis://127.0.0.1:30000", "redis://127.0.0.1:30001", "redis://127.0.0.1:30002");
        return config;
    }

    @Bean
    public CacheManager cacheManager(Config config) {
        CacheManager manager = null;
        Iterable iterable = Caching.getCachingProviders();
        if (iterable.iterator().hasNext()){
            CachingProvider next = (CachingProvider) iterable.iterator().next();
            manager = next.getCacheManager();

        }
        manager.createCache("cache", RedissonConfiguration.fromConfig(config));
        return manager;
    }

    @Bean
    ProxyManager<String> proxyManager(CacheManager cacheManager) {
        return new JCacheProxyManager<>(cacheManager.getCache("cache"));
    }

}
