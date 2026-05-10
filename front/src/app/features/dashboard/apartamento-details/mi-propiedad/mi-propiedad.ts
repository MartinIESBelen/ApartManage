import { Component, inject, input, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
// Quita la línea que importa ReservaService y pon esta:
import { ContratoService } from '../../../../core/services/contrato/contrato.service';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';
import {GestorDocumentos} from '../../gestor-documentos/gestor-documentos';

@Component({
  selector: 'app-mi-propiedad',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, GestorDocumentos],
  templateUrl: './mi-propiedad.html'
})
export class MiPropiedad {
  apartamento = input.required<ApartamentoModel>();

  mostrarFormularioReserva = signal<boolean>(false);
  codigoGenerado = signal<string | null>(null);
  private cdr = inject(ChangeDetectorRef);

  nuevaReserva = {
    fechaEntrada: '',
    fechaSalida: '',
    precioBaseAlquiler: 0,
    fianza: 0
  };

  private contratoService = inject(ContratoService);

  sonFechasInvalidas(): boolean {
    if (!this.nuevaReserva.fechaEntrada || !this.nuevaReserva.fechaSalida) return false;

    const inicio = new Date(this.nuevaReserva.fechaEntrada);
    const fin = new Date(this.nuevaReserva.fechaSalida);
    return inicio >= fin;
  }

  formularioInvalido(): boolean {
    return !this.nuevaReserva.fechaEntrada ||
      !this.nuevaReserva.fechaSalida ||
      this.nuevaReserva.precioBaseAlquiler <= 0 ||
      this.sonFechasInvalidas();
  }

  generarCodigo() {
    const id = this.apartamento().id;

    this.contratoService.crearContrato(id, this.nuevaReserva).subscribe({
      next: (res) => {
        this.codigoGenerado.set(res.codigoVinculacion);
        this.mostrarFormularioReserva.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert("Error al generar el código: " + (err.error?.message || 'Verifica las fechas'));
      }
    });
  }

  copiarCodigo() {
    const codigo = this.codigoGenerado();
    if (codigo) {
      navigator.clipboard.writeText(codigo);
      alert("¡Código copiado al portapapeles!");
      this.cdr.detectChanges();
    }
  }
}
