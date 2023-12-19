import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MissionsRoutingModule } from './missions-routing.module';
import { MissionsListComponent } from './missions-list/missions-list.component';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';


@NgModule({
  declarations: [
    MissionsListComponent
  ],
  imports: [
    CommonModule,
    MissionsRoutingModule,
    MatProgressBarModule,
    TranslateModule,
  ],
})
export class MissionsModule { }
