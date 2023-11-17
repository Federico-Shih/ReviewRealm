import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {UserCreateDto} from "../../../shared/data-access/users/users.dtos";

export type UserCreateErrors = Partial<{
  [P in keyof UserCreateDto]: string;
}>;

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterFormComponent implements OnInit {
  @Output()
  createUser = new EventEmitter<UserCreateDto>();
  userCreateErrors: UserCreateErrors | null = {};

  @Input() set errors(value: UserCreateErrors | null) {
    this.userCreateErrors = value;
    if (value?.email) {
      this.registerForm.get('email')?.setErrors({server: value.email});
    }
    if (value?.username) {
      this.registerForm.get('username')?.setErrors({server: value.username});
    }
  }

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

  get password() {
    return this.registerForm.get('password');
  }

  get confirmPassword() {
    return this.registerForm.get('confirmPassword');
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

  onSubmit() {
    if (this.registerForm.status !== 'VALID')
      return;
    const {username, email, password} = this.registerForm.value;
    this.createUser.emit({username: username!!, email: email!!, password: password!!});
  }

  ngOnInit(): void {
    this.registerForm.get('password')?.valueChanges.subscribe(() => {
      this.registerForm.get('confirmPassword')?.updateValueAndValidity();
    })
  }
}
