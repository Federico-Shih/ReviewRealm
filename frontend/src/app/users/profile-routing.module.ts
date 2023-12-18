import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileDetailComponent } from './feature/profile-detail/profile-detail.component';
import {FollowersComponent} from "./feature/followers/followers.component";
import {FollowingComponent} from "./feature/following/following.component";

const routes: Routes =
    [{ path: ':id', component: ProfileDetailComponent },
    { path: ':id/followers', component: FollowersComponent },
      { path: ':id/following', component: FollowingComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProfileRoutingModule {}
