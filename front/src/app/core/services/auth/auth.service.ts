import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthResponse, LoginRequest } from '../../models/auth.model';
import { RegisterRequest} from '../../models/auth.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor() { }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials);
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData);
  }

  guardarToken(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  obtenerToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
  }

  solicitarRecuperacion(email: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/recuperar-password`, { email }, { responseType: 'text' });
  }

  resetearPassword(token: string, nuevaPassword: string): Observable<string> {
    return this.http.post(`${this.apiUrl}/reset-password`, { token, nuevaPassword }, { responseType: 'text' });
  }

}
