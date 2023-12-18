import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GameListComponent } from './feature/game-list/game-list.component';
import { GameDetailComponent } from './feature/game-detail/game-detail.component';
import { GameSubmitComponent } from './feature/game-submit/game-submit.component';
import { GameEditComponent } from './feature/game-edit/game-edit.component';
import { isAuthenticatedGuard } from '../shared/guards/is-authenticated.guard';
import { isModeratorGuard } from '../shared/guards/is-moderator.guard';
import { GameSubmissionsComponent } from './feature/game-submissions/game-submissions.component';

const routes: Routes = [
  {
    path: 'submit',
    component: GameSubmitComponent,
    canActivate: [isAuthenticatedGuard],
  },
  {
    path: 'submissions',
    component: GameSubmissionsComponent,
    canActivate: [isModeratorGuard],
  },
  {
    path: ':id/edit',
    component: GameEditComponent,
    canActivate: [isModeratorGuard],
  },
  { path: ':id', component: GameDetailComponent },
  { path: '', component: GameListComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GamesRoutingModule {}
