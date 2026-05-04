import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApartamentoService } from '../../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';

@Component({
  selector: 'app-nuevo-movimiento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './nuevo-movimiento.html',
})
export class NuevoMovimiento implements OnInit {
  private apartamentoService = inject(ApartamentoService);

  // --- ESTADOS (Usando ApartamentoModel) ---
  apartamentos = signal<ApartamentoModel[]>([]);
  terminoBusqueda = signal<string>('');
  apartamentoSeleccionado = signal<ApartamentoModel | null>(null);

  // --- FORMULARIO STATE ---
  tipoMovimiento = signal<'INGRESO' | 'GASTO'>('GASTO');
  dividirGasto = signal<boolean>(false);

  // --- BUSCADOR REACTIVO ---
  apartamentosFiltrados = computed(() => {
    const termino = this.terminoBusqueda().toLowerCase().trim();
    if (!termino) return this.apartamentos();

    return this.apartamentos().filter(apto =>
      (apto.nombreInterno && apto.nombreInterno.toLowerCase().includes(termino)) ||
      (apto.ciudad && apto.ciudad.toLowerCase().includes(termino))
    );
  });

  ngOnInit() {
    this.cargarMisPropiedades();
  }

  // Cargamos los pisos de la base de datos
  cargarMisPropiedades() {
    this.apartamentoService.filtrarApartamentos('', '', false).subscribe({
      next: (data: ApartamentoModel[]) => {
        const misPisos = data.filter(apto => apto.relacion === 'PROPIETARIO');
        this.apartamentos.set(misPisos);
      },
      error: (err: any) => {
        console.error('Error al cargar propiedades:', err);
      }
    });
  }

  seleccionarApartamento(apto: ApartamentoModel) {
    this.apartamentoSeleccionado.set(apto);
    this.tipoMovimiento.set('GASTO');
  }

  volverALista() {
    this.apartamentoSeleccionado.set(null);
  }

  guardarTransaccion() {
    console.log('Guardando...', this.tipoMovimiento(), 'para el piso', this.apartamentoSeleccionado()?.nombreInterno);
  }
}
