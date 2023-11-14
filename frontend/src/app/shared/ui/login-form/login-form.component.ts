import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AuthenticationDto} from "../../data-access/authentication/authentication.dtos";

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.sass']
})
export class LoginFormComponent {
  email: string = '';
  password: string = '';

  @Input()
  errorMessage: string | null = null;

  @Output()
  loginEvent = new EventEmitter<AuthenticationDto>();

  login() {
    this.loginEvent.emit(AuthenticationDto.of(this.email, this.password));
  }
}
