import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subject, debounceTime, distinctUntilChanged, switchMap, catchError, of } from 'rxjs';
import { GeoFeatureService, GeoFeatureDto } from '../../core/services/geo-feature.service';
import { I18nService } from '../../core/i18n/i18n.service';
import { TranslatePipe } from '../../core/i18n/translate.pipe';

@Component({
  selector: 'app-geo-search',
  standalone: true,
  templateUrl: './geo-search.component.html',
  styleUrl: './geo-search.component.scss',
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TranslatePipe,
  ],
})
export class GeoSearchComponent {
  private readonly geoFeatureService = inject(GeoFeatureService);
  readonly i18n = inject(I18nService);

  private readonly queryChanged = new Subject<string>();

  query = '';
  results: GeoFeatureDto[] = [];
  loading = false;
  searched = false;

  constructor() {
    this.queryChanged
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(q => {
          if (q.trim().length < 2) {
            this.loading = false;
            this.searched = false;
            return of<GeoFeatureDto[]>([]);
          }
          this.loading = true;
          return this.geoFeatureService.search(q).pipe(
            catchError(() => of<GeoFeatureDto[]>([]))
          );
        })
      )
      .subscribe(results => {
        this.results = results;
        this.loading = false;
        this.searched = true;
      });
  }

  onInput() {
    this.queryChanged.next(this.query);
  }
}