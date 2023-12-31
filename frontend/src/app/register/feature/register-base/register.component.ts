import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UserCreateDto } from '../../../shared/data-access/users/users.dtos';
import { UsersService } from '../../../shared/data-access/users/users.service';
import { environment } from '../../../../environments/environment';
import {RequestError, ValidationError} from '../../../shared/data-access/shared.models';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { UserCreateErrors } from '../../ui/register-form/register-form.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent {
  userCreateErrors = new BehaviorSubject<UserCreateErrors>({});
  loading$ = new BehaviorSubject(false);

  constructor(
    private readonly userService: UsersService,
    private _snackBar: MatSnackBar,
    private translateService: TranslateService,
    private readonly router: Router
  ) {}

  createUser(userCreateDto: UserCreateDto) {
    this.loading$.next(true);
    this.userCreateErrors.next({});
    this.userService
      .createUser(environment.API_ENDPOINT + '/users', userCreateDto)
      .subscribe({
        error: err => {
          if (err instanceof RequestError && err.status === 409) {
            const errorMessages: UserCreateErrors = {
              generic: err.exceptions?.message,
            };
            this.userCreateErrors.next(errorMessages);
            this.loading$.next(false);
          } else if (
            !(err instanceof ValidationError) ||
            err.status !== 400 ||
            err.exceptions === null
          ) {
            this._snackBar.open(
              this.translateService.instant('errors.unknown'),
              this.translateService.instant('errors.dismiss')
            );
            this.loading$.next(false);
            return;
          }

        },
        next: user => {
          this.loading$.next(false);
          this.router.navigate(['register', 'validate'], {
            queryParams: { email: user.email },
          });
        },
      });
  }
}
