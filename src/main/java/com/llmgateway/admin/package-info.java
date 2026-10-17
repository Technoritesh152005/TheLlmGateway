/**
 * Admin module: tenants, API keys and (later) routing and guardrail configuration.
 *
 * <p>Public API = the types in this package (e.g. {@link com.llmgateway.admin.ApiKeyRevokedEvent}).
 * Everything in sub-packages is internal and cannot be used by other modules.
 */
@ApplicationModule(displayName = "Admin", allowedDependencies = "shared")
package com.llmgateway.admin;

import org.springframework.modulith.ApplicationModule;
