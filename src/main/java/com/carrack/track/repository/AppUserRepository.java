package com.carrack.track.repository;

import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.entity.AppUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByStatus(AccountStatus status);

    long countByRole(Role role);

    long countByCreatedAtAfter(LocalDateTime createdAt);

    long countByStatusNot(AccountStatus status);

    long countByRoleAndStatusNot(Role role, AccountStatus status);

    List<AppUser> findTop5ByStatusNotOrderByCreatedAtDesc(AccountStatus status);
}
