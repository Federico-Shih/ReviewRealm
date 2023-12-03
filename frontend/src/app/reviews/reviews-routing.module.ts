import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ReviewDetailComponent} from "./feature/review-detail/review-detail.component";

const routes: Routes = [
  {path: ':id', component: ReviewDetailComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReviewsRoutingModule { }
