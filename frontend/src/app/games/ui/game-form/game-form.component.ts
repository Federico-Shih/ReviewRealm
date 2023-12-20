import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  Validators,
} from '@angular/forms';
import { Genre } from '../../../shared/data-access/enums/enums.class';
import { Game } from '../../../shared/data-access/games/games.class';
import { mapCheckedToType } from '../../../home/utils/mappers';
import {GameFormType, ReviewFormType} from '../../../shared/data-access/shared.enums';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-game-form',
  templateUrl: './game-form.component.html',
  styleUrls: ['./game-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class GameFormComponent implements OnInit {
  @Input()
  loading: boolean | null = false;

  @Input({ required: true })
  genres: Genre[] = [];

  @Output()
  gameFormEvent = new EventEmitter<FormData | null>();

  genreError = false;

  @Input()
  editGame: Game | null = null;

  gameForm = this.formBuilder.group({
    name: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(80),
    ]),
    description: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(300),
    ]),
    developer: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(50),
    ]),
    publisher: new FormControl('', [
      Validators.required,
      Validators.minLength(1),
      Validators.maxLength(50),
    ]),
    genres: this.formBuilder.array([]),
    releaseDate: new FormControl('', [Validators.required]),
    _image: new FormControl('', [Validators.required]), //Dummy
  });

  temporaryImage: File | null = null;

  imageURL = '';

  @Input({ required: true })
  gameFormType: GameFormType = GameFormType.Suggest;

  handleImage(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files === null) {
      return;
    }
    const image: File = target.files[0];

    const allowedMediaTypes = ['image/png', 'image/jpeg', 'image/gif'];
    const maxFileSizeInBytes = 128 * 1024 * 1024; // 128 MB
    this.gameForm.get('_image')?.setValue('');
    this.temporaryImage = null;
    this.imageURL = '';
    if (!allowedMediaTypes.includes(image.type)) {
      this.gameForm.get('_image')?.setErrors({ 'invalid-file-type': true });
      return;
    }
    if (image.size > maxFileSizeInBytes) {
      this.gameForm.get('_image')?.setErrors({ 'invalid-file-size': true });
      return;
    }
    this.gameForm.get('_image')?.setValue(image.name);
    this.temporaryImage = image;
    this.imageURL = URL.createObjectURL(image);
  }
  maxDate = new Date();

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.genres.forEach(() => {
      (this.gameForm.get('genres') as FormArray).push(new FormControl(false));
    });
    if (this.editGame !== null) {
      this.gameForm.get('name')?.setValue(this.editGame.name);
      this.gameForm.get('description')?.setValue(this.editGame.description);
      this.gameForm.get('developer')?.setValue(this.editGame.developer);
      this.gameForm.get('publisher')?.setValue(this.editGame.publisher);
      this.gameForm
        .get('_image')
        ?.setValue(this.translateService.instant('game-image'));
      this.gameForm
        .get('releaseDate')
        ?.setValue(this.editGame.publishDate.toISOString());
      const gameGenres = this.editGame.genres;
      this.imageURL = this.editGame.links.image;
      if (gameGenres !== undefined) {
        const genreControl = this.gameForm.get('genres') as FormArray;
        this.genres.forEach((genre, index) => {
          const isSelected =
            gameGenres.find(selection => selection.id === genre.id) !==
            undefined;
          genreControl.at(index).setValue(isSelected);
        });
      }
    }
  }

  cancel() {
    this.gameFormEvent.emit(null);
  }

  createGame() {
    const selectedGenres = this.gameForm.get('genres')?.value as boolean[];

    if (selectedGenres.every((value: boolean) => value !== null && !value)) {
      this.genreError = true;
      setTimeout(() => {
        this.genreError = false;
      }, 2000);
      return;
    }
    const name = this.gameForm.get('name')?.value;
    const description = this.gameForm.get('description')?.value;
    const developer = this.gameForm.get('developer')?.value;
    const publisher = this.gameForm.get('publisher')?.value;
    const date = this.gameForm.get('releaseDate')?.value;
    if (
      this.gameForm.status !== 'VALID' ||
      !name ||
      !description ||
      !developer ||
      !publisher ||
      !date
    ) {
      return;
    }
    const output = new FormData();
    output.append('name', name);
    output.append('description', description);
    output.append('developer', developer);
    output.append('publisher', publisher);
    const isoDate = new Date(date).toISOString();
    output.append('releaseDate', isoDate.split('T')[0]);
    if (this.temporaryImage !== null) {
      //Caso en edit donde no cambiaste la imagen
      output.append('image', this.temporaryImage);
    }
    const output_genres = mapCheckedToType(
      selectedGenres as boolean[],
      this.genres as Genre[],
      genre => genre.id
    );
    output_genres.map(value => {
      output.append('genres', value.toString());
    });
    this.gameFormEvent.emit(output);
  }

  protected readonly GameFormType = GameFormType;
}
