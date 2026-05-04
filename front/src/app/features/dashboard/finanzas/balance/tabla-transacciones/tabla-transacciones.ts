import { Component, input, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // <-- MUY IMPORTANTE

export interface Transaccion {
  id: number;
  apartamentoId: number;
  apartamentoNombre?: string;
  reservaId?: number;
  inquilinoNombre?: string;
  tipo: 'INGRESO' | 'GASTO';
  categoria: string;
  estado: 'PENDIENTE' | 'PAGADO' | 'VENCIDO';
  concepto: string;
  importe: number;
  comentario?: string;
  fechaEmision: string;
  fechaVencimiento?: string;
  fechaPago?: string;
}

@Component({
  selector: 'app-tabla-transacciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tabla-transacciones.html'
})
export class TablaTransacciones {
  // Recibimos TODAS las transacciones del padre
  transacciones = input.required<Transaccion[]>();

  // --- ESTADOS DE LOS FILTROS ---
  filtroTipo = signal<string>('TODOS');
  filtroEstado = signal<string>('TODOS');
  filtroCategoria = signal<string>('TODOS');

  // --- ESTADOS DE PAGINACIÓN ---
  paginaActual = signal<number>(1);
  itemsPorPagina = 8; // Puedes cambiar a 10 o 15

  // Extraer las categorías únicas dinámicamente para el desplegable
  categoriasUnicas = computed(() => {
    const categorias = this.transacciones()
      .map(t => t.categoria)
      .filter(c => !!c) as string[]; // Solo las que existan
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
}
