package com.geoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.geoloc.entity.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
}
