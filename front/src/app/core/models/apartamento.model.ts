export type TipoAlerta = 'INVENTARIO_ROTO' | 'NUEVA_INCIDENCIA';

export interface ApartamentoModel {
  id: number;
  nombreInterno: string;
  direccion: string;
  ciudad: string;
  descripcion: string;
  estado: string;
  alertas?: TipoAlerta[];
  relacion: 'PROPIETARIO' | 'INQUILINO';
  inquilinoActual?: string;
  reservaActivaId?: number;
  rutaImagenPrincipal?: string;
}
