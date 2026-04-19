import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ReservaService } from '../../../core/services/reserva/reserva';

@Component({
  selector: 'app-vincular-codigo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './vincular-codigo.html'
})
export class VincularCodigoComponent {
  codigo: string = '';
  cargando: boolean = false;
  mensajeError: string = '';
  mensajeExito: string = '';

  private reservaService = inject(ReservaService);
  private router = inject(Router);

  vincular() {
    // Evitamos enviar si está vacío
    if (!this.codigo || this.codigo.trim() === '') return;

    this.cargando = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    this.reservaService.vincularCodigo(this.codigo.trim().toUpperCase()).subscribe({
      next: (respuesta) => {
        this.cargando = false;
        this.mensajeExito = '¡Vivienda vinculada con éxito!';

        // Redirigimos al home después de 2 segundos para que el usuario vea el éxito
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 2000);
      },
      error: (err) => {
        this.cargando = false;
        // Leemos el mensaje de error que lanza tu RuntimeException en Spring Boot
        this.mensajeError = err.error?.message || 'El código no es válido o ya está en uso.';
      }
    });
  }
}
