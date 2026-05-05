import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { InventarioService, InventarioItem } from '../../../../core/services/inventario/inventario.service';

@Component({
  selector: 'app-elemento-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './elemento-detalle.html'
})
export class ElementoDetalle implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private inventarioService = inject(InventarioService);

  apartamentoId!: number;
  itemId!: number;

  item = signal<InventarioItem | null>(null);
  cargando = signal<boolean>(true);

  // TODO: Conectar esto con tu lógica real (ej: authService.getRol() === 'PROPIETARIO' o leyendo los datos del piso)
  esPropietario = signal<boolean>(true);

  ngOnInit() {
    this.apartamentoId = Number(this.route.snapshot.paramMap.get('id'));
    this.itemId = Number(this.route.snapshot.paramMap.get('itemId'));
    this.cargarDatos();
  }

  cargarDatos() {
    // Como no tenemos GET por ID en el backend, pedimos la lista y filtramos
    this.inventarioService.listarInventario(this.apartamentoId).subscribe({
      next: (lista) => {
        const encontrado = lista.find(i => i.id === this.itemId);
        if (encontrado) {
          this.item.set(encontrado);
        } else {
          alert('Elemento no encontrado');
          void this.router.navigate(['/apartamento', this.apartamentoId, 'inventario']);
        }
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.cargando.set(false);
      }
    });
  }

  marcarComoRoto() {
    if (!confirm('¿Estás seguro de que quieres marcar este elemento como ROTO? Se generará una alerta.')) return;

    this.cargando.set(true);
    this.inventarioService.marcarComoRoto(this.apartamentoId, this.itemId).subscribe({
      next: (itemActualizado) => {
        this.item.set(itemActualizado);
        this.cargando.set(false);
        alert('Elemento marcado como roto. (La alerta se implementará más adelante)');
      },
      error: (err) => {
        alert('Error: ' + err.error?.message);
        this.cargando.set(false);
      }
    });
  }

  eliminar() {
    if (!confirm('¿Estás completamente seguro de borrar este elemento? Esta acción no se puede deshacer.')) return;

    this.inventarioService.eliminarItem(this.apartamentoId, this.itemId).subscribe({
      next: () => {
        alert('Elemento eliminado correctamente');
        void this.router.navigate(['/apartamento', this.apartamentoId, 'inventario']);
      },
      error: (err) => alert('Error al eliminar: ' + err.error?.message)
    });
  }
}
