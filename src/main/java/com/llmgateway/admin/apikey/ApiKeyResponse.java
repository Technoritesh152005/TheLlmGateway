package com.llmgateway.admin.apikey;

import com.llmgateway.shared.RateLimitTier;
import java.time.Instant;
import java.util.UUID;

/** Safe-to-list view of a key: no hash, no secret. */
public record ApiKeyResponse(
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

    static ApiKeyResponse from(ApiKey k) {
        return new ApiKeyResponse(
                k.id(),
                k.tenantId(),
                k.name(),
                k.keyPrefix(),
                k.status(),
                k.rateLimitTier(),
                k.expiresAt(),
                k.lastUsedAt(),
                k.createdAt(),
                k.revokedAt());
    }
}
