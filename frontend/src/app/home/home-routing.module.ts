import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReviewSearchComponent } from './feature/review-search/review-search.component';

const routes: Routes = [{ path: '', component: ReviewSearchComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HomeRoutingModule {}
