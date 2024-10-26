import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface RegisterData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  formation: string;
  niveau: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'votre_api_url/api'; // À remplacer par votre URL d'API

  constructor(private http: HttpClient) {}

  register(userData: RegisterData): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/auth/register`, userData)
      .pipe(
        tap(
          (response) => {
            // Log du succès de l'inscription
            console.log('Inscription réussie:', response);
          },
          (error) => {
            // Gestion des erreurs
            console.error('Erreur d\'inscription:', error);
            throw error; // Relance l'erreur pour la gestion dans le composant
          }
        )
      );
  }
}