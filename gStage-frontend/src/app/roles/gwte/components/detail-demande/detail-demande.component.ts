import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import Swal from 'sweetalert2';
import { GwteService } from '../../services/gwte.service';
import { Router } from '@angular/router';
import { InternshipStatus } from '../../../../enums/gstage.enum';

@Component({
  selector: 'app-internship-details-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detail-demande.component.html',
  styleUrl: './detail-demande.component.scss'
})
export class InternshipDetailModalComponent {
  @Input() demande: any;
  @Output() close = new EventEmitter<void>();

  InternshipStatus = InternshipStatus

  constructor(
    private gwteService: GwteService,
    private router: Router
  ){
  }

// Nouvelles méthodes pour les actions contextuelles
sendWelcomeEmail() {
  // Montrer une notification de chargement
  Swal.fire({
    title: 'Envoi en cours...',
    text: 'Envoi du mail de bienvenue',
    icon: 'info',
    allowOutsideClick: false,
    showConfirmButton: false,
    didOpen: () => {
      Swal.showLoading();
    }
  });

  if (!this.demande.candidat?.email) {
    Swal.fire({
      icon: 'error',
      title: 'Erreur',
      text: 'Email du candidat non trouvé',
    });
    return;
  }

  this.gwteService.sendWelcomeEmail(this.demande.candidat.email)
    .subscribe({
      next: () => {
      // Notification de succès
      Swal.fire({
        icon: 'success',
        title: 'Mail envoyé !',
        text: `Le mail de bienvenue a été envoyé à ${this.demande.candidat.email}`,
        showConfirmButton: true,
        timer: 3000,
        position: 'top-end',
        toast: true
      });
    },
    error: (error) => {
      // Notification d'erreur
      Swal.fire({
        icon: 'error',
        title: 'Erreur',
        text: 'Une erreur est survenue lors de l\'envoi du mail',
        footer: error.message
      });
    }
  });
}


scheduleEvaluation(): void {
  // Implémentation de la planification d'évaluation
  console.log('Planification évaluation pour:', this.demande.demandeStage.reference);
}

generateCertificate(): void {
  // Implémentation de la génération d'attestation
  console.log('Génération attestation pour:', this.demande.demandeStage.reference);
}

archiveApplication(): void {
  // Implémentation de l'archivage
  console.log('Archivage de la demande:', this.demande.demandeStage.reference);
}


canViewCV(): boolean {
  return !!this.demande?.demandeStage?.cv;
}
// canDownloadCV(demande: any): boolean {
//   return !!demande.demandeStage.cv;
// }

canViewCoverLetter(): boolean {
  return !!this.demande?.demandeStage?.coverLetter;
}
canDownloadCoverLetter(demande: any): boolean {
  return !!demande.demandeStage.coverLetter;
}

// Méthode pour afficher le CV
viewCV(): void {
  const fileData = this.demande?.demandeStage?.cv;
  if (fileData) {
    const byteCharacters = atob(fileData);
    const byteArrays = [];
    for (let offset = 0; offset < byteCharacters.length; offset += 1024) {
      const slice = byteCharacters.slice(offset, offset + 1024);
      const byteNumbers = new Array(slice.length);
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }
      byteArrays.push(new Uint8Array(byteNumbers));
    }
    const blob = new Blob(byteArrays, { type: this.demande.demandeStage.cvContentType });
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
  }
}

// Méthode pour télécharger le CV
downloadCV(demande: any): void {
const fileData = demande.demandeStage.cv; // Base64 du CV
if (fileData) {
  const byteCharacters = atob(fileData); // Décodage du base64
  const byteArrays = [];
  for (let offset = 0; offset < byteCharacters.length; offset += 1024) {
    const slice = byteCharacters.slice(offset, offset + 1024);
    const byteNumbers = new Array(slice.length);
    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i);
    }
    byteArrays.push(new Uint8Array(byteNumbers));
  }
  const blob = new Blob(byteArrays, { type: demande.demandeStage.cvContentType });
  const url = URL.createObjectURL(blob); // Crée une URL pour le fichier
  const a = document.createElement('a');
  a.href = url;
  a.download = 'cv.pdf'; // Vous pouvez ajuster l'extension en fonction du type de fichier
  a.click();
  URL.revokeObjectURL(url); // Libérer l'URL après le téléchargement
}
}

// Méthode pour afficher la lettre de motivation
viewCoverLetter(): void {
  const fileData = this.demande?.demandeStage?.coverLetter;
  if (fileData) {
    const byteCharacters = atob(fileData);
    const byteArrays = [];
    for (let offset = 0; offset < byteCharacters.length; offset += 1024) {
      const slice = byteCharacters.slice(offset, offset + 1024);
      const byteNumbers = new Array(slice.length);
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }
      byteArrays.push(new Uint8Array(byteNumbers));
    }
    const blob = new Blob(byteArrays, { type: this.demande.demandeStage.coverLetterContentType });
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
  }
}

// Méthode pour télécharger la lettre de motivation
downloadCoverLetter(demande: any): void {
const fileData = demande.demandeStage.coverLetter; // Base64 de la lettre de motivation
if (fileData) {
  const byteCharacters = atob(fileData); // Décodage du base64
  const byteArrays = [];
  for (let offset = 0; offset < byteCharacters.length; offset += 1024) {
    const slice = byteCharacters.slice(offset, offset + 1024);
    const byteNumbers = new Array(slice.length);
    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i);
    }
    byteArrays.push(new Uint8Array(byteNumbers));
  }
  const blob = new Blob(byteArrays, { type: demande.demandeStage.coverLetterContentType });
  const url = URL.createObjectURL(blob); // Crée une URL pour le fichier
  const a = document.createElement('a');
  a.href = url;
  a.download = 'coverLetter.pdf'; // Vous pouvez ajuster l'extension en fonction du type de fichier
  a.click();
  URL.revokeObjectURL(url); // Libérer l'URL après le téléchargement
}
}

proposeToManager(): void {
  this.gwteService.setDemande(this.demande);
  this.router.navigate(['/propose-to-manager']);  // Redirection après la soumission
}

getInternshipStatusLabel(status: InternshipStatus): string {
  switch (status) {
    case InternshipStatus.EN_ATTENTE:
      return 'En attente';
    case InternshipStatus.ACCEPTE:
      return 'Accepté';
    case InternshipStatus.REFUSE:
      return 'Rejeté';
    case InternshipStatus.EN_COURS:
      return 'En cours';
    case InternshipStatus.TERMINER:
      return 'Termine'
    default:
      return 'Statut inconnu';
  }
}
getInternshipStatusClass(status: InternshipStatus): string {
  switch (status) {
    case InternshipStatus.EN_ATTENTE:
      return 'bg-yellow-100 text-yellow-800';
    case InternshipStatus.ACCEPTE:
      return 'bg-green-100 text-green-800';
    case InternshipStatus.REFUSE:
      return 'bg-red-100 text-red-800';
    case InternshipStatus.EN_COURS:
      return 'bg-blue-100 text-blue-800';
    case InternshipStatus.TERMINER:
      return 'bg-gray-100 text-gray-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
}

closeModal() {
  this.close.emit();
}
}

