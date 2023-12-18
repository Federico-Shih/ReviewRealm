import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import { map, Observable, of } from 'rxjs';
import { Role } from '../data-access/shared.enums';

export const isModeratorGuard: CanActivateFn = (): Observable<boolean> => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  const user = authService.getLoggedUser();
  if (authService.getLoggedUser() === null) {
    router.navigate(['/login']);
    return of(false);
  }
  return user.pipe(
    map(user => {
      if (user === null || user.role !== Role.MODERATOR) {
        router.navigate(['/errors/forbidden']);
        return false;
      }
      return true;
    })
  );
};
