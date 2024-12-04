import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
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
export class InternshipDetailModalComponent implements OnInit {
  @Input() demande: any;
  manager: any;
  @Output() close = new EventEmitter<void>();
  @Output() statusUpdated = new EventEmitter<void>();  // Nouvel EventEmitter

  showAttestationModal: boolean = false;
  isActionDropdownOpen: boolean = false;


  InternshipStatus = InternshipStatus

  constructor(
    private gwteService: GwteService,
    private router: Router
  ){
  }
  ngOnInit(): void {
    if(this.demande.demandeStage.status == InternshipStatus.PROPOSE || this.demande.demandeStage.status == InternshipStatus.ACCEPTE )
      this.loadManagerDetails(this.demande.demandeStage.appUser.id);
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

      this.closeModal();
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

cancelProposal(){
  Swal.fire({
    title: 'Confirmer l\'annulation',
    text: 'Êtes-vous sûr de vouloir retirer cette proposition pour ce manager ? Cette action est irréversible et la demande sera remise en attente.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Oui, retirer',
    cancelButtonText: 'Non, annuler'
  }).then((result) => {
    if (result.isConfirmed) {
      // Afficher le loader
      Swal.fire({
        title: 'Retrait en cours...',
        text: 'Veuillez patienter pendant le traitement de l\'annulation de la proposition.',
        icon: 'info',
        allowOutsideClick: false,
        showConfirmButton: false,
        didOpen: () => {
          Swal.showLoading();
        }
      });

      this.gwteService.cancelProposal(this.demande.demandeStage.id)
        .subscribe({
          next: () => {
            // Notification de succès
            Swal.fire({
              icon: 'success',
              title: 'Proposition retirée',
              text: 'La proposition a été annulée avec succès et la demande est de nouveau disponible.',
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
              text: 'Une erreur est survenue lors de l\'annulation de la proposition.',
              footer: error.message
            });
          }
        });
    }
  });
}

rejectApplication(): void {
  Swal.fire({
    title: 'Confirmer le rejet',
    text: 'Êtes-vous sûr de vouloir rejeter cette demande de stage ?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#3085d6',
    confirmButtonText: 'Oui, rejeter',
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

      this.gwteService.rejectInternshipApplication(this.demande.demandeStage.id)
        .subscribe({
          next: () => {
            Swal.fire({
              icon: 'success',
              title: 'Demande rejetée',
              text: 'La demande de stage a été rejetée avec succès',
              showConfirmButton: true,
              timer: 3000,
              position: 'top-end',
              toast: true
            });
            this.statusUpdated.emit();  // Émettre l'événement de mise à jour
            this.closeModal();

          },
          error: (error) => {
            Swal.fire({
              icon: 'error',
              title: 'Erreur',
              text: 'Une erreur est survenue lors du rejet de la demande',
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

private loadManagerDetails(managerId: string) {
  this.gwteService.getManagerById(managerId).subscribe({
    next: (user) => {
      this.manager = user;
    },
    error: (error) => {
      console.error('Erreur lors du chargement des informations du manager:', error);
    }
  });
}


closeModal() {
  this.close.emit();
}

  openAttestationModal(): void {
    this.showAttestationModal = true;
  }

  // Ferme le modal
  closeAttestationModal(): void {
    this.showAttestationModal = false;
  }




  viewAttestation(attestation: any) {
    if (attestation.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(attestation.docs);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);
      // Créer un Blob pour un fichier Word
      const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
      const fileName = this.generateAttestationFileName(attestation);

      // Créer un lien de téléchargement
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;

      // Déclencher le téléchargement
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // Libérer la mémoire
      URL.revokeObjectURL(link.href);
    } else {
      console.error('Document Base64 non trouvé dans l’attestation.');
    }
  }


  downloadDocument(attestation: any): void {
    if (attestation.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(attestation.docs);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);

      // Créer un Blob pour un fichier Word
      const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

      // Générer un nom de fichier personnalisé
      const fileName = this.generateAttestationFileName(attestation);

      // Créer un lien de téléchargement
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;

      // Déclencher le téléchargement
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // Libérer la mémoire
      URL.revokeObjectURL(link.href);
    } else {
      console.error('Document Base64 non trouvé dans l\'attestation.');
    }
  }

// Méthode pour générer un nom de fichier personnalisé pour l'attestation
  generateAttestationFileName(attestation: any): string {
    // Vous pouvez personnaliser cette méthode selon vos besoins
    const prefix = 'Attestation_de_presence';
    const date = new Date().toISOString().split('T')[0]; // Date au format AAAA-MM-JJ
    const identifier = attestation.id || 'sans_id'; // Utiliser un identifiant unique si disponible

    return `${prefix}_${identifier}_${date}.docx`;
  }

  downloadcontrat(contrat: any): void {
    if (contrat.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(contrat.docs);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);

      // Créer un Blob pour un fichier Word
      const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

      // Générer un nom de fichier personnalisé
      const fileName = this.generateFileName(contrat);

      // Créer un lien de téléchargement
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;

      // Déclencher le téléchargement
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // Libérer la mémoire
      URL.revokeObjectURL(link.href);
    } else {
      console.error('Document Base64 non trouvé dans l\'attestation.');
    }
  }

// Méthode pour générer un nom de fichier personnalisé
  generateFileName(contrat: any): string {
    // Vous pouvez personnaliser cette méthode selon vos besoins
    const prefix = 'Contrat';
    const date = new Date().toISOString().split('T')[0]; // Date au format AAAA-MM-JJ
    const identifier = contrat.id || 'sans_id'; // Utiliser un identifiant unique si disponible

    return `${prefix}_${identifier}_${date}.docx`;
  }

  downloadAttestationFindeStage(attestationF: any): void {
    if (attestationF.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(attestationF.docs);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);

      // Créer un Blob pour un fichier Word
      const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

      // Générer un nom de fichier personnalisé
      const fileName = this.generateAttestationFindeStageFileName(attestationF);

      // Créer un lien de téléchargement
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;

      // Déclencher le téléchargement
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // Libérer la mémoire
      URL.revokeObjectURL(link.href);
    } else {
      console.error('Document Base64 non trouvé dans l\'attestation de fin de stage.');
    }
  }

// Méthode pour générer un nom de fichier personnalisé pour l'attestation de fin de stage
  generateAttestationFindeStageFileName(attestationF: any): string {
    // Vous pouvez personnaliser cette méthode selon vos besoins
    const prefix = 'Attestation_de_fin_de_stage';
    const date = new Date().toISOString().split('T')[0]; // Date au format AAAA-MM-JJ
    const identifier = attestationF.id || 'sans_id'; // Utiliser un identifiant unique si disponible
    const entreprise = attestationF.entreprise || 'entreprise'; // Nom de l'entreprise si disponible

    return `${prefix}_${entreprise}_${identifier}_${date}.docx`;
  }


  toggleActionDropdown() {
    this.isActionDropdownOpen = !this.isActionDropdownOpen;
  }

  closeActionDropdown() {
    this.isActionDropdownOpen = false;
  }

}

