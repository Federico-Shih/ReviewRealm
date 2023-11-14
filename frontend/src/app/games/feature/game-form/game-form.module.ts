import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GameFormRoutingModule } from './game-form-routing.module';
import { GameFormComponent } from './game-form.component';


@NgModule({
  declarations: [
    GameFormComponent
  ],
  imports: [
    CommonModule,
    GameFormRoutingModule
  ]
})
export class GameFormModule { }
