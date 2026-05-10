import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UsuarioService } from '../../../core/services/usuario/usuario.service';
import { UsuarioPerfil } from '../../../core/models/usuario.model';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './perfil.html'
})
export class Perfil implements OnInit {
  private usuarioService = inject(UsuarioService);
  private cdr = inject(ChangeDetectorRef);
  private sanitizer = inject(DomSanitizer);

  imagenSegura: SafeUrl | null = null;
  usuario: UsuarioPerfil | null = null;
  cargando: boolean = true;
  error: string = '';
  subiendoFoto: boolean = false;
  timestamp: number = Date.now();

  ngOnInit() {
    this.cargarPerfil();
  }

  cargarPerfil() {
    this.cargando = true;
    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (datos) => {
        this.usuario = datos;
        if (this.usuario.imagenPerfil) {
          this.cargarImagenSegura(this.usuario.imagenPerfil);
        } else {
          this.cargando = false;
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('Error al cargar perfil:', err);
        this.error = 'No se pudo cargar la información del perfil.';
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarImagenSegura(nombreArchivo: string) {
    this.usuarioService.obtenerImagenProtegida(nombreArchivo).subscribe({
      next: (blob) => {
        const objectUrl = URL.createObjectURL(blob);
        this.imagenSegura = this.sanitizer.bypassSecurityTrustUrl(objectUrl);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('No se pudo cargar la imagen protegida', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  subirFoto(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const archivo = input.files[0];

      if (this.usuario && this.usuario.id) {
        this.subiendoFoto = true;
        this.cdr.detectChanges();

        this.usuarioService.subirImagenPerfil(this.usuario.id, archivo).subscribe({
          next: () => {
            this.usuarioService.obtenerMiPerfil().subscribe(datosActualizados => {
              this.usuario = datosActualizados;
              if (this.usuario.imagenPerfil) {
                this.cargarImagenSegura(this.usuario.imagenPerfil);
              }
              this.timestamp = Date.now();
              this.subiendoFoto = false;
              this.cdr.detectChanges();
            });
          },
          error: (err) => {
            console.error('Error al subir la foto', err);
            this.error = 'Ocurrió un error al subir la foto de perfil.';
            this.subiendoFoto = false;
            this.cdr.detectChanges();
          }
        });
      }
    }
  }
}
