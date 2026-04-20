import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private http = inject(HttpClient);
  // Ajusta la URL base según el puerto de tu backend
  private apiUrl = 'http://localhost:8080/api/v1';

  vincularCodigo(codigo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reservas/vincular`, { codigoVinculacion: codigo });
  }

  crearReserva(apartamentoId: number, reservaData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/apartamentos/${apartamentoId}/reservas`, reservaData);
  }

  crearReservaManual(apartamentoId: number, reservaManualData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/apartamentos/${apartamentoId}/reservas/manual`, reservaManualData);
  }
}
