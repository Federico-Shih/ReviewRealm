import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportsRoutingModule } from './reports-routing.module';
import { ReportListComponent } from './feature/report-list/report-list.component';
import { ReportCardComponent } from './ui/report-card/report-card.component';
import { SharedModule } from '../shared/shared.module';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
  declarations: [ReportListComponent, ReportCardComponent],
  imports: [
    CommonModule,
    ReportsRoutingModule,
    SharedModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
})
export class ReportsModule {}
