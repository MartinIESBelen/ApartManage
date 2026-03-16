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
  // Con { required: true } Angular nos avisará si olvidamos pasarle el apartamento
  @Input({ required: true }) apartamento!: ApartamentoModel;
}
