export class AuthenticationDto {
  username: string;
  password: string;

  private constructor(username: string, password: string) {
    this.username = username;
    this.password = password;
  }

  static of(username: string, password: string) {
    return new AuthenticationDto(username, password);
  }
}
