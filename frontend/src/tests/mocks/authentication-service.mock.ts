import { Observable, of } from 'rxjs';
import { User } from '../../app/shared/data-access/users/users.class';
import { userMock1, userMock2 } from './user.mock';

export class AuthenticationServiceMockNull {
  getLoggedUser(): Observable<User | null> {
    return of(null);
  }
}

export class AuthenticationServiceMock {
  getLoggedUser(): Observable<User | null> {
    return of(userMock1);
  }
}

export class AuthenticationServiceUserModerator {
  getLoggedUser(): Observable<User | null> {
    return of(userMock2);
  }
}
