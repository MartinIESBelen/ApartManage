import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats, FinanzasMes, Transaccion, TransaccionRequest, TransaccionResponse } from '../../models/finanzas.model';

@Injectable({
  providedIn: 'root'
})
export class FinanzasService {
  private http = inject(HttpClient);

  // Ajusta estas URLs según cómo las tengas en tu backend
  private apiUrlStats = 'http://localhost:8080/api/stats';
  private apiUrlTransacciones = 'http://localhost:8080/api/transacciones';

  obtenerTransacciones(pisoId: string, periodo: string): Observable<TransaccionResponse[]> {
    // Construimos los parámetros para la URL (?pisoId=...&periodo=...)
    let params = new HttpParams().set('periodo', periodo);

    if (pisoId !== 'TODOS') {
      params = params.set('apartamentoId', pisoId);
    }

    return this.http.get<TransaccionResponse[]>(this.apiUrlTransacciones, { params });
  }

  obtenerResumenDashboard(email: string): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.apiUrlStats}/resumen?email=${email}`);
  }

  obtenerBalanceAnual(email: string, anio: number): Observable<FinanzasMes[]> {
    return this.http.get<FinanzasMes[]>(`${this.apiUrlStats}/balance?email=${email}&anio=${anio}`);
  }

  obtenerTransaccionesPorApartamento(apartamentoId: number): Observable<Transaccion[]> {
    return this.http.get<Transaccion[]>(`${this.apiUrlTransacciones}/apartamento/${apartamentoId}`);
  }

  crearTransaccion(request: TransaccionRequest): Observable<Transaccion[]> {
    return this.http.post<Transaccion[]>(this.apiUrlTransacciones, request);
  }
}
