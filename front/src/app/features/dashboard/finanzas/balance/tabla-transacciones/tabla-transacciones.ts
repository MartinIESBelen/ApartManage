import {Component, input, signal, computed, inject, output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {TransaccionResponse} from '../../../../../core/models/finanzas.model';
import {FinanzasService} from '../../../../../core/services/finanzas/finanzas.service';


@Component({
  selector: 'app-tabla-transacciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tabla-transacciones.html'
})
export class TablaTransacciones {
  transacciones = input<TransaccionResponse[]>([]);
  private finanzasService = inject(FinanzasService);

  filtroTipo = signal<string>('TODOS');
  filtroEstado = signal<string>('TODOS');
  filtroCategoria = signal<string>('TODOS');

  recargar = output<void>();
  editar = output<TransaccionResponse>();

  paginaActual = signal<number>(1);
  itemsPorPagina = 8;

  categoriasUnicas = computed(() => {
    const categorias = this.transacciones()
      .map(t => t.categoria)
      .filter(c => !!c) as string[];
    return [...new Set(categorias)]; // Quitar duplicados
  });

  // 1. Primero filtramos
  transaccionesFiltradas = computed(() => {
    return this.transacciones().filter(tx => {
      const cumpleTipo = this.filtroTipo() === 'TODOS' || tx.tipo === this.filtroTipo();
      const cumpleEstado = this.filtroEstado() === 'TODOS' || tx.estado === this.filtroEstado();
      const cumpleCategoria = this.filtroCategoria() === 'TODOS' || tx.categoria === this.filtroCategoria();

      return cumpleTipo && cumpleEstado && cumpleCategoria;
    });
  });

  // 2. Luego paginamos el resultado filtrado
  transaccionesPaginadas = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.transaccionesFiltradas().slice(inicio, fin);
  });

  // --- LÓGICA DE CÁLCULO PARA LA VISTA ---
  totalPaginas = computed(() => Math.ceil(this.transaccionesFiltradas().length / this.itemsPorPagina) || 1);
  totalResultados = computed(() => this.transaccionesFiltradas().length);
  itemInicio = computed(() => this.totalResultados() === 0 ? 0 : (this.paginaActual() - 1) * this.itemsPorPagina + 1);
  itemFin = computed(() => Math.min(this.paginaActual() * this.itemsPorPagina, this.totalResultados()));

  // --- MÉTODOS ---
  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 1 && nuevaPagina <= this.totalPaginas()) {
      this.paginaActual.set(nuevaPagina);
    }
  }

  // Si el usuario cambia un filtro, lo devolvemos a la página 1
  onFiltroChange() {
    this.paginaActual.set(1);
  }

  onEditar(tx: TransaccionResponse) {
    this.editar.emit(tx);
  }

  onBorrar(id: number) {
    if (confirm('¿Estás seguro de que quieres borrar este movimiento? Esta acción no se puede deshacer y alterará tus balances.')) {
      this.finanzasService.borrarTransaccion(id).subscribe({
        next: () => this.recargar.emit(), // Avisamos al padre para que refresque la tabla
        error: (err) => alert('Error al borrar la transacción.')
      });
    }
  }
}
