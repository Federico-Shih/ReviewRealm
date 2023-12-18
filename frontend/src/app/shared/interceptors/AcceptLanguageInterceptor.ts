import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest,} from '@angular/common/http';
import {Observable} from 'rxjs';
import {TranslateService} from "@ngx-translate/core";
import {Injectable} from "@angular/core";

@Injectable()
export class AcceptLanguageInterceptor implements HttpInterceptor {
  constructor(private readonly translate: TranslateService) {

  }

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const apiReq = req.clone({
      setHeaders: {
        'Accept-Language': this.translate.getBrowserLang() ?? 'es',
      },
    });
    return next.handle(apiReq);
  }
}
