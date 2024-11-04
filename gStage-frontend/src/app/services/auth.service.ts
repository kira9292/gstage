import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

export interface RegisterData {
  firstName: string;
  name: string;
  email: string;
  password: string;
  username: string;
}

export interface LoginData {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://127.0.0.1:8081/api';
  private readonly TOKEN_KEY = 'jwtToken';

  constructor(private http: HttpClient, private router: Router) {}

  // Méthode pour l'inscription
  register(userData: RegisterData): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/inscription`, userData).pipe(
      tap(
        (response) => {
          console.log('Inscription réussie:', response);
        },
        (error) => {
          console.error('Erreur d\'inscription:', error);
          throw error;
        }
      )
    );
  }

  // Méthode pour la connexion
  login(userData: LoginData): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/connexion`, userData).pipe(
      tap(
        (response) => {
          console.log('Connexion réussie:', response);

          // Stocker le token JWT du champ "bearer"
          const token = response.bearer;
          localStorage.setItem(this.TOKEN_KEY, token);

          // Rediriger en fonction du rôle
          this.redirectUserBasedOnRole();
        },
        (error) => {
          console.error('Erreur de connexion:', error);
          throw error;
        }
      )
    );
  }

  // Méthode pour décoder le token JWT et récupérer le rôle
  getRole(): string | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (token) {
      const decodedToken: any = jwtDecode(token);
      const roles = decodedToken.roles;
      return roles && roles.length ? roles[0].authority : null;
    }
    return null;
  }

  // Redirection en fonction du rôle
  redirectUserBasedOnRole() {
    const role = this.getRole();
    if (role === 'ROLE_ADMIN') {
      this.router.navigate(['/admin-interface']);
    } else if (role === 'ROLE_DEMANDEUR') {
      this.router.navigate(['/dashboard']);
    } else {
      this.router.navigate(['/login']); // Redirection par défaut si le rôle est inconnu
    }
  }

  // Déconnexion de l'utilisateur
  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }
}
