package com.geoloc.dto;


import org.locationtech.jts.geom.Geometry;
import java.util.Map;
import java.util.UUID;

public record GeoFeatureDto(
    UUID id,
    String name,
    String featureClass,
    String featureCode,
    String sourceId,
    Geometry geom, // Sera automatiquement converti en GeoJSON pour le front
    Map<String, Object> properties
) {}