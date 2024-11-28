import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GwteService } from '../../services/gwte.service';
import { AuthService } from '../../../../core/services/auth.service';
import Swal from 'sweetalert2';

interface AppUser {
  id: number;
}

interface Notification {
  id: number;
  message: string;
  sendingDate: string;
  typeNotification: 'ACCEPTE' | 'REFUSE' | 'EN_COURS' | 'TERMINE' ;
  read: boolean;
  appUser: AppUser;
}

@Component({
  selector: 'app-notifications-gwte',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications-gwte.component.html',
  styleUrls: ['./notifications-gwte.component.scss']
})
export class NotificationsGwteComponent implements OnInit {
  notifications: Notification[] = [];
  allNotifications: Notification[] = [];
  showAllNotifications: boolean = false;
  @Output() readed = new EventEmitter<void>();

  constructor(
    private gwteService: GwteService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.getNotificationsForUser();
  }

  getNotificationsForUser(): void {
    const userId = this.authService.getUserIdFromToken();
    this.gwteService
      .getNotificationsForUser(userId)
      .subscribe((data: Notification[]) => {
        this.allNotifications = data;
        this.notifications = data.slice(0, 2);
      });
  }

  markNotificationAsRead(notificationId: number): void {
    const userId = this.authService.getUserIdFromToken();
    if(userId) {
      this.gwteService.markAsRead(notificationId, userId).subscribe(() => {
        this.readed.emit();
        this.getNotificationsForUser();
      });
    }
  }


  deleteNotification(notificationId: number): void{
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr de vouloir archiver cette notification ?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, archiver',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        // Afficher le loader
        Swal.fire({
          title: 'Archivage en cours...',
          text: 'Veuillez patienter pendant l\'archivage de la notification',
          icon: 'info',
          allowOutsideClick: false,
          showConfirmButton: false,
          didOpen: () => {
            Swal.showLoading();
          }
        });

        const userId = this.authService.getUserIdFromToken();
        if(userId) {
          this.gwteService.deleteNotification(notificationId, userId).subscribe({
            next: () => {
              // Notification de succès
              Swal.fire({
                icon: 'success',
                title: 'Notification archivée',
                text: 'La notification a été archivée avec succès',
                showConfirmButton: true,
                timer: 3000,
                position: 'top-end',
                toast: true
              });
              
              this.getNotificationsForUser();
              this.readed.emit();
            },
            error: (error) => {
              // Notification d'erreur
              Swal.fire({
                icon: 'error',
                title: 'Erreur',
                text: 'Une erreur est survenue lors de l\'archivage de la notification',
                footer: error.message
              });
            }
          });
      }
    }
    });
  }

  markAllNotificationsAsRead(): void {
    const userId = this.authService.getUserIdFromToken();
  
    if(userId){
      this.gwteService.markAllAsRead(userId).subscribe(() => {
        this.readed.emit();
        this.getNotificationsForUser();
      });
    }
  }
  
  deleteAllNotifications(): void {
    Swal.fire({
      title: 'Confirmer la suppression',
      text: 'Êtes-vous sûr de vouloir archiver toutes vos notifications ?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Oui, archiver',
      cancelButtonText: 'Annuler'
    }).then((result) => {
      if (result.isConfirmed) {
        // Afficher le loader
        Swal.fire({
          title: 'Archivage en cours...',
          text: 'Veuillez patienter pendant l\'archivage des notification',
          icon: 'info',
          allowOutsideClick: false,
          showConfirmButton: false,
          didOpen: () => {
            Swal.showLoading();
          }
        });

        const userId = this.authService.getUserIdFromToken();
        if(userId) {
          this.gwteService.deleteAllNotifications(userId).subscribe({
            next: () => {
              // Notification de succès
              Swal.fire({
                icon: 'success',
                title: 'Notifications archivées',
                text: 'Les notifications ont été archivées avec succès',
                showConfirmButton: true,
                timer: 3000,
                position: 'top-end',
                toast: true
              });
              
              this.getNotificationsForUser();
              this.readed.emit();
            },
            error: (error) => {
              // Notification d'erreur
              Swal.fire({
                icon: 'error',
                title: 'Erreur',
                text: 'Une erreur est survenue lors de l\'archivage des notifications',
                footer: error.message
              });
            }
          });
      }
    }
    });
  }

  viewAllNotifications() {
    this.showAllNotifications = true;
    this.notifications = this.allNotifications;
  }

  viewSomeNotifications() {
    this.showAllNotifications = false;
    this.notifications = this.allNotifications.slice(0,2);
  }

  getNotificationStatusIcon(type: 'ACCEPTE' | 'REFUSE' | 'EN_COURS' | 'TERMINE'): string {
    return type === 'ACCEPTE' 
      ? '✓' 
      : '✗';
  }

  getNotificationStatusColor(type: 'ACCEPTE' | 'REFUSE' | 'EN_COURS' | 'TERMINE'): string {
    return type === 'ACCEPTE' 
      ? 'text-green-500' 
      : 'text-red-500';
  }
}