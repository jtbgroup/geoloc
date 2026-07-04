package com.geoloc.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "geo_feature")
public class GeoFeature {

    public void setName(String name) {
        this.name = name;
    }

    public void setFeatureClass(String featureClass) {
        this.featureClass = featureClass;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    @Column(nullable = false)
    private String name;

    @Column(name = "feature_class", length = 1, nullable = false)
    private String featureClass;

    @Column(name = "feature_code", length = 50, nullable = false)
    private String featureCode;

    @Column(name = "source_id", length = 100, nullable = false)
    private String sourceId;

    // Mapping PostGIS via JTS
    @Column(columnDefinition = "geometry(Geometry,4326)", nullable = false)
    private Geometry geom;

    // Mapping JSONB natif (Hibernate 6)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> properties;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public String getName() {
        return name;
    }

    public String getFeatureClass() {
        return featureClass;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public String getSourceId() {
        return sourceId;
    }

    public Geometry getGeom() {
        return geom;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}