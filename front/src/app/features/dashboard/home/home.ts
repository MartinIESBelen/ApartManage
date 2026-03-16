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

  private apartamentoService = inject(ApartamentoService);
  private cd = inject(ChangeDetectorRef);


  filtroNombre: string = '';
  filtroEstado: string = '';
  filtroAlertas: boolean = false;

  ngOnInit() {
    this.buscar();
  }

  //MÉTODO: Se llama al pulsar el botón "Filtrar" en el HTML
  buscar() {
    this.apartamentoService.filtrarApartamentos(
      this.filtroNombre,
      this.filtroEstado || undefined,
      this.filtroAlertas
    ).subscribe({
      next: (data) => {

        this.apartamentosList = data;
        console.log('Apartamentos filtrados:', data);

        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al filtrar los apartamentos:', err);
      }
    });
  }
}
