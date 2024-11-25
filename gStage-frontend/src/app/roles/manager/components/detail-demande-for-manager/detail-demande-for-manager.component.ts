import { Component, EventEmitter, Input, Output } from '@angular/core';
import { InternshipStatus } from '../../../../enums/gstage.enum';
import { GwteService } from '../../../gwte/services/gwte.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { CommonModule } from '@angular/common';
import { ManagerService } from '../../services/manager.service';

@Component({
  selector: 'app-detail-demande-for-manager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './detail-demande-for-manager.component.html',
  styleUrl: './detail-demande-for-manager.component.scss'
})
export class DetailDemandeForManagerComponent {
  @Input() demande: any;
  @Output() close = new EventEmitter<void>();
  @Output() statusUpdated = new EventEmitter<void>();  // Nouvel EventEmitter


  InternshipStatus = InternshipStatus

  constructor(
    private gwteService: GwteService,
    private router: Router,
    private managerService: ManagerService
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

acceptInternship(internship: any): void {
  Swal.fire({
    title: 'Confirmer l\'acceptation',
    text: `Voulez-vous accepter le stage de ${internship.candidat.firstName} ${internship.candidat.lastName} ?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Accepter',
    cancelButtonText: 'Annuler'
  }).then((result) => {
    if (result.isConfirmed) {
      this.managerService.validateInternshipRequest(internship.demandeStage.id)
      .subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Stage accepté !',
            text: `Le stage de ${internship.candidat.firstName} a été accepté`
          });
          this.statusUpdated.emit();
          this.closeModal();
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible d\'accepter le stage'
          });
        }
      });
    }
  });
}

rejectInternship(internship: any): void {
  Swal.fire({
    title: 'Motif de rejet',
    input: 'textarea',
    inputLabel: 'Raison du rejet',
    inputPlaceholder: 'Saisissez une raison...',
    inputAttributes: {
      'aria-label': 'Raison du rejet'
    },
    showCancelButton: true,
    confirmButtonText: 'Rejeter',
    cancelButtonText: 'Annuler',
    // inputValidator: (value) => {
    //   if (!value) {
    //     return 'Vous devez saisir un motif de rejet';
    //   }
    // }
  }).then((result) => {
    if (result.isConfirmed && result.value) {
      this.managerService.rejectInternshipRequest(internship.demandeStage.id)
      .subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Stage rejeté',
            text: `Le stage de ${internship.candidat.firstName} a été rejeté`
          });
          this.statusUpdated.emit();
          this.closeModal();
        },
        error: () => {
          Swal.fire({
            icon: 'error',
            title: 'Erreur',
            text: 'Impossible de rejeter le stage'
          });
        }
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
  Swal.fire({
    title: 'Confirmer l\'archivage',
    text: 'Êtes-vous sûr de vouloir archiver cette demande de stage ? Cette action est irréversible.',
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
        text: 'Veuillez patienter pendant l\'archivage de la demande',
        icon: 'info',
        allowOutsideClick: false,
        showConfirmButton: false,
        didOpen: () => {
          Swal.showLoading();
        }
      });

      this.gwteService.archiveInternshipApplication(this.demande.demandeStage.id)
        .subscribe({
          next: () => {
            // Notification de succès
            Swal.fire({
              icon: 'success',
              title: 'Demande archivée',
              text: 'La demande de stage a été archivée avec succès',
              showConfirmButton: true,
              timer: 3000,
              position: 'top-end',
              toast: true
            });
            
            // Émettre l'événement pour mettre à jour la liste
            this.statusUpdated.emit();
            
            // Fermer le modal
            this.closeModal();
          },
          error: (error) => {
            // Notification d'erreur
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Une erreur est survenue lors de l\'archivage de la demande',
              footer: error.message
            });
          }
        });
    }
  });
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
    case InternshipStatus.TERMINE:
      return 'Termine';
    case InternshipStatus.PROPOSE:
      return 'Proposé';
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
    case InternshipStatus.TERMINE:
      return 'bg-gray-100 text-gray-800';
    case InternshipStatus.PROPOSE:
      return 'bg-purple-100 text-purple-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
}

closeModal() {
  this.close.emit();
}
}
