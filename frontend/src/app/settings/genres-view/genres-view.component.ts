import { ChangeDetectionStrategy, Component} from '@angular/core';
import { UsersService } from '../../shared/data-access/users/users.service';
import { EnumsService } from '../../shared/data-access/enums/enums.service';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../../shared/data-access/authentication/authentication.service';
import { combineLatest, map, Observable, of, switchMap } from 'rxjs';
import { Genre } from '../../shared/data-access/enums/enums.class';
import { FormArray, FormBuilder, FormControl } from '@angular/forms';
import { GenresDto } from '../../shared/data-access/users/users.dtos';
import { Router } from '@angular/router';
import { mapCheckedToType } from '../../home/utils/mappers';

@Component({
  selector: 'app-genres-view',
  templateUrl: './genres-view.component.html',
  styleUrls: ['./genres-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GenresViewComponent{
  loggedInUser$ = this.authService.getLoggedUser();

  userId: number | null = null;


  favgenres$: Observable<Genre[]> = this.loggedInUser$.pipe(
    switchMap(user => {
      if (user !== null && user.links && user.links.preferences) {
        this.userId = user.id;

        return this.genreService.getGenres(user.links.preferences);
      } else {
        return of([]);
      }
    })
  );
  genresGroup = this.formBuilder.group({
    genres: this.formBuilder.array([]),
  });

  allgenres$: Observable<Genre[]> = this.genreService.getGenres(
    `${environment.API_ENDPOINT}/genres`
  );
  genres: Genre[] = [];
  completeGenres$ = combineLatest([this.favgenres$, this.allgenres$]).pipe(
    map(([favgenres, allgenres]) => {
      this.genres = allgenres;
      this.genres.forEach(() => {
        (this.genresGroup.get('genres') as FormArray).push(new FormControl(false));
      });
      const genreControl = this.genresGroup.get('genres') as FormArray;
      allgenres.forEach((genre, index) => {
        const isSelected = favgenres.find(fav => fav.id === genre.id) !== undefined;
        genreControl.at(index).setValue(isSelected);
      });
      return allgenres;
    }));

  constructor(
    private readonly genreService: EnumsService,
    private readonly authService: AuthenticationService,
    private formBuilder: FormBuilder,
    private readonly userService: UsersService,
    private readonly router: Router
  ) {}

  onSubmit() {
    const selectedGenres = this.genresGroup.get('genres')?.value as boolean[];
    const output = mapCheckedToType(selectedGenres, this.genres,genre => genre.id)

    const dto: GenresDto = { genres: output };

    this.userService
      .editUserGenres(`${environment.API_ENDPOINT}/users/${this.userId}`, dto)
      .subscribe(() => {
        this.router.navigate(['/profile', `${this.userId}`]);
      });
  }
}
