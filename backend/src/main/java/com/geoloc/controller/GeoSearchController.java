package com.geoloc.controller;

import org.springframework.web.bind.annotation.*;

import com.geoloc.dto.GeoFeatureDtos.GeoFeatureResponse;
import com.geoloc.service.GeoFeatureService;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
public class GeoSearchController {

    private final GeoFeatureService service;

    public GeoSearchController(GeoFeatureService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public List<GeoFeatureResponse> search(@RequestParam("q") String query) {
        return service.searchLocations(query);
    }

    @GetMapping("/countries")
    public List<GeoFeatureResponse> countries() {
        return service.listCountries();
    }

    @GetMapping("/provinces")
    public List<GeoFeatureResponse> provinces() {
        return service.listProvinces();
    }

    @GetMapping("/seas")
    public List<GeoFeatureResponse> seasAndOceans() {
        return service.listSeasAndOceans();
    }

    @GetMapping("/ports")
    public List<GeoFeatureResponse> ports() {
        return service.listPorts();
    }

    @GetMapping("/airports")
    public List<GeoFeatureResponse> airports() {
        return service.listAirports();
    }

    @GetMapping("/custom")
    public List<GeoFeatureResponse> custom(@RequestParam String featureClass,
                                            @RequestParam String featureCode) {
        return service.listByFeatureCode(featureClass, featureCode);
    }
}