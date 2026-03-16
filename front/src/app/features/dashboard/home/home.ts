import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core'; // <-- Importamos ChangeDetectorRef
import { CommonModule } from '@angular/common';
import { ApartamentoService } from '../../../core/services/apartamento.service';
import { ApartamentoModel } from '../../../core/models/apartamento.model';
import { ApartamentoCard } from '../apartamento-card/apartamento-card';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ApartamentoCard, FormsModule, RouterModule],
  templateUrl: './home.html'
})
export class Home implements OnInit {
  apartamentosList: ApartamentoModel[] = [];

  // Inyectamos nuestras herramientas
  private apartamentoService = inject(ApartamentoService);
  private cd = inject(ChangeDetectorRef); // <-- El actualizador de pantalla

  // Variables para guardar lo que el usuario selecciona en los filtros
  filtroNombre: string = '';
  filtroEstado: string = ''; // Vacio significa "Todos"
  filtroAlertas: boolean = false;

  ngOnInit() {
    this.buscar();
  }

  // 2. MÉTODO: Se llama al pulsar el botón "Filtrar" en el HTML
  buscar() {
    this.apartamentoService.filtrarApartamentos(
      this.filtroNombre,
      this.filtroEstado || undefined, // Si está vacío, mandamos undefined para que Spring lo ignore
      this.filtroAlertas
    ).subscribe({
      next: (data) => {
        // Guardamos los datos filtrados
        this.apartamentosList = data;
        console.log('Apartamentos filtrados:', data);

        // Le recordamos a Angular repintar las tarjetas
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al filtrar los apartamentos:', err);
      }
    });
  }
}
