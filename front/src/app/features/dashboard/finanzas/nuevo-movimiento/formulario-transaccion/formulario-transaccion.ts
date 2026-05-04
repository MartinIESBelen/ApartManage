import { Component, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FinanzasService } from '../../../../../core/services/finanzas/finanzas.service';
import { TransaccionRequest } from '../../../../../core/models/finanzas.model';

@Component({
  selector: 'app-formulario-transaccion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulario-transaccion.html'
})
export class FormularioTransaccion {
  private finanzasService = inject(FinanzasService);
  private router = inject(Router);

  // INPUT: Recibe obligatoriamente el apartamento del padre
  apartamento = input.required<any>();

  // OUTPUT: Avisa al padre que queremos volver atrás
  volver = output<void>();

  // Señales del formulario
  tipoMovimiento = signal<'INGRESO' | 'GASTO'>('GASTO');
  dividirGasto = signal<boolean>(false);
  concepto = signal<string>('');
  importe = signal<number | null>(null);
  fechaEmision = signal<string>(new Date().toISOString().split('T')[0]);
  categoria = signal<string>('OTROS');
  reservaId = signal<number | null>(null);

  cancelar() {
    this.volver.emit(); // Avisamos al padre
  }

  guardarTransaccion() {
    if (!this.concepto() || !this.importe() || !this.fechaEmision()) return;

    const request: TransaccionRequest = {
      apartamentoId: this.apartamento().id,
      reservaId: this.reservaId(),
      dividirEntreTodos: this.dividirGasto(),
      tipo: this.tipoMovimiento(),
      categoria: this.categoria(),
      estado: this.tipoMovimiento() === 'INGRESO' ? 'PENDIENTE' : 'PAGADO',
      concepto: this.concepto(),
      importe: this.importe()!,
      fechaEmision: this.fechaEmision()
    };

    this.finanzasService.crearTransaccion(request).subscribe({
      next: () => this.router.navigate(['/finanzas/balance']),
      error: (err) => console.error(err)
    });
  }
}
