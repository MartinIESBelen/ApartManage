import { Component, inject, input, output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FinanzasService } from '../../../../../core/services/finanzas/finanzas.service';
import { TransaccionRequest, TransaccionResponse } from '../../../../../core/models/finanzas.model';

@Component({
  selector: 'app-formulario-transaccion',
  standalone: true,
  // 1. Cambiamos FormsModule por ReactiveFormsModule
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './formulario-transaccion.html'
})
export class FormularioTransaccion implements OnInit {
  private finanzasService = inject(FinanzasService);
  private fb = inject(FormBuilder);

  // INPUTS
  apartamento = input.required<any>();
  transaccionAEditar = input<TransaccionResponse | null>(null);

  // OUTPUTS
  volver = output<void>();
  guardadoExitoso = output<void>();

  // 2. Creamos el FormGroup con sus validaciones estrictas
  formTransaccion: FormGroup = this.fb.group({
    tipoMovimiento: ['GASTO', Validators.required],
    concepto: ['', [Validators.required, Validators.minLength(3)]],
    importe: [null, [Validators.required, Validators.min(0.01)]],
    fechaEmision: [new Date().toISOString().split('T')[0], Validators.required],
    categoria: ['OTROS', Validators.required],
    estado: ['PAGADO', Validators.required],
    reservaId: [null],
    dividirGasto: [false]
  });

  guardando = false;
  mensajeError = '';

  ngOnInit() {
    // Si recibimos una transacción, rellenamos el formulario automáticamente (Modo Edición)
    const tx = this.transaccionAEditar();
    if (tx) {
      this.formTransaccion.patchValue({
        tipoMovimiento: tx.tipo,
        concepto: tx.concepto,
        importe: tx.importe,
        fechaEmision: tx.fechaEmision,
        categoria: tx.categoria || 'OTROS',
        estado: tx.estado,
        reservaId: tx.reservaId
      });
    }
  }

  // Helper para los botones superiores de Ingreso/Gasto
  cambiarTipo(tipo: 'INGRESO' | 'GASTO') {
    this.formTransaccion.patchValue({
      tipoMovimiento: tipo,
      // Autocompletamos el estado por comodidad
      estado: tipo === 'INGRESO' ? 'PENDIENTE' : 'PAGADO'
    });
  }

  // Getter para usarlo fácilmente en el HTML
  get tipoActual() {
    return this.formTransaccion.get('tipoMovimiento')?.value;
  }

  cancelar() {
    this.volver.emit();
  }

  guardarTransaccion() {
    if (this.formTransaccion.invalid) {
      this.formTransaccion.markAllAsTouched(); // Marca todos en rojo si faltan datos
      return;
    }

    this.guardando = true;
    this.mensajeError = '';

    const valores = this.formTransaccion.value;

    const request: TransaccionRequest = {
      apartamentoId: this.apartamento().id,
      reservaId: valores.reservaId,
      dividirEntreTodos: valores.dividirGasto,
      tipo: valores.tipoMovimiento,
      categoria: valores.categoria,
      estado: valores.estado,
      concepto: valores.concepto,
      importe: valores.importe,
      fechaEmision: valores.fechaEmision
    };

    const tx = this.transaccionAEditar();

    if (tx) {
      // MODO EDICIÓN
      this.finanzasService.actualizarTransaccion(tx.id, request).subscribe({
        next: () => this.guardadoExitoso.emit(),
        error: (err) => {
          console.error(err);
          this.mensajeError = 'Error al actualizar el movimiento.';
          this.guardando = false;
        }
      });
    } else {
      // MODO CREACIÓN
      this.finanzasService.crearTransaccion(request).subscribe({
        next: () => this.guardadoExitoso.emit(),
        error: (err) => {
          console.error(err);
          this.mensajeError = 'Error al guardar el movimiento.';
          this.guardando = false;
        }
      });
    }
  }
}
