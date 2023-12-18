import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainFeedRoutingModule } from './main-feed-routing.module';
import { MainFeedPageComponent } from './feature/main-feed-page/main-feed-page.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { MatGridListModule } from '@angular/material/grid-list';
import { SharedModule } from '../shared/shared.module';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RecommendationsPageComponent } from './feature/recommendations-page/recommendations-page.component';
import { GamesModule } from '../games/games.module';
import { MatButtonModule } from '@angular/material/button';

@NgModule({
  declarations: [MainFeedPageComponent, RecommendationsPageComponent],
  imports: [
    CommonModule,
    MainFeedRoutingModule,
    MatButtonToggleModule,
    TranslateModule,
    MatIconModule,
    ReactiveFormsModule,
    MatGridListModule,
    SharedModule,
    MatProgressSpinnerModule,
    GamesModule,
    MatButtonModule,
  ],
})
export class MainFeedModule {}
