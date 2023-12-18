import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainFeedPageComponent } from './main-feed-page.component';
import { CommonModule } from '@angular/common';
import { MainFeedRoutingModule } from '../../main-feed-routing.module';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { MatGridListModule } from '@angular/material/grid-list';
import { SharedModule } from '../../../shared/shared.module';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { GamesModule } from '../../../games/games.module';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute } from '@angular/router';

describe('MainFeedPageComponent', () => {
  let component: MainFeedPageComponent;
  let fixture: ComponentFixture<MainFeedPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MainFeedPageComponent],
      imports: [
        CommonModule,
        MainFeedRoutingModule,
        MatButtonToggleModule,
        TranslateModule,
        MatIconModule,
        ReactiveFormsModule,
        MatGridListModule,
        SharedModule,
        MatProgressSpinnerModule,
        GamesModule,
        MatButtonModule,
        ActivatedRoute,
      ],
    });
    fixture = TestBed.createComponent(MainFeedPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
