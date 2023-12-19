import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import { ChangePasswordComponent } from './change-password.component';
import {CommonModule} from "@angular/common";
import {LoginRoutingModule} from "../../login-routing.module";
import {SharedModule} from "../../../shared/shared.module";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {BehaviorSubject, Observable, of, throwError} from "rxjs";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {ActivatedRoute, convertToParamMap, Router} from "@angular/router";
import {UsersService} from "../../../shared/data-access/users/users.service";

describe('ChangePasswordComponent', () => {
  let component: ChangePasswordComponent;
  let fixture: ComponentFixture<ChangePasswordComponent>;
  const PASSWORD_EXPECTED = '12345678';
  const PASSWORD_NOT_MATCH = '123456789';

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChangePasswordComponent],
      imports: [ReactiveFormsModule, MatSnackBarModule,
        CommonModule,
        LoginRoutingModule,
        SharedModule,
        FormsModule,
        MatButtonModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot()],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: (key: string) => {
                  return key === 'email'? 'email@imail.com' : 'adlfjlasd'
                }
              }
            }
          }
        }
      ]
    });

    fixture = TestBed.createComponent(ChangePasswordComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return null if passwords match', () => {
    component.changePasswordForm.controls['password'].setValue(PASSWORD_EXPECTED);
    component.changePasswordForm.controls['confirmPassword'].setValue(PASSWORD_EXPECTED);
    const result = component.passwordMatch(component.changePasswordForm);
    expect(result).toBeNull();
  });

  it('should return error if passwords do not match', () => {
    component.changePasswordForm.controls['password'].setValue(PASSWORD_EXPECTED);
    component.changePasswordForm.controls['confirmPassword'].setValue(PASSWORD_NOT_MATCH);
    const result = component.passwordMatch(component.changePasswordForm);
    expect(result).toEqual({passwordMismatch: true});
  });

  it('should call changePassword() and return success', () => {
    const userService = TestBed.inject(UsersService);
    const router = TestBed.inject(Router);
    const spy = spyOn(userService, 'patchUser').and.returnValue(of({}));
    const spy2 = spyOn(router, 'navigate').and.returnValue(new Promise(()=>true));
    component.userLink$ = new BehaviorSubject(1<2?'xd/users/7': null)
    component.changePasswordForm.controls['password'].setValue(PASSWORD_EXPECTED);
    component.changePassword();

    expect(spy).toHaveBeenCalled();
    expect(spy2).toHaveBeenCalled();
  });


});
