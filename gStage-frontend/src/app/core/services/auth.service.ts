import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { LoginData, RegisterData } from '../interfaces/auth.interface';
import Swal from 'sweetalert2';


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
  ) {
    // this.startTokenExpirationCheck();
  }

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

    // Vérifier si le token est expiré
    private isTokenExpired(token: string): boolean {
      try {
        const decodedToken: any = jwtDecode(token);
        const expirationDate = decodedToken.exp * 1000; // L'expiration est en secondes, il faut la multiplier par 1000
        return Date.now() >= expirationDate;
      } catch (error) {
        console.error('Erreur lors du décodage du token pour vérifier son expiration:', error);
        return true;
      }
    }

    // Vérifier si l'utilisateur est authentifié et si le token est expiré
    private checkTokenAndRedirect(): boolean {
      const token = localStorage.getItem(this.TOKEN_KEY);
      if (!token || this.isTokenExpired(token)) {
        this.logout();
        return false;
      }
      return true;
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
        break;
      
      case 'ROLE_STAGIAIRE':
        this.router.navigate(['/dashboard-stagiaire']);
        break;
      
      case 'ROLE_RH':
        this.router.navigate(['/dashboard-rh']);
        break;

      case 'ROLE_MANAGER':
        this.router.navigate(['dashboard-manager']);
        break;
      
      case 'ROLE_ASSISTANT_GWTE':
        this.router.navigate(['dashboard-gwte']);
        break;
      
      case 'ROLE_DFC':
        this.router.navigate(['/dashboard-dfc']);
        break;
      
      default:
        console.log("Nothing");
        
        this.router.navigate(['/login']);
        break;
    }
  }

  // Méthode pour vérifier si l'utilisateur est authentifié
  isAuthenticated(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY);
    return Boolean(token && !this.isTokenExpired(token));
  }
  
  hasRole(role: string): boolean {
    return this.getRole() === role;
  }

  // Déconnexion de l'utilisateur
  logout() {
    const token = localStorage.getItem(this.TOKEN_KEY); // Récupérer le token stocké
    
    // Créer un nouvel objet HttpHeaders et ajouter le token
    let headers = new HttpHeaders();
    headers = headers.append('Authorization', `Bearer ${token}`);
    headers = headers.append('Content-type', 'application/json');
    
    console.log(headers.get('Authorization')); // Vérifier que l'en-tête est bien présent

    // Envoyer la requête de déconnexion avec les credentials (cookies ou session)
    this.http.post<any>(`${this.API_URL}/deconnexion`, null, { 
        headers, 
        withCredentials: true // Indique que les cookies et autres informations d'authentification doivent être envoyés
    }).subscribe(
        () => {
            // Suppression du token et redirection après la déconnexion réussie
            localStorage.removeItem(this.TOKEN_KEY);
            localStorage.removeItem(this.USER_INFO_KEY);
            this.userInfoSubject.next(null);
            this.router.navigate(['/login']);
            Swal.fire({
              icon: 'success',
              title: 'Deconnexion reussie',
              text: 'La deconnexion a reussie !',
              showConfirmButton: true,
              timer: 3000,
              position: 'top-end',
              toast: true
            });
        },

        error => {
            console.error("Erreur lors de la déconnexion", error);
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Une erreur est survenue lors de la deconnexion',
              footer: error.message
            });        
          });
    }
    
  // startTokenExpirationCheck(): void {
  //   setInterval(() => {
  //     const token = localStorage.getItem(this.TOKEN_KEY);
  //     if (token && this.isTokenExpired(token)) {
  //       localStorage.removeItem(token);
  //       console.warn('Token expiré, déconnexion automatique effectuée.');
  //     }
  //   }, 10000); // Vérification toutes les 10 secondes (ajustez selon vos besoins)
  // }
  
}


