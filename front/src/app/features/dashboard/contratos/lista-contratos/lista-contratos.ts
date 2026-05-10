import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ContratoService } from '../../../../core/services/contrato/contrato.service';
import { ContratoLista } from '../../../../core/models/contrato.model';
import {RouterLink, Router} from '@angular/router';

@Component({
  selector: 'app-lista-contratos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './lista-contratos.html'
})
export class ListaContratos implements OnInit {
  private contratoService = inject(ContratoService);
  private router = inject(Router);

  // --- ESTADOS ---
  contratos = signal<ContratoLista[]>([]);

  // --- FILTROS ---
  filtroEstado = signal<string>('TODOS');
  terminoBusqueda = signal<string>('');

  // --- DATOS COMPUTADOS ---
  contratosFiltrados = computed(() => {
    let filtrados = this.contratos();

    // Filtrar por estado
    if (this.filtroEstado() !== 'TODOS') {
      filtrados = filtrados.filter(c => c.estado === this.filtroEstado());
    }

    // Filtrar por texto (Piso, Inquilino o Código)
    const termino = this.terminoBusqueda().toLowerCase().trim();
    if (termino) {
      filtrados = filtrados.filter(c =>
        c.nombreApartamento.toLowerCase().includes(termino) ||
        (c.nombreInquilino && c.nombreInquilino.toLowerCase().includes(termino)) ||
        c.codigoVinculacion.toLowerCase().includes(termino)
      );
    }

    return filtrados;
  });

  ngOnInit() {
    this.cargarContratos();
  }

  cargarContratos() {
    this.contratoService.getMisContratos().subscribe({
      next: (data) => this.contratos.set(data),
      error: (err) => console.error('Error al cargar los contratos:', err)
    });
  }

  verDetalle(id: number) {
    this.router.navigate(['/contratos', id]);
    console.log('Navegando al detalle del contrato:', id);
  }
}
