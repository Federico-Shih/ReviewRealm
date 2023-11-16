import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {RegisterRoutingModule} from './register-routing.module';
import {RegisterComponent} from './feature/register.component';
import {RegisterFormComponent} from './ui/register-form/register-form.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {TranslateModule} from "@ngx-translate/core";
import {SharedModule} from "../shared/shared.module";


@NgModule({
  declarations: [
    RegisterComponent,
    RegisterFormComponent
  ],
  imports: [
    CommonModule,
    RegisterRoutingModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    TranslateModule,
    SharedModule
  ]
})
export class RegisterModule {
}
