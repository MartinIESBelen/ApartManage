import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats, FinanzasMes, TransaccionRequest, TransaccionResponse } from '../../models/finanzas.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FinanzasService {
  private http = inject(HttpClient);

  private apiUrlStats = `${environment.apiUrl.replace('/api/v1', '/api')}/stats`;
  private apiUrlTransacciones = `${environment.apiUrl.replace('/api/v1', '/api')}/transacciones`;

  obtenerTransacciones(pisoId: string, periodo: string): Observable<TransaccionResponse[]> {
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

  obtenerTransaccionesPorApartamento(apartamentoId: number): Observable<TransaccionResponse[]> {
    return this.http.get<TransaccionResponse[]>(`${this.apiUrlTransacciones}/apartamento/${apartamentoId}`);
  }

  crearTransaccion(request: TransaccionRequest): Observable<TransaccionResponse[]> {
    return this.http.post<TransaccionResponse[]>(this.apiUrlTransacciones, request);
  }

  actualizarTransaccion(id: number, request: TransaccionRequest): Observable<TransaccionResponse> {
    return this.http.put<TransaccionResponse>(`${this.apiUrlTransacciones}/${id}`, request);
  }

  borrarTransaccion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrlTransacciones}/${id}`);
  }

  obtenerTransaccionesPorContrato(contratoId: number): Observable<TransaccionResponse[]> {
    return this.http.get<TransaccionResponse[]>(`${this.apiUrlTransacciones}/contrato/${contratoId}`);
  }
}
