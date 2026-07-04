import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { GeoFeatureService, GeoFeatureDto } from '../../../core/services/geo-feature.service';
import { I18nService } from '../../../core/i18n/i18n.service';
import { TranslatePipe } from '../../../core/i18n/translate.pipe';

const FEATURE_CLASSES = [
  { code: 'A', label: 'Country / State / Region' },
  { code: 'H', label: 'Stream / Lake / Sea / Ocean' },
  { code: 'L', label: 'Parks / Area' },
  { code: 'P', label: 'City / Village' },
  { code: 'R', label: 'Road / Railroad' },
  { code: 'S', label: 'Spot / Building / Airport / Port' },
  { code: 'T', label: 'Mountain / Hill / Rock' },
  { code: 'U', label: 'Undersea' },
  { code: 'V', label: 'Forest / Heath' },
];

@Component({
  selector: 'app-geo-feature-form',
  standalone: true,
  templateUrl: './geo-feature-form.component.html',
  styleUrl: './geo-feature-form.component.scss',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    TranslatePipe,
  ],
})
export class GeoFeatureFormComponent implements OnInit {
  private readonly geoFeatureService = inject(GeoFeatureService);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly i18n = inject(I18nService);

  readonly featureClasses = FEATURE_CLASSES;

  editingFeature: GeoFeatureDto | null = null;
  error: string | null = null;

  featureForm = this.fb.group({
    name: ['', Validators.required],
    featureClass: ['', Validators.required],
    featureCode: ['', Validators.required],
    sourceId: ['USER_CUSTOM', Validators.required],
    latitude: [0, [Validators.required, Validators.min(-90), Validators.max(90)]],
    longitude: [0, [Validators.required, Validators.min(-180), Validators.max(180)]],
  });

  ngOnInit() {
    const featureId = this.route.snapshot.paramMap.get('id');
    if (featureId) {
      this.geoFeatureService.get(featureId).subscribe({
        next: feature => {
          this.editingFeature = feature;
          this.featureForm.reset({
            name: feature.name,
            featureClass: feature.featureClass,
            featureCode: feature.featureCode,
            sourceId: feature.sourceId,
            latitude: feature.latitude ?? 0,
            longitude: feature.longitude ?? 0,
          });
          this.featureForm.get('sourceId')?.disable();
        },
        error: () => this.router.navigate(['/geo-features']),
      });
    }
  }

  get isEdit(): boolean {
    return !!this.editingFeature;
  }

  cancel() {
    this.router.navigate(['/geo-features']);
  }

  onSubmit() {
    if (this.featureForm.invalid) return;

    const { name, featureClass, featureCode, sourceId, latitude, longitude } = this.featureForm.getRawValue();

    if (this.editingFeature) {
      this.geoFeatureService.update(this.editingFeature.id, {
        name: name ?? undefined,
        featureClass: featureClass ?? undefined,
        featureCode: featureCode ?? undefined,
        latitude: latitude ?? undefined,
        longitude: longitude ?? undefined,
      }).subscribe({
        next: () => this.router.navigate(['/geo-features']),
        error: () => { this.error = this.i18n.translate('geoFeatures.errorUpdate'); },
      });
      return;
    }

    this.geoFeatureService.create({
      name: name ?? '',
      featureClass: featureClass ?? '',
      featureCode: featureCode ?? '',
      sourceId: sourceId ?? 'USER_CUSTOM',
      latitude: latitude ?? 0,
      longitude: longitude ?? 0,
    }).subscribe({
      next: () => this.router.navigate(['/geo-features']),
      error: () => { this.error = this.i18n.translate('geoFeatures.errorCreate'); },
    });
  }
}