import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './navbar/navbar.component';
import { BreadcrumbComponent } from './breadcrump/breadcrump.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { CommonModule } from '@angular/common';


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

  constructor(private router: Router) {}

  // Vérifie si on est sur une page d'authentification
  isAuthPage(): boolean {
    const currentRoute = this.router.url;
    return currentRoute.includes('/login') || 
           currentRoute.includes('/register') ||
           currentRoute.includes('/inscription') ||
           currentRoute.includes('/connexion');
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  closeSidebar() {
    this.isSidebarOpen = false;
  }
}
