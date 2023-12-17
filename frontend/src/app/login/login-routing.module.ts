import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './feature/login-page/login.component';
import { RecoverPasswordComponent } from './feature/recover-password/recover-password.component';
import { ChangePasswordComponent } from './feature/change-password/change-password.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'recover-password', component: RecoverPasswordComponent },
  { path: 'change-password', component: ChangePasswordComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LoginRoutingModule {}
