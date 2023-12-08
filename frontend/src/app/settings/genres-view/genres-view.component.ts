import { ChangeDetectionStrategy, Component } from '@angular/core';
import {UsersService} from "../../shared/data-access/users/users.service";

@Component({
  selector: 'app-genres-view',
  templateUrl: './genres-view.component.html',
  styleUrls: ['./genres-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GenresViewComponent {
  favgenres= ["hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios", "hola", "chau", "adios"];

  constructor(userService: UsersService) {
  }

}
