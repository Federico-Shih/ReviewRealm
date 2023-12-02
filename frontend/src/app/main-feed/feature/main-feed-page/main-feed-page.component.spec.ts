import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MainFeedPageComponent} from './main-feed-page.component';

describe('MainFeedPageComponent', () => {
  let component: MainFeedPageComponent;
  let fixture: ComponentFixture<MainFeedPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MainFeedPageComponent]
    });
    fixture = TestBed.createComponent(MainFeedPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
