import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {NotFoundComponent} from "./feature/not-found/not-found.component";
import {ForbiddenComponent} from "./feature/forbidden/forbidden.component";

const routes: Routes = [
  { path: 'not-found', component: NotFoundComponent },
  { path: 'forbidden', component: ForbiddenComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ErrorsRoutingModule { }
