package com.llmgateway.admin.tenant;

import com.llmgateway.shared.RateLimitTier;

import java.time.Instant;
import java.util.UUID;

public record Tenant(
        UUID id,
        String name,
        String slug,
        TenantStatus status,
        RateLimitTier tier,
        Instant createdAt,
        Instant updatedAt

) {


}
