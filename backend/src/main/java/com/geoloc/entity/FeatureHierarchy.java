package com.geoloc.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "feature_hierarchy")
public class FeatureHierarchy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private GeoFeature parent;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private GeoFeature child;

    @Column(name = "relation_type")
    private String relationType; // ex: "contains"

    public FeatureHierarchy() {}

    public FeatureHierarchy(GeoFeature parent, GeoFeature child, String relationType) {
        this.parent = parent;
        this.child = child;
        this.relationType = relationType;
    }

    // Getters et Setters
    public UUID getId() { return id; }

    public GeoFeature getParent() { return parent; }
    public void setParent(GeoFeature parent) { this.parent = parent; }

    public GeoFeature getChild() { return child; }
    public void setChild(GeoFeature child) { this.child = child; }

    public String getRelationType() { return relationType; }
    public void setRelationType(String relationType) { this.relationType = relationType; }
}