import { Component, Input, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SafeUrl } from '@angular/platform-browser';
import { ApartamentoModel } from '../../../core/models/apartamento.model';
import { ImagenService } from '../../../core/services/imagen/imagen.service';

@Component({
  selector: 'app-apartamento-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './apartamento-card.html'
})
export class ApartamentoCard implements OnInit {
  @Input({ required: true }) apartamento!: ApartamentoModel;

  private imagenService = inject(ImagenService);
  private cdr = inject(ChangeDetectorRef);

  imagenSegura: SafeUrl | null = null;
  cargandoImagen = true;

  ngOnInit() {
    const ruta = this.imagenService.extraerRutaImagen(this.apartamento);
    if (ruta) {
      this.imagenService.cargarImagenApartamento(ruta).subscribe({
        next: (url) => {
          this.imagenSegura = url;
          this.cargandoImagen = false;
          this.cdr.detectChanges();
        },
        error: () => {
          this.cargandoImagen = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.cargandoImagen = false;
    }
  }
}
