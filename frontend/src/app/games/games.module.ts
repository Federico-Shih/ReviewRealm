import { NgModule } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';

import { GamesRoutingModule } from './games-routing.module';
import { GameDetailComponent } from './feature/game-detail/game-detail.component';
import { GameFormComponent } from './feature/game-form/game-form.component';
import { GameListComponent } from './feature/game-list/game-list.component';
import { GameCardComponent } from './ui/game-card/game-card.component';
import {MatCardModule} from "@angular/material/card";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatChipsModule} from "@angular/material/chips";
import {SharedModule} from "../shared/shared.module";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";


@NgModule({
  declarations: [
    GameDetailComponent,
    GameFormComponent,
    GameListComponent,
    GameCardComponent
  ],
  imports: [
    CommonModule,
    GamesRoutingModule,
    MatCardModule,
    NgOptimizedImage,
    MatGridListModule,
    MatChipsModule,
    SharedModule,
    MatProgressSpinnerModule
  ]
})
export class GamesModule { }
