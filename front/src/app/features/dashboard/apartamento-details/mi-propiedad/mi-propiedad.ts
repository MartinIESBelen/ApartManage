import { Component, inject, input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReservaService } from '../../../../core/services/reserva/reserva';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';

@Component({
  selector: 'app-mi-propiedad',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './mi-propiedad.html'
})
export class MiPropiedad {
  // Magia de Angular 21: Recibe los datos del padre de forma segura y reactiva
  apartamento = input.required<ApartamentoModel>();

  // Variables exclusivas del propietario
  mostrarFormularioReserva = signal<boolean>(false);
  codigoGenerado = signal<string | null>(null);

  nuevaReserva = {
    fechaEntrada: '',
    fechaSalida: '',
    precioBaseAlquiler: 0
  };

  private reservaService = inject(ReservaService);

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
    // Leemos el ID del piso a través de la Signal que nos pasa el padre
    const id = this.apartamento().id;

    this.reservaService.crearReserva(id, this.nuevaReserva).subscribe({
      next: (res) => {
        this.codigoGenerado.set(res.codigoVinculacion);
        this.mostrarFormularioReserva.set(false);
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
    }
  }
}
