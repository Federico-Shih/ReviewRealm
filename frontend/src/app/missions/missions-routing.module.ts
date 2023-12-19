import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MissionsListComponent } from './missions-list/missions-list.component';

const routes: Routes = [
  { path: '', component: MissionsListComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MissionsRoutingModule { }
