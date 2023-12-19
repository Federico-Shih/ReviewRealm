import {Component, OnDestroy, OnInit} from '@angular/core';
import { AuthenticationDto } from '../../../shared/data-access/authentication/authentication.dtos';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import {BehaviorSubject, Subscription} from 'rxjs';
import { RequestError } from '../../../shared/data-access/shared.models';
import {NavigationSkipped, Router} from '@angular/router';
import { Location } from '@angular/common';
import {NavigationHistoryService} from "../../../shared/data-access/navigation-history/navigation-history.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  errorMessage$ = new BehaviorSubject<string | null>(null);
  loading$ = new BehaviorSubject(false);
  eventSuscription: Subscription | null = null;

  constructor(
    private readonly authService: AuthenticationService,
    private readonly router: Router,
    private readonly location: Location,
    private readonly navigationHistoryService: NavigationHistoryService
  ) {
  }

  onLoginSubmit(authenticationDto: AuthenticationDto) {
    this.errorMessage$.next(null);
    this.loading$.next(true);
    this.authService.login(authenticationDto).subscribe({
      next: () => {
        this.eventSuscription = this.navigationHistoryService.previousUrl.subscribe((previousUrl) => {
          this.router.navigate([previousUrl ?? '/'], { replaceUrl: true });
        });
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

  ngOnDestroy(): void {
    this.eventSuscription?.unsubscribe();
  }

  ngOnInit(): void {
  }
}
