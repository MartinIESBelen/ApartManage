import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApartamentoModel } from '../models/apartamento.model'; // Ajusta la ruta si es necesario
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ApartamentoService {
  private http = inject(HttpClient);

  // Indicamos cual es la url en nuestro controlador Spring
  private apiUrl = 'http://localhost:8080/api/v1/apartamentos';

  constructor() { }

  // Equivale a tu antiguo getAllHousingLocations()
  getMisApartamentos(): Observable<ApartamentoModel[]> {
    return this.http.get<ApartamentoModel[]>(this.apiUrl);
  }

  // Método para traer los detalles de 1 solo apartamento
  getApartamentoById(id: number): Observable<ApartamentoModel> {
    return this.http.get<ApartamentoModel>(`${this.apiUrl}/${id}`);
  }

  // Método para enviar los filtros al Backend
  filtrarApartamentos(nombre?: string, estado?: string, conAlertas?: boolean): Observable<ApartamentoModel[]> {
    // Construimos los parámetros de la URL de forma dinámica
    let params = new HttpParams();

    if (nombre) params = params.set('nombre', nombre);
    if (estado) params = params.set('estado', estado);
    if (conAlertas) params = params.set('conAlertas', conAlertas);

    return this.http.get<ApartamentoModel[]>(`${this.apiUrl}/filtrar`, { params });
  }

  // Método para actualizar un apartamento
  actualizarApartamento(id: number, datosActualizados: any): Observable<ApartamentoModel> {
    return this.http.put<ApartamentoModel>(`${this.apiUrl}/${id}`, datosActualizados);
  }

  // Método para crear un apartamento
  crearApartamento(datos: any): Observable<ApartamentoModel> {
    return this.http.post<ApartamentoModel>(this.apiUrl, datos);
  }
}
