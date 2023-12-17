import { TestBed, waitForAsync } from '@angular/core/testing';
import { isAuthenticatedGuard } from './is-authenticated.guard';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  RouterStateSnapshot,
} from '@angular/router';
import {
  AuthenticationServiceMock,
  AuthenticationServiceMockNull,
} from '../../../tests/mocks/authentication-service.mock';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import { Observable, of } from 'rxjs';

describe('IsAuthenticatedGuard', () => {
  const guard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() =>
      isAuthenticatedGuard(...guardParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRouteSnapshot,
          useValue: {
            params: of([{ id: 1 }]),
          },
        },
        {
          provide: RouterStateSnapshot,
          useValue: {},
        },
      ],
    });
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('given not logged in should return false', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceMockNull(),
    });
    expect(
      guard(
        TestBed.inject(ActivatedRouteSnapshot),
        TestBed.inject(RouterStateSnapshot)
      )
    ).toBeInstanceOf(Observable);
    (
      guard(
        TestBed.inject(ActivatedRouteSnapshot),
        TestBed.inject(RouterStateSnapshot)
      ) as Observable<boolean>
    ).subscribe(result => {
      expect(result).toBeFalsy();
    });
  }));

  it('given logged in should return true', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceMock(),
    });
    expect(
      guard(
        TestBed.inject(ActivatedRouteSnapshot),
        TestBed.inject(RouterStateSnapshot)
      )
    ).toBeInstanceOf(Observable);
    (
      guard(
        TestBed.inject(ActivatedRouteSnapshot),
        TestBed.inject(RouterStateSnapshot)
      ) as Observable<boolean>
    ).subscribe(result => {
      expect(result).toBeTrue();
    });
  }));
});
