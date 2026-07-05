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

    // Recherche large : nom principal, tous les noms traduits (feature_name),
    // et toute valeur présente dans les properties JSONB (icao, iata, iso_a3, locode, ...).
    @Query(value = """
        SELECT DISTINCT gf.* FROM geo_feature gf
        LEFT JOIN feature_name fn ON fn.feature_id = gf.id
        WHERE gf.name ILIKE CONCAT('%', :q, '%')
           OR fn.name ILIKE CONCAT('%', :q, '%')
           OR gf.properties::text ILIKE CONCAT('%', :q, '%')
        ORDER BY gf.name
        LIMIT 50
        """, nativeQuery = true)
    List<GeoFeature> searchByNameOrProperties(@Param("q") String query);

    List<GeoFeature> findByFeatureClassAndFeatureCodeIn(String featureClass, List<String> featureCodes);

    List<GeoFeature> findByFeatureClassAndFeatureCode(String featureClass, String featureCode);

    Optional<GeoFeature> findBySourceId(String sourceId);
}