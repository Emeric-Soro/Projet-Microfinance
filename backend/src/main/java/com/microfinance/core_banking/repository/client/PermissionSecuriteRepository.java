package com.microfinance.core_banking.repository.client;

import com.microfinance.core_banking.entity.PermissionSecurite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionSecuriteRepository extends JpaRepository<PermissionSecurite, Long> {

    Optional<PermissionSecurite> findByCodePermission(String codePermission);

    boolean existsByCodePermission(String codePermission);

    List<PermissionSecurite> findByModuleCodeOrderByCodePermissionAsc(String moduleCode);
}
