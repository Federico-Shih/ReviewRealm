import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {isAuthenticatedGuard} from "./shared/guards/is-authenticated.guard";
import {isModeratorGuard} from "./shared/guards/is-moderator.guard";
import {notAuthenticatedGuard} from "./shared/guards/not-authenticated.guard";

const routes: Routes = [
  {path: '', loadChildren: () => import('./home/home.module').then(m => m.HomeModule)},
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
    canActivate: [notAuthenticatedGuard]
  },
  {
    path: 'register',
    loadChildren: () => import('./register/register.module').then(m => m.RegisterModule),
    canActivate: [notAuthenticatedGuard]
  },
  {path: 'games', loadChildren: () => import('./games/games.module').then(m => m.GamesModule)},
  {path: 'search', loadChildren: () => import('./search/search.module').then(m => m.SearchModule)},
  {
    path: 'settings',
    loadChildren: () => import('./settings/settings.module').then(m => m.SettingsModule),
    canActivate: [isAuthenticatedGuard]
  },
  {
    path: 'profile',
    loadChildren: () => import('./users/profile.module').then(m => m.ProfileModule),
  },
  {path: 'reviews', loadChildren: () => import('./reviews/reviews.module').then(m => m.ReviewsModule)},
  {
    path: 'reports',
    loadChildren: () => import('./reports/reports.module').then(m => m.ReportsModule),
    canActivate: [isModeratorGuard]
  },
  {
    path: 'for-you',
    loadChildren: () => import('./main-feed/main-feed.module').then(m => m.MainFeedModule),
    canActivate: [isAuthenticatedGuard]
  },
  {path: 'community', loadChildren: () => import('./community/community.module').then(m => m.CommunityModule)},
  {
    path: 'errors',
    loadChildren : () => import('./errors/errors.module').then(m => m.ErrorsModule),
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
