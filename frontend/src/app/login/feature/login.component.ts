import {Component} from '@angular/core';
import {AuthenticationDto} from "../../shared/data-access/authentication/authentication.dtos";
import {AuthenticationService} from "../../shared/data-access/authentication/authentication.service";
import {BehaviorSubject} from "rxjs";
import {RequestError} from "../../shared/data-access/shared.models";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  errorMessage = new BehaviorSubject<string | null>(null);

  constructor(private readonly authService: AuthenticationService, private readonly router: Router) {
  }

  onLoginSubmit(authenticationDto: AuthenticationDto) {
    this.errorMessage.next(null);
    this.authService.login(authenticationDto).subscribe({
      next: (_) => {
        // TODO: on unauthenticated, return to previous page
        this.router.navigate(['/']);
      },
      error: (err) => {
        if (err instanceof RequestError && err.status === 401) {
          this.errorMessage.next('login-form.invalid-credentials');
        }
      }
    });
  }
}
