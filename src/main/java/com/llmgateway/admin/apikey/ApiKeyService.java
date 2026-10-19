package com.llmgateway.admin.apikey;

import com.llmgateway.admin.config.AdminProperties;
import com.llmgateway.admin.error.ConflictException;
import com.llmgateway.admin.error.ResourceNotFoundException;
import com.llmgateway.admin.tenant.TenantService;
import com.llmgateway.shared.RateLimitTier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repository;
    private final TenantService tenants;
    private final ApiKeyGenerator generator;
    private final int maxUsableKeysPerEvent;

    public ApiKeyService(ApiKeyRepository repository, TenantService tenants, ApiKeyGenerator generator, AdminProperties admin) {
        this.repository = repository;
        this.tenants = tenants;
        this.generator = generator;
        this.maxUsableKeysPerEvent = admin.apiKeys().maxActivePerTenant();
    }

    @Transactional
   CreatedApiKey create(
           UUID tenantId , String name , RateLimitTier tier , Instant expiresAt
   ){

        //lock that row in tenant
       tenants.lockActiveTenant(tenantId);

       if(repository.countUsable(tenantId) >= maxUsableKeysPerEvent){
           throw new ConflictException("Tenant already has a maximum of "+maxUsableKeysPerEvent+" usable APi keys. Revoke one first");
       }

       GeneratedApiKey result =  generator.generate();
       ApiKey storeddata =repository.insert(tenantId , name.strip(), result.prefix(), result.hash(), tier, expiresAt);
       return new CreatedApiKey(storeddata, result.plainText());

   }

   @Transactional
   List<ApiKey> list(UUID tenantId){
        //it onlyc checks whether this resource exist and handles exception or throws it
    tenants.get(tenantId);
    return repository.findByTenant((tenantId));

   }

  public void  revoke(UUID tenantId , UUID keyId){
       Optional<String> revokedData = repository.revoke(tenantId,keyId);
       //after revoking the key just inform other parts of spplication by releasing this event saying dont use this apiKey its been revoked

        if(revokedData.isPresent()){
            events.publishEvent(new ApiKeyRevokedEvent(tenantId,keyId,revokedData));
            return ;
        }
        //if not got any hash means apikey not found
       if(!repository.exists(tenantId,keyId)){
           throw new ResourceNotFoundException("API KEY "+keyId +" Not Found");
       }
   }

}
