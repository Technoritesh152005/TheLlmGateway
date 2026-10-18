package com.llmgateway.admin;

import com.llmgateway.shared.RateLimitTier;

import java.util.UUID;
// import com.llmgateway.shared.RateLimitTier;
import java.time.Instant;


//record is a predefined class which is used to store data
//avoids boilerplat and it is immutable->not able to change state when created
public record ApiKeyIdentity(
    UUID keyid,
    UUID tenantId,
     RateLimitTier tier,
    boolean Revoked,
    boolean tenantSuspended,
    Instant expiresAt
){}
