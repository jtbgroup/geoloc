import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface GeoFeatureDto {
  id: string;
  name: string;
  featureClass: string;
  featureCode: string;
  sourceId: string;
  latitude: number | null;
  longitude: number | null;
  properties: Record<string, unknown> | null;
}

export interface CreateGeoFeatureDto {
  name: string;
  featureClass: string;
  featureCode: string;
  sourceId: string;
  latitude: number;
  longitude: number;
}

export interface UpdateGeoFeatureDto {
  name?: string;
  featureClass?: string;
  featureCode?: string;
  latitude?: number;
  longitude?: number;
}

@Injectable({ providedIn: 'root' })
export class GeoFeatureService {
  private readonly apiUrl = `${environment.apiBaseUrl}/api/geo-features`;
  private readonly geoUrl = `${environment.apiBaseUrl}/api/geo`;
  private readonly http = inject(HttpClient);

  list(): Observable<GeoFeatureDto[]> {
    return this.http.get<GeoFeatureDto[]>(this.apiUrl, { withCredentials: true });
  }

  get(id: string): Observable<GeoFeatureDto> {
    return this.http.get<GeoFeatureDto>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  create(payload: CreateGeoFeatureDto): Observable<GeoFeatureDto> {
    return this.http.post<GeoFeatureDto>(this.apiUrl, payload, { withCredentials: true });
  }

  update(id: string, payload: UpdateGeoFeatureDto): Observable<GeoFeatureDto> {
    return this.http.put<GeoFeatureDto>(`${this.apiUrl}/${id}`, payload, { withCredentials: true });
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  search(query: string): Observable<GeoFeatureDto[]> {
    return this.http.get<GeoFeatureDto[]>(`${this.geoUrl}/search`, {
      params: { q: query },
      withCredentials: true,
    });
  }
}