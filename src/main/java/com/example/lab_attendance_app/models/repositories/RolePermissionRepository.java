package com.example.lab_attendance_app.models.repositories;

import com.example.lab_attendance_app.models.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    List<RolePermission> findByRole(String role);

    @Query("SELECT DISTINCT rp.role FROM RolePermission rp")
    List<String> findAllDistinctRoles();

    @Query("SELECT rp FROM RolePermission rp WHERE rp.permissionType != 'n/a'")
    List<RolePermission> findAllRolesWithPermissions();

    @Query("select rp FROM RolePermission rp where rp.role = :role and rp.permissionType = :permissionType")
    Optional<RolePermission> findByRoleAndPermissionType(String role, String permissionType);

    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.role = :role")
    void deleteByRole(String role);

    @Modifying
    @Transactional
    @Query("DELETE FROM RolePermission rp WHERE rp.role = :role AND rp.permissionType != 'na'")
    void deleteAllRolePermissionsExceptNA(String role);


}
