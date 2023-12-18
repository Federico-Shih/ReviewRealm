import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenresViewComponent } from './genres-view.component';

describe('GenresViewComponent', () => {
  let component: GenresViewComponent;
  let fixture: ComponentFixture<GenresViewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenresViewComponent],
    });
    fixture = TestBed.createComponent(GenresViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
