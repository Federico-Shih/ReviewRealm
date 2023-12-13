import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {FormBuilder, FormControl, Validators} from "@angular/forms";
import {TranslateService} from "@ngx-translate/core";
import {Platform, ReviewFormType} from "../../../shared/data-access/shared.enums";
import {Review} from "../../../shared/data-access/reviews/review.class";
import {Game} from "../../../shared/data-access/games/games.class";
import {MatFormField} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {ReviewSubmitDto} from "../../../shared/data-access/reviews/reviews.dtos";

@Component({
  selector: 'app-review-form',
  templateUrl: './review-form.component.html',
  styleUrls: ['./review-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewFormComponent {
  @Input()
  loading: boolean | null = false;

  @Input({required: true})
  game: Game | undefined;

  @Output()
  reviewFormEvent = new EventEmitter<ReviewSubmitDto | null>();

  @Input()
  editReview:Review | null = null

  reviewForm = this.formBuilder.group({
    reviewTitle: new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(100)]),
    reviewContent: new FormControl('', [Validators.required, Validators.minLength(8)]),
    reviewRating: new FormControl('',[Validators.required, Validators.min(1), Validators.max(10)]),
    replayability: new FormControl(false),
    platform: new FormControl(''),
    difficulty: new FormControl(''),
    completed: new FormControl(false),
    gameLength: new FormControl('', [Validators.min(0), Validators.max(100000000)]),
    unit: new FormControl('hours')
  });

  @Input({required:true})
  reviewFormType:ReviewFormType = ReviewFormType.Create;


  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly translateService: TranslateService) {
  }

  ngOnInit(): void {
    if (this.editReview !== null) {
      this.reviewForm.get('reviewTitle')?.setValue(this.editReview.title);
      this.reviewForm.get('reviewContent')?.setValue(this.editReview.content);
      this.reviewForm.get('reviewRating')?.setValue(this.editReview.rating.toString());
      this.reviewForm.get('replayability')?.setValue(this.editReview.replayable);
      this.reviewForm.get('platform')?.setValue(this.editReview.platform);
      this.reviewForm.get('difficulty')?.setValue(this.editReview.difficulty);
      this.reviewForm.get('completed')?.setValue(this.editReview.completed);
      if(this.editReview.getGameLengthInUnits()) {
        this.reviewForm.get('gameLength')?.setValue(this.editReview.getGameLengthInUnits()!.value.toString());
        this.reviewForm.get('unit')?.setValue(this.editReview.getGameLengthInUnits()!.units);
      }
    }
  }

  cancel() {
    this.reviewFormEvent.emit(null);
  }

  submitReview() {
    if (this.reviewForm.status !== 'VALID' || this.loading)
      return;
    if (!this.reviewForm.get('reviewRating')?.value) {
      return;
    }
    const dto: ReviewSubmitDto = {
      completed: this.reviewForm.get('completed')?.value!,
      replayability: this.reviewForm.get('replayability')?.value!,
      reviewContent: this.reviewForm.get('reviewContent')?.value!,
      reviewRating: parseInt(this.reviewForm.get('reviewRating')?.value!),
      reviewTitle: this.reviewForm.get('reviewTitle')?.value!,
    }
    if (this.editReview == null && this.game) {
      dto['gameId'] = this.game?.id
    }
    if (this.reviewForm.get('platform')?.value) {
      dto['platform'] = this.reviewForm.get('platform')?.value!.toString()!;
    }
    if (this.reviewForm.get('difficulty')?.value) {
      dto['difficulty'] =  this.reviewForm.get('difficulty')?.value!.toString()!;
    }
    if (this.reviewForm.get('gameLength')?.value) {
      dto['gameLength'] = parseFloat(this.reviewForm.get('gameLength')?.value!);
      dto['unit'] = this.reviewForm.get('unit')?.value!;
    }
    this.reviewFormEvent.emit(dto);
  }

  protected readonly ReviewFormType = ReviewFormType;
  protected readonly Platform = Platform;
}

//TODO: confiramtion dialog
