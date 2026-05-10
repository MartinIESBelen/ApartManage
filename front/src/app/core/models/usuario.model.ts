// Lo que recibimos del backend (UsuarioPerfilResponse)
export interface UsuarioPerfil {
  id: number;
  nombre: string;
  apellidos: string;
  email: string;
  telefono?: string;
  dniPasaporte?: string;
  fechaNacimiento?: string;
  rol: string;
  imagenPerfil?: string;
}

// Lo que enviamos al backend para actualizar (UsuarioUpdateRequest)
export interface UsuarioUpdate {
  nombre: string;
  apellidos: string;
  telefono?: string;
  dniPasaporte?: string;
  fechaNacimiento?: string;
}
