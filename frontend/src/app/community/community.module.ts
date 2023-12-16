import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CommunityRoutingModule } from './community-routing.module';
import { CommunityComponent } from './feature/community/community.component';
import {SharedModule} from "../shared/shared.module";
import {TranslateModule} from "@ngx-translate/core";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {MatIconModule} from "@angular/material/icon";
import {ReactiveFormsModule} from "@angular/forms";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatDividerModule} from "@angular/material/divider";


@NgModule({
  declarations: [
    CommunityComponent
  ],
    imports: [
        CommonModule,
        CommunityRoutingModule,
        SharedModule,
        TranslateModule,
        MatProgressSpinnerModule,
        MatButtonToggleModule,
        MatIconModule,
        ReactiveFormsModule,
        MatPaginatorModule,
        MatDividerModule
    ]
})
export class CommunityModule { }
