import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioPerfil, UsuarioUpdate } from '../../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private http = inject(HttpClient);


  private apiUrl = 'http://localhost:8080/api/v1/usuarios';

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
    return this.http.get(`http://localhost:8080/api/v1/archivos/${nombreArchivo}`, {
      responseType: 'blob'
    });
  }
}
