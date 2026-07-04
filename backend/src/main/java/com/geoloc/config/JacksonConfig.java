package com.geoloc.config;


import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    
    // Ce Bean est détecté automatiquement par Spring Boot au démarrage.
    // Il apprend à Jackson (le convertisseur JSON) comment transformer
    // les objets Geometry en GeoJSON standard.
    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }
}
