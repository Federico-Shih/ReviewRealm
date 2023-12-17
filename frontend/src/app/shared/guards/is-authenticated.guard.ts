import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, Observable } from 'rxjs';
import { AuthenticationService } from '../data-access/authentication/authentication.service';

export const isAuthenticatedGuard: CanActivateFn = (): Observable<boolean> => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  return authService.getLoggedUser().pipe(
    map(user => {
      if (user !== null) return true;
      router.navigate(['/login'], {});
      return false;
    })
  );
};
