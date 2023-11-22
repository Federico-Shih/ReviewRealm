import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HomeRoutingModule} from "./home-routing.module";
import {HomeComponent} from './ui/home-base/home.component';
import {SharedModule} from "../shared/shared.module";
import {HttpClientModule} from "@angular/common/http";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatDividerModule} from "@angular/material/divider";
import {MatRadioModule} from "@angular/material/radio";


@NgModule({
  declarations: [
    HomeComponent
  ],
  imports: [
    CommonModule, HomeRoutingModule, HttpClientModule,
    SharedModule, MatSidenavModule, MatDividerModule, MatRadioModule
  ]
})
export class HomeModule {
}
