import {inject} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot} from '@angular/router';
import {map, Observable} from 'rxjs';
import {AuthenticationService} from "../data-access/authentication/authentication.service";

export const isAuthenticatedGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> => {
  const authService = inject(AuthenticationService);
  return authService.getLoggedUser().pipe(map(user => user !== null));
}
