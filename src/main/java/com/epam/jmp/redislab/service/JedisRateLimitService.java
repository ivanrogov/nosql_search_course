package com.epam.jmp.redislab.service;


import com.epam.jmp.redislab.api.RequestDescriptor;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitRule;
import io.github.bucket4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitTimeInterval;
import io.github.bucket4j.distributed.proxy.ProxyManager;


import java.time.Duration;

import java.util.*;
import java.util.function.Supplier;

@Service
public class JedisRateLimitService implements RateLimitService {

    @Autowired
    private Set<RateLimitRule> rateLimitRules;

    @Autowired
    private ProxyManager proxyManager;


    @Override
    public boolean shouldLimit(Set<RequestDescriptor> requestDescriptors) {
        // Iterate over the descriptors and check against rate limiting.
        for (RequestDescriptor descriptor : requestDescriptors) {
            String redisKey = generateRedisKey(descriptor);

            // Match the descriptor to a rate limit rule.
            RateLimitRule matchingRule = findMatchingRule(descriptor);
            if (matchingRule == null) {
                // No matching rule, skip this descriptor.
                continue;
            }

            // Create or get the bucket for rate limiting.
            Bucket bucket = createBucket(redisKey, matchingRule);

            // Consume one token from the bucket. If not allowed, return true (rate limit violated).
            if (bucket.tryConsume(1)) {
                continue; // Allow the request for this descriptor.
            } else {
                return true; // Rate limit reached.
            }
        }

        // No descriptor was blocked for rate limiting.
        return false;
    }

    private RateLimitRule findMatchingRule(RequestDescriptor descriptor) {
        // Implement logic to find the matching rule.
        // This stub assumes `rateLimitRules` is a preloaded map of rules.

        List<RateLimitRule> result = new ArrayList();
        for (RateLimitRule rule : rateLimitRules)
        {

            if (matchRule(descriptor, rule))
            {
                result.add(rule);
            }
        }

        if (result.isEmpty()) return null;

        Collections.sort(result, (t0, t1) -> {
            if (t0.getAccountId().isPresent() && !t0.getAccountId().get().isEmpty()) {
                return -1;
            }
            if (t1.getAccountId().isPresent() && !t1.getAccountId().get().isEmpty()) {
                return 1;
            }
            return 0;
        });
        return result.get(0);
    }

    private boolean matchRule(RequestDescriptor descriptor, RateLimitRule rule) {
        // Match logic for rate limiting. Expects at least one non-empty field to match.
        if (rule.getAccountId().isPresent() && descriptor.getAccountId().isPresent()
                && rule.getAccountId().get().equals(descriptor.getAccountId().get())) {
            return true;
        }
        if (rule.getClientIp().isPresent() && descriptor.getClientIp().isPresent()
                && rule.getClientIp().get().equals(descriptor.getClientIp().get())) {
            return true;
        }
        if (rule.getRequestType().isPresent() && descriptor.getRequestType().isPresent()
                && rule.getRequestType().get().equals(descriptor.getRequestType().get())) {
            return true;
        }
        boolean accountIdIndeedEmpty = rule.getAccountId().isEmpty() || rule.getAccountId().get().isEmpty();
        return rule.getClientIp().isEmpty() && accountIdIndeedEmpty && rule.getRequestType().isEmpty();
    }


    private Bucket createBucket(String redisKey, RateLimitRule rule) {
        Supplier<BucketConfiguration> configSupplier = getConfigSupplierForUser(rule);
        return proxyManager.builder().build(redisKey, configSupplier);

    }

    private Supplier<BucketConfiguration> getConfigSupplierForUser(RateLimitRule rule) {
        // Convert rule's timeInterval to Duration.
        Duration interval = convertToDuration(rule.getRateLimitTimeInterval());

        // Create a bandwidth limit based on the rule.
        Refill refill = Refill.intervally(rule.getAllowedNumberOfRequests(), interval);
        Bandwidth limit = Bandwidth.classic(rule.getAllowedNumberOfRequests(), refill);

        return () -> (BucketConfiguration.builder()
                .addLimit(limit)
                .build());
    }

    private Duration convertToDuration(RateLimitTimeInterval interval) {
        switch (interval) {
            case MINUTE:
                return Duration.ofMinutes(1);
            case HOUR:
                return Duration.ofHours(1);
            default:
                throw new IllegalArgumentException("Unsupported timeInterval: " + interval);
        }
    }

    private String generateRedisKey(RequestDescriptor descriptor) {
        return "rate_limit:" + descriptor.getAccountId().orElse("any") + ":" +
                descriptor.getClientIp().orElse("any") + ":" +
                descriptor.getRequestType().orElse("any");
    }
}