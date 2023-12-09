import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AuthenticationDto} from "../../data-access/authentication/authentication.dtos";
import {FormControl, Validators} from "@angular/forms";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
})
export class LoginFormComponent {
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required]);

  @Input()
  errorMessage: string | null = null;

  @Input()
  loading: boolean | null = false;

  @Output()
  loginSubmit = new EventEmitter<AuthenticationDto>();

  login() {
    if (this.email.value === null || this.email.invalid || this.password.invalid || this.password.value === null) {
      return;
    }
    this.loginSubmit.emit(AuthenticationDto.of(this.email.value, this.password.value));
  }
}
