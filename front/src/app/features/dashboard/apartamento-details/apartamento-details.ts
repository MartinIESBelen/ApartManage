import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../core/models/apartamento.model';

import { MiPropiedad } from './mi-propiedad/mi-propiedad';
import { VistaInquilino } from './vista-inquilino/vista-inquilino';

@Component({
  selector: 'app-apartamento-details',
  standalone: true,
  // Ahora el padre renderiza a los hijos
  imports: [CommonModule, RouterModule, MiPropiedad, VistaInquilino],
  templateUrl: './apartamento-details.html'
})
export class ApartamentoDetails implements OnInit {
  // Única fuente de la verdad
  apartamento = signal<ApartamentoModel | undefined>(undefined);

  private route = inject(ActivatedRoute);
  private apartamentoService = inject(ApartamentoService);

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.apartamentoService.getApartamentoById(Number(idParam)).subscribe({
        next: (data) => this.apartamento.set(data),
        error: (err) => console.error("Error al cargar detalles:", err)
      });
    }
  }
}
