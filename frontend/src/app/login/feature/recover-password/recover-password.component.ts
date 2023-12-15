import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {UsersService} from "../../../shared/data-access/users/users.service";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-recover-password',
  templateUrl: './recover-password.component.html',
  styleUrls: ['./recover-password.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RecoverPasswordComponent {
  email = new FormControl('', [Validators.required, Validators.email]);
  loading = new BehaviorSubject(false);
  success = new BehaviorSubject<boolean | null>(null);

  constructor(private readonly userService: UsersService) {
  }

  requestRecoverPassword() {
    if (this.email.value) {
      this.success.next(null);
      this.loading.next(true);
      this.userService.changePasswordRequest(this.email.value).subscribe((found) => {
        this.loading.next(false);
        if (!found) {
          this.email.setErrors({notFound: true});
        } else {
          this.success.next(true);
        }
      })
    }
  }
}
