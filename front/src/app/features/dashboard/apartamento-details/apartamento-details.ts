import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <-- 1. IMPRESCINDIBLE PARA EL FORMULARIO
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';
import { ReservaService } from '../../../core/services/reserva/reserva';
import { ApartamentoModel } from '../../../core/models/apartamento.model';

@Component({
  selector: 'app-apartamento-details',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './apartamento-details.html'
})
export class ApartamentoDetails implements OnInit {

  apartamento = signal<ApartamentoModel | undefined>(undefined);

  // Lo inicializamos en false por seguridad
  esPropietario = signal<boolean>(false);

  // --- VARIABLES PARA EL NUEVO CÓDIGO ---
  mostrarFormularioReserva = signal<boolean>(false);
  codigoGenerado = signal<string | null>(null);

  nuevaReserva = {
    fechaEntrada: '',
    fechaSalida: '',
    precioBaseAlquiler: 0
  };

  sonFechasInvalidas(): boolean {
    if (!this.nuevaReserva.fechaEntrada || !this.nuevaReserva.fechaSalida) return false;

    const inicio = new Date(this.nuevaReserva.fechaEntrada);
    const fin = new Date(this.nuevaReserva.fechaSalida);

    // Devuelve true (error) si la fecha de entrada es igual o mayor a la de salida
    return inicio >= fin;
  }

  formularioInvalido(): boolean {
    // El formulario es inválido si faltan datos, el precio es 0 o las fechas están mal
    return !this.nuevaReserva.fechaEntrada ||
      !this.nuevaReserva.fechaSalida ||
      this.nuevaReserva.precioBaseAlquiler <= 0 ||
      this.sonFechasInvalidas();
  }

  private route = inject(ActivatedRoute);
  private apartamentoService = inject(ApartamentoService);
  private reservaService = inject(ReservaService); // Inyectamos el servicio

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      const id = Number(idParam);
      this.apartamentoService.getApartamentoById(id).subscribe({
        next: (data) => {
          this.apartamento.set(data);

          this.esPropietario.set(data.relacionUsuario === 'PROPIETARIO');
        },
        error: (err) => console.error("Error al cargar detalles:", err)
      });
    }
  }

  // --- LÓGICA DE VINCULACIÓN ---
  generarCodigo() {
    const id = this.apartamento()?.id;
    if (!id) return;

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
