-- =========================================================================
-- Flyway Migration: V1__init_spatial_schema.sql
-- Description: Initialisation de PostGIS, des tables géographiques et index
-- =========================================================================

-- 1. Activation de l'extension PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Table centrale : geo_feature
CREATE TABLE geo_feature (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    feature_class CHAR(1) NOT NULL,       -- ex: 'A' (Admin), 'H' (Hydro), 'S' (Structure)
    feature_code VARCHAR(50) NOT NULL,     -- ex: 'PCLI' (Pays), 'AIRP' (Aéroport), 'PORT' (Port)
    geom GEOMETRY(Geometry, 4326) NOT NULL, -- Stocke Point, Polygon, MultiPolygon... en WGS84
    properties JSONB NOT NULL DEFAULT '{}'::jsonb, -- Conteneur pour attributs customs (ICAO, STANAG...)
    source_id VARCHAR(100) NOT NULL,       -- Traçabilité: 'ONU', 'STANAG_NATO', 'USER_CUSTOM'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Table satellite : feature_name (Pour le multilinguisme et les alias)
CREATE TABLE feature_name (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    feature_id UUID NOT NULL,
    language VARCHAR(10) NOT NULL,         -- ex: 'fr', 'en', ou tags techniques 'iata', 'icao', 'locode'
    name VARCHAR(255) NOT NULL,
    is_preferred BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feature_name_geo_feature FOREIGN KEY (feature_id) 
        REFERENCES geo_feature(id) ON DELETE CASCADE
);

-- 4. Table satellite : feature_hierarchy (Pour modéliser le graphe des relations)
CREATE TABLE feature_hierarchy (
    parent_id UUID NOT NULL,
    child_id UUID NOT NULL,
    relation_type VARCHAR(50) DEFAULT 'contains', -- ex: 'contains', 'borders_with', 'custom_link'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (parent_id, child_id),
    CONSTRAINT fk_hierarchy_parent FOREIGN KEY (parent_id) 
        REFERENCES geo_feature(id) ON DELETE CASCADE,
    CONSTRAINT fk_hierarchy_child FOREIGN KEY (child_id) 
        REFERENCES geo_feature(id) ON DELETE CASCADE
);

-- 5. Création des Index de Performance
-- Index Spatial (GiST) crucial pour toutes les requêtes géographiques (ex: ST_Within, ST_Distance)
CREATE INDEX idx_geo_feature_geom ON geo_feature USING GIST (geom);

-- Index GIN sur le JSONB pour interroger instantanément les clés internes (ex: properties->>'icao')
CREATE INDEX idx_geo_feature_properties ON geo_feature USING GIN (properties);

-- Index standards (B-Tree) pour les filtres de recherche fréquents
CREATE INDEX idx_geo_feature_class_code ON geo_feature (feature_class, feature_code);
CREATE INDEX idx_geo_feature_source ON geo_feature (source_id);
CREATE INDEX idx_feature_name_lookup ON feature_name (feature_id, language);

-- Trigger automatique pour mettre à jour le champ updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_geo_feature_modtime
    BEFORE UPDATE ON geo_feature
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();