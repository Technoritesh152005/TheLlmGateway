package com.llmgateway.admin.tenant;

import com.llmgateway.admin.error.ConflictException;
import com.llmgateway.admin.error.ResourceNotFoundException;
import com.llmgateway.shared.RateLimitTier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.web.servlet.function.ServerResponse.notFound;

@Service
public class TenantService {

    private final TenantRepository repository ;

    TenantService(TenantRepository repo){
        this.repository = repo;
    }

    @Transactional
    public Tenant create(String name , String slug, RateLimitTier tier){
        try{
        return repository.insert(name.strip(), slug, tier == null? RateLimitTier.FREE : tier);
        }catch(DuplicateKeyException e){
            throw new ConflictException("A tenant with slug "+slug+" already exist");
        }
    }

    //this tells in persistence context dont try to create copy in persistence contex. im not updating anything, which skips dirty checking->making optimization
    @Transactional(reaoOnly =true)
    public Tenant get(UUID id) {
        return repository.findById(id).orElseThrow(()-> notFound(id));
    }

    @Transactional(readOnly =true)
    public List<Tenant> list(int limit, int offset){
        return repository.findAll(limit,offset);
    }

    private static ResourceNotFoundException notFound(UUID id){
        throw new ResourceNotFoundException("tenant "+id +" not found");
    }

    //outer method or who is calling must already have a transaction actively running
    @Transactional(propagation = Propagation.MANDATORY)
    public Tenant lockActiveTenant(UUID id){
        Tenant tenant = repository.findByIdForUpdate(id).orElseThrow(()-> notFound(id));
        if(tenant.status() != TenantStaus.ACTIVE){
            throw new ConflictException("tenant "+id+" is not active");
        }
        return tenant;
    }


}
