import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterDrawerComponent } from './filter-drawer.component';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatRadioModule } from '@angular/material/radio';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('FilterDrawerComponent', () => {
  let component: FilterDrawerComponent<string>;
  let fixture: ComponentFixture<FilterDrawerComponent<string>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FilterDrawerComponent],
      imports: [
        CommonModule,
        HttpClientModule,
        MatToolbarModule,
        MatInputModule,
        MatCardModule,
        FormsModule,
        MatButtonModule,
        TranslateModule.forRoot(),
        RouterLink,
        MatIconModule,
        ReactiveFormsModule,
        MatMenuModule,
        MatSidenavModule,
        MatRadioModule,
        MatListModule,
        NgOptimizedImage,
        MatChipsModule,
        MatCheckboxModule,
        MatSnackBarModule,
        MatExpansionModule,
        MatProgressSpinnerModule,
        BrowserAnimationsModule,
      ],
    });
    fixture = TestBed.createComponent(FilterDrawerComponent<string>);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
