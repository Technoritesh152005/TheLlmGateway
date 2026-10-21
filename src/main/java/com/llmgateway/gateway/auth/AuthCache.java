package com.llmgateway.gateway.auth;

import com.llmgateway.admin.ApiKeyIdentity;
import com.llmgateway.shared.ApiKeyFormat;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.w3c.dom.css.Counter;
import reactor.core.publisher.Mono;
import reactor.netty.resources.ConnectionProvider;

import java.util.Optional;
import java.util.logging.Logger;

@Component
public class AuthCache {

    private static final Logger log = LoggerFactory.getLogger(AuthCache.class);
    private static final String KEY_PREFIX = "gw:auth:v1:";

    //the below is a way that lets java application communicate with redis async using strings
    private final ReactiveStringRedisTemplate redis;
    private final GatewayAuthProperties properties;
    //helpd monitor metrics when scope gets over
    private final Counter hits;
    private final Counter misses;
    private final Counter negativeHits;
    private final Counter errors;

    AuthCache(ReactiveStringRedisTemplate redis , GateayAuthProperties properties,
              MeterRegistry registry
              ){

        this.redis = redis;
        this.properties = properties;
        this.hits = counter(registry, "hit");
        this.negativeHits = counter(registry , "negative_hit");
        this.misses = counter(registry, "miss");
        this.errors = counter(registry,"error");
    }

    Mono<Optional<ApiKeyFormat>> get(String keyHash){
        //check in hash whether this hashkey exist in redis. if found return the string
        return redis.opsForValue().get(KEY_PREFIX+keyHash)
                .flatMap(
                        raw -> {
                            //take the raw data got ferom  redias and convert in java object
                            //u could have used map but map return normal value where as flatMap can return reactive elements
                            try{
                                Optional<ApiKeyIdentity> decoded = CacheCodec.decode(raw);
                                if(decoded.isPresent()){
                                    hits.increment();
                                }else negativeHits.increment();
                                return Mono.just(decoded);
                            }catch(IllegalArgumentException e){
                            log.warn("Ignoring unreadable auth cache entry from redis or not able to decode cache raw data of gateway auth");
                            return Mono.empty();
                            }
                        }
                ).switchIfEmpty(
                        Mono.defer(()->
                        {
                            misses.increment();
                            return Mono.empty();
                        })
                ).onErrorResume(
                        e->{
                            errors.increment();
                            log.warn("Auth cache read failed, falling back to database: {}", e.toString());
                            return Mono.empty();
                        }
                );
    }


}
