export interface InventarioItem {
  id: number;
  nombre: string;
  categoria: string;
  estado: string;
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
