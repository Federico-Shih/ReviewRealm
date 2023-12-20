import {ChangeDetectionStrategy, Component, EventEmitter, Inject, Input, OnInit, Output,} from '@angular/core';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {Platform, ReviewFormType,} from '../../../shared/data-access/shared.enums';
import {Review} from '../../../shared/data-access/reviews/review.class';
import {Game} from '../../../shared/data-access/games/games.class';
import {ReviewSubmitDto} from '../../../shared/data-access/reviews/reviews.dtos';
import {AsyncPipe, CommonModule} from "@angular/common";
import {SharedModule} from "../../../shared/shared.module";
import {MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-review-form',
  templateUrl: './review-form.component.html',
  styleUrls: ['./review-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewFormComponent implements OnInit {
  @Input()
  loading: boolean | null = false;

  @Input({ required: true })
  game: Game | undefined;

  @Output()
  reviewFormEvent = new EventEmitter<ReviewSubmitDto | null>();

  @Input()
  editReview: Review | null = null;

  reviewForm = this.formBuilder.group({
    reviewTitle: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(100),
    ]),
    reviewContent: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
    reviewRating: new FormControl('', [
      Validators.required,
      Validators.min(1),
      Validators.max(10),
    ]),
    replayability: new FormControl(false),
    platform: new FormControl(''),
    difficulty: new FormControl(''),
    completed: new FormControl(false),
    gameLength: new FormControl('', [
      Validators.min(0),
      Validators.max(100000000),
    ]),
    unit: new FormControl('hours'),
  });

  @Input({ required: true })
  reviewFormType: ReviewFormType = ReviewFormType.Create;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly translateService: TranslateService,
    public dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    if (this.editReview !== null) {
      this.reviewForm.get('reviewTitle')?.setValue(this.editReview.title);
      this.reviewForm.get('reviewContent')?.setValue(this.editReview.content);
      this.reviewForm
        .get('reviewRating')
        ?.setValue(this.editReview.rating.toString());
      this.reviewForm
        .get('replayability')
        ?.setValue(this.editReview.replayable);
      this.reviewForm.get('platform')?.setValue(this.editReview.platform);
      this.reviewForm.get('difficulty')?.setValue(this.editReview.difficulty);
      this.reviewForm.get('completed')?.setValue(this.editReview.completed);
      const gameLengthInUnit = this.editReview.getGameLengthInUnits();
      if (gameLengthInUnit) {
        this.reviewForm
          .get('gameLength')
          ?.setValue(gameLengthInUnit.value.toString());
        this.reviewForm.get('unit')?.setValue(gameLengthInUnit.units);
      }
    }
  }

  cancel() {
    this.reviewFormEvent.emit(null);
  }

  submitReview() {
    if (this.reviewForm.status !== 'VALID' || this.loading) return;
    const {
      completed,
      platform,
      unit,
      difficulty,
      gameLength,
      replayability,
      reviewContent,
      reviewTitle,
      reviewRating,
    } = this.reviewForm.value;

    if (
      !reviewRating ||
      completed === null ||
      completed === undefined ||
      replayability === null ||
      replayability === undefined ||
      reviewContent === null ||
      reviewContent === undefined ||
      reviewTitle === null ||
      reviewTitle === undefined
    ) {
      return;
    }
    const dialogRef: MatDialogRef<ReviewConfirmDialog, boolean> = this.dialog.open(ReviewConfirmDialog, {
      data: {
        isCreation: this.editReview === null
      },
      width: '60%',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const dto: ReviewSubmitDto = {
          completed,
          replayability,
          reviewContent,
          reviewRating: parseInt(reviewRating),
          reviewTitle,
        };
        if (this.editReview == null && this.game) {
          dto['gameId'] = this.game?.id;
        }
        if (platform !== null && platform !== undefined) {
          dto['platform'] = platform.toString();
        }
        if (difficulty) {
          dto['difficulty'] = difficulty.toString();
        }
        if (gameLength && unit) {
          dto['gameLength'] = parseFloat(gameLength);
          dto['unit'] = unit;
        }
        this.reviewFormEvent.emit(dto);
      }
    });

  }

  protected readonly ReviewFormType = ReviewFormType;
  protected readonly Platform = Platform;
}

type Props = {
  isCreation: boolean;
}

@Component({
  selector: 'app-review-form-dialog',
  templateUrl: './review-form-confirm-dialog.html',
  imports: [MatDialogModule, SharedModule, AsyncPipe, CommonModule, MatButtonModule],
  standalone: true,
})
export class ReviewConfirmDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public props: Props) {
    this.isCreation = props.isCreation;
  }
  isCreation: boolean = true;
}
