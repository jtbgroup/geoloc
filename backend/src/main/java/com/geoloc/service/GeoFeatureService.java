package com.geoloc.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geoloc.dto.GeoFeatureDtos.AddFeatureNameRequest;
import com.geoloc.dto.GeoFeatureDtos.CreateGeoFeatureRequest;
import com.geoloc.dto.GeoFeatureDtos.GeoFeatureResponse;
import com.geoloc.dto.GeoFeatureDtos.NameEntry;
import com.geoloc.dto.GeoFeatureDtos.UpdateGeoFeatureRequest;
import com.geoloc.entity.FeatureHierarchy;
import com.geoloc.entity.FeatureName;
import com.geoloc.entity.GeoFeature;
import com.geoloc.repository.FeatureHierarchyRepository;
import com.geoloc.repository.FeatureNameRepository;
import com.geoloc.repository.GeoFeatureRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GeoFeatureService {

    private static final int WGS84_SRID = 4326;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    private final GeoFeatureRepository repository;
    private final FeatureNameRepository nameRepository;
    private final FeatureHierarchyRepository hierarchyRepository;

    public GeoFeatureService(GeoFeatureRepository repository,
            FeatureNameRepository nameRepository,
            FeatureHierarchyRepository hierarchyRepository) {
        this.repository = repository;
        this.nameRepository = nameRepository;
        this.hierarchyRepository = hierarchyRepository;
    }

public List<GeoFeatureResponse> searchLocations(String query) {
    if (query == null || query.trim().length() < 2) {
        return List.of();
    }
    return repository.searchByNameOrProperties(query.trim())
            .stream().map(this::mapToResponse).collect(Collectors.toList());
}

    public List<GeoFeatureResponse> listAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public GeoFeatureResponse getById(UUID id) {
        return repository.findById(id).map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found"));
    }

    public List<GeoFeatureResponse> listCountries() {
        return byClassAndCodes("A", List.of("PCLI", "PCL", "PCLD", "PCLF", "PCLS"));
    }

    public List<GeoFeatureResponse> listProvinces() {
        return byClassAndCodes("A", List.of("ADM1"));
    }

    public List<GeoFeatureResponse> listSeasAndOceans() {
        return byClassAndCodes("H", List.of("SEA", "OCN", "GULF", "BAY", "STRT"));
    }

    public List<GeoFeatureResponse> listPorts() {
        return byClassAndCodes("S", List.of("PRT", "HBR"));
    }

    public List<GeoFeatureResponse> listAirports() {
        return byClassAndCodes("S", List.of("AIRP", "AIRB", "AIRF"));
    }

    public List<GeoFeatureResponse> listByFeatureCode(String featureClass, String featureCode) {
        return repository.findByFeatureClassAndFeatureCode(featureClass, featureCode)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

   @Transactional
public GeoFeatureResponse create(CreateGeoFeatureRequest request) {
    GeoFeature feature = new GeoFeature();
    feature.setName(request.name());
    feature.setFeatureClass(request.featureClass());
    feature.setFeatureCode(request.featureCode());
    feature.setSourceId(request.sourceId());
    feature.setGeom(toPoint(request.latitude(), request.longitude()));
    feature.setStartDate(request.startDate());
    feature.setEndDate(request.endDate());
    feature.setProperties(request.properties());

    GeoFeature saved = repository.save(feature);

    saveNames(saved, request.names(), request.name());

    if (request.properties() != null && request.properties().containsKey("parentSourceId")) {
        String parentSourceId = (String) request.properties().get("parentSourceId");
        if (parentSourceId != null) {
            repository.findBySourceId(parentSourceId).ifPresent(parent ->
                    hierarchyRepository.save(new FeatureHierarchy(parent, saved, "contains")));
        }
    }

    return mapToResponse(saved);
}

// Enregistre les traductions fournies ; si aucune n'est fournie, on retombe
// sur le nom unique avec une langue "und" (undetermined) plutôt que de
// supposer à tort que c'est de l'anglais.
private void saveNames(GeoFeature feature, List<NameEntry> names, String fallbackName) {
    if (names == null || names.isEmpty()) {
        nameRepository.save(new FeatureName(feature, "und", fallbackName, true));
        return;
    }
    for (NameEntry entry : names) {
        nameRepository.save(new FeatureName(
                feature,
                entry.language(),
                entry.name(),
                Boolean.TRUE.equals(entry.isPrimary())));
    }
}

    @Transactional
    public GeoFeatureResponse update(UUID id, UpdateGeoFeatureRequest request) {
        GeoFeature feature = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feature not found"));

        if (request.name() != null) {
            feature.setName(request.name());
        }
        if (request.featureClass() != null) {
            feature.setFeatureClass(request.featureClass());
        }
        if (request.featureCode() != null) {
            feature.setFeatureCode(request.featureCode());
        }
        if (request.latitude() != null && request.longitude() != null) {
            feature.setGeom(toPoint(request.latitude(), request.longitude()));
        }
        if (request.properties() != null) {
            feature.setProperties(request.properties());
        }
        return mapToResponse(repository.save(feature));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Feature not found");
        }
        repository.deleteById(id);
    }

    private List<GeoFeatureResponse> byClassAndCodes(String featureClass, List<String> codes) {
        return repository.findByFeatureClassAndFeatureCodeIn(featureClass, codes)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private Point toPoint(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    @Transactional
public void addName(UUID featureId, AddFeatureNameRequest request) {
    GeoFeature feature = repository.findById(featureId)
            .orElseThrow(() -> new IllegalArgumentException("Feature not found"));
    nameRepository.save(new FeatureName(
            feature, request.language(), request.name(), Boolean.TRUE.equals(request.isPrimary())));
}

@Transactional(readOnly = true)
public List<NameEntry> listNames(UUID featureId) {
    if (!repository.existsById(featureId)) {
        throw new IllegalArgumentException("Feature not found");
    }
    return nameRepository.findByFeature_Id(featureId).stream()
            .map(fn -> new NameEntry(fn.getLanguage(), fn.getName(), fn.isPrimary()))
            .collect(Collectors.toList());
}

    private GeoFeatureResponse mapToResponse(GeoFeature entity) {
        Double lat = null;
        Double lon = null;
        Geometry geom = entity.getGeom();
        if (geom instanceof Point point) {
            lat = point.getY();
            lon = point.getX();
        } else if (geom != null) {
            Point centroid = geom.getCentroid();
            lat = centroid.getY();
            lon = centroid.getX();
        }
        return new GeoFeatureResponse(
                entity.getId(),
                entity.getName(),
                entity.getFeatureClass(),
                entity.getFeatureCode(),
                entity.getSourceId(),
                lat,
                lon,
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getProperties());
    }
}