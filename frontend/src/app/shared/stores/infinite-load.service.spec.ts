import { TestBed } from '@angular/core/testing';
import {GameInfiniteLoadService} from "./infinite-load.service";
import {gameServiceMock} from "../../../tests/mocks/game-service.mock";
import {SharedModule} from "../shared.module";


describe('InfiniteLoadService', () => {
  let service: GameInfiniteLoadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule
      ]
    });
    service = TestBed.inject(GameInfiniteLoadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
