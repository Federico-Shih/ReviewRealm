import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ValidateUserComponent} from './validate-user.component';

describe('ValidateUserComponent', () => {
  let component: ValidateUserComponent;
  let fixture: ComponentFixture<ValidateUserComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ValidateUserComponent]
    });
    fixture = TestBed.createComponent(ValidateUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
