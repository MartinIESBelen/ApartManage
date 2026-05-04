import { Component, input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';

// Interfaz para los datos que nos pasará el padre
export interface DatosMes {
  mes: string;
  ingresos: number;
  gastos: number;
}

@Component({
  selector: 'app-graficos-balance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './graficos-balance.html'
})
export class GraficosBalance {
  // Recibimos un array con el historial de meses
  datos = input.required<DatosMes[]>();

  //Encontramos el valor más alto (sea ingreso o gasto) para usarlo como el 100% de la altura
  maxValor = computed(() => {
    if (this.datos().length === 0) return 1; // Evitar dividir por cero

    const maxIngreso = Math.max(...this.datos().map(d => d.ingresos));
    const maxGasto = Math.max(...this.datos().map(d => d.gastos));

    return Math.max(maxIngreso, maxGasto, 1);
  });

  // Calcula qué porcentaje de altura debe tener cada barra CSS
  calcularAltura(valor: number): string {
    const porcentaje = (valor / this.maxValor()) * 100;
    return `${porcentaje}%`;
  }

}
