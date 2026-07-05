package com.geoloc.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "geo_feature")
public class GeoFeature {

    // 1. Identifiant
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 2. Attributs principaux
    @Column(nullable = false)
    private String name;

    @Column(name = "feature_class", length = 1, nullable = false)
    private String featureClass;

    @Column(name = "feature_code", length = 50, nullable = false)
    private String featureCode;

    @Column(name = "source_id", length = 100, nullable = false)
    private String sourceId;

    @Column(columnDefinition = "geometry")
    private Geometry geom;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> properties;

    // 3. Gestion temporelle
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // 4. Lifecycle Callbacks (Nettoyé)
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.startDate == null) {
            this.startDate = LocalDate.of(1900, 1, 1);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // 5. Getters & Setters (Regroupés pour la lisibilité)
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFeatureClass() { return featureClass; }
    public void setFeatureClass(String featureClass) { this.featureClass = featureClass; }

    public String getFeatureCode() { return featureCode; }
    public void setFeatureCode(String featureCode) { this.featureCode = featureCode; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public Geometry getGeom() { return geom; }
    public void setGeom(Geometry geom) { this.geom = geom; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}