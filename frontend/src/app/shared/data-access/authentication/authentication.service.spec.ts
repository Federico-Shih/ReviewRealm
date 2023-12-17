import { TestBed, waitForAsync } from '@angular/core/testing';

import {
  AuthenticationService,
  AUTHORIZATION_TOKEN_LABEL,
} from './authentication.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { UsersService } from '../users/users.service';
import { of } from 'rxjs';
import { userMock1 } from '../../../../tests/mocks/user.mock';
import { HttpHeaders } from '@angular/common/http';
import jasmine_c = jasmine;
import { RequestError } from '../shared.models';

jasmine_c.getEnv().configure({ random: false });

describe('AuthenticationService', () => {
  const MOCK_TOKEN =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImZlZGVzaGloIiwiZW1haWwiOiJlbWFpbCIsImlkIjoxfQ.2iluEA5yklu8HT6fX9IIsUAlm-HPj98Dc0RS1XEjmV8';
  const INVALID_TOKEN = 'aaaa';

  let service: AuthenticationService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: UsersService,
          useValue: {
            getUsers: () => {
              return of({
                content: [userMock1],
              });
            },
          },
        },
      ],
    });

    const localStorageMock = (function () {
      let store: Record<string, unknown> = {};

      return {
        getItem(key: string) {
          return store[key] || null;
        },

        setItem(key: string, value: string) {
          store[key] = value;
        },

        clear() {
          store = {};
        },

        removeItem(key: string) {
          delete store[key];
        },

        getAll() {
          return store;
        },
      };
    })();

    Object.defineProperty(window, 'localStorage', { value: localStorageMock });

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    service = TestBed.inject(AuthenticationService);
    expect(service).toBeTruthy();
  });

  describe('When logging in', () => {
    beforeEach(() => {
      service = TestBed.inject(AuthenticationService);
    });
    it('given a valid authentication pair then returned user should be the user', waitForAsync(() => {
      service.login({ username: '', password: '' }).subscribe(user => {
        expect(user).toBe(userMock1);
      });

      const req = httpTestingController.expectOne(
        AuthenticationService.AUTHENTICATION_ENDPOINT
      );
      expect(req.request.method).toBe('GET');

      const httpHeaders = new HttpHeaders().set(
        'Authorization',
        'Bearer ' + MOCK_TOKEN
      );
      req.flush([userMock1], { headers: httpHeaders });
    }));

    it('given an invalid authentication pair should throw RequestError', waitForAsync(() => {
      service.login({ username: '', password: '' }).subscribe({
        error: error => {
          expect(error).toBeInstanceOf(RequestError);
          expect((error as RequestError).status).toBe(401);
        },
      });

      const req = httpTestingController.expectOne(
        AuthenticationService.AUTHENTICATION_ENDPOINT
      );
      expect(req.request.method).toBe('GET');

      req.flush(null);
    }));
  });

  describe('When loaded service', () => {
    describe('Given no token in local storage', () => {
      it('Then loggedUser$ should be null', waitForAsync(() => {
        service = TestBed.inject(AuthenticationService);

        service.getLoggedUser().subscribe(user => {
          expect(user).toBeNull();
        });
      }));
    });

    describe('given a valid token in local storage', () => {
      beforeEach(() => {
        window.localStorage.setItem(AUTHORIZATION_TOKEN_LABEL, MOCK_TOKEN);
        service = TestBed.inject(AuthenticationService);
      });

      it('Then loggedUser$ should be the user', waitForAsync(() => {
        service.getLoggedUser().subscribe(user => {
          expect(user).toBe(userMock1);
        });

        const req = httpTestingController.expectOne(
          AuthenticationService.AUTHENTICATION_ENDPOINT
        );
        expect(req.request.method).toBe('GET');
        const httpHeaders = new HttpHeaders().set(
          'Authorization',
          'Bearer ' + MOCK_TOKEN
        );
        req.flush([userMock1], { headers: httpHeaders });
      }));
    });

    describe('given an invalid token in local storage', () => {
      beforeEach(() => {
        window.localStorage.setItem(AUTHORIZATION_TOKEN_LABEL, INVALID_TOKEN);
        service = TestBed.inject(AuthenticationService);
      });

      it('then loggedUser$ should be the null', waitForAsync(() => {
        service.getLoggedUser().subscribe(user => {
          expect(user).toBe(null);
        });

        const req = httpTestingController.expectOne(
          AuthenticationService.AUTHENTICATION_ENDPOINT
        );
        expect(req.request.method).toBe('GET');
        req.flush([userMock1]);
      }));
    });

    describe('given an expired token in local storage', () => {
      beforeEach(() => {
        // Assuming this token is expired
        window.localStorage.setItem(AUTHORIZATION_TOKEN_LABEL, MOCK_TOKEN);
        service = TestBed.inject(AuthenticationService);
      });

      it('then loggedUser$ should be the null', waitForAsync(() => {
        service.getLoggedUser().subscribe(user => {
          expect(user).toBe(null);
        });

        const req = httpTestingController.expectOne(
          AuthenticationService.AUTHENTICATION_ENDPOINT
        );
        expect(req.request.method).toBe('GET');
        const httpHeaders = new HttpHeaders();
        req.flush([userMock1], { headers: httpHeaders });
      }));
    });
  });
});
