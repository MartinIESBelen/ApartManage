import { Component, computed, inject, OnInit, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApartamentoService } from '../../../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../../../core/models/apartamento.model';

@Component({
  selector: 'app-selector-propiedad',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './selector-propiedad.html'
})
export class SelectorPropiedad implements OnInit {
  private apartamentoService = inject(ApartamentoService);

  // OUTPUT: Emite el apartamento seleccionado (con tipado estricto) hacia el componente padre
  propiedadSeleccionada = output<ApartamentoModel>();

  // Cambiamos any[] por ApartamentoModel[]
  apartamentos = signal<ApartamentoModel[]>([]);
  terminoBusqueda = signal<string>('');

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

  cargarMisPropiedades() {
    this.apartamentoService.filtrarApartamentos('', '', false).subscribe({
      next: (data: ApartamentoModel[]) => {
        // Filtramos para que solo salgan los pisos donde es propietario
        const misPisos = data.filter(apto => apto.relacion === 'PROPIETARIO');
        this.apartamentos.set(misPisos);
      },
      error: (err: any) => {
        console.error('Error al cargar propiedades en el selector:', err);
      }
    });
  }

  seleccionar(apto: ApartamentoModel) {
    this.propiedadSeleccionada.emit(apto);
  }
}
