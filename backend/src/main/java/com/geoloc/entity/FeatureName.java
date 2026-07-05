package com.geoloc.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "feature_name")
public class FeatureName {
  @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private GeoFeature feature;

    private String language; // ex: "fr", "en"
    private String name;
    
    @Column(name = "is_primary")
    private boolean isPrimary;

    public FeatureName() {}
    public FeatureName(GeoFeature feature, String language, String name, boolean isPrimary) {
        this.feature = feature;
        this.language = language;
        this.name = name;
        this.isPrimary = isPrimary;
    }

    public GeoFeature getFeature() {
        return feature;
    }
    public void setFeature(GeoFeature feature) {
        this.feature = feature;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isPrimary() {
        return isPrimary;
    }
    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public UUID getId() {
        return id;
    }
    
}