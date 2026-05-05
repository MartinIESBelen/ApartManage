import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { InventarioService, InventarioRequest } from '../../../../core/services/inventario/inventario.service';

@Component({
  selector: 'app-crear-elemento',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './crear-elemento.html'
})
export class CrearElemento implements OnInit {
  apartamentoId!: number;
  cargando = false;

  // Arrays basados en tus Enums de Java
  categorias = ['ELECTRODOMESTICO', 'MUEBLE', 'DECORACION', 'MENAJE', 'OTRO'];
  estados = ['NUEVO', 'BUENO', 'DESGASTADO', 'AVERIADO', 'ROTO'];

  form: InventarioRequest = {
    nombre: '',
    categoria: '',
    estado: 'BUENO', // Valor por defecto amigable
    precioCompra: null,
    fechaCompra: null
  };

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private inventarioService = inject(InventarioService);

  ngOnInit() {
    this.apartamentoId = Number(this.route.snapshot.paramMap.get('id'));
  }

  guardar(itemForm: NgForm) {
    if (itemForm.invalid) return;

    this.cargando = true;
    this.inventarioService.agregarItem(this.apartamentoId, this.form).subscribe({
      next: () => {
        // Si va bien, volvemos a la lista de inventario de este piso
        void this.router.navigate(['/apartamento', this.apartamentoId, 'inventario']);
      },
      error: (err) => {
        this.cargando = false;
        alert('Error al añadir el elemento: ' + (err.error?.message || 'Revisa los datos'));
      }
    });
  }
}
