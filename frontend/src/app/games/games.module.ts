import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GamesRoutingModule } from './games-routing.module';
import { GameDetailComponent } from './feature/game-detail/game-detail.component';
import { GameFormComponent } from './feature/game-form/game-form.component';
import { GameListComponent } from './feature/game-list/game-list.component';


@NgModule({
  declarations: [
    GameDetailComponent,
    GameFormComponent,
    GameListComponent
  ],
  imports: [
    CommonModule,
    GamesRoutingModule
  ]
})
export class GamesModule { }
