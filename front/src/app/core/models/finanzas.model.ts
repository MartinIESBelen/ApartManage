export interface DashboardStats {
  totalApartamentos: number;
  apartamentosOcupados: number;
  ingresosMesActual: number;
  deudaPendienteTotal: number;
  incidenciasAbiertas: number;
}

export interface FinanzasMes {
  mes: string;
  ingresos: number;
  gastos: number;
}


export interface TransaccionRequest {
  apartamentoId: number;
  contratoId?: number | null;
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
  contratoId?: number;
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
