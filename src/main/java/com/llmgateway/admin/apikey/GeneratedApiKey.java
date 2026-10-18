package com.llmgateway.admin.apikey;

record GeneratedApiKey(String plainText, String prefix, String hash) {

    @Override
    public String toString() {
        return "GeneratedApiKey[prefix=" + prefix + ", hash=" + hash + "]";
    }
}
