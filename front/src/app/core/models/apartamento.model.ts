export interface ApartamentoModel {
  id: number;
  nombreInterno: string;
  direccion: string;
  ciudad: string;
  descripcion: string;
  estado: string;
  alertas?: string[];
  relacionUsuario: 'PROPIETARIO' | 'INQUILINO';
}
