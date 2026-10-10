package com.llmgateway.admin;

import java.util.Optional;

// interface means defines a rule and any class implementing is depend on him how it implement
public interface ApiKeyDirectory{

    //optional means of this type it may return value or may not
    Optional<ApiKeyIdentity> findByHash(String keyHash);
}


