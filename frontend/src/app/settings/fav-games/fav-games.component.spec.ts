import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavGamesComponent } from './fav-games.component';

describe('FavGamesComponent', () => {
  let component: FavGamesComponent;
  let fixture: ComponentFixture<FavGamesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FavGamesComponent],
    });
    fixture = TestBed.createComponent(FavGamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
