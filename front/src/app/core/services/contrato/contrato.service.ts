import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Usamos la estructura de tu ReservaResponse de Spring Boot
export interface ContratoLista {
  id: number;
  codigoVinculacion: string;
  fechaEntrada: string;
  fechaSalida: string;
  precioBaseAlquiler: number;
  estado: string;
  nombreApartamento: string;
  nombreInquilino: string;
}

export interface InquilinoPublico {
  id: number;
  nombre: string;
  apellidos: string;
  email: string;
  telefono?: string;
}

export interface ContratoDetalle {
  id: number;
  codigoVinculacion: string;
  nombreApartamento: string;
  fechaEntrada: string;
  fechaSalida: string;
  precioBaseAlquiler: number;
  fianza: number;
  estado: string;
  creadoEn: string;
  inquilino: InquilinoPublico | null;
}

@Injectable({
  providedIn: 'root'
})
export class ContratoService {
  private http = inject(HttpClient);

  // Ajusta esto a la ruta real de tu controlador en Spring Boot
  private apiUrl = 'http://localhost:8080/api/v1/reservas';

  // Método para obtener todos los contratos del propietario
  getMisContratos(): Observable<ContratoLista[]> {
    return this.http.get<ContratoLista[]>(this.apiUrl);
  }

  getDetalleContrato(id: number): Observable<ContratoDetalle> {
    return this.http.get<ContratoDetalle>(`${this.apiUrl}/${id}`);
  }
}
