// GeoFeatureDtos.java
package com.geoloc.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GeoFeatureDtos {

    private GeoFeatureDtos() { }

    public record NameEntry(String language, String name, Boolean isPrimary) { }

    public record GeoFeatureResponse(
            UUID id,
            String name,
            String featureClass,
            String featureCode,
            String sourceId,
            Double latitude,
            Double longitude,
            LocalDate startDate,
            LocalDate endDate,
            Map<String, Object> properties) { }

    public record CreateGeoFeatureRequest(
            String name,
            String featureClass,
            String featureCode,
            String sourceId,
            Double latitude,
            Double longitude,
            LocalDate startDate,
            LocalDate endDate,
            Map<String, Object> properties,
            List<NameEntry> names) { }   // <-- ajouté, optionnel

    public record UpdateGeoFeatureRequest(
            String name,
            String featureClass,
            String featureCode,
            Double latitude,
            Double longitude,
            LocalDate startDate,
            LocalDate endDate,
            Map<String, Object> properties) { }

    public record AddFeatureNameRequest(String language, String name, Boolean isPrimary) { }
}