import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReviewsRoutingModule } from './reviews-routing.module';
import { ReviewDetailComponent } from './feature/review-detail/review-detail.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {SharedModule} from "../shared/shared.module";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatCardModule} from "@angular/material/card";
import {MatDividerModule} from "@angular/material/divider";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatButtonModule} from "@angular/material/button";
import {MatChipsModule} from "@angular/material/chips";
import {MatDialogModule} from "@angular/material/dialog";
import { ReviewSubmitSearchComponent } from './feature/review-submit-search/review-submit-search.component';
import { ReviewSubmitComponent } from './feature/review-submit/review-submit.component';
import { ReviewEditComponent } from './feature/review-edit/review-edit.component';
import { ReviewFormComponent } from './ui/review-form/review-form.component';
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";


@NgModule({
  declarations: [
    ReviewDetailComponent,
    ReviewSubmitSearchComponent,
    ReviewSubmitComponent,
    ReviewEditComponent,
    ReviewFormComponent
  ],
  imports: [
    CommonModule,
    ReviewsRoutingModule,
    MatProgressSpinnerModule,
    SharedModule,
    MatGridListModule,
    MatCardModule,
    MatDividerModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule,
    MatChipsModule,
    MatDialogModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatSelectModule,
    MatCheckboxModule,
  ]
})
export class ReviewsModule { }
