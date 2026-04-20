import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ReservaService } from '../../../core/services/reserva/reserva';

@Component({
  selector: 'app-reserva-manual',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reserva-manual.html'
})
export class ReservaManualComponent implements OnInit {
  apartamentoId!: number;
  cargando = false;

  // El modelo del formulario coincide con tu ReservaManualRequest de Java
  form = {
    fechaEntrada: '',
    fechaSalida: '',
    precioBaseAlquiler: 0,
    nombreInquilino: '',
    emailInquilino: '',
    telefonoInquilino: ''
  };

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private reservaService = inject(ReservaService);

  ngOnInit() {
    this.apartamentoId = Number(this.route.snapshot.paramMap.get('id'));
  }

  guardar() {
    this.cargando = true;
    this.reservaService.crearReservaManual(this.apartamentoId, this.form).subscribe({
      next: () => {
        alert('¡Contrato e Inquilino registrados correctamente!');
        this.router.navigate(['/apartamento', this.apartamentoId]);
      },
      error: (err) => {
        this.cargando = false;
        alert('Error: ' + (err.error?.message || 'No se pudo crear el registro'));
      }
    });
  }
}
