package com.llmgateway.admin.apikey;

import com.llmgateway.shared.RateLimitTier;
import java.time.Instant;
import java.util.UUID;
public record CreatedApiKeyResponse(
        UUID id,
        UUID tenantId,
        String name,
        String keyPrefix,
        ApiKeyStatus status,
        RateLimitTier rateLimitTier,
        Instant expiresAt,
        Instant lastUsedAt,
        Instant createdAt,
        Instant revokedAt
){

    static CreatedApiKeyResponse from (CreatedApiKey created){
        //constructor call kela ApiKeyResponse cha
        ApiKey k = created.apiKey();
        return new CreatedApiKeyResponse(
                k.id(),
                k.tenantId(),
                k.name(),
                k.keyPrefix(),
                k.status(),
                k.rateLimitTier(),
                k.expiresAt(),
                k.lastUsedAt(),
                k.createdAt(),
                k.revokedAt()
        );
    }
    @Override
    public String toString() {
        return "CreatedApiKeyResponse[id=" + id + ", keyPrefix=" + keyPrefix + ", key=***]";
    }

}
