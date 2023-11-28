import {NgModule} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';

import { GamesRoutingModule } from './games-routing.module';
import { GameDetailComponent } from './feature/game-detail/game-detail.component';
import { GameFormComponent } from './feature/game-form/game-form.component';
import { GameListComponent } from './feature/game-list/game-list.component';
import {MatCardModule} from "@angular/material/card";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatChipsModule} from "@angular/material/chips";
import {SharedModule} from "../shared/shared.module";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatButtonModule} from "@angular/material/button";
import {ReactiveFormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import {MatSliderModule} from "@angular/material/slider";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatListModule} from "@angular/material/list";


@NgModule({
  declarations: [
    GameDetailComponent,
    GameFormComponent,
    GameListComponent
  ],
  imports: [
    CommonModule,
    GamesRoutingModule,
    MatCardModule,
    NgOptimizedImage,
    MatGridListModule,
    MatChipsModule,
    SharedModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatSidenavModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatSliderModule,
    MatExpansionModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatListModule
  ]
})
export class GamesModule { }
