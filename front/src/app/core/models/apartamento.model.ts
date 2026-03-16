export interface ApartamentoModel {
  id: number;
  nombreInterno: string;
  direccion: string;
  ciudad: string;
  descripcion: string;
  estado: string;
  alertas?: string[];
  // Omitimos el Propietario entero aquí para no sobrecargar los datos,
  // con esta info me sobra para pintar las tarjetas por ahora.
}
