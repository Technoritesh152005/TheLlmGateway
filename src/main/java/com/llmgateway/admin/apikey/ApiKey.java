package com.llmgateway.admin.apikey;

import com.llmgateway.shared.RateLimitTier;

import java.time.Instant;
import java.util.UUID;

//complete information of apiKey
public record ApiKey(
        UUID id,
        UUID tenantId,
        String name,
        String keyPrefix,
         ApiKeyStatus status,
         RateLimitTier rateLimitTier,
        Instant expiresAt,
        Instant lastUsedAt,
        Instant createdAt,
        Instant revokedAt) {

}
