import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthenticationService } from '../data-access/authentication/authentication.service';
import { map } from 'rxjs';

export const notAuthenticatedGuard: CanActivateFn = () => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);
  return authService.getLoggedUser().pipe(
    map(user => {
      console.log(user);
      if (user !== null) {
        router.navigate(['/']);
        return false;
      }
      return true;
    })
  );
};
