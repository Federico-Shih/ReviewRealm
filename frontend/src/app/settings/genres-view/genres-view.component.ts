import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {UsersService} from "../../shared/data-access/users/users.service";
import {EnumsService} from "../../shared/data-access/enums/enums.service";
import {environment} from "../../../environments/environment";
import {AuthenticationService} from "../../shared/data-access/authentication/authentication.service";
import {combineLatest, map, Observable, of, switchMap} from "rxjs";
import {Genre} from "../../shared/data-access/enums/enums.class";
import {FormArray, FormBuilder, FormControl} from "@angular/forms";
import {GenresDto} from "../../shared/data-access/users/users.dtos";
import {Router} from "@angular/router";

@Component({
  selector: 'app-genres-view',
  templateUrl: './genres-view.component.html',
  styleUrls: ['./genres-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GenresViewComponent implements OnInit{

  loggedInUser$ = this.authService.getLoggedUser();

  userId:Number|null = null;

  allgenres$!: Observable<Genre[]>;
  favgenres$!: Observable<Genre[]>;

  genresGroup = this.formBuilder.group({
    genres: this.formBuilder.array([])
  }) ;

  constructor(private readonly genreService: EnumsService, private readonly authService:AuthenticationService, private formBuilder: FormBuilder, private readonly userService: UsersService, private readonly router:Router) {
  }

  ngOnInit(): void {
    this.favgenres$ = this.loggedInUser$.pipe(
      switchMap((user) => {

        if (user !== null && user.links && user.links.preferences) {

          this.userId = user.id;

          return this.genreService.getGenres(user.links.preferences);
        } else {
          return of([]);
        }
      })
    );
    this.allgenres$ = this.genreService.getGenres(`${environment.API_ENDPOINT}/genres`);

    combineLatest([this.favgenres$, this.allgenres$]).subscribe(([favgenres, allgenres]) => {
      const favGenreIds = favgenres.map(fav => fav.id);
      const filteredGenres = allgenres.filter(genre => !favGenreIds.includes(genre.id));
      this.allgenres$ = of(filteredGenres);

      allgenres.forEach((genre) => {
        this.genres.push(new FormControl(false));
      });

      favGenreIds.forEach((genre) => {
        this.genres.at(genre-1).setValue(true);
      })

    });
  }

  get genres(): FormArray {
    return this.genresGroup.get('genres') as FormArray;
  }


  onSubmit() {
    const newPreferences:number[] = []

    this.genresGroup.get('genres')?.value.forEach((value, index) => {
      if(value)
        newPreferences.push(index+1);
    })

    const dto:GenresDto = { genres: newPreferences};

    this.userService.editUserGenres(`${environment.API_ENDPOINT}/users/${this.userId}`, dto).subscribe(
      (genres) => {
        this.router.navigate(['/profile',`${this.userId}`]);
      },
    )

  }
}
