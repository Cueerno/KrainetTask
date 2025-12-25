package com.radiuk.auth_service.repository;

import com.radiuk.auth_service.model.RefreshToken;
import com.radiuk.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByJtiAndRevokedFalse(String jti);

    void deleteAllByUser(User user);

    @Modifying
    @Query("update RefreshToken t set t.revoked = true where t.jti = :jti")
    void revokeByJti(@Param("jti") String jti);

    @Modifying
    @Query("update RefreshToken t set t.revoked = true where t.user = :user")
    void revokeAllByUser(@Param("user") User user);
}
