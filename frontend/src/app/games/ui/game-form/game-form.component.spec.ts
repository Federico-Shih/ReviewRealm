import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameFormComponent } from './game-form.component';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { GamesRoutingModule } from '../../games-routing.module';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatChipsModule } from '@angular/material/chips';
import { SharedModule } from '../../../shared/shared.module';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { FormArray, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatSliderModule } from '@angular/material/slider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatListModule } from '@angular/material/list';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { genresMock4 } from '../../../../tests/mocks/enums.mock';
import { gameMock3 } from '../../../../tests/mocks/games.mock';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('GameFormComponent', () => {
  let component: GameFormComponent;
  let fixture: ComponentFixture<GameFormComponent>;
  const LONG_TEXT = 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo';
  const SHORT_TEXT = 'a';
  const genres = genresMock4;
  const game = gameMock3;
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameFormComponent],
      imports: [
        CommonModule,
        GamesRoutingModule,
        MatCardModule,
        NgOptimizedImage,
        MatGridListModule,
        MatChipsModule,
        SharedModule,
        MatProgressSpinnerModule,
        MatPaginatorModule,
        MatSidenavModule,
        MatButtonModule,
        ReactiveFormsModule,
        MatIconModule,
        MatSliderModule,
        MatExpansionModule,
        MatCheckboxModule,
        TranslateModule.forRoot(),
        MatTooltipModule,
        MatListModule,
        FormsModule,
        NoopAnimationsModule,
        MatInputModule,
        MatDatepickerModule,
        MatSnackBarModule,
        MatNativeDateModule,
        MatFormFieldModule,
        MatDialogModule
      ],
      schemas: [NO_ERRORS_SCHEMA],
    });
    fixture = TestBed.createComponent(GameFormComponent);
    component = fixture.componentInstance;
    component.genres = genres;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`submits correctly when all fields have been completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).toHaveBeenCalled();
  });
  it(`no submit when name was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue('');
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when description was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue('');
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when developer was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue('');
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when publisher was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue('');
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when release date was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue('');
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });
  it(`no submit when genres was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([false,false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when image was not completed`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue('');

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when name is too long`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(LONG_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when description is too long`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(LONG_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`no submit when developer is too long`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(LONG_TEXT);
    component.gameForm.get('publisher')?.setValue(SHORT_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });
  it(`no submit when publisher is too long`, () => {
    fixture.detectChanges();
    component.gameForm.get('name')?.setValue(SHORT_TEXT);
    component.gameForm.get('description')?.setValue(SHORT_TEXT);
    component.gameForm.get('developer')?.setValue(SHORT_TEXT);
    component.gameForm.get('publisher')?.setValue(LONG_TEXT);
    component.gameForm.get('releaseDate')?.setValue(new Date().toISOString());
    component.gameForm.get('genres')?.setValue([true, false]);
    component.gameForm.get('_image')?.setValue(SHORT_TEXT);

    spyOn(component.gameFormEvent, 'emit');

    component.createGame();
    expect(component.gameFormEvent.emit).not.toHaveBeenCalled();
  });

  it(`should complete form group if editing game` ,() =>{
    component.editGame = game
    fixture.detectChanges();

    expect(component.gameForm.get('name')?.value).toEqual(game.name);
    expect(component.gameForm.get('description')?.value).toEqual(game.description);
    expect(component.gameForm.get('developer')?.value).toEqual(game.developer);
    expect(component.gameForm.get('publisher')?.value).toEqual(game.publisher);
    expect(component.gameForm.get('releaseDate')?.value).toEqual(game.publishDate.toISOString());

    const genreControl = component.gameForm.get('genres') as FormArray;
    expect(genreControl.at(0).value).toEqual(true);
    expect(genreControl.at(1).value).toEqual(false);

    expect(component.imageURL).toEqual(game.links.image);

  });


});
