import { Component, inject, input, output, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FinanzasService } from '../../../../../core/services/finanzas/finanzas.service';
import { TransaccionRequest, TransaccionResponse } from '../../../../../core/models/finanzas.model';
import { ContratoService} from '../../../../../core/services/contrato/contrato.service';


@Component({
  selector: 'app-formulario-transaccion',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './formulario-transaccion.html'
})
export class FormularioTransaccion implements OnInit {
  private finanzasService = inject(FinanzasService);
  private contratoService = inject(ContratoService);
  private fb = inject(FormBuilder);

  apartamento = input.required<any>();
  transaccionAEditar = input<TransaccionResponse | null>(null);

  volver = output<void>();
  guardadoExitoso = output<void>();

  contratosActivos = signal<any[]>([]);
  guardando = false;
  mensajeError = '';

  formTransaccion: FormGroup = this.fb.group({
    tipoMovimiento: ['GASTO', Validators.required],
    concepto: ['', [Validators.required, Validators.minLength(3)]],
    importe: [null, [Validators.required, Validators.min(0.01)]],
    fechaEmision: [new Date().toISOString().split('T')[0], Validators.required],
    categoria: ['OTROS', Validators.required],
    estado: ['PAGADO', Validators.required],
    // NUEVO: Unificamos el destino en un solo campo desplegable
    asignacion: ['GENERAL', Validators.required]
  });

  ngOnInit() {
    this.cargarInquilinos();

    const tx = this.transaccionAEditar();
    if (tx) {
      let valorAsignacion = 'GENERAL';
      if (tx.contratoId) {
        valorAsignacion = tx.contratoId.toString();
      } // Nota: No podemos saber si fue "dividirEntreTodos" al editar, porque el backend ya las separó.

      this.formTransaccion.patchValue({
        tipoMovimiento: tx.tipo,
        concepto: tx.concepto,
        importe: tx.importe,
        fechaEmision: tx.fechaEmision,
        categoria: tx.categoria || 'OTROS',
        estado: tx.estado,
        asignacion: valorAsignacion
      });
    }
  }

  cargarInquilinos() {
    this.contratoService.obtenerContratosPorApartamento(this.apartamento().id).subscribe({
      next: (contratos: any[]) => {
        console.log('🔎 DATOS DEL BACKEND:', contratos);
        const activos = contratos.filter(c => c.estado === 'CONFIRMADA');
        this.contratosActivos.set(activos);
      },
      error: (err) => {
        console.error('Error al cargar los inquilinos del apartamento:', err);
      }
    });
  }

  cambiarTipo(tipo: 'INGRESO' | 'GASTO') {
    this.formTransaccion.patchValue({
      tipoMovimiento: tipo,
      estado: tipo === 'INGRESO' ? 'PENDIENTE' : 'PAGADO'
    });
  }

  get tipoActual() {
    return this.formTransaccion.get('tipoMovimiento')?.value;
  }

  cancelar() {
    this.volver.emit();
  }

  guardarTransaccion() {
    if (this.formTransaccion.invalid) {
      this.formTransaccion.markAllAsTouched();
      return;
    }

    this.guardando = true;
    this.mensajeError = '';

    const valores = this.formTransaccion.value;

    let idDelContrato = null;
    let dividir = false;

    if (valores.asignacion === 'TODOS') {
      dividir = true;
    } else if (valores.asignacion !== 'GENERAL') {
      idDelContrato = Number(valores.asignacion);
    }

    // CREAMOS EL REQUEST ACORDE AL BACKEND
    const request: TransaccionRequest = {
      apartamentoId: this.apartamento().id,
      contratoId: idDelContrato,
      dividirEntreTodos: dividir,
      tipo: valores.tipoMovimiento,
      categoria: valores.categoria,
      estado: valores.estado,
      concepto: valores.concepto,
      importe: valores.importe,
      fechaEmision: valores.fechaEmision
      // fechaVencimiento
    };

    const tx = this.transaccionAEditar();

    if (tx) {
      this.finanzasService.actualizarTransaccion(tx.id, request).subscribe({
        next: () => this.guardadoExitoso.emit(),
        error: (err) => {
          this.mensajeError = 'Error al actualizar el movimiento.';
          this.guardando = false;
        }
      });
    } else {
      this.finanzasService.crearTransaccion(request).subscribe({
        next: () => this.guardadoExitoso.emit(),
        error: (err) => {
          this.mensajeError = err.error?.message || 'Error al guardar el movimiento.';
          this.guardando = false;
        }
      });
    }
  }
}
