import {ChangeDetectionStrategy, Component} from '@angular/core';
import {UserCreateDto} from "../../../shared/data-access/users/users.dtos";
import {UsersService} from "../../../shared/data-access/users/users.service";
import {environment} from "../../../../environments/environment";
import {ValidationError} from "../../../shared/data-access/shared.models";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TranslateService} from "@ngx-translate/core";
import {BehaviorSubject} from "rxjs";
import {UserCreateErrors} from "../../ui/register-form/register-form.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterComponent {
  userCreateErrors = new BehaviorSubject<UserCreateErrors>({});
  loading$ = new BehaviorSubject(false);

  constructor(private readonly userService: UsersService, private _snackBar: MatSnackBar, private translateService: TranslateService, private readonly router: Router) {
  }

  createUser(userCreateDto: UserCreateDto) {
    this.loading$.next(true);
    this.userService.createUser(environment.API_ENDPOINT + '/users', userCreateDto).subscribe(
      {
        error: (err) => {
          if (!(err instanceof ValidationError) || err.status !== 400 || err.exceptions === null) {
            this._snackBar.open(this.translateService.instant('errors.unknown'), this.translateService.instant('errors.dismiss'));
            this.loading$.next(false);
            return;
          }
          const errorMessages: UserCreateErrors = err.exceptions.reduce((acc: UserCreateErrors, curr) => {
            acc[curr.property as keyof UserCreateErrors] = curr.message;
            return acc;
          }, {});
          this.userCreateErrors.next(errorMessages);
          this.loading$.next(false);
        },
        next: (user) => {
          this.loading$.next(false);
          this.router.navigate(['register', 'validate'], {queryParams: {email: user.email}});
        }
      }
    );
  }
}
