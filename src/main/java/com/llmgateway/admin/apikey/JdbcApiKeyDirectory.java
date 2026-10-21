package com.llmgateway.admin.apikey;

import com.llmgateway.admin.ApiKeyDirectory;
import com.llmgateway.admin.ApiKeyIdentity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JdbcApiKeyDirectory implements ApiKeyDirectory {

    public JdbcApiKeyDirectory(ApiKeyRepository repo) {
        this.repo = repo;
    }

    private final ApiKeyRepository repo;

    @Override
    public Optional<ApiKeyIdentity> findByHash(String hash){
        return repo.findIdentityByHash(hash);
    }
}
