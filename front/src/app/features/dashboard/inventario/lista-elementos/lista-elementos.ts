import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { InventarioService } from '../../../../core/services/inventario/inventario.service';
import { InventarioItem } from '../../../../core/models/inventario.model';
import { ApartamentoService } from '../../../../core/services/apartamento/apartamento.service';

export enum EstadoElemento {
  NUEVO      = 'NUEVO',
  BUENO      = 'BUENO',
  DESGASTADO = 'DESGASTADO',
  ROTO       = 'ROTO',
}

const ESTADOS_MAL: EstadoElemento[] = [EstadoElemento.ROTO, EstadoElemento.DESGASTADO];
const ESTADOS_BIEN: EstadoElemento[] = [EstadoElemento.NUEVO, EstadoElemento.BUENO];

@Component({
  selector: 'app-lista-elementos',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './lista-elementos.html'
})
export class ListaElementos implements OnInit {
  private route              = inject(ActivatedRoute);
  private inventarioService  = inject(InventarioService);
  private apartamentoService = inject(ApartamentoService);

  apartamentoId = 0;
  inventario    = signal<InventarioItem[]>([]);
  cargando      = signal(true);
  esPropietario = signal(false);

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.apartamentoId = +idParam;
      this.comprobarRelacion();
      this.cargarInventario();
    }
  }

  comprobarRelacion() {
    this.apartamentoService.getApartamentoById(this.apartamentoId).subscribe({
      next:  (apto) => this.esPropietario.set(apto.relacion === 'PROPIETARIO'),
      error: (err)  => console.error('Error al comprobar relación:', err),
    });
  }

  cargarInventario() {
    this.cargando.set(true);
    this.inventarioService.listarInventario(this.apartamentoId).subscribe({
      next:  (data) => { this.inventario.set(data); this.cargando.set(false); },
      error: (err)  => { console.error('Error al cargar inventario:', err); this.cargando.set(false); },
    });
  }

  // --- helpers de estado ---

  estaRoto(item: InventarioItem): boolean {
    return item.estado === EstadoElemento.ROTO;
  }

  private estaEnMalEstado(item: InventarioItem): boolean {
    return ESTADOS_MAL.includes(item.estado as EstadoElemento);
  }

  // --- clases por sección ---

  filaClases(item: InventarioItem): string {
    return this.estaRoto(item)
      ? 'bg-habitalis-gold/5 hover:bg-habitalis-gold/10'
      : 'hover:bg-habitalis-cream/50';
  }

  nombreClases(item: InventarioItem): string {
    return this.estaRoto(item) ? 'text-habitalis-gold' : 'text-habitalis-navy';
  }

  categoriaClases(item: InventarioItem): string {
    return this.estaRoto(item)
      ? 'bg-habitalis-gold/10 text-habitalis-gold border-habitalis-gold/20'
      : 'bg-habitalis-cream text-habitalis-navy/60 border-habitalis-navy/10';
  }

  estadoBadgeClases(item: InventarioItem): string {
    return this.estaEnMalEstado(item)
      ? 'bg-habitalis-gold/10 text-habitalis-gold border-habitalis-gold/20'
      : 'bg-habitalis-olive/10 text-habitalis-olive border-habitalis-olive/20';
  }

  estadoPuntoClases(item: InventarioItem): string {
    return this.estaEnMalEstado(item) ? 'bg-habitalis-gold' : 'bg-habitalis-olive';
  }

  accionClases(item: InventarioItem): string {
    return this.estaRoto(item)
      ? 'text-habitalis-gold group-hover:text-habitalis-gold/80'
      : 'text-habitalis-navy/30 group-hover:text-habitalis-olive';
  }
}
