/**
 * Gateway module: the data plane. Authenticates callers by gateway API key and (from task 1.5)
 * serves the unified, OpenAI-compatible API.
 *
 * <p>Public API = the types in this package (e.g. {@link com.llmgateway.gateway.GatewayPrincipal}).
 * Sub-packages are internal.
 */
@ApplicationModule(displayName = "Gateway", allowedDependencies = {"admin", "shared"})
package com.llmgateway.gateway;

import org.springframework.modulith.ApplicationModule;
