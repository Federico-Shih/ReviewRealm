import {ChangeDetectionStrategy, Component, EventEmitter, Output} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {UserCreateDto} from "../../../shared/data-access/users/users.dtos";

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterFormComponent {
  @Output()
  createUser = new EventEmitter<UserCreateDto>();

  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    username: new FormControl('',
      [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(20)
      ]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmPassword: new FormControl('', Validators.required)
  }, [this.passwordMatch]);

  passwordMatch(g: AbstractControl) {
    return (g.get('password')?.value === g.get('confirmPassword')?.value) ? null : {'mismatch': true};
  }

  onSubmit() {
    if (this.registerForm.status !== 'VALID')
      return;
    const {username, email, password} = this.registerForm.value;
    this.createUser.emit({username: username!!, email: email!!, password: password!!});
  }
}
