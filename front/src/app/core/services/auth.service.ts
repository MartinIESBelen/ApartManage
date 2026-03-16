import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthResponse, LoginRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Inyectamos el cliente HTTP (el cartero)
  private http = inject(HttpClient);

  // La URL base de tu Spring Boot
  private apiUrl = 'http://localhost:8080/api/v1/auth';

  constructor() { }

  //Método para hacer Login
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials);
  }

  //Método para guardar el Token en la memoria del navegador
  guardarToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  //Método para recuperar el Token (lo usaremos más adelante)
  obtenerToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  //Método para cerrar sesión
  logout(): void {
    localStorage.removeItem('jwt_token');
  }
}
