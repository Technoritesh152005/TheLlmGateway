package com.llmgateway.admin.tenant;

import com.llmgateway.shared.RateLimitTier;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.swing.tree.RowMapper;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;

//This class is responsible for talking directly to our database. It handles all our SQL queries like saving, finding, updating, and deleting data."
@Repository
public class TenantRepository {

    private static final String COLUMNS = "id , slug, name , status , rate_limit_tier , created_at, updated_at";

    //Rowmapper is a interface wich has a method that helps to map the raw data and create a brand new object
    private static final RowMapper<Tenant> mapper = (rs, rowNum)->
            new Tenant(
                    rs.getObject("id", UUID.class),
                    rs.getString("name"),
                    rs.getString("slug"),
                    //as TenantStatus is a enum u cant pass string so .valueof taes string and check enum to transform in type safe object .Actibe
                    TenantStatus.valueOf(rs.getString("status")),
                    RateLimitTier.valueOf(rs.getString("rate_limit_tier")),
                    Rows.instant (rs,"created_at"),
                    Rows.instant(rs, "updated_at")
            );

    private final JdbcClient jdbc = null;

    TenantRepository(JdbcClient jdbc){
        this.jdbc = jdbc;
    }

    Tenant insert(String name , String slug, RateLimitTier tier){
       //after running query it lets u send all the columns newly created data now
        return jdbc.sql(
                "INSERT INTO tenants (name,slug,rate_limit_tier)"
                +"VALUES(:name , :slug, :tier) RETURNING"
                + COLUMNS)
                .params("name",name)
                .param("slug",slug)
                .param("tier", tier.name())
                .query(mapper)
                .single();

    }

    Optional<T> findById(UUID id){
        //selecting all columns ur rowMapper needs all selected data
        return jdbc.sql(
                "SELECT " + COLUMNS +"FROM tenants WHERE id = :id")
                .param("id",id)
                .query(mapper)
                .optional();

    }

    //puts a lock on row
    //for update tells to lock this row. this lock will stau active unitl transaction is executing
    Optional<Tenant> findByIdForUpdate(UUID id){
        return jdbc.sql("SELECT " + COLUMNS + " FROM tenants WHERE id = :id FOR UPDATE")
                .param("id", id)
                .query(mapper)
                .optional();
    }
    
    List<Tenant> findAll(int limit , int offset){
        return jdbc.sql(
                "SELECT "+
                        COLUMNS + " FROM tenants ORDER BY created_at DESC , id LIMIT :limit , OFFSET:offset"
        )
                .param("limit",limit)
                .param("offset",offset)
                .query(mapper)
                .list();
    }
}
