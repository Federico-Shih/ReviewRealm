import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ReviewFormComponent} from './review-form.component';
import {ReviewSubmitDto} from "../../../shared/data-access/reviews/reviews.dtos";
import {CommonModule} from "@angular/common";
import {ReviewsRoutingModule} from "../../reviews-routing.module";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {SharedModule} from "../../../shared/shared.module";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatCardModule} from "@angular/material/card";
import {MatDividerModule} from "@angular/material/divider";
import {MatIconModule} from "@angular/material/icon";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatButtonModule} from "@angular/material/button";
import {MatChipsModule} from "@angular/material/chips";
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatInputModule} from "@angular/material/input";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {NO_ERRORS_SCHEMA} from "@angular/core";
import {TranslateModule} from "@ngx-translate/core";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {of} from "rxjs";
import {gameMock} from "../../../../tests/mocks/games.mock";

describe('ReviewFormComponent', () => {
    let component: ReviewFormComponent;
    let fixture: ComponentFixture<ReviewFormComponent>;
    const TITLE_EXPECTED = 'test title';
    const CONTENT_EXPECTED = 'test content';
    const RATING_EXPECTED = '10';
    const REPLAYABILITY_EXPECTED = true;
    const PLATFORM_EXPECTED = 'PC';
    const DIFFICULTY_EXPECTED = 'Hard';
    const COMPLETED_EXPECTED = true;
    const GAME_LENGTH_EXPECTED = '10';
    const UNIT_EXPECTED = 'hours';

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [ReviewFormComponent],
            imports: [
                CommonModule,
                ReviewsRoutingModule,
                MatProgressSpinnerModule,
                SharedModule,
                MatGridListModule,
                MatCardModule,
                MatDividerModule,
                MatIconModule,
                MatTooltipModule,
                MatButtonModule,
                MatChipsModule,
                MatDialogModule,
                MatInputModule,
                FormsModule,
                ReactiveFormsModule,
                MatSelectModule,
                MatCheckboxModule,
                BrowserAnimationsModule,
                TranslateModule.forRoot()
            ],
            providers: [],
            schemas: [NO_ERRORS_SCHEMA]
        });
        fixture = TestBed.createComponent(ReviewFormComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('submits correctly when all fields are set', waitForAsync(() => {
        component.reviewForm.get('reviewTitle')?.setValue(TITLE_EXPECTED);
        component.reviewForm.get('reviewContent')?.setValue(CONTENT_EXPECTED);
        component.reviewForm.get('reviewRating')?.setValue(RATING_EXPECTED);
        component.reviewForm.get('replayability')?.setValue(REPLAYABILITY_EXPECTED);
        component.reviewForm.get('platform')?.setValue(PLATFORM_EXPECTED);
        component.reviewForm.get('difficulty')?.setValue(DIFFICULTY_EXPECTED);
        component.reviewForm.get('completed')?.setValue(COMPLETED_EXPECTED);
        component.reviewForm.get('gameLength')?.setValue(GAME_LENGTH_EXPECTED);
        component.reviewForm.get('unit')?.setValue(UNIT_EXPECTED);
        component.game = gameMock;
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        const dto: ReviewSubmitDto = {
            reviewTitle: TITLE_EXPECTED,
            reviewContent: CONTENT_EXPECTED,
            reviewRating: parseInt(RATING_EXPECTED),
            replayability: REPLAYABILITY_EXPECTED,
            platform: PLATFORM_EXPECTED,
            difficulty: DIFFICULTY_EXPECTED,
            completed: COMPLETED_EXPECTED,
            gameLength: parseInt(GAME_LENGTH_EXPECTED),
            unit: UNIT_EXPECTED,
            gameId: gameMock.id,
        }
        expect(component.reviewFormEvent.emit).toHaveBeenCalledOnceWith(
            dto
        );
    }));

    it('no submit when title is not set', waitForAsync(() => {
        component.reviewForm.get('reviewContent')?.setValue(CONTENT_EXPECTED);
        component.reviewForm.get('reviewRating')?.setValue(RATING_EXPECTED);
        component.reviewForm.get('replayability')?.setValue(REPLAYABILITY_EXPECTED);
        component.reviewForm.get('platform')?.setValue(PLATFORM_EXPECTED);
        component.reviewForm.get('difficulty')?.setValue(DIFFICULTY_EXPECTED);
        component.reviewForm.get('completed')?.setValue(COMPLETED_EXPECTED);
        component.reviewForm.get('gameLength')?.setValue(GAME_LENGTH_EXPECTED);
        component.reviewForm.get('unit')?.setValue(UNIT_EXPECTED);
        component.game = gameMock;
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        expect(component.reviewFormEvent.emit).not.toHaveBeenCalled();
    }));

    it('no submit when content is not set', waitForAsync(() => {
        component.reviewForm.get('reviewTitle')?.setValue(TITLE_EXPECTED);
        component.reviewForm.get('reviewRating')?.setValue(RATING_EXPECTED);
        component.reviewForm.get('replayability')?.setValue(REPLAYABILITY_EXPECTED);
        component.reviewForm.get('platform')?.setValue(PLATFORM_EXPECTED);
        component.reviewForm.get('difficulty')?.setValue(DIFFICULTY_EXPECTED);
        component.reviewForm.get('completed')?.setValue(COMPLETED_EXPECTED);
        component.reviewForm.get('gameLength')?.setValue(GAME_LENGTH_EXPECTED);
        component.reviewForm.get('unit')?.setValue(UNIT_EXPECTED);
        component.game = gameMock;
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        expect(component.reviewFormEvent.emit).not.toHaveBeenCalled();
    }));

    it('no submit when rating is not set', waitForAsync(() => {
        component.reviewForm.get('reviewTitle')?.setValue(TITLE_EXPECTED);
        component.reviewForm.get('reviewContent')?.setValue(CONTENT_EXPECTED);
        component.reviewForm.get('replayability')?.setValue(REPLAYABILITY_EXPECTED);
        component.reviewForm.get('platform')?.setValue(PLATFORM_EXPECTED);
        component.reviewForm.get('difficulty')?.setValue(DIFFICULTY_EXPECTED);
        component.reviewForm.get('completed')?.setValue(COMPLETED_EXPECTED);
        component.reviewForm.get('gameLength')?.setValue(GAME_LENGTH_EXPECTED);
        component.reviewForm.get('unit')?.setValue(UNIT_EXPECTED);
        component.game = gameMock;
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        expect(component.reviewFormEvent.emit).not.toHaveBeenCalled();
    }));

    it('submits correctly when all not optional fields', waitForAsync(() => {
        component.reviewForm.get('reviewTitle')?.setValue(TITLE_EXPECTED);
        component.reviewForm.get('reviewContent')?.setValue(CONTENT_EXPECTED);
        component.reviewForm.get('reviewRating')?.setValue(RATING_EXPECTED);
        component.reviewForm.get('platform')?.setValue(null);
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        const dto: ReviewSubmitDto = {
            reviewTitle: TITLE_EXPECTED,
            reviewContent: CONTENT_EXPECTED,
            reviewRating: parseInt(RATING_EXPECTED),
            replayability: false,
            completed: false,
        }
        expect(component.reviewFormEvent.emit).toHaveBeenCalledOnceWith(
            dto
        );
    }));

    it('no submit when rating is invalid', waitForAsync(() => {
        component.reviewForm.get('reviewTitle')?.setValue(TITLE_EXPECTED);
        component.reviewForm.get('reviewContent')?.setValue(CONTENT_EXPECTED);
        component.reviewForm.get('reviewRating')?.setValue('11');
        component.reviewForm.get('platform')?.setValue(PLATFORM_EXPECTED);
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        expect(component.reviewFormEvent.emit).not.toHaveBeenCalled();
    }));

    it('no submit when title is too short', waitForAsync(() => {
        component.reviewForm.get('reviewTitle')?.setValue('a');
        component.reviewForm.get('reviewContent')?.setValue(CONTENT_EXPECTED);
        component.reviewForm.get('reviewRating')?.setValue(RATING_EXPECTED);
        spyOn(component.reviewFormEvent, 'emit');
        spyOn(component.dialog, 'open')
            .and
            .returnValue({
                afterClosed: () => of(true)
            } as MatDialogRef<typeof component>);

        component.submitReview();

        fixture.detectChanges();
        expect(component.reviewFormEvent.emit).not.toHaveBeenCalled();
    }));

});
