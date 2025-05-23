import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AttestationPresence, Contract } from '../interfaces/trainee.interface';

@Injectable({
  providedIn: 'root'
})
export class TraineeService {

  private apiUrl = 'http://127.0.0.1:8081/api/stagiaire';

  constructor(private http: HttpClient){}

  getContract(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/contrat`);
  }

  getPresenceAttestations(): Observable<AttestationPresence[]> {
    return this.http.get<AttestationPresence[]>(`${this.apiUrl}/attestation-de-presence`);
  }

  getPayments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/payments`);
  }

  getEndingInternAttestation(): Observable<any> {
    return this.http.get(`${this.apiUrl}/ending-attestation`);
  }


  getNotifications() {
    return [
      { title: 'Demande de stage acceptée', date: '24/10/2024', type: 'success' },
      { title: 'Contrat disponible pour signature', date: '23/10/2024', type: 'info' }
    ];
  }

  getDocuments() {
    return [
      { name: 'Contrat de stage', date: '24/10/2024', status: 'signed', downloadable: true },
      { name: 'Attestation de présence', date: 'En attente', status: 'pending', downloadable: false }
    ];
  }

  getApplications() {
    return [
      { type: 'PROFESSIONNEL', submissionDate: '20/10/2024', status: 'ACCEPTE', department: 'DSI', duration: '6 mois' }
    ];
  }
}
