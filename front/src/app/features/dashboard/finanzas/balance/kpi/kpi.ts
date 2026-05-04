import { Component, input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-kpi',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kpi.html'
})
export class Kpi {
  // Recibimos los datos básicos del padre
  ingresos = input.required<number>();
  gastos = input.required<number>();

  // Calculamos el beneficio neto y el margen automáticamente con Signals
  beneficioNeto = computed(() => this.ingresos() - this.gastos());

  margenBeneficio = computed(() => {
    if (this.ingresos() === 0) return 0;
    return (this.beneficioNeto() / this.ingresos()) * 100;
  });

  // Helper para decidir si el balance es positivo o negativo
  esPositivo = computed(() => this.beneficioNeto() >= 0);
}
