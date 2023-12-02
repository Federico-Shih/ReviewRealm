import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainFeedPageComponent} from "./feature/main-feed-page/main-feed-page.component";

const routes: Routes = [{
  path: '',
  component: MainFeedPageComponent
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainFeedRoutingModule {
}
