package com.llmgateway.admin.config;

import ch.qos.logback.core.joran.spi.DefaultClass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/** Validated at startup: a misconfigured service refuses to boot instead of failing later. */
@Validated
@ConfigurationProperties(prefix = "admin")
public record AdminProperties(
        /* Bearer token protecting the admin API. Interim: replaced by real auth with the dashboard. */
        @NotBlank @Size(min = 24, message = "must be at least 24 characters") String apiToken,
        @Valid @DefaultValue ApiKeyLimits apiKeys) {

    public record ApiKeyLimits(@DefaultValue("25") @Min(1) @Max(1000) int maxActivePerTenant) {}

    /** Never print the token, even if this record ends up in a log line. */
    @Override
    public String toString() {
        return "AdminProperties[apiToken=***, apiKeys=" + apiKeys + "]";
    }
}

