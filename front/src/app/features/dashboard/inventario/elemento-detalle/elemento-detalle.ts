import { Component, inject, signal, computed } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { InventarioService } from '../../../../core/services/inventario/inventario.service';
import {  InventarioItem } from '../../../../core/models/inventario.model';
import { ApartamentoService } from '../../../../core/services/apartamento/apartamento.service';

const ESTADOS_MAL = ['ROTO', 'AVERIADO', 'DESGASTADO'];

@Component({
  selector: 'app-elemento-detalle',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './elemento-detalle.html'
})
export class ElementoDetalle {
  private route             = inject(ActivatedRoute);
  private router            = inject(Router);
  private inventarioService = inject(InventarioService);
  private apartamentoService = inject(ApartamentoService);

  procesando  = signal(false);
  errorMsg    = signal('');
  confirmando = signal<'roto' | 'eliminar' | null>(null);

  private params = toSignal(this.route.paramMap);

  apartamentoId = computed(() => Number(this.params()?.get('id')     ?? 0));
  itemId        = computed(() => Number(this.params()?.get('itemId') ?? 0));

  private inventario = toSignal(
    this.route.paramMap.pipe(
      switchMap(p => this.inventarioService.listarInventario(Number(p.get('id'))))
    ),
    { initialValue: [] as InventarioItem[] }
  );

  cargando = computed(() => this.inventario().length === 0 && !this.errorMsg());

  item = computed(() =>
    this.inventario().find(i => i.id === this.itemId()) ?? null
  );

  private apartamento = toSignal(
    this.route.paramMap.pipe(
      switchMap(p => this.apartamentoService.getApartamentoById(Number(p.get('id'))))
    )
  );

  esPropietario = computed(() => this.apartamento()?.relacion === 'PROPIETARIO');

  // --- confirmación inline ---

  pedirConfirmacion(accion: 'roto' | 'eliminar') {
    this.confirmando.set(accion);
  }

  cancelarConfirmacion() {
    this.confirmando.set(null);
  }

  confirmarAccion() {
    const accion = this.confirmando();
    this.confirmando.set(null);
    if (accion === 'roto')     this.ejecutarMarcarRoto();
    if (accion === 'eliminar') this.ejecutarEliminar();
  }

  private ejecutarMarcarRoto() {
    this.procesando.set(true);
    this.inventarioService.marcarComoRoto(this.apartamentoId(), this.itemId()).subscribe({
      next: (itemActualizado) => {
        // Actualizamos el item en la lista local sin recargar
        const lista = [...this.inventario()];
        const idx = lista.findIndex(i => i.id === itemActualizado.id);
        if (idx !== -1) lista[idx] = itemActualizado;
        this.procesando.set(false);
      },
      error: (err) => {
        this.errorMsg.set(err.error?.message ?? 'Error al marcar como roto.');
        this.procesando.set(false);
      }
    });
  }

  private ejecutarEliminar() {
    this.procesando.set(true);
    this.inventarioService.eliminarItem(this.apartamentoId(), this.itemId()).subscribe({
      next: () => void this.router.navigate(['/apartamento', this.apartamentoId(), 'inventario']),
      error: (err) => {
        this.errorMsg.set(err.error?.message ?? 'Error al eliminar el elemento.');
        this.procesando.set(false);
      }
    });
  }

  // --- helpers de clases ---

  estaEnMalEstado(i: InventarioItem): boolean {
    return ESTADOS_MAL.includes(i.estado);
  }

  estadoBadgeClases(i: InventarioItem): string {
    return this.estaEnMalEstado(i)
      ? 'bg-habitalis-gold/10 text-habitalis-gold border-habitalis-gold/20'
      : 'bg-habitalis-olive/10 text-habitalis-olive border-habitalis-olive/20';
  }

  estadoPuntoClases(i: InventarioItem): string {
    return this.estaEnMalEstado(i) ? 'bg-habitalis-gold' : 'bg-habitalis-olive';
  }

  formatearFecha(fecha?: string): string {
    if (!fecha) return 'No registrada';
    const [y, m, d] = fecha.split('-');
    const meses = ['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'];
    return `${d} ${meses[Number(m) - 1]} ${y}`;
  }

  formatearPrecio(precio?: number): string {
    if (precio == null) return 'No registrado';
    return precio.toLocaleString('es-ES', { style: 'currency', currency: 'EUR' });
  }
}
