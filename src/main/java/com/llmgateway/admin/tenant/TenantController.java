package com.llmgateway.admin.tenant;

import com.llmgateway.shared.BlockingJdbc;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

//tells spring this a file which handles http request and convert teh object response into JSOn data
@RestController
@RequestMapping("/admin/v1/tenants")
//create is a special method in responsentity which tells the location u need to go and also sets status as 201 http status get
public class TenantController {

    private static final int MAX_PAGE_SIZE = 200;

    private final TenantService service;
    private final BlockingJdbc blocking ;

    public TenantController(TenantService service, BlockingJdbc blocking) {
        this.service = service;
        this.blocking = blocking;
    }

    @PostMapping
    Mono<ResponseEntity<TenantResponse>> create(@Valid @RequestBody CreateTenantRequest request){
        return blocking.call(
                ()-> service.create(request.name(), request.slug(), request.rateLimitTier())
        ).map(
                tenant -> ResponseEntity.created(URI.create("/admin/v1/tenants/"+tenant.id()))
                        .body(TenantResponse.from(tenant))
        );
    }

    @GetMapping("/{tenantId}")
    Mono<Tenant> get(@PathVariable UUID tenantId){
        return blocking.call(()->service.get(id)).map( tenant -> TenantResponse.from(tenant));
    }

    @GetMapping
    Mono<List<TenantResponse>> list(@RequestParam(defaultValue = "50") int limit , @RequestParam(defaultValue = "0") int offset){
        int boundedLimit = Math.min(MAX_PAGE_SIZE, Math.max(1,limit));
        int boundedOffset = Math.max(offset,0);
        return blocking.call(()-> service.list(boundedLimit,boundedOffset))
                .map(tenants -> tenants.stream().map(tenant->TenantResponse.from(tenant).toList()));
    }
}
