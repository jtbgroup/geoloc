package com.geoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.geoloc.entity.FeatureName;

import java.util.UUID;

@Repository
public interface FeatureNameRepository extends JpaRepository<FeatureName, UUID> {

}