import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../core/models/apartamento.model';
import { ApartamentoCard } from '../apartamento-card/apartamento-card'; // Asegura que el nombre coincida
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ApartamentoCard, FormsModule, RouterModule],
  templateUrl: './home.html'
})
export class Home implements OnInit {
  // Ya no usamos una sola lista, la dividimos en dos:
  misPropiedades: ApartamentoModel[] = [];
  misAlquileres: ApartamentoModel[] = [];

  private apartamentoService = inject(ApartamentoService);
  private cd = inject(ChangeDetectorRef);

  filtroNombre: string = '';
  filtroEstado: string = '';
  filtroAlertas: boolean = false;

  ngOnInit() {
    this.buscar();
  }

  buscar() {
    this.apartamentoService.filtrarApartamentos(
      this.filtroNombre,
      this.filtroEstado || undefined,
      this.filtroAlertas
    ).subscribe({
      next: (data) => {
        // Adiós al truco temporal. Ahora filtramos la realidad:
        this.misPropiedades = data.filter(apto => apto.relacionUsuario === 'PROPIETARIO');
        this.misAlquileres = data.filter(apto => apto.relacionUsuario === 'INQUILINO');

        console.log('Propiedades Reales:', this.misPropiedades);
        console.log('Alquileres Reales:', this.misAlquileres);

        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al filtrar los apartamentos:', err);
      }
    });
  }
}
