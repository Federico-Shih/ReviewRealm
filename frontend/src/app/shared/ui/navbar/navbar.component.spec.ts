import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarComponent } from './navbar.component';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
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
import { of } from 'rxjs';
import { SharedModule } from '../../shared.module';
import { userMock1 } from '../../../../tests/mocks/user.mock';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NavbarComponent],
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
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of([{ id: 1 }]),
          },
        },
      ],
    });
    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('given an not logged in user, show login/register button', () => {
    component.user = null;
    fixture.detectChanges();

    expect(
      fixture.debugElement.nativeElement.querySelector('#login-link')
    ).toBeTruthy();
    expect(
      fixture.debugElement.nativeElement.querySelector('#register-link')
    ).toBeTruthy();
    expect(
      fixture.debugElement.nativeElement.querySelector('#logged-in-button')
    ).toBeFalsy();
  });

  it('given a logged in user, show menu button', async () => {
    component.user = userMock1;
    fixture.detectChanges();
    await fixture.whenStable();
    expect(
      fixture.debugElement.nativeElement.querySelector('#login-link')
    ).toBeFalsy();
    expect(
      fixture.debugElement.nativeElement.querySelector('#register-link')
    ).toBeFalsy();
    expect(
      fixture.debugElement.nativeElement.querySelector('#logged-in-button')
    ).toBeTruthy();
  });
});
