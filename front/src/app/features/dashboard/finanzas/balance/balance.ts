import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FormularioTransaccion } from '../nuevo-movimiento/formulario-transaccion/formulario-transaccion';

import { ApartamentoService } from '../../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';
import { FinanzasService } from '../../../../core/services/finanzas/finanzas.service';
import { TransaccionResponse } from '../../../../core/models/finanzas.model';

import { Kpi } from './kpi/kpi';
import { GraficosBalance } from './graficos-balance/graficos-balance';
import { TablaTransacciones } from './tabla-transacciones/tabla-transacciones';

@Component({
  selector: 'app-finanzas',
  standalone: true,
  imports: [CommonModule, FormsModule, Kpi, GraficosBalance, TablaTransacciones, FormularioTransaccion],
  templateUrl: './balance.html'
})
export class Balance implements OnInit {

  private aptoService = inject(ApartamentoService);
  private finanzasService = inject(FinanzasService);

  misPropiedades = signal<ApartamentoModel[]>([]);
  ultimasTransacciones = signal<TransaccionResponse[]>([]);
  cargando = signal<boolean>(true);

  filtroPiso = signal<string>('TODOS');
  filtroPeriodo = signal<string>('ANIO_ACTUAL');

  mostrandoFormulario = signal<boolean>(false);
  transaccionAEditar = signal<TransaccionResponse | null>(null);

  ingresosTotales = computed(() => {
    return this.ultimasTransacciones()
      .filter(t => t.tipo === 'INGRESO' && t.estado === 'PAGADO') // Solo contamos lo cobrado
      .reduce((sum, current) => sum + current.importe, 0);
  });

  gastosTotales = computed(() => {
    return this.ultimasTransacciones()
      .filter(t => t.tipo === 'GASTO')
      .reduce((sum, current) => sum + current.importe, 0);
  });

  datosGrafico = computed(() => {
    const transacciones = this.ultimasTransacciones();
    if (transacciones.length === 0) return [];

    const nombresMeses = ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'];
    const mapa = new Map<string, { mes: string, ingresos: number, gastos: number }>();

    transacciones.forEach(tx => {
      const fecha = new Date(tx.fechaEmision);
      const key = `${fecha.getFullYear()}-${String(fecha.getMonth() + 1).padStart(2, '0')}`;

      if (!mapa.has(key)) {
        mapa.set(key, { mes: nombresMeses[fecha.getMonth()], ingresos: 0, gastos: 0 });
      }

      const mesData = mapa.get(key)!;

      if (tx.tipo === 'INGRESO' && tx.estado === 'PAGADO') {
        mesData.ingresos += tx.importe;
      } else if (tx.tipo === 'GASTO') {
        mesData.gastos += tx.importe;
      }
    });

    return Array.from(mapa.keys())
      .sort()
      .map(key => mapa.get(key)!);
  });

  ngOnInit() {
    this.cargarPropiedades();
    this.cargarDatosFinancieros();
  }

  cargarPropiedades() {
    this.aptoService.filtrarApartamentos('', '', false).subscribe({
      next: (data) => {
        const propiedades = data.filter(apto => apto.relacion === 'PROPIETARIO');
        this.misPropiedades.set(propiedades);
      }
    });
  }

  cargarDatosFinancieros() {
    this.cargando.set(true);

    // Llamada real a Spring Boot
    this.finanzasService.obtenerTransacciones(this.filtroPiso(), this.filtroPeriodo())
      .subscribe({
        next: (transaccionesReales) => {
          this.ultimasTransacciones.set(transaccionesReales);
          this.cargando.set(false);
        },
        error: (err) => {
          console.error('Error al descargar datos de finanzas', err);
          this.cargando.set(false);
          alert('Hubo un error al conectar con el servidor.');
        }
      });
  }

  abrirEdicion(tx: TransaccionResponse) {
    this.transaccionAEditar.set(tx);
    this.mostrandoFormulario.set(true);
  }

  cerrarEdicion() {
    this.mostrandoFormulario.set(false);
    this.transaccionAEditar.set(null);
  }

  recargarDespuesDeEditar() {
    this.cerrarEdicion();
    this.cargarDatosFinancieros();
  }
}
