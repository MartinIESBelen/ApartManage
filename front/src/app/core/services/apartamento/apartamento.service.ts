import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApartamentoModel } from '../../models/apartamento.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApartamentoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/apartamentos`;
  private archivoUrl = environment.archivoUrl;

  getMisApartamentos(): Observable<ApartamentoModel[]> {
    return this.http.get<ApartamentoModel[]>(this.apiUrl);
  }

  getApartamentoById(id: number): Observable<ApartamentoModel> {
    return this.http.get<ApartamentoModel>(`${this.apiUrl}/${id}`);
  }

  filtrarApartamentos(nombre?: string, estado?: string, conAlertas?: boolean): Observable<ApartamentoModel[]> {
    let params = new HttpParams();
    if (nombre) params = params.set('nombre', nombre);
    if (estado) params = params.set('estado', estado);
    if (conAlertas) params = params.set('conAlertas', conAlertas);
    return this.http.get<ApartamentoModel[]>(`${this.apiUrl}/filtrar`, { params });
  }

  actualizarApartamento(id: number, datosActualizados: any): Observable<ApartamentoModel> {
    return this.http.put<ApartamentoModel>(`${this.apiUrl}/${id}`, datosActualizados);
  }

  crearApartamento(datos: any): Observable<ApartamentoModel> {
    return this.http.post<ApartamentoModel>(this.apiUrl, datos);
  }

  subirImagen(apartamentoId: number, archivo: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', archivo);
    return this.http.post(`${this.apiUrl}/${apartamentoId}/imagenes`, formData, { responseType: 'text' });
  }

  obtenerImagenProtegida(rutaArchivo: string): Observable<Blob> {
    return this.http.get(`${this.archivoUrl}/${rutaArchivo}`, { responseType: 'blob' });
  }

  obtenerDocumentos(apartamentoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${apartamentoId}/documentos`);
  }

  subirDocumento(apartamentoId: number, archivo: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', archivo);
    return this.http.post(`${this.apiUrl}/${apartamentoId}/documentos`, formData, { responseType: 'text' });
  }

  borrarDocumento(apartamentoId: number, documentoId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${apartamentoId}/documentos/${documentoId}`);
  }

  descargarDocumento(rutaArchivo: string): Observable<Blob> {
    return this.http.get(`${this.archivoUrl}/${rutaArchivo}`, { responseType: 'blob' });
  }
}
