package com.geoloc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geoloc.dto.GeoFeatureDto;
import com.geoloc.entity.GeoFeature;
import com.geoloc.repository.GeoFeatureRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GeoFeatureService {

    private final GeoFeatureRepository repository;

    public GeoFeatureService(GeoFeatureRepository repository) {
        this.repository = repository;
    }

    // L'endpoint ouvert sans contrôle
    public List<GeoFeatureDto> searchLocations(String query) {
        if (query == null || query.trim().length() < 2) {
            return List.of(); // Ne pas chercher si moins de 2 caractères
        }
        
        return repository.searchByName(query)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Méthode utilitaire de mapping (tu peux aussi utiliser MapStruct)
    private GeoFeatureDto mapToDto(GeoFeature entity) {
        return new GeoFeatureDto(
                entity.getId(),
                entity.getName(),
                entity.getFeatureClass(),
                entity.getFeatureCode(),
                entity.getSourceId(),
                entity.getGeom(),
                entity.getProperties()
        );
    }
    
    // Ajoute ici tes méthodes CRUD (create, update, delete) avec @Transactional
}