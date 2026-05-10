import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApartamentoService } from '../../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../../core/models/apartamento.model';
import { FormularioTransaccion } from './formulario-transaccion/formulario-transaccion';

@Component({
  selector: 'app-nuevo-movimiento',
  standalone: true,
  imports: [CommonModule, FormsModule, FormularioTransaccion],
  templateUrl: './nuevo-movimiento.html',
})
export class NuevoMovimiento implements OnInit {
  private apartamentoService = inject(ApartamentoService);
  private router = inject(Router);

  apartamentos = signal<ApartamentoModel[]>([]);
  terminoBusqueda = signal<string>('');
  apartamentoSeleccionado = signal<ApartamentoModel | null>(null);

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
        const misPisos = data.filter(apto => apto.relacion === 'PROPIETARIO');
        this.apartamentos.set(misPisos);
      },
      error: (err: any) => console.error('Error al cargar propiedades:', err)
    });
  }

  seleccionarApartamento(apto: ApartamentoModel) {
    this.apartamentoSeleccionado.set(apto);
  }

  volverALista() {
    this.apartamentoSeleccionado.set(null);
  }

  irABalance() {
    this.router.navigate(['/finanzas/balance']);
  }
}
