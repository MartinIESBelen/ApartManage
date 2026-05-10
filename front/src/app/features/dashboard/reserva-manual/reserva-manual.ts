import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms'; // Añadido NgForm
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {ContratoService} from '../../../core/services/contrato/contrato.service';

@Component({
  selector: 'app-reserva-manual',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reserva-manual.html'
})
export class ReservaManualComponent implements OnInit {
  apartamentoId!: number;
  cargando = false;

  form = {
    fechaEntrada: '',
    fechaSalida: '',
    precioBaseAlquiler: 0,
    fianza: 0,
    nombreInquilino: '',
    apellidosInquilino: '',
    dniInquilino: '',
    fechaNacimientoInquilino: '',
    emailInquilino: '',
    telefonoInquilino: ''
  };

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private contratoService = inject(ContratoService);

  ngOnInit() {
    this.apartamentoId = Number(this.route.snapshot.paramMap.get('id'));
  }

  guardar(reservaForm: NgForm) {
    if (reservaForm.invalid) return;

    this.cargando = true;
    this.contratoService.crearContratoManual(this.apartamentoId, this.form).subscribe({
      next: () => {
        alert('¡Contrato e Inquilino Fantasma registrados correctamente!');
        void this.router.navigate(['/apartamento', this.apartamentoId]);
      },
      error: (err) => {
        this.cargando = false;
        alert('Error: ' + (err.error?.message || 'No se pudo crear el registro'));
      }
    });
  }
}
