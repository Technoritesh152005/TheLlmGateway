package com.llmgateway.admin.tenant;

import com.llmgateway.shared.RateLimitTier;

import java.time.Instant;
import java.util.UUID;

public record TenantResponse (
        UUID id,
        String name,
        String slug,
        TenantStatus status ,
        RateLimitTier tier,
        Instant createdAt,
        Instant updatedAt
) {

    static TenantResponse from(Tenant t){
       return new TenantResponse(
               t.id(), t.name(),t.slug(), t.status(), t.tier(), t.createdAt(), t.updatedAt()
       );
    }


}
