package com.geoloc.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geoloc.dto.GeoFeatureDtos.CreateGeoFeatureRequest;
import com.geoloc.dto.GeoFeatureDtos.GeoFeatureResponse;
import com.geoloc.dto.GeoFeatureDtos.UpdateGeoFeatureRequest;
import com.geoloc.entity.GeoFeature;
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

    public GeoFeatureService(GeoFeatureRepository repository) {
        this.repository = repository;
    }

    public List<GeoFeatureResponse> searchLocations(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        return repository.searchByName(query).stream().map(this::mapToResponse).collect(Collectors.toList());
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
        feature.setProperties(request.properties());
        return mapToResponse(repository.save(feature));
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
                entity.getProperties()
        );
    }
}