import {NgModule} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';

import {GamesRoutingModule} from './games-routing.module';
import {GameDetailComponent} from './feature/game-detail/game-detail.component';
import {GameFormComponent} from './ui/game-form/game-form.component';
import {GameListComponent} from './feature/game-list/game-list.component';
import {MatCardModule} from "@angular/material/card";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatChipsModule} from "@angular/material/chips";
import {SharedModule} from "../shared/shared.module";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatButtonModule} from "@angular/material/button";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIconModule} from "@angular/material/icon";
import {MatSliderModule} from "@angular/material/slider";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatListModule} from "@angular/material/list";
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {GameSubmitComponent} from './feature/game-submit/game-submit.component';
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatNativeDateModule} from "@angular/material/core";
import {MatFormFieldModule} from "@angular/material/form-field";
import {GameEditComponent} from './feature/game-edit/game-edit.component';
import {MatDialogModule} from "@angular/material/dialog";
import {GameDetailPageComponent} from "./ui/game-detail-page/game-detail-page.component";
import { GameSubmissionsComponent } from './feature/game-submissions/game-submissions.component';
import { GameSubmissionCardComponent } from './ui/game-submission-card/game-submission-card.component';

@NgModule({
  declarations: [
    GameDetailComponent,
    GameFormComponent,
    GameListComponent,
    GameSubmitComponent,
    GameEditComponent,
    GameDetailPageComponent,
    GameSubmissionsComponent,
    GameSubmissionCardComponent
  ],
  exports: [
    GameDetailPageComponent
  ],
  imports: [
    CommonModule,
    GamesRoutingModule,
    MatCardModule,
    NgOptimizedImage,
    MatGridListModule,
    MatChipsModule,
    SharedModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatSidenavModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatIconModule,
    MatSliderModule,
    MatExpansionModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatListModule,
    FormsModule,
    MatInputModule,
    MatDatepickerModule,
    MatSnackBarModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatDialogModule,
  ]
})
export class GamesModule { }
