import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioPerfil, UsuarioUpdate } from '../../models/usuario.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private http = inject(HttpClient);


  private apiUrl = `${environment.apiUrl}/usuarios`;

  constructor() { }

  obtenerMiPerfil(): Observable<UsuarioPerfil> {
    return this.http.get<UsuarioPerfil>(`${this.apiUrl}/me`);
  }

  actualizarMiPerfil(datos: UsuarioUpdate): Observable<UsuarioPerfil> {
    return this.http.put<UsuarioPerfil>(`${this.apiUrl}/me`, datos);
  }

  subirImagenPerfil(usuarioId: number, archivo: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', archivo);

    return this.http.post(`${this.apiUrl}/${usuarioId}/imagen`, formData, { responseType: 'text' });
  }

  obtenerImagenProtegida(nombreArchivo: string): Observable<Blob> {
    return this.http.get(`${environment.archivoUrl}/${nombreArchivo}`, {
      responseType: 'blob'
    });
  }
}
