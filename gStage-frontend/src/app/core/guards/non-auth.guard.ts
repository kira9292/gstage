import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class NonAuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      const userRole = this.authService.getRole();
      if (userRole) {
        // Redirige vers le tableau de bord correspondant en fonction du rôle
        switch (userRole) {
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
            this.router.navigate(['/default-dashboard']);
            break;
        }
      }
      return false; // Empêche l'utilisateur connecté d'accéder à la route
    }
    return true; // Permet aux utilisateurs non connectés d'accéder à la route
  }
}
