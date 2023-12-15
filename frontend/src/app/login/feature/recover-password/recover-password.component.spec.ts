import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RecoverPasswordComponent} from './recover-password.component';

describe('RecoverPasswordComponent', () => {
  let component: RecoverPasswordComponent;
  let fixture: ComponentFixture<RecoverPasswordComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RecoverPasswordComponent]
    });
    fixture = TestBed.createComponent(RecoverPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
