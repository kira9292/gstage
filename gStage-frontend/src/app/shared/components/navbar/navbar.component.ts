import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { NavigationService } from '../../../services/navigation.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink, 
    CommonModule, 
    ReactiveFormsModule, 
    RouterLinkActive
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})


export class NavbarComponent implements OnInit {
  @Input() userType: 'intern' | 'candidate' = 'intern';
  activeTab: string = 'dashboard';
  userInfo: { firstName: string; name: string } | null = null;

  constructor(
    private navigationService: NavigationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.navigationService.activeTab$.subscribe(tab => {
      this.activeTab = tab;
    });
     // S'abonner au BehaviorSubject pour récupérer les infos utilisateur
     this.authService.userInfo$.subscribe(userInfo => {
      this.userInfo = userInfo;
    });
  }

    // Vérifie si on est sur une page d'authentification
    isAuthPage(): boolean {
      const currentRoute = this.router.url;
      return currentRoute.includes('/login') || 
             currentRoute.includes('/register') ||
             currentRoute.includes('/inscription') ||
             currentRoute.includes('/connexion');
    }
  

  @Output() menuToggle = new EventEmitter<void>(); // Émet un événement pour toggler le menu

  toggleMenu() {
    this.menuToggle.emit(); // Émettre un événement pour signaler que le menu doit être togglé
  }
}
