import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProfileRoutingModule } from './profile-routing.module';
import { ProfileDetailComponent } from './feature/profile-detail/profile-detail.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { UserDetailsComponent } from './ui/user-details/user-details.component';
import { FavGamesComponent } from './ui/fav-games/fav-games.component';
import { SharedModule } from '../shared/shared.module';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { FollowersComponent } from './feature/followers/followers.component';
import { FollowingComponent } from './feature/following/following.component';
import {MatTooltipModule} from "@angular/material/tooltip";

@NgModule({
  declarations: [
    ProfileDetailComponent,
    UserDetailsComponent,
    FavGamesComponent,
    FollowersComponent,
    FollowingComponent,
  ],
    imports: [
        CommonModule,
        ProfileRoutingModule,
        MatGridListModule,
        MatProgressSpinnerModule,
        SharedModule,
        MatPaginatorModule,
        MatButtonModule,
        MatChipsModule,
        MatTooltipModule,
    ],
})
export class ProfileModule {}
