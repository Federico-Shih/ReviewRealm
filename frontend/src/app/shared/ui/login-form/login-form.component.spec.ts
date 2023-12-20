import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LoginFormComponent } from './login-form.component';
import { MatCardModule } from '@angular/material/card';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {ActivatedRoute, convertToParamMap, RouterLink} from '@angular/router';
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
import { AuthenticationDto } from '../../data-access/authentication/authentication.dtos';
import {ActivatedRouteMock} from "../../guards/is-review-author.guard.spec";

describe('LoginFormComponent', () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;
  const EMAIL_EXPECTED = 'test@gmail.com';
  const PASSWORD_EXPECTED = 'password';
  const EMAIL_INVALID = 'test';

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteMock(convertToParamMap({ id: '1' })),
        },
      ],
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
      declarations: [LoginFormComponent],
      schemas: [NO_ERRORS_SCHEMA],
    });
    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('submits correctly when email and password are set', waitForAsync(() => {
    component.email.setValue(EMAIL_EXPECTED);
    component.password.setValue(PASSWORD_EXPECTED);
    spyOn(component.loginSubmit, 'emit');

    component.login();

    fixture.detectChanges();
    expect(component.loginSubmit.emit).toHaveBeenCalledOnceWith(
      AuthenticationDto.of(EMAIL_EXPECTED, PASSWORD_EXPECTED)
    );
  }));

  it('no submit when email is not set', waitForAsync(() => {
    component.password.setValue(PASSWORD_EXPECTED);
    spyOn(component.loginSubmit, 'emit');

    component.login();

    fixture.detectChanges();
    expect(component.loginSubmit.emit).not.toHaveBeenCalled();
  }));

  it('no submit when password is not set', waitForAsync(() => {
    spyOn(component.loginSubmit, 'emit');

    component.login();

    fixture.detectChanges();
    expect(component.loginSubmit.emit).not.toHaveBeenCalled();
  }));

  it('no submit when email is invalid', waitForAsync(() => {
    component.email.setValue(EMAIL_INVALID);
    component.password.setValue(PASSWORD_EXPECTED);
    spyOn(component.loginSubmit, 'emit');

    component.login();

    fixture.detectChanges();
    expect(component.loginSubmit.emit).not.toHaveBeenCalled();
  }));
});
