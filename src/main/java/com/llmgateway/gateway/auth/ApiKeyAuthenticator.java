package com.llmgateway.gateway.auth;

import com.llmgateway.admin.ApiKeyDirectory;
import com.llmgateway.admin.ApiKeyIdentity;
import com.llmgateway.shared.ApiKeyFormat;
import com.llmgateway.shared.BlockingJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ApiKeyAuthenticator {

//    used to log the error of this class regarding
    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticator.class);

    private final AuthCache cache ;
    private final ApiKeyDirectory directory;
    private final BlockingJdbc blocking;
    private final Clock clock;
    private final ConcurrentMap<String, Mono<Optional<ApiKeyIdentity>>> inFlight =
            new ConcurrentHashMap<>();

    ApiKeyAuthenticator(
            AuthCache cache , ApiKeyDirectory directory , BlockingJdbc blocking, Clock clock
    ){
        this.cache = cache;
        this.directory = directory;
        this.clock = clock;
        this.blocking = blocking;
    }

    Mono<AuthResult> authenticate (String token){
        if(!ApiKeyFormat.isWellFormed(token)){
            return Mono.just(Authresult.invalidKey());
        }
        String keyHash = ApiKeyFormat.sha256hex(token);
        return
    }

    //checks in redis and
    private Mono<Optional<ApiKeyFormat>> resolve(String keyHash){
        return cache.get(keyHash).
    }
}
