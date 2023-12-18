import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ErrorsRoutingModule } from './errors-routing.module';
import { NotFoundComponent } from './feature/not-found/not-found.component';
import { ForbiddenComponent } from './feature/forbidden/forbidden.component';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [NotFoundComponent, ForbiddenComponent],
  imports: [CommonModule, ErrorsRoutingModule, SharedModule],
})
export class ErrorsModule {}
