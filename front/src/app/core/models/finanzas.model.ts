// Corresponde a tu DTO DashboardStatsResponse
export interface DashboardStats {
  totalApartamentos: number;
  apartamentosOcupados: number;
  ingresosMesActual: number;
  deudaPendienteTotal: number;
  incidenciasAbiertas: number;
}

// Corresponde a tu DTO FinanzasMesResponse
export interface FinanzasMes {
  mes: string;
  ingresos: number;
  gastos: number;
}

// Corresponde a tu DTO TransaccionResponse
export interface Transaccion {
  id: string; // O number, según lo mande tu backend
  fecha: string;
  concepto: string;
  tipo: 'INGRESO' | 'GASTO';
  monto: number;
  estado: 'PENDIENTE' | 'PAGADO' | 'VENCIDO';
}

// Corresponde a tu DTO TransaccionRequest (para cuando hagamos el formulario)
export interface TransaccionRequest {
  apartamentoId: number;
  reservaId?: number | null;
  tipo: 'INGRESO' | 'GASTO';
  categoria: string;
  estado: 'PENDIENTE' | 'PAGADO' | 'VENCIDO';
  concepto: string;
  importe: number;
  comentario?: string;
  dividirEntreTodos: boolean;
  fechaEmision?: string;
}

export interface TransaccionResponse {
  id: number;
  apartamentoId: number;
  apartamentoNombre?: string;
  reservaId?: number;
  inquilinoNombre?: string;
  tipo: 'INGRESO' | 'GASTO';
  categoria: string;
  estado: 'PENDIENTE' | 'PAGADO' | 'VENCIDO';
  concepto: string;
  importe: number;
  comentario?: string;
  fechaEmision: string;
  fechaVencimiento?: string;
  fechaPago?: string;
}
