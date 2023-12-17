import { Component } from '@angular/core';
import { MatDialogModule } from '@angular/material/dialog';
import { SharedModule } from '../../../shared/shared.module';

@Component({
  selector: 'app-game-delete-dialog',
  templateUrl: './game-delete-dialog.html',
  imports: [MatDialogModule, SharedModule],
  standalone: true,
})
export class GameDeleteDialogComponent {}
