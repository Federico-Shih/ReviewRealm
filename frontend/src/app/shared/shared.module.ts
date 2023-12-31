import {NgModule} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {UsersService} from './data-access/users/users.service';
import {ReviewsService} from './data-access/reviews/reviews.service';
import {HttpClientModule} from '@angular/common/http';
import {NavbarComponent} from './ui/navbar/navbar.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatInputModule} from '@angular/material/input';
import {LoginFormComponent} from './ui/login-form/login-form.component';
import {MatCardModule} from '@angular/material/card';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {TranslateModule} from '@ngx-translate/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {FilterDrawerComponent} from './ui/filter-drawer/filter-drawer.component';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatRadioModule} from '@angular/material/radio';
import {MatListModule} from '@angular/material/list';
import {GamesService} from './data-access/games/games.service';
import {EnumsService} from './data-access/enums/enums.service';
import {ReviewCardComponent} from './ui/review-card/review-card.component';
import {MatChipsModule} from '@angular/material/chips';
import {ProfileCardComponent} from './ui/profile-card/profile-card.component';
import {TriCheckboxComponent} from './ui/tri-checkbox/tri-checkbox.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {SearchBarComponent} from './ui/searchbar/search-bar.component';
import {GameCardComponent} from './ui/game-card/game-card.component';
import {BreadcrumbsComponent} from './ui/breadcrumbs/breadcrumbs.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ReportsService} from './data-access/reports/reports.service';
import {CommunityGuidelinesComponent} from './ui/community-guidelines/community-guidelines.component';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {HeaderBannerComponent} from './ui/header-banner/header-banner.component';
import {LoadingSpinnerComponent} from './ui/loading-spinner/loading-spinner.component';
import {
  GameInfiniteLoadService,
  ReviewInfiniteLoadService,
  UserInfiniteLoadService
} from "./stores/infinite-load.service";

@NgModule({
  declarations: [
    NavbarComponent,
    LoginFormComponent,
    FilterDrawerComponent,
    ReviewCardComponent,
    ProfileCardComponent,
    TriCheckboxComponent,
    SearchBarComponent,
    GameCardComponent,
    BreadcrumbsComponent,
    HeaderBannerComponent,
    BreadcrumbsComponent,
    CommunityGuidelinesComponent,
    LoadingSpinnerComponent,
  ],
  providers: [
    UsersService,
    ReviewsService,
    GamesService,
    EnumsService,
    ReportsService,
    GameInfiniteLoadService,
    ReviewInfiniteLoadService,
    UserInfiniteLoadService
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    MatToolbarModule,
    MatInputModule,
    MatCardModule,
    FormsModule,
    MatButtonModule,
    TranslateModule,
    RouterLink,
    MatIconModule,
    ReactiveFormsModule,
    MatMenuModule,
    MatSidenavModule,
    MatRadioModule,
    MatListModule,
    NgOptimizedImage,
    MatChipsModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    RouterLinkActive,
  ],
  exports: [
    NavbarComponent,
    TranslateModule,
    LoginFormComponent,
    FilterDrawerComponent,
    ReviewCardComponent,
    TriCheckboxComponent,
    SearchBarComponent,
    ProfileCardComponent,
    GameCardComponent,
    BreadcrumbsComponent,
    HeaderBannerComponent,
    CommunityGuidelinesComponent,
    LoadingSpinnerComponent,
  ],
})
export class SharedModule {}
