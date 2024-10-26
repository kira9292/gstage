import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TraineeService {
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
