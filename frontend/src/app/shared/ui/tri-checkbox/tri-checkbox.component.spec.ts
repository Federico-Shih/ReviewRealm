import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TriCheckboxComponent} from './tri-checkbox.component';

describe('TriCheckboxComponent', () => {
  let component: TriCheckboxComponent;
  let fixture: ComponentFixture<TriCheckboxComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TriCheckboxComponent]
    });
    fixture = TestBed.createComponent(TriCheckboxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
