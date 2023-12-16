import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {CommunityComponent} from "./feature/community/community.component";

const routes: Routes = [
  { path: '', component: CommunityComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CommunityRoutingModule { }
