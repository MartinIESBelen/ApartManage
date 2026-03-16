export interface ApartamentoModel {
  id: number;
  nombreInterno: string;
  direccion: string;
  ciudad: string;
  descripcion: string;
  estado: string;
  alertas?: string[];
  // Nota: Omitimos el Propietario entero aquí para no sobrecargar los datos,
  // con esta info nos sobra para pintar las tarjetas.
}
