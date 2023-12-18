import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest,} from '@angular/common/http';
import {delay, Observable, of, retry, throwError} from 'rxjs';
import {AUTHORIZATION_TOKEN_LABEL, REFRESH_TOKEN_LABEL,} from '../data-access/authentication/authentication.service';
import {Router} from "@angular/router";
import {Injectable} from "@angular/core";

@Injectable()
export class RetryWithRefreshInterceptor implements HttpInterceptor {
  constructor(private readonly router: Router) {
  }

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      retry({
        count: 2,
        delay: (err, retryCount) => {
          if (err instanceof HttpErrorResponse) {
            if (err.status === 401) {
              if (retryCount > 1) {
                this.router.navigate(['/login']);
                return throwError(err);
              } else {
                const refreshToken = localStorage.getItem(REFRESH_TOKEN_LABEL);
                if (refreshToken) {
                  localStorage.setItem(AUTHORIZATION_TOKEN_LABEL, refreshToken);
                  return of(null).pipe(delay(1));
                }
              }
            } else if (err.status === 403) {
              this.router.navigate(['/forbidden']);
              return throwError(err);
            }
          }
          return throwError(err);
        },
      })
    );
  }
}
