import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
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
  private readonly USER_INFO_KEY = 'userInfo';


   // BehaviorSubject pour stocker les infos utilisateur
   private userInfoSubject = new BehaviorSubject<{ firstName: string; name: string } | null>(this.getUserInfoFromStorage());
   userInfo$ = this.userInfoSubject.asObservable();

  constructor(
    private http: HttpClient, 
    private router: Router
  ) {}

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
          // Stocker le token JWT du champ "bearer"
          const token = response.bearer;
          localStorage.setItem(this.TOKEN_KEY, token);

           // Extraire les informations nom et prénom
           const userInfo = this.extractUserInfo(token);
           localStorage.setItem(this.USER_INFO_KEY, JSON.stringify(userInfo));
 
           // Mettre à jour le BehaviorSubject
           this.userInfoSubject.next(userInfo);

          // Rediriger en fonction du rôle
          this.redirectUserBasedOnRole();
        },
        (error) => {
          console.error('Erreur de connexion:', error);
        }
      )
    );
  }

    // Extraire nom et prénom du token
    private extractUserInfo(token: string): { firstName: string; name: string } | null {
      try {
        const decodedToken: any = jwtDecode(token);
        return {
          firstName: decodedToken.prenom,
          name: decodedToken.nom
        };
      } catch (error) {
        console.error("Erreur lors du décodage du token:", error);
        return null;
      }
    }
  
    // Récupérer les informations utilisateur du stockage local
    private getUserInfoFromStorage(): { firstName: string; name: string } | null {
      const userInfoString = localStorage.getItem(this.USER_INFO_KEY);
      return userInfoString ? JSON.parse(userInfoString) : null;
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
    
    switch (role) {
      case 'ROLE_ADMIN':
        this.router.navigate(['/dashboard-admin']);
        console.log("Admin");
        
        break;
      
      case 'ROLE_STAGIAIRE':
        this.router.navigate(['/dashboard-stagiaire']);
        console.log("Stagiaire");

        break;
      
      case 'ROLE_RH':
        this.router.navigate(['/dashboard-rh']);
        console.log("RH");

        break;
      
      case 'ROLE_DFC':
        this.router.navigate(['/dashboard-dfc']);
        console.log("DFC");
        break;
      
      default:
        console.log("Nothing");
        
        this.router.navigate(['/login']);
        break;
    }
    
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }
  
  hasRole(role: string): boolean {
    return this.getRole() === role;
  }

  // Déconnexion de l'utilisateur
  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_INFO_KEY);
    this.userInfoSubject.next(null);
    this.router.navigate(['/login']);
  }
}
