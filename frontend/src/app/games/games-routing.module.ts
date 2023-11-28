import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {GameListComponent} from "./feature/game-list/game-list.component";
import {GameDetailComponent} from "./feature/game-detail/game-detail.component";

const routes: Routes = [
  { path: '', component: GameListComponent },
  { path: ':id', component: GameDetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GamesRoutingModule { }
