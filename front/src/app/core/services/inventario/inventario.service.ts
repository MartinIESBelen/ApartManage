import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {InventarioItem, InventarioRequest} from '../../models/inventario.model';

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/apartamentos';

  listarInventario(apartamentoId: number): Observable<InventarioItem[]> {
    return this.http.get<InventarioItem[]>(`${this.apiUrl}/${apartamentoId}/inventario`);
  }

  agregarItem(apartamentoId: number, item: InventarioRequest): Observable<InventarioItem> {
    return this.http.post<InventarioItem>(`${this.apiUrl}/${apartamentoId}/inventario`, item);
  }

  editarItem(apartamentoId: number, itemId: number, item: InventarioRequest): Observable<InventarioItem> {
    return this.http.put<InventarioItem>(`${this.apiUrl}/${apartamentoId}/inventario/${itemId}`, item);
  }

  eliminarItem(apartamentoId: number, itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${apartamentoId}/inventario/${itemId}`);
  }

  marcarComoRoto(apartamentoId: number, itemId: number): Observable<InventarioItem> {
    return this.http.patch<InventarioItem>(`${this.apiUrl}/${apartamentoId}/inventario/${itemId}/roto`, {});
  }
}
