// src/app/auth/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

interface LoginResponse {
  token: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = 'http://localhost:8081/api/auth';
  private tokenKey = 'auth_token';

  private loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());

  constructor(private http: HttpClient) { }

  login(email: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API}/login`, {
      email,
      senha,
    }).pipe(
      tap(response => {
        localStorage.setItem(this.tokenKey, response.token);
        this.loggedIn$.next(true);
      })
    );
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.loggedIn$.next(false);
  }

  isLoggedIn(): boolean {
    return !!this.isAuthenticated();
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }


  getUserRole(): string | null {
    const payload = this.getTokenPayload();
    const rawRole = payload?.role;

    return rawRole?.replace('ROLE_', '') ?? null;
  }

  private getTokenPayload(): any {
    const token = this.getToken();
    if (!token) return null;

    const payloadBase64 = token.split('.')[1];
    try {
      return JSON.parse(atob(payloadBase64));
    } catch (e) {
      return null;
    }
  }
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    const payload = this.getTokenPayload();
    const now = Math.floor(Date.now() / 1000);

    return payload && payload.exp && payload.exp > now;
  }
}
