import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ApartamentoModel } from '../../../core/models/apartamento.model';

@Component({
  selector: 'app-apartamento-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './apartamento-card.html'
})
export class ApartamentoCard {
  // Con { required: true } Angular avisa si no le pasamos el apartamento al usuario
  @Input({ required: true }) apartamento!: ApartamentoModel;
}
