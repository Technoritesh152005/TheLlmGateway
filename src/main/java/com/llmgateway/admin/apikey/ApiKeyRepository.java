package com.llmgateway.admin.apikey;

import com.llmgateway.admin.ApiKeyIdentity;
import com.llmgateway.admin.tenant.TenantStatus;
import com.llmgateway.shared.RateLimitTier;
import com.llmgateway.admin.persistence.Rows;

import javax.swing.tree.RowMapper;
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
    

}
