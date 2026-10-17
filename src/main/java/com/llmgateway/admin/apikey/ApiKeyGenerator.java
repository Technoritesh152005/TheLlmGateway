package com.llmgateway.admin.apikey;

import java.security.SecureRandom;
import com.llmgateway.shared.ApiKeyFormat;
import org.springframework.stereotype.Component;

//makes the beans of it
@Component
public class ApiKeyGenerator {
    
    private static final char [] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
   
    private final SecureRandom random = new SecureRandom();

    GeneratedApiKey generate(){
        char[]secret = new char[ApiKeyFormat.SECRET_LENGTH];
        for(int i = 0 ; i < secret.length ; i++){
            secret[i] = alphabet[random.nextInt(alphabet.length)];
        }
        //passing it to constructor
        String plainText = ApiKeyFormat.PREFIX + new String(secret);

        return new GeneratedApiKey(plainText, ApiKeyFormat.displayPrefix(plainText),ApiKeyFormat.sha256hex(plainText) );
    }
}
