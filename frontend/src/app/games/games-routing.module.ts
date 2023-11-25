import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {GameCardComponent} from "./ui/game-card/game-card.component";
import {GameListComponent} from "./feature/game-list/game-list.component";

const routes: Routes = [
  { path: '', component: GameListComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GamesRoutingModule { }
