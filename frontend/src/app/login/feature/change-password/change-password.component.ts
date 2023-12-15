import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {BehaviorSubject} from "rxjs";
import {UsersService} from "../../../shared/data-access/users/users.service";
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from "../../../../environments/environment";
import {RequestError} from "../../../shared/data-access/shared.models";
import {MatSnackBar} from "@angular/material/snack-bar";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChangePasswordComponent implements OnInit {
  userLink$ = new BehaviorSubject<string | null>(null);
  loading$ = new BehaviorSubject(false);
  changePasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmPassword: new FormControl(''),
  }, [this.passwordMatch]);

  constructor(private readonly userService: UsersService,
              private readonly route: ActivatedRoute,
              private readonly router: Router,
              private readonly snackBar: MatSnackBar,
              private readonly translate: TranslateService
  ) {

  }

  get username() {
    return this.route.snapshot.queryParamMap.get('username');
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      if (!params.has('token') || !params.has('email')) {
        this.router.navigate(['login'], {replaceUrl: true});
        return;
      }
      this.userService.getUsers(`${environment.API_ENDPOINT}/users`, {email: params.get('email')!})
        .subscribe((users) => {
          if (users.totalPages === 0) {
            this.router.navigate(['login'], {replaceUrl: true});
            return;
          }
          this.userLink$.next(users.content[0].links.self);
        })
    })
  }

  passwordMatch(formGroup: AbstractControl) {
    if (
      formGroup.get('password')?.value === formGroup.get('confirmPassword')?.value ||
      formGroup.get('confirmPassword')?.value === '' || formGroup.get('password')?.value === ''
    )
      return null;
    else
      return {passwordMismatch: true};
  }

  changePassword() {
    const userLink = this.userLink$.getValue();
    const password = this.changePasswordForm.get('password')?.value;
    if (userLink && password) {
      this.loading$.next(true);
      this.userService.patchUser(userLink, {password: password.trim()}, {
        email: this.route.snapshot.queryParamMap.get('email')!,
        token: this.route.snapshot.queryParamMap.get('token')!
      }).subscribe({
        next: () => {
          this.loading$.next(false);
          this.snackBar.open(this.translate.instant('change-password.success'), this.translate.instant('change-password.dismiss'), {
            panelClass: "green-snackbar"
          });
          this.router.navigate(['login'], {replaceUrl: true});
        },
        error: (err) => {
          this.loading$.next(false);
          if (err instanceof RequestError) {
            if (err.status === 401) {
              this.router.navigate(['login'], {replaceUrl: true});
              return;
            }
          }
        }
      });
    }
  }
}
