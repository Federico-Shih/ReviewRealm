import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SettingsRoutingModule } from './settings-routing.module';
import { SettingViewComponent } from './setting-view/setting-view.component';
import { NotificationsComponent } from './notifications/notifications.component';
import {TranslateModule} from "@ngx-translate/core";


@NgModule({
  declarations: [
    SettingViewComponent,
    NotificationsComponent
  ],
  imports: [
    CommonModule,
    SettingsRoutingModule,
    TranslateModule
  ]
})
export class SettingsModule { }
