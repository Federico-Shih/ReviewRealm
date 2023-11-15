import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UsersService} from "./data-access/users/users.service";
import {ReviewsService} from "./data-access/reviews/reviews.service";
import {HttpClientModule} from "@angular/common/http";
import {NavbarComponent} from "./ui/navbar/navbar.component";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatInputModule} from "@angular/material/input";
import {LoginFormComponent} from "./ui/login-form/login-form.component";
import {MatCardModule} from "@angular/material/card";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {TranslateModule} from "@ngx-translate/core";
import {RouterLink} from "@angular/router";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";


@NgModule({
  declarations: [NavbarComponent, LoginFormComponent],
  providers: [UsersService, ReviewsService],
  imports: [
    CommonModule,
    HttpClientModule,
    MatToolbarModule,
    MatInputModule,
    MatCardModule,
    FormsModule,
    MatButtonModule,
    TranslateModule,
    RouterLink,
    MatIconModule,
    ReactiveFormsModule,
    MatMenuModule
  ],
  exports: [
    NavbarComponent,
    TranslateModule,
    LoginFormComponent,
  ]
})
export class SharedModule {
}
