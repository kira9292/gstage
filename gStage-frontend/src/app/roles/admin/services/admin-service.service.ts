import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Service, User} from "../components/dashboard-admin/dashboard-admin.component";


@Injectable({
  providedIn: 'root'
})
export class AdminServiceService {
  private apiUrl = 'http://localhost:8081/api'; // URL de l'API

  constructor(private http: HttpClient) { }

  // Méthode pour récupérer tous les utilisateurs
  getAppUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl+"/list-users");
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`);
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${user.id}`, user);
  }
  getServices(): Observable<Service[]> {
    return this.http.get<Service[]>(`${this.apiUrl}/list-services`);
  }

  saveUser(payload: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });

    return this.http.post<any>(this.apiUrl+"/inscription", payload, { headers });
  }

}
