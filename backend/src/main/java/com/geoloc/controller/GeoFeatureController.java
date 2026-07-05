package com.geoloc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.geoloc.dto.GeoFeatureDtos.AddFeatureNameRequest;
import com.geoloc.dto.GeoFeatureDtos.CreateGeoFeatureRequest;
import com.geoloc.dto.GeoFeatureDtos.GeoFeatureResponse;
import com.geoloc.dto.GeoFeatureDtos.NameEntry;
import com.geoloc.dto.GeoFeatureDtos.UpdateGeoFeatureRequest;
import com.geoloc.service.GeoFeatureService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/geo-features")
public class GeoFeatureController {

    private final GeoFeatureService service;

    public GeoFeatureController(GeoFeatureService service) {
        this.service = service;
    }

    @GetMapping
    public List<GeoFeatureResponse> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeoFeatureResponse> get(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GeoFeatureResponse> create(@RequestBody CreateGeoFeatureRequest request) {
        try {
            return ResponseEntity.ok(service.create(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeoFeatureResponse> update(@PathVariable UUID id,
                                                      @RequestBody UpdateGeoFeatureRequest request) {
        try {
            return ResponseEntity.ok(service.update(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            service.delete(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/names")
public ResponseEntity<List<NameEntry>> listNames(@PathVariable UUID id) {
    try {
        return ResponseEntity.ok(service.listNames(id));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    }
}

@PostMapping("/{id}/names")
public ResponseEntity<Void> addName(@PathVariable UUID id, @RequestBody AddFeatureNameRequest request) {
    try {
        service.addName(id, request);
        return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
}