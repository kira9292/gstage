import { Component, OnInit } from '@angular/core';
import { GwteService } from '../../services/gwte.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-notifications-gwte',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-4 bg-gray-100 rounded-xl shadow-sm">
      <h3 class="font-bold text-lg mb-4">Notifications</h3>
      <ul>
        <li *ngFor="let notification of notifications" class="mb-2">
          <span [ngClass]="getNotificationClass(notification.type)">
            {{ notification.message }}
          </span>
          <small class="text-gray-500 block">
            {{ notification.timestamp | date: 'short' }}
          </small>
        </li>
      </ul>
    </div>
  `,
  styles: [
    `
      .accepted {
        color: green;
      }
      .rejected {
        color: red;
      }
    `,
  ],
})
export class NotificationsGwteComponent implements OnInit {
  notifications: any[] = [];

  constructor(
    private gwteService: GwteService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const userId = 1; // Remplacez par l'ID de l'utilisateur connecté
    this.gwteService
      .getNotificationsForUser(userId)
      .subscribe((data) => {
        (this.notifications = data);
        console.log("Notifications: " + this.notifications);
        
      });
  }

  getNotificationClass(type: string): string {
    return type === 'ACCEPTED' ? 'accepted' : 'rejected';
  }
}
