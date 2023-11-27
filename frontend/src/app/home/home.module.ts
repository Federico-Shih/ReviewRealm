import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HomeRoutingModule} from "./home-routing.module";
import {SharedModule} from "../shared/shared.module";
import {HttpClientModule} from "@angular/common/http";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatDividerModule} from "@angular/material/divider";
import {MatRadioModule} from "@angular/material/radio";
import {ReviewSearchComponent} from './feature/review-search/review-search.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatIconModule} from "@angular/material/icon";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {ReactiveFormsModule} from "@angular/forms";
import {MatSliderModule} from "@angular/material/slider";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginatorModule} from "@angular/material/paginator";


@NgModule({
  declarations: [
    ReviewSearchComponent
  ],
  imports: [
    CommonModule, HomeRoutingModule, HttpClientModule,
    SharedModule, MatSidenavModule, MatDividerModule, MatRadioModule, MatProgressSpinnerModule, MatGridListModule, MatExpansionModule, MatIconModule, MatCheckboxModule, ReactiveFormsModule, MatSliderModule, MatFormFieldModule, MatButtonModule, MatPaginatorModule
  ]
})
export class HomeModule {
}
