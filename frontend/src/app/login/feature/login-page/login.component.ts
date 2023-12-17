import { Component } from '@angular/core';
import { AuthenticationDto } from '../../../shared/data-access/authentication/authentication.dtos';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import { BehaviorSubject } from 'rxjs';
import { RequestError } from '../../../shared/data-access/shared.models';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  errorMessage$ = new BehaviorSubject<string | null>(null);
  loading$ = new BehaviorSubject(false);

  constructor(
    private readonly authService: AuthenticationService,
    private readonly router: Router,
    private readonly location: Location
  ) {}

  onLoginSubmit(authenticationDto: AuthenticationDto) {
    this.errorMessage$.next(null);
    this.loading$.next(true);
    this.authService.login(authenticationDto).subscribe({
      next: () => {
        this.location.back();
        this.loading$.next(false);
      },
      error: err => {
        if (err instanceof RequestError && err.status === 401) {
          this.errorMessage$.next('login-form.invalid-credentials');
        }
        this.loading$.next(false);
      },
    });
  }
}
