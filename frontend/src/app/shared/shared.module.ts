import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UsersService} from "./data-access/users/users.service";
import {ReviewsService} from "./data-access/reviews/reviews.service";
import {HttpClientModule} from "@angular/common/http";
import {AuthenticationService} from "./data-access/authentication/authentication.service";
import {NavbarComponent} from "./ui/navbar/navbar.component";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatInputModule} from "@angular/material/input";
import {LoginFormComponent} from "./ui/login-form/login-form.component";
import {MatCardModule} from "@angular/material/card";
import {FormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {TranslateModule} from "@ngx-translate/core";


@NgModule({
  declarations: [NavbarComponent, LoginFormComponent],
  providers: [UsersService, ReviewsService, AuthenticationService],
  imports: [
    CommonModule,
    HttpClientModule,
    MatToolbarModule,
    MatInputModule,
    MatCardModule,
    FormsModule,
    MatButtonModule,
    TranslateModule
  ],
  exports: [
    NavbarComponent,
    TranslateModule,
  ]
})
export class SharedModule {
}
