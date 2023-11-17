import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RegisterComponent} from "./feature/register-base/register.component";
import {ValidateUserComponent} from "./feature/validate-user/validate-user.component";

const routes: Routes = [{
  path: '',
  component: RegisterComponent
}, {
  path: 'validate',
  component: ValidateUserComponent
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RegisterRoutingModule {
}
