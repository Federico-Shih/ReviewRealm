import {Component, OnDestroy, OnInit} from '@angular/core';
import { AuthenticationDto } from '../../../shared/data-access/authentication/authentication.dtos';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';
import {BehaviorSubject, Subscription} from 'rxjs';
import { RequestError } from '../../../shared/data-access/shared.models';
import {ActivatedRoute, NavigationSkipped, Router} from '@angular/router';
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
    private readonly navigationHistoryService: NavigationHistoryService,
    private readonly route: ActivatedRoute,
  ) {
  }

  onLoginSubmit(authenticationDto: AuthenticationDto) {
    this.errorMessage$.next(null);
    this.loading$.next(true);
    this.authService.login(authenticationDto).subscribe({
      next: () => {
        const redirect = this.route.snapshot.queryParamMap.get('redirect');
        if (redirect) {
          this.router.navigate([decodeURIComponent(redirect)], { replaceUrl: true });
        } else {
          this.eventSuscription = this.navigationHistoryService.previousUrl.subscribe((previousUrl) => {
            if (previousUrl?.includes('login') || previousUrl?.includes('register')) {
              previousUrl = null;
            }
            this.router.navigate([previousUrl?.split('?')[0] ?? '/'], { replaceUrl: true });
          });
        }
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
