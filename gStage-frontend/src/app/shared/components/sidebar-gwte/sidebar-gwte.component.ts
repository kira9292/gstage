import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NavigationService } from '../../../services/navigation.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar-gwte',
  standalone: true,
  imports: [
    RouterLink, 
    CommonModule, 
    ReactiveFormsModule, 
    RouterLinkActive
  ],
  templateUrl: './sidebar-gwte.component.html',
  styleUrl: './sidebar-gwte.component.scss'
})


export class SidebarGwteComponent {
  activeTab: string = 'dashboard';
  constructor(
    private navigationService: NavigationService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.navigationService.activeTab$.subscribe(tab => {
      this.activeTab = tab;
    });
  }

   // Méthode pour déconnecter l'utilisateur
   logout() {
    this.authService.logout();
  }
  @Input() isSidebarOpen: boolean = false;
  @Output() sidebarClose = new EventEmitter<void>();
  
  closeSidebar() {
    this.sidebarClose.emit();
  }
}
