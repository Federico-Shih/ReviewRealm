import { TestBed, waitForAsync } from '@angular/core/testing';
import {
  ActivatedRoute,
  CanActivateFn,
  convertToParamMap,
  Params,
  Router,
  RouterStateSnapshot,
} from '@angular/router';

import { isReviewAuthorGuard } from './is-review-author.guard';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import {
  AuthenticationServiceMock,
  AuthenticationServiceMockNull,
  AuthenticationServiceUserModerator,
} from '../../../tests/mocks/authentication-service.mock';
import { Observable, of, throwError } from 'rxjs';
import { ReviewsService } from '../data-access/reviews/reviews.service';
import { RequestError } from '../data-access/shared.models';

export class ActivatedRouteMock {
  snapshot: {
    params: Params;
    paramMap: Params;
  };

  paramMap: Observable<Params>;
  params: Record<string, string> = {};
  constructor(params: Params) {
    this.params = params;
    const extendedParams = {
      ...params,
      get: (paramName: string) => {
        return params[paramName];
      },
    };
    this.snapshot = {
      params: extendedParams,
      paramMap: extendedParams,
    };
    this.paramMap = of(extendedParams);
  }
}

describe('isReviewAuthorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() =>
      isReviewAuthorGuard(...guardParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteMock(convertToParamMap({ id: '1' })),
        },
        {
          provide: RouterStateSnapshot,
          useValue: {},
        }
      ],
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'login',
            component: class {},
          },
          {
            path: 'errors/not-found',
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
    expect(executeGuard).toBeTruthy();
  });

  it('given not logged in should return false and navigate', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceMockNull(),
    });
    TestBed.overrideProvider(ReviewsService, {
      useValue: {},
    });

    const router = TestBed.inject(Router);
    const navigateSpy = spyOn(router, 'navigate');

    expect(
      executeGuard(
        TestBed.inject(ActivatedRoute).snapshot,
        TestBed.inject(RouterStateSnapshot)
      )
    ).toBeInstanceOf(Observable);
    (
      executeGuard(
        TestBed.inject(ActivatedRoute).snapshot,
        TestBed.inject(RouterStateSnapshot)
      ) as Observable<boolean>
    ).subscribe(result => {
      expect(result).toBeFalsy();
    });

    expect(navigateSpy).toHaveBeenCalledOnceWith(['/login']);
  }));


  it('given logged in user and valid route but no review, should redirect to 404', waitForAsync(() => {
    TestBed.overrideProvider(AuthenticationService, {
      useValue: new AuthenticationServiceUserModerator(),
    });
    TestBed.overrideProvider(ReviewsService, {
      useValue: {
        getReviewById: () => {
          return throwError(() => {
            return new RequestError(404, null);
          });
        },
      },
    });
    TestBed.overrideProvider(ActivatedRoute, {
      useValue: new ActivatedRouteMock(convertToParamMap({ id: '1' })),
    });
    const router = TestBed.inject(Router),
      navigateSpy = spyOn(router, 'navigate');
    const guardExec = executeGuard(
      TestBed.inject(ActivatedRoute).snapshot,
      TestBed.inject(RouterStateSnapshot)
    );
    (guardExec as Observable<boolean>).subscribe(result => {
      expect(result).toBeFalsy();
    });

    expect(navigateSpy).toHaveBeenCalledOnceWith(['/errors/not-found']);
  }));
});
