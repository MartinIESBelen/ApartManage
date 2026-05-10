// src/app/core/models/auth.model.ts

// Lo que enviamos al backend
export interface LoginRequest {
  email: string;
  password: string;
}

// Lo que nos devuelve el backend
export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface RegisterRequest {
  nombre: string;
  apellidos: string;
  email: string;
  password: string;
  dniPasaporte: string;
  fechaNacimiento: string;
  rol: string;
}
