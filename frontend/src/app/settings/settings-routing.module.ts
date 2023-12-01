import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {SettingViewComponent} from "./setting-view/setting-view.component";
import {NotificationsComponent} from "./notifications/notifications.component";

const routes: Routes = [
    { path: '', component: SettingViewComponent },
    { path: 'notifications', component: NotificationsComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SettingsRoutingModule { }
