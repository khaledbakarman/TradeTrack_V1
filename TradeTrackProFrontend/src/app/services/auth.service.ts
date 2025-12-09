import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(username: string, password: string) {
    return this.http.post(`${this.baseUrl}/login`, { username, password });
  }

  register(username: string, password: string, securityQuestion: string, securityAnswer: string) {
    const body = { username, password, securityQuestion, securityAnswer };
    return this.http.post<any>(`${this.baseUrl}/register`, body);
  }

  logout() {
    localStorage.removeItem('token');
  }

  getSecurityQuestion(username: string): Observable<{ securityQuestion: string }> {
    return this.http.post<{ securityQuestion: string }>(`${this.baseUrl}/get-security-question`, { username });
  }

  verifySecurityAnswer(username: string, answer: string): Observable<{ success: boolean }> {
    return this.http.post<{ success: boolean }>(`${this.baseUrl}/verify-security-answer`, { username, answer });
  }

  resetPassword(username: string, newPassword: string): Observable<{ success: boolean; message?: string }> {
    return this.http.post<{ success: boolean; message?: string }>(`${this.baseUrl}/reset-password`, { username, newPassword });
  }
}

