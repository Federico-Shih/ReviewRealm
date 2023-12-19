import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecoverPasswordComponent } from './recover-password.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {CommonModule} from "@angular/common";
import {LoginRoutingModule} from "../../login-routing.module";
import {SharedModule} from "../../../shared/shared.module";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {TranslateModule} from "@ngx-translate/core";
import {of} from "rxjs";
import {UsersService} from "../../../shared/data-access/users/users.service";

describe('RecoverPasswordComponent', () => {
  let component: RecoverPasswordComponent;
  let fixture: ComponentFixture<RecoverPasswordComponent>;

  const EMAIL = 'email@email.com'

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecoverPasswordComponent],
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
    });
    fixture = TestBed.createComponent(RecoverPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit if email is present', () => {
    const userService = TestBed.inject(UsersService);
    component.email.setValue(EMAIL);
    const spy = spyOn(userService, 'changePasswordRequest').and.returnValue(of(true));
    component.requestRecoverPassword()
    expect(spy).toHaveBeenCalled();
  })
});
