import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NavigationService } from '../../../services/navigation.service';
import { AuthService } from '../../../core/services/auth.service';
import Swal from 'sweetalert2';

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
  userInfo: { firstName: string; name: string } | null = null;
  constructor(
    private navigationService: NavigationService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.navigationService.activeTab$.subscribe(tab => {
      this.activeTab = tab;
    });

    this.authService.userInfo$.subscribe(userInfo => {
      this.userInfo = userInfo;
    });
  }

   // Méthode pour déconnecter l'utilisateur
   logout() {
       Swal.fire({
      title: 'Confirmer la deconnexion',
      text: 'Êtes-vous sûr de vouloir vous deconnecter ?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, me deconnecter',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        // Montrer une notification de chargement
        Swal.fire({
          title: 'Traitement en cours...',
          text: 'Rejet de la demande de stage',
          icon: 'info',
          allowOutsideClick: false,
          showConfirmButton: false,
          didOpen: () => {
            Swal.showLoading();
          }
        });
  
        this.authService.logout()
      }
    });
  }


  @Input() isSidebarOpen: boolean = false;
  @Output() sidebarClose = new EventEmitter<void>();
  
  closeSidebar() {
    this.sidebarClose.emit();
  }
}
