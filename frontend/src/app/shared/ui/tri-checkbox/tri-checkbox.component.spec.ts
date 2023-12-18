import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TriCheckboxComponent } from './tri-checkbox.component';
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
import { SharedModule } from '../../shared.module';

describe('TriCheckboxComponent', () => {
  let component: TriCheckboxComponent;
  let fixture: ComponentFixture<TriCheckboxComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TriCheckboxComponent],
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
        SharedModule,
      ],
    });
    fixture = TestBed.createComponent(TriCheckboxComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit true when clicked for first time', () => {
    fixture.detectChanges();
    spyOn(component.checkboxClick, 'emit');
    component.check();
    expect(component.checkboxClick.emit).toHaveBeenCalledWith(true);
  });

  it('should emit false when its already true', () => {
    component.value = true;
    fixture.detectChanges();
    spyOn(component.checkboxClick, 'emit');
    component.check();
    expect(component.checkboxClick.emit).toHaveBeenCalledWith(false);
  });

  it('should emit undefined when its already false', () => {
    component.value = false;
    fixture.detectChanges();
    spyOn(component.checkboxClick, 'emit');
    component.check();
    expect(component.checkboxClick.emit).toHaveBeenCalledWith(undefined);
  });
});
