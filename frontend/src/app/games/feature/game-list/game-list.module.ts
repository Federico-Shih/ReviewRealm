import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GameListRoutingModule } from './game-list-routing.module';
import { GameListComponent } from './game-list.component';


@NgModule({
  declarations: [
    GameListComponent
  ],
  imports: [
    CommonModule,
    GameListRoutingModule
  ]
})
export class GameListModule { }
