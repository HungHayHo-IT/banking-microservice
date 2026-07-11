package com.user_account_service_service.repository;

import com.user_account_service_service.entity.Role;
import com.user_account_service_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    long countByEnabledTrue();

    List<User> findByEnabledTrue();


    //Dem so luong user thong qua role cu the
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    //Lay toan bo user so huu mot role cu the
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRolesContaining(@Param("role") Role role);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);








}
