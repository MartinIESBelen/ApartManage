import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento.service';
import { ApartamentoModel } from '../../../core/models/apartamento.model';

@Component({
  selector: 'app-apartamento-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './apartamento-details.html'
})
export class ApartamentoDetails implements OnInit {

  // Usamos Signal para guardar los detalles
  apartamento = signal<ApartamentoModel | undefined>(undefined);

  // Temporal: Simulamos que somos el propietario para ver el botón.
  // Más adelante sacaremos esto leyendo el Token JWT.
  esPropietario = signal<boolean>(true);

  private route = inject(ActivatedRoute);
  private apartamentoService = inject(ApartamentoService);

  ngOnInit() {
    // 1. Leemos la ID de la URL (ej: /apartamento/1)
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      const id = Number(idParam);
      // 2. Pedimos los datos a Spring Boot
      this.apartamentoService.getApartamentoById(id).subscribe({
        next: (data) => {
          this.apartamento.set(data);
        },
        error: (err) => console.error("Error al cargar detalles:", err)
      });
    }
  }
}
