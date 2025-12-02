import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(username: string, password: string) {
    return this.http.post(`${this.baseUrl}/login`, { username, password });
  }

  register(username: string, password: string) {
    const body = { username, password };
    return this.http.post<any>(`${this.baseUrl}/register`, body);
  }
}
