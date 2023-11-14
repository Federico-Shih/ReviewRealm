import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AUTHORIZATION_TOKEN_LABEL} from "../data-access/authentication/authentication.service";

export class AuthenticationInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authorizationToken = localStorage.getItem(AUTHORIZATION_TOKEN_LABEL);
    // Si no hay token o estoy manualmente enviando un Authorization
    if (authorizationToken === null || req.headers.get('Authorization') !== null) {
      return next.handle(req);
    }
    const apiReq = req.clone({
      setHeaders: {
        'Authorization': `Bearer ${authorizationToken}`
      }
    })
    return next.handle(apiReq);
  }
}
