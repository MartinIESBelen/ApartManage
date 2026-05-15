import { Component, inject, signal, computed, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { UsuarioService } from '../../../core/services/usuario/usuario.service';
import { UsuarioPerfil } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './perfil.html'
})
export class Perfil implements OnDestroy {
  private usuarioService = inject(UsuarioService);
  private sanitizer      = inject(DomSanitizer);

  usuario      = signal<UsuarioPerfil | null>(null);
  imagenSegura = signal<string | null>(null);   // object URL en crudo
  cargando     = signal(true);
  subiendoFoto = signal(false);
  error        = signal('');

  // La URL saneada se deriva del signal, sin lógica duplicada
  imagenSaneada = computed(() => {
    const url = this.imagenSegura();
    return url ? this.sanitizer.bypassSecurityTrustUrl(url) : null;
  });

  private objectUrlActual: string | null = null;

  constructor() {
    this.cargarPerfil();
  }

  ngOnDestroy() {
    this.liberarObjectUrl();
  }

  private liberarObjectUrl() {
    if (this.objectUrlActual) {
      URL.revokeObjectURL(this.objectUrlActual);
      this.objectUrlActual = null;
    }
  }

  cargarPerfil() {
    this.cargando.set(true);
    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (datos) => {
        this.usuario.set(datos);
        if (datos.imagenPerfil) {
          this.cargarImagenSegura(datos.imagenPerfil);
        } else {
          this.cargando.set(false);
        }
      },
      error: (err) => {
        console.error('Error al cargar perfil:', err);
        this.error.set('No se pudo cargar la información del perfil.');
        this.cargando.set(false);
      }
    });
  }

  private cargarImagenSegura(nombreArchivo: string) {
    this.usuarioService.obtenerImagenProtegida(nombreArchivo).subscribe({
      next: (blob) => {
        this.liberarObjectUrl();                       // Revoca la URL anterior antes de crear una nueva
        this.objectUrlActual = URL.createObjectURL(blob);
        this.imagenSegura.set(this.objectUrlActual);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('No se pudo cargar la imagen protegida', err);
        this.cargando.set(false);
      }
    });
  }

  subirFoto(event: Event) {
    const input = event.target as HTMLInputElement;
    const archivo = input.files?.[0];
    const id = this.usuario()?.id;

    if (!archivo || !id) return;

    this.subiendoFoto.set(true);
    this.error.set('');

    this.usuarioService.subirImagenPerfil(id, archivo).subscribe({
      next: () => {
        // Recargamos el perfil completo para tener los datos actualizados
        this.usuarioService.obtenerMiPerfil().subscribe({
          next: (datosActualizados) => {
            this.usuario.set(datosActualizados);
            if (datosActualizados.imagenPerfil) {
              this.cargarImagenSegura(datosActualizados.imagenPerfil);
            }
            this.subiendoFoto.set(false);
          },
          error: () => this.subiendoFoto.set(false)
        });
      },
      error: (err) => {
        console.error('Error al subir la foto', err);
        this.error.set('Ocurrió un error al subir la foto de perfil.');
        this.subiendoFoto.set(false);
      }
    });
  }
}
