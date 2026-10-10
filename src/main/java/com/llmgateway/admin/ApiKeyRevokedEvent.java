package com.llmgateway.admin;

import java.util.UUID;
public record ApiKeyRevokedEvent (
    UUID tenantId,
    UUID keyId,
    String keyHash
){

    @Override 
    public String toString(){
        return "ApikeyRevokedEvent[tenantId="+tenantId + ",keyId"+ "]";
    }
}
