import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';

@Component({
  selector: 'app-vista-inquilino',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vista-inquilino.html'
})
export class VistaInquilino {
  // En Angular 21, input.required obliga al componente padre a pasarle este dato sí o sí
  apartamento = input.required<ApartamentoModel>();

  // Métodos mockeados hasta que conectemos los servicios reales de pagos e incidencias
  pagarRecibo() {
    alert('Redirigiendo a pasarela de pago segura...');
  }

  reportarIncidencia() {
    alert('Abriendo formulario de nueva avería...');
  }
}
