import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReviewDetailComponent } from './feature/review-detail/review-detail.component';
import { ReviewSubmitSearchComponent } from './feature/review-submit-search/review-submit-search.component';
import { ReviewSubmitComponent } from './feature/review-submit/review-submit.component';
import { ReviewEditComponent } from './feature/review-edit/review-edit.component';
import { isAuthenticatedGuard } from '../shared/guards/is-authenticated.guard';
import { isReviewAuthorGuard } from '../shared/guards/is-review-author.guard';

const routes: Routes = [
  { path: ':id', component: ReviewDetailComponent },
  {
    path: 'submit/search',
    component: ReviewSubmitSearchComponent,
    canActivate: [isAuthenticatedGuard],
  },
  {
    path: 'submit/:game_id',
    component: ReviewSubmitComponent,
    canActivate: [isAuthenticatedGuard],
  },
  {
    path: ':id/edit',
    component: ReviewEditComponent,
    canActivate: [isReviewAuthorGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ReviewsRoutingModule {}
