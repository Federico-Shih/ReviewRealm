import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {isAuthenticatedGuard} from "./shared/guards/is-authenticated.guard";

const routes: Routes = [
  {path: '', loadChildren: () => import('./home/home.module').then(m => m.HomeModule)},
  {path: 'login', loadChildren: () => import('./login/login.module').then(m => m.LoginModule)},
  {path: 'register', loadChildren: () => import('./register/register.module').then(m => m.RegisterModule)},
  {path: 'games', loadChildren: () => import('./games/games.module').then(m => m.GamesModule)},
  {path: 'search', loadChildren: () => import('./search/search.module').then(m => m.SearchModule)},
  {path: 'settings', loadChildren: () => import('./settings/settings.module').then(m => m.SettingsModule)},
  {path: 'profile', loadChildren: () => import('./users/profile.module').then(m => m.ProfileModule)},
  {path: 'reports', loadChildren: () => import('./reports/reports.module').then(m => m.ReportsModule)},
  {
    path: 'for-you',
    loadChildren: () => import('./main-feed/main-feed.module').then(m => m.MainFeedModule),
    canActivate: [isAuthenticatedGuard]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
