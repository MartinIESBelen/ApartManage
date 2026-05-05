import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { InventarioService, InventarioRequest } from '../../../../core/services/inventario/inventario.service';

@Component({
  selector: 'app-editar-elemento',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './editar-elemento.html'
})
export class EditarElemento implements OnInit {
  apartamentoId!: number;
  itemId!: number;

  cargando = signal<boolean>(true); // Para saber si estamos buscando los datos
  guardando = false; // Para el botón de submit

  categorias = ['ELECTRODOMESTICO', 'MUEBLE', 'DECORACION', 'MENAJE', 'OTRO'];
  estados = ['NUEVO', 'BUENO', 'DESGASTADO', 'AVERIADO', 'ROTO'];

  form: InventarioRequest = {
    nombre: '',
    categoria: '',
    estado: '',
    precioCompra: null,
    fechaCompra: null
  };

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private inventarioService = inject(InventarioService);

  ngOnInit() {
    this.apartamentoId = Number(this.route.snapshot.paramMap.get('id'));
    this.itemId = Number(this.route.snapshot.paramMap.get('itemId'));
    this.cargarDatosActuales();
  }

  cargarDatosActuales() {
    this.inventarioService.listarInventario(this.apartamentoId).subscribe({
      next: (lista) => {
        const encontrado = lista.find(i => i.id === this.itemId);
        if (encontrado) {
          // Rellenamos el formulario con los datos encontrados
          this.form = {
            nombre: encontrado.nombre,
            categoria: encontrado.categoria,
            estado: encontrado.estado,
            precioCompra: encontrado.precioCompra || null,
            fechaCompra: encontrado.fechaCompra || null
          };
          this.cargando.set(false);
        } else {
          alert('Elemento no encontrado');
          void this.router.navigate(['/apartamento', this.apartamentoId, 'inventario']);
        }
      },
      error: (err) => {
        console.error(err);
        this.cargando.set(false);
      }
    });
  }

  guardar(itemForm: NgForm) {
    if (itemForm.invalid) return;

    this.guardando = true;
    this.inventarioService.editarItem(this.apartamentoId, this.itemId, this.form).subscribe({
      next: () => {
        // Si va bien, volvemos a la ficha de detalles de este elemento
        void this.router.navigate(['/apartamento', this.apartamentoId, 'inventario', this.itemId]);
      },
      error: (err) => {
        this.guardando = false;
        alert('Error al editar el elemento: ' + (err.error?.message || 'Revisa los datos'));
      }
    });
  }
}
