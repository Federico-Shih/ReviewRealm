import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {GameListComponent} from "./feature/game-list/game-list.component";
import {GameDetailComponent} from "./feature/game-detail/game-detail.component";
import {GameSubmitComponent} from "./feature/game-submit/game-submit.component";
import {GameEditComponent} from "./feature/game-edit/game-edit.component";

const routes: Routes = [
  { path: 'submit', component: GameSubmitComponent },
  { path: ':id/edit', component: GameEditComponent },
  { path: '', component: GameListComponent },
  { path: ':id', component: GameDetailComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GamesRoutingModule { }
