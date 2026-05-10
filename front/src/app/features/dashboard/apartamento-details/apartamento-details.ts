import { Component, inject, OnInit, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApartamentoService } from '../../../core/services/apartamento/apartamento.service';
import { ApartamentoModel } from '../../../core/models/apartamento.model';
import { MiPropiedad } from './mi-propiedad/mi-propiedad';
import { VistaInquilino } from './vista-inquilino/vista-inquilino';


@Component({
  selector: 'app-apartamento-details',
  standalone: true,
  imports: [CommonModule, RouterModule, MiPropiedad, VistaInquilino],
  templateUrl: './apartamento-details.html'
})
export class ApartamentoDetails implements OnInit {
  apartamento = signal<ApartamentoModel | undefined>(undefined);
  error = signal<string>('');

  private route = inject(ActivatedRoute);
  private apartamentoService = inject(ApartamentoService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.apartamentoService.getApartamentoById(Number(idParam)).subscribe({
        next: (data) => {
          this.apartamento.set(data);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar detalles:', err);
          this.error.set('No se pudo cargar el apartamento.');
          this.cdr.detectChanges();
        }
      });
    }
  }
}
