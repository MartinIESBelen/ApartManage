import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface InventarioItem {
  id: number;
  nombre: string;
  categoria: string; // 'ELECTRODOMESTICO', 'MUEBLE', etc.
  estado: string;    // 'NUEVO', 'BUENO', 'ROTO', etc.
  precioCompra?: number;
  fechaCompra?: string;
}

export interface InventarioRequest {
  nombre: string;
  categoria: string;
  estado: string;
  precioCompra?: number | null;
  fechaCompra?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/apartamentos';

  // Obtener lista
  listarInventario(apartamentoId: number): Observable<InventarioItem[]> {
    return this.http.get<InventarioItem[]>(`${this.apiUrl}/${apartamentoId}/inventario`);
  }

  // Crear elemento
  agregarItem(apartamentoId: number, item: InventarioRequest): Observable<InventarioItem> {
    return this.http.post<InventarioItem>(`${this.apiUrl}/${apartamentoId}/inventario`, item);
  }

  // Editar elemento
  editarItem(apartamentoId: number, itemId: number, item: InventarioRequest): Observable<InventarioItem> {
    return this.http.put<InventarioItem>(`${this.apiUrl}/${apartamentoId}/inventario/${itemId}`, item);
  }

  // Eliminar elemento
  eliminarItem(apartamentoId: number, itemId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${apartamentoId}/inventario/${itemId}`);
  }

  // Marcar como roto
  marcarComoRoto(apartamentoId: number, itemId: number): Observable<InventarioItem> {
    return this.http.patch<InventarioItem>(`${this.apiUrl}/${apartamentoId}/inventario/${itemId}/roto`, {});
  }
}
