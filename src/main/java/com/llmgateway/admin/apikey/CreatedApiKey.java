package com.llmgateway.admin.apikey;

public record CreatedApiKey (
        ApiKey apiKey , String plaintext
) {
    @Override
    public String toString(){
        return "CreatedApiKey[apiKey=" + apiKey + ", plaintextKey=***]";
    }
}
