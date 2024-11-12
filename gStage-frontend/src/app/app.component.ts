import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';

import { CommonModule } from '@angular/common';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { BreadcrumbComponent } from './shared/components/breadcrump/breadcrump.component';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { AuthService } from './core/services/auth.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    SidebarComponent,
    BreadcrumbComponent,
    NavbarComponent,
    CommonModule
],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'gStage-frontend';
  isSidebarOpen = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  // Vérifie si on est sur une page d'authentification
  isAuthPage(): boolean {
    const currentRoute = this.router.url;
    return currentRoute.includes('/login') || 
           currentRoute.includes('/register') ||
           currentRoute.includes('/inscription') ||
           currentRoute.includes('/connexion');
  }


  
    // Affiche le sidebar si l'utilisateur est authentifié et est ROLE_STAGIAIRE
    showSidebar(): boolean {
      return this.authService.isAuthenticated();
      //  && this.authService.hasRole('ROLE_STAGIAIRE');
    }

    isAuthenticated(): boolean {
      return this.authService.isAuthenticated();
    }
  

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar() {
    this.isSidebarOpen = false;
  }
}
