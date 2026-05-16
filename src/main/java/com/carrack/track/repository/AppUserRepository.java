package com.carrack.track.repository;

import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.entity.AppUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
            select u from AppUser u
            where u.status <> com.carrack.track.enums.AccountStatus.DELETED
              and (:status is null or u.status = :status)
              and (:keyword is null
                   or lower(u.fullName) like lower(concat('%', :keyword, '%'))
                   or lower(u.email) like lower(concat('%', :keyword, '%'))
                   or lower(coalesce(u.phone, '')) like lower(concat('%', :keyword, '%')))
            """)
    Page<AppUser> searchUsers(@Param("keyword") String keyword,
                              @Param("status") AccountStatus status,
                              Pageable pageable);

    long countByStatus(AccountStatus status);

    long countByRole(Role role);

    long countByCreatedAtAfter(LocalDateTime createdAt);

    long countByStatusNot(AccountStatus status);

    long countByRoleAndStatusNot(Role role, AccountStatus status);

    List<AppUser> findTop5ByStatusNotOrderByCreatedAtDesc(AccountStatus status);

    List<AppUser> findByRoleAndStatusNotOrderByFullNameAsc(Role role, AccountStatus status);
}
