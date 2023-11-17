import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../shared/data-access/authentication/authentication.service";
import {BehaviorSubject} from "rxjs";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-validate-user',
  templateUrl: './validate-user.component.html',
  styleUrls: ['./validate-user.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ValidateUserComponent implements OnInit {
  loading = new BehaviorSubject(false);
  initialLoading = new BehaviorSubject(true);
  form = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    token: new FormControl('', [Validators.required])
  });

  constructor(private readonly authenticationService: AuthenticationService, private readonly router: Router, private readonly route: ActivatedRoute) {
  }

  onSubmit() {
    if (!this.form.value.email || !this.form.value.token) return;
    this.loading.next(true);
    this.authenticationService.enableUser(this.form.value.email, this.form.value.token).subscribe(
      {
        next: (user) => {
          this.loading.next(false);
          this.initialLoading.next(false);
          this.router.navigate(['/']);
        },
        error: (error) => {
          this.form.controls.token.setErrors({unknown: true});
          this.form.setErrors({unknown: true})
          this.loading.next(false);
          this.initialLoading.next(false);
        }
      }
    );
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const email = params.get('email');
      const token = params.get('token');
      this.form.controls.email.setValue(email || '');
      this.form.controls.token.setValue(token || '');

      if (!!email && !!token) {
        this.form.controls.email.setValue(email);
        this.form.controls.token.setValue(token);
        this.onSubmit();
      } else {
        this.initialLoading.next(false);
      }
    })
  }
}
