import { Component, input, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';
import { FinanzasService } from '../../../../core/services/finanzas/finanzas.service';
import { TransaccionResponse } from '../../../../core/models/finanzas.model';

@Component({
  selector: 'app-vista-inquilino',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './vista-inquilino.html'
})
export class VistaInquilino implements OnInit {
  apartamento = input.required<ApartamentoModel>();

  private finanzasService = inject(FinanzasService);

  recibosPendientes = signal<TransaccionResponse[]>([]);

  totalPendiente = computed(() => {
    return this.recibosPendientes().reduce((total, recibo) => total + recibo.importe, 0);
  });

  ngOnInit() {
    const contratoId = this.apartamento().reservaActivaId;
    if (contratoId) {
      this.cargarDeudasInquilino(contratoId);
    }
  }

  cargarDeudasInquilino(contratoId: number) {
    this.finanzasService.obtenerTransaccionesPorContrato(contratoId).subscribe({
      next: (transacciones) => {
        // Filtramos SOLO los que son ingresos (cobros del dueño) y están pendientes
        const deudas = transacciones.filter(t => t.tipo === 'INGRESO' && t.estado === 'PENDIENTE');
        this.recibosPendientes.set(deudas);
      },
      error: (err) => console.error('Error al cargar recibos:', err)
    });
  }

  tieneAlertaInventario(): boolean {
    const alertas = this.apartamento().alertas;
    return alertas ? alertas.includes('INVENTARIO_ROTO') : false;
  }
}
