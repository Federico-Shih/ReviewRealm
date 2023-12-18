import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidateUserComponent } from './validate-user.component';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from '../../../shared/data-access/authentication/authentication.service';

describe('ValidateUserComponent', () => {
  let component: ValidateUserComponent;
  let fixture: ComponentFixture<ValidateUserComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ValidateUserComponent],
      imports: [HttpClient, AuthenticationService],
    });
    fixture = TestBed.createComponent(ValidateUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
