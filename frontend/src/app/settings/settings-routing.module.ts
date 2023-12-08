import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {SettingViewComponent} from "./setting-view/setting-view.component";
import {NotificationsComponent} from "./notifications/notifications.component";
import {GenresViewComponent} from "./genres-view/genres-view.component";
import {AvatarComponent} from "./avatar/avatar.component";
import {FavGamesComponent} from "./fav-games/fav-games.component";

const routes: Routes = [
    { path: '', component: SettingViewComponent },
    { path: 'notifications', component: NotificationsComponent },
    { path: 'genres', component: GenresViewComponent },
    { path: 'avatar', component: AvatarComponent},
    { path: 'favgames', component: FavGamesComponent}
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SettingsRoutingModule { }
