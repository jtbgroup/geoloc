package com.geoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.geoloc.entity.UserAudit;

import java.util.UUID;

public interface UserAuditRepository extends JpaRepository<UserAudit, UUID> {
}
