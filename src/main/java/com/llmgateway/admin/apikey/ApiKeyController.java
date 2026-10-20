package com.llmgateway.admin.apikey;

import com.llmgateway.shared.BlockingJdbc;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/v1/tenants/{tenantId}/api-keys")
public class ApiKeyController {
    private  final ApiKeyService service;
    private  final BlockingJdbc blocking;
    public ApiKeyController(ApiKeyService service, BlockingJdbc jdbc) {
        this.service = service;
        this.blocking = jdbc;
    }

    @PostMapping
    Mono<ResponseEntity<CreatedApiKeyResponse>> create(
//            // let the request pass through this dto
            @PathVariable UUID tenantId, @Valid @RequestBody CreateApiKeyRequest request
    ){
    return blocking.call(
            ()-> service.create(tenantId, request.name(), request.rateLimitTier(),request.expiresAt() )
    ).map(created ->
            ResponseEntity.created(URI.create("/admin/v1/tenants" +tenantId+"/api-keys/"+created.apiKey().id()))
                    .header(HttpHeaders.CACHE_CONTROL,"no-store")
                    .body(CreatedApiKeyResponse.from(created))
            );
    }

    @GetMapping
    Mono<List<ApiKeyResponse>> list(@PathVariable UUID tenantId) {
        return blocking
                .call(() -> service.list(tenantId))
                .map(keys -> keys.stream().map(key -> ApiKeyResponse.from(key)).toList());
    }

    @PostMapping("/{keyId}/revoke")
    Mono<ResponseEntity<Void>>revoke(@PathVariable UUID tenantId , @PathVariable UUID keyId){
        return blocking.run(()->service.revoke(tenantId, keyId))
                //revoke succeed but with 204 status code->no content found means only return status code no body
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }

}


//we use blocking jdbc cayse jdbc block thread when db query is running and thread wiats or freeze until response is not get
//so we pass a piece of code  in lambda which states execute tjis service in another thread . dont block it