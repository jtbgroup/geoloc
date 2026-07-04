import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { GeoFeatureService, GeoFeatureDto } from '../../../core/services/geo-feature.service';
import { I18nService } from '../../../core/i18n/i18n.service';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';

@Component({
  selector: 'app-geo-feature-list',
  standalone: true,
  templateUrl: './geo-feature-list.component.html',
  styleUrl: './geo-feature-list.component.scss',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, TranslatePipe],
})
export class GeoFeatureListComponent implements OnInit {
  private readonly geoFeatureService = inject(GeoFeatureService);
  private readonly router = inject(Router);
  readonly i18n = inject(I18nService);

  features: GeoFeatureDto[] = [];
  displayedColumns = ['name', 'featureClass', 'featureCode', 'coordinates', 'actions'];
  error: string | null = null;

  ngOnInit() {
    this.reload();
  }

  goToCreate() {
    this.router.navigate(['/geo-features/new']);
  }

  goToEdit(feature: GeoFeatureDto) {
    this.router.navigate(['/geo-features', feature.id, 'edit']);
  }

  remove(feature: GeoFeatureDto) {
    this.geoFeatureService.delete(feature.id).subscribe({
      next: () => this.reload(),
      error: () => (this.error = this.i18n.translate('geoFeatures.errorDelete')),
    });
  }

  private reload() {
    this.geoFeatureService.list().subscribe({
      next: features => (this.features = features),
      error: () => (this.error = this.i18n.translate('geoFeatures.errorLoad')),
    });
  }
}