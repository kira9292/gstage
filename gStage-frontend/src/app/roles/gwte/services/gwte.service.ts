import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';


export interface AppUser {
  id: string;
  firstName: string;
  lastName: string;
  service?: string;
}
@Injectable({
  providedIn: 'root'
})
export class GwteService {

  private apiUrl = 'http://127.0.0.1:8081/api';

  constructor(private http: HttpClient){}

  getDemandesStages(): Observable<any[]> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-type', 'application/json');
    return this.http.get<any[]>(`${this.apiUrl}/demandes`, {headers});
  }

  private demandeSource = new BehaviorSubject<any>(null);
  currentDemande = this.demandeSource.asObservable();

  setDemande(demande: any): void {
    this.demandeSource.next(demande);
  }

  sendWelcomeEmail(email: string) {
    return this.http.post(`${this.apiUrl}/sendWelcomeEmail`, { mail: email });
  }

  rejectInternshipApplication(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/internships/${id}/reject`, {});
  }

  archiveInternshipApplication(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/internships/${id}/archive`, {});
  }

  getManagers():Observable<any[]> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-type', 'application/json');
    return this.http.get<any[]>(`${this.apiUrl}/managers`, {headers});
  }

  getManagerById(userId: string): Observable<AppUser> {
    let headers = new HttpHeaders();
    headers = headers.append('Content-type', 'application/json');
    return this.http.get<AppUser>(`${this.apiUrl}/users/${userId}`, {headers});
  }

      
  cancelProposal(id: number){
    return this.http.put(`${this.apiUrl}/internships/${id}/cancel`, {});
  }


  getNotificationsForUser(userId: number | null): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/notifications/user/${userId}`);
  }


  // Marquer une notification comme lue
  markAsRead(notificationId: number, userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/notifications/${notificationId}/mark-as-read`, null, {
      params: { userId: userId.toString() },
    });
  }

  // Supprimer une notification
  deleteNotification(notificationId: number, userId: number): Observable<void> {
    return this.http.delete<void> (`${this.apiUrl}/notifications/${notificationId}/delete-notification`, 
      {params: {userId: userId.toString()}
    });
  }

  // Marquer toutes les notifications comme lues
  markAllAsRead(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/notifications/mark-all-as-read`, null, {
      params: { userId: userId.toString() },
    });
  }

  // Supprimer toutes les notifications
  deleteAllNotifications(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/notifications/delete-all-notifications`, {
      params: { userId: userId.toString() },
    });  
  }
}
