import { NgModule } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';

import { SettingsRoutingModule } from './settings-routing.module';
import { SettingViewComponent } from './setting-view/setting-view.component';
import { NotificationsComponent } from './notifications/notifications.component';
import {TranslateModule} from "@ngx-translate/core";
import {MatRadioModule} from "@angular/material/radio";
import { GenresViewComponent } from './genres-view/genres-view.component';
import { AvatarComponent } from './avatar/avatar.component';
import { FavGamesComponent } from './fav-games/fav-games.component';


@NgModule({
  declarations: [
    SettingViewComponent,
    NotificationsComponent,
    GenresViewComponent,
    AvatarComponent,
    FavGamesComponent
  ],
  imports: [
    CommonModule,
    SettingsRoutingModule,
    TranslateModule,
    MatRadioModule,
    NgOptimizedImage
  ]
})
export class SettingsModule { }
