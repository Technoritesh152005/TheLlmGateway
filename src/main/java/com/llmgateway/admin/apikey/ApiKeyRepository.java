package com.llmgateway.admin.apikey;

import com.llmgateway.admin.ApiKeyIdentity;
import com.llmgateway.admin.tenant.TenantStatus;
import com.llmgateway.shared.RateLimitTier;
import com.llmgateway.admin.persistence.Rows;

import javax.swing.tree.RowMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ApiKeyRepository {
    private static final String COLUMNS = "id, tenant_id, name, key_prefix, status, rate_limit_tier, "
            + "expires_at, last_used_at, created_at, revoked_at";

    //when u used {} u need to explicitly pass return
    private static final RowMapper<ApiKey> mapper  =
            (rs , rowNum)->{
        String tier = rs.getString("rate_limit_tier");
        return new ApiKey(
                rs.getObject("id", UUID.class),
                rs.getObject("tenant_id", UUID.class),
                rs.getString("name"),
                rs.getString("key_prefix"),
                ApikeyStatus.valueOf(rs.getString("status")),
                tier == null? null : RateLimitTier.valueOf(tier),
                Rows.Instant(rs,"expires_at"),
                Rows.instant(rs, "last_used_at"),
                Rows.instant(rs, "created_at"),
                Rows.instant(rs, "revoked_at")
        );
    };

    private static final RowMapper<ApiKeyIdentity> identity_Mapper =
            (rs,rowNum)->{
        return new ApiKeyIdentity(
             rs.getObject("id", UUID.class),
             rs.getObjct("tenant_id",UUID.class),
             RateLimitTier.valueOf(rs.getString("tier")),
                ApiKeyStatus.valueOf(rs.getString("key_status"))==ApiKeyStatus.REVOKED,
                TenantStatus.valueOf(rs.getString("tenant_status"))== TenantStatus.SUSPENDED,
                Rows.instant(rs,"expires_at")
        );
            };

    private final JdbcClient jdbc;
    ApiKeyRepository(JdbcClient j){
        this.jdbc = j;
    }

    ApiKey insert(
            UUID tenantId,
            String name,
            String keyPrefix,
            String keyHash,
            RateLimitTier tier,
            Instant expiresAt
    ){

        return jdbc.sql(
                "INSERT INTO api_keys"
                +" (tenant_id, name , key_prefix , key_hash , rate_limit_tier, expiresAt)"
                +" VALUES(:tenant_id , :name , :keyPrefix , keyHash, "
                +"CAST(:tier AS VARCHAR), CAST(:expiresAt AS TIMESTAMPTZ)) "
                +"RETURNING "
                +COLUMNS)
                .param("tenant_id", tenantId)
                .param("name", name)
                .param("keyPrefix",keyPrefix)
                .param("keyHash",keyHash)
                .param("tier", tier == null? null : tier.name())
                .param(
                        "expiresAt",
                        expiresAt == null ? null : OffsetDateTime.ofInstant(expiresAt, ZoneOffset.UTC))
                .query(mapper)
                .single();

    }

    //if both apikey have same timestamp then get it by id
    List<ApiKey> findByTenant(UUID tenantId){
        return jdbc.sql(
                "SELECT "+ COLUMNS +" FROM api_keys WHERE tenant_id = :tenantId ORDER BY created_at DESC, id"
        ).param("tenantId", tenantId)
                .query(mapper)
                .list();
    }

    //gives a count of apikey which r active
//    .query tells db how to return object
    int countUsable(UUID tenantId){
        return jdbc.sql(
                "SELECT count(*) FROM api_keys where tenant_id = :tenantId "
                +"AND status ='ACTIVE' AND (expires_at IS NULL OR expires_at > now()) "
        ).param("tenantId", tenantId)
                .query(Integer.class)
                .single();
    }

    //Revoking the apiKey
    Optional<String> revoke(UUID tenantId, UUID keyId){
        return jdbc.sql(
                "UPDATE api_keys WHERE SET status = 'REVOKED', revoked_at = now() WHERE tenant_id = :tenantId AND id = :keyId AND status = 'ACTIVE' + RETURNING key_hash"
        ).param("keyId", keyId)
                .param("tenantId", tenantId)
                .query(String.class)
                .optional();
    }

//    SELECT EXIST is a query which takes a task and check and return boolean value if founf=d
    Boolean exists(UUID tenantId ,UUID  keyId){
        return jdbc.sql(
                "SELECT EXISTS (SELECT 1 from api_keys WHERE id = :id AND tenant_id = :tenantId)"
        ).param("id", keyId)
                .param("tenantId", tenantId)
                .query(Boolean.class)
                .single();
    }

    //here u give keyhash and takes the infoo from tenant also to authenticate urself
    Optional<ApiKeyIdentity> findIdentityByHash(String keyHash){
        return jdbc.sql(
                    """
                        SELECT k.id, k.tenant_id , k.status AS key_status , k.expiresAt,
                        COALESCE(k.rate_limit_tier, t.rate_limit_tier) AS tier,
                        t.status AS tenant_status
                        FROM api_keys k
                        JOIN tenants t ON t.id = k.tenant_id
                        WHERE k.key_hash = :keyHash
                        """
        ).param("keyHash",keyHash)
                .query(identity_Mapper)
                .optional();

    }
}
