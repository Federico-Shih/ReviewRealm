import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { delay, Observable, of, retry, throwError } from 'rxjs';
import {
  AUTHORIZATION_TOKEN_LABEL,
  REFRESH_TOKEN_LABEL,
} from '../data-access/authentication/authentication.service';

// TODO: test if it works
export class RetryWithRefreshInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      retry({
        count: 1,
        delay: err => {
          if (err instanceof HttpErrorResponse && err.status === 401) {
            const refreshToken = localStorage.getItem(REFRESH_TOKEN_LABEL);
            if (refreshToken) {
              localStorage.setItem(AUTHORIZATION_TOKEN_LABEL, refreshToken);
              return of(null).pipe(delay(1));
            }
          }
          return throwError(err);
        },
      })
    );
  }
}
