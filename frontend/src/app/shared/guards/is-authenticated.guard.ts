import {inject} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot} from '@angular/router';
import {map, Observable, tap} from 'rxjs';
import {AuthenticationService} from "../data-access/authentication/authentication.service";

export const isAuthenticatedGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> => {
  const authService = inject(AuthenticationService);
  return authService.getLoggedUser().pipe(tap(console.log)).pipe(map(user => user !== null));
}
