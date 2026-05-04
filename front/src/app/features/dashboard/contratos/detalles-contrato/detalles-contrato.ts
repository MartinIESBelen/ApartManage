import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ContratoService, ContratoDetalle } from '../../../../core/services/contrato/contrato.service'; // Ajusta tu ruta

@Component({
  selector: 'app-detalles-contrato',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './detalles-contrato.html'
})
export class DetallesContrato implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private contratoService = inject(ContratoService);

  // Estados
  contrato = signal<ContratoDetalle | null>(null);
  cargando = signal<boolean>(true);
  error = signal<string>('');

  ngOnInit() {
    // Leemos el ID que viene en la URL (ej: /contratos/5)
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.cargarDetalle(+idParam);
    } else {
      void this.router.navigate(['/contratos']);
    }
  }

  cargarDetalle(id: number) {
    this.contratoService.getDetalleContrato(id).subscribe({
      next: (data) => {
        this.contrato.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo cargar la información del contrato.');
        this.cargando.set(false);
      }
    });
  }
}
