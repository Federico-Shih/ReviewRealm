import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewSearchComponent } from './review-search.component';
import { ReviewsService } from '../../../shared/data-access/reviews/reviews.service';
import { ActivatedRoute, Router } from '@angular/router';
import { EnumsService } from '../../../shared/data-access/enums/enums.service';
import { FormBuilder } from '@angular/forms';
import { Renderer2, RendererFactory2 } from '@angular/core';

describe('ReviewSearchComponent', () => {
  let component: ReviewSearchComponent;
  let fixture: ComponentFixture<ReviewSearchComponent>;

  const renderer2: Renderer2 = {
    addClass: jasmine.createSpy('addClass'),
    removeClass: jasmine.createSpy('removeClass'),
    setStyle: jasmine.createSpy('setStyle'),
    selectRootElement: jasmine.createSpy('selectRootElement'),
  } as unknown as Renderer2;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReviewSearchComponent],
      providers: [
        {
          provide: ReviewsService,
          useValue: {
            // TODO: stub
          },
        },
        {
          provide: ActivatedRoute,
          useValue: {
            // TODO: stub
          },
        },
        {
          provide: EnumsService,
          useValue: {
            // TODO: stub
          },
        },
        {
          provide: Router,
          useValue: {
            // TODO: stub
          },
        },
        FormBuilder,
        {
          provide: RendererFactory2,
          useValue: {
            createRenderer: () => renderer2,
          },
        },
      ],
    });

    fixture = TestBed.createComponent(ReviewSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
