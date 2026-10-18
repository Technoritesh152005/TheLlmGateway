package com.llmgateway.admin.tenant;

import com.llmgateway.shared.RateLimitTier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank
        @Pattern(
                regexp = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$",
                message = "must be 3-63 chars of lowercase letters, digits or hyphens")
        String slug,
        /* Optional; defaults to FREE. */ RateLimitTier rateLimitTier) {}
// TenantController → CreateTenantRequest / TenantResponse
//                 → TenantService
//                 → Tenant / TenantStatus
//                 → TenantRepository
