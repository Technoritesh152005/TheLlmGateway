package com.llmgateway.admin.apikey;

import com.llmgateway.shared.RateLimitTier;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateApiKeyRequest (
       @NotBlank @Size(max=20) String name,
       //future makes sure the data is of future.
       @Future Instant expiresAt,
       RateLimitTier rateLimitTier
){
}
