package com.geoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.geoloc.entity.GeoFeature;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeoFeatureRepository extends JpaRepository<GeoFeature, UUID> {

    @Query(value = "SELECT * FROM geo_feature WHERE name ILIKE '%' || :query || '%' LIMIT 20", nativeQuery = true)
    List<GeoFeature> searchByName(@Param("query") String query);

    @Query(value = "SELECT * FROM geo_feature WHERE properties->>'icao' = :icao", nativeQuery = true)
    List<GeoFeature> findByIcaoCode(@Param("icao") String icao);

    List<GeoFeature> findByFeatureClassAndFeatureCodeIn(String featureClass, List<String> featureCodes);

    List<GeoFeature> findByFeatureClassAndFeatureCode(String featureClass, String featureCode);

    Optional<GeoFeature> findBySourceId(String sourceId);
}