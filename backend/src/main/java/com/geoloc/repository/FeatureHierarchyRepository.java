package com.geoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.geoloc.entity.FeatureHierarchy;

import java.util.UUID;

@Repository
public interface FeatureHierarchyRepository extends JpaRepository<FeatureHierarchy, UUID> {

}