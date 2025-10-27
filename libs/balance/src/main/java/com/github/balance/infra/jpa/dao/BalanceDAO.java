package com.github.balance.infra.jpa.dao;

import com.github.balance.infra.jpa.entity.BalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceDAO extends JpaRepository<BalanceEntity, Long> {

    @Query(
            value = """ 
                    SELECT *
                    FROM balance
                    WHERE balance.account_id = :account_id
                """,
            nativeQuery = true
    )
    BalanceEntity checkBalance(@Param("accountId") UUID accountId);

}
