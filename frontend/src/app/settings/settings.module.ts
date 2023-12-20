import { NgModule } from '@angular/core';
import { CommonModule, NgOptimizedImage } from '@angular/common';

import { SettingsRoutingModule } from './settings-routing.module';
import { SettingViewComponent } from './setting-view/setting-view.component';
import { NotificationsComponent } from './notifications/notifications.component';
import { TranslateModule } from '@ngx-translate/core';
import { MatRadioModule } from '@angular/material/radio';
import { GenresViewComponent } from './genres-view/genres-view.component';
import { AvatarComponent } from './avatar/avatar.component';
import { FavGamesComponent } from './fav-games/fav-games.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import {MatTooltipModule} from "@angular/material/tooltip";

@NgModule({
  declarations: [
    SettingViewComponent,
    NotificationsComponent,
    GenresViewComponent,
    AvatarComponent,
    FavGamesComponent,
  ],
    imports: [
        CommonModule,
        SettingsRoutingModule,
        TranslateModule,
        MatRadioModule,
        NgOptimizedImage,
        ReactiveFormsModule,
        SharedModule,
        MatGridListModule,
        MatDividerModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatProgressBarModule,
        MatCheckboxModule,
        MatButtonModule,
        MatTooltipModule,
    ],
})
export class SettingsModule {}
