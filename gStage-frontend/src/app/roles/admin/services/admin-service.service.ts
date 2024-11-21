import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AdminServiceService {
  private apiUrl = 'http://localhost:8081/api'; // URL de l'API

  constructor(private http: HttpClient) { }

  // Méthode pour récupérer tous les utilisateurs
  getAppUsers(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl+"/app-users");
  }
}
