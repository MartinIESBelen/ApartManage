import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { InventarioService, InventarioItem } from '../../../../core/services/inventario/inventario.service';
import {ApartamentoService} from '../../../../core/services/apartamento/apartamento.service';

@Component({
  selector: 'app-lista-elementos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './lista-elementos.html'
})
export class ListaElementos implements OnInit {
  private route = inject(ActivatedRoute);
  private inventarioService = inject(InventarioService);
  private apartamentoService = inject(ApartamentoService);

  apartamentoId!: number;
  inventario = signal<InventarioItem[]>([]);
  cargando = signal<boolean>(true);

  esPropietario = signal<boolean>(false);

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.apartamentoId = +idParam;
      this.comprobarRelacion(); // Comprobamos si somos dueños o inquilinos
      this.cargarInventario();
    }
  }

  comprobarRelacion() {
    this.apartamentoService.getApartamentoById(this.apartamentoId).subscribe({
      next: (apto) => {
        this.esPropietario.set(apto.relacion === 'PROPIETARIO');
      },
      error: (err) => console.error('Error al comprobar relación:', err)
    });
  }

  cargarInventario() {
    this.cargando.set(true);
    this.inventarioService.listarInventario(this.apartamentoId).subscribe({
      next: (data) => {
        this.inventario.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar inventario:', err);
        this.cargando.set(false);
      }
    });
  }
}
