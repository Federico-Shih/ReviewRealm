import { TestBed, waitForAsync } from '@angular/core/testing';
import {
  ActivatedRoute,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';

import { notAuthenticatedGuard } from './not-authenticated.guard';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import {
  AuthenticationServiceMockNull,
  AuthenticationServiceUserModerator,
} from '../../../tests/mocks/authentication-service.mock';
import { Observable } from 'rxjs';

describe('notAuthenticatedGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() =>
      notAuthenticatedGuard(...guardParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: {} },
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
            path: '404',
            component: class {},
          },
          {
            path: '403',
            component: class {},
          },
        ]),
      ],
    });
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('given not logged in should return true', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceMockNull(),
    });
    (
      executeGuard(
        TestBed.inject(ActivatedRoute).snapshot,
        TestBed.inject(RouterStateSnapshot)
      ) as Observable<boolean>
    ).subscribe(result => {
      expect(result).toBeTruthy();
    });
  }));

  it('given logged in should return false', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceUserModerator(),
    });
    const router = TestBed.inject(Router),
      navigateSpy = spyOn(router, 'navigate');
    (
      executeGuard(
        TestBed.inject(ActivatedRoute).snapshot,
        TestBed.inject(RouterStateSnapshot)
      ) as Observable<boolean>
    ).subscribe(result => {
      expect(result).toBeFalse();
    });
    expect(navigateSpy).toHaveBeenCalledOnceWith(['/']);
  }));
});
