import { Injectable, inject } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, map } from 'rxjs';
import { ApartamentoService } from '../apartamento/apartamento.service';
import { UsuarioService } from '../usuario/usuario.service';

@Injectable({
  providedIn: 'root'
})
export class ImagenService {
  private sanitizer = inject(DomSanitizer);
  private apartamentoService = inject(ApartamentoService);
  private usuarioService = inject(UsuarioService);

  cargarImagenApartamento(rutaArchivo: string): Observable<SafeUrl> {
    return this.apartamentoService.obtenerImagenProtegida(rutaArchivo).pipe(
      map(blob => this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob)))
    );
  }

  cargarImagenPerfil(rutaArchivo: string): Observable<SafeUrl> {
    return this.usuarioService.obtenerImagenProtegida(rutaArchivo).pipe(
      map(blob => this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob)))
    );
  }

  extraerRutaImagen(apto: any): string | null {
    if (apto.imagenes?.length > 0) {
      const principal = apto.imagenes.find((img: any) => img.esPrincipal);
      return principal ? principal.rutaArchivo : apto.imagenes[0].rutaArchivo;
    }
    return apto.imagenPrincipal ?? null;
  }
}
