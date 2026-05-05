import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';

@Component({
  selector: 'app-vista-inquilino',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './vista-inquilino.html'
})
export class VistaInquilino {
  apartamento = input.required<ApartamentoModel>();

  // Métodos mockeados hasta que conectemos los servicios reales de pagos e incidencias
  pagarRecibo() {
    alert('Redirigiendo a pasarela de pago segura...');
  }

  reportarIncidencia() {
    alert('Abriendo formulario de nueva avería...');
  }
}
