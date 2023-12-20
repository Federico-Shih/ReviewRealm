import { TestBed, waitForAsync } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import {
  AuthenticationServiceMock,
  AuthenticationServiceMockNull,
  AuthenticationServiceUserModerator,
} from '../../../tests/mocks/authentication-service.mock';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import { Observable, of } from 'rxjs';
import { isModeratorGuard } from './is-moderator.guard';
import { RouterTestingModule } from '@angular/router/testing';

describe('isModeratorGuard', () => {
  const guard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => isModeratorGuard(...guardParameters));
  let router: Router;

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
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'login',
            component: class {},
          },
          {
            path: 'errors/forbidden',
            component: class {},
          },
        ]),
      ],
    });
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('given not logged in should return false and navigate', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceMockNull(),
    });
    router = TestBed.inject(Router);
    const navigateSpy = spyOn(router, 'navigate');

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

    expect(navigateSpy).toHaveBeenCalledOnceWith(['/errors/forbidden']);
  }));

  it('given logged in but not moderator, return false and navigate', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceMock(),
    });
    router = TestBed.inject(Router);
    const navigateSpy = spyOn(router, 'navigate');
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
      expect(result).toBeFalse();
    });
    expect(navigateSpy).toHaveBeenCalledOnceWith(['/errors/forbidden']);
  }));

  it('given logged in and is moderator, return true', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceUserModerator(),
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
