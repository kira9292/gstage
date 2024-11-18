import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { EducationLevel, InternshipStatus, InternshipType } from '../../../../enums/gstage.enum';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Formation } from '../../../candidat/stagiaire/enums/trainee.enum';
import { GwteService } from '../../services/gwte.service';
import {AssistantgwteService} from "../../services/assistantgwte.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-dashboard-gwte',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './dashboard-gwte.component.html',
  styleUrls: ['./dashboard-gwte.component.scss']
})
export class DashboardGwteComponent implements OnInit {
  InternshipStatus = InternshipStatus; // Pour l'utiliser dans le template
  demandesStage: any[] = [];

  filteredDemandesStage: any[] = [];
  statusFilter: InternshipStatus | null = null;
  searchTerm: string = '';
  internshipStatuses = Object.values(InternshipStatus);
  
    // Stats data
    statsData = [
      {
        label: 'Total Demandes',
        value: 0,
        icon: 'fa-file-alt',
        borderColor: 'border-blue-500',
        bgColor: 'bg-blue-100',
        iconColor: 'text-blue-500'
      },
      {
        label: 'En Attente',
        value: 0,
        icon: 'fa-clock',
        borderColor: 'border-yellow-500',
        bgColor: 'bg-yellow-100',
        iconColor: 'text-yellow-500'
      },
      {
        label: 'Acceptées',
        value: 0,
        icon: 'fa-check-circle',
        borderColor: 'border-green-500',
        bgColor: 'bg-green-100',
        iconColor: 'text-green-500'
      },
      {
        label: 'En Cours',
        value: 0,
        icon: 'fa-spinner',
        borderColor: 'border-purple-500',
        bgColor: 'bg-purple-100',
        iconColor: 'text-purple-500'
      }
    ];
    
  constructor(
    private sanitizer: DomSanitizer,
    private gwteService: GwteService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadDemandesStage();
  }

  loadDemandesStage(): void {
      this.gwteService.getDemandesStages().subscribe(
        (data) => {
          this.demandesStage = data; // Stocke les demandes récupérées
          this.applyFilters(); // Applique les filtres après avoir chargé les données
          this.updateStats();
        },
        (error) => {
          console.error('Erreur lors de la récupération des demandes de stage', error);
        }
      );
  }

  
  updateStats(): void {
    this.statsData[0].value = this.demandesStage.length;
    this.statsData[1].value = this.demandesStage.filter(d => d.demandeStage.status === InternshipStatus.EN_ATTENTE).length;
    this.statsData[2].value = this.demandesStage.filter(d => d.demandeStage.status === InternshipStatus.ACCEPTE).length;
    this.statsData[3].value = this.demandesStage.filter(d => d.demandeStage.status === InternshipStatus.EN_COURS).length;
  }


  applyFilters(): void {
    let filtered = this.demandesStage;

    if (this.statusFilter) {
      filtered = filtered.filter(demande => demande.demandeStage.status === this.statusFilter);
    }

    if (this.searchTerm) {
      filtered = filtered.filter(demande =>
        demande.reference.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (demande.candidat?.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        demande.candidat?.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()))
      );
    }

    this.filteredDemandesStage = filtered;
  }

    // Nouvelles méthodes pour les actions contextuelles
    sendWelcomeEmail(demande: any): void {
      // Implémentation de l'envoi d'email d'accueil
      console.log('Envoi email d\'accueil pour:', demande.demandeStage.reference);
    }
    scheduleEvaluation(demande: any): void {
      // Implémentation de la planification d'évaluation
      console.log('Planification évaluation pour:', demande.demandeStage.reference);
    }

    generateCertificate(demande: any): void {
      // Implémentation de la génération d'attestation
      console.log('Génération attestation pour:', demande.demandeStage.reference);
    }
  
    archiveApplication(demande: any): void {
      // Implémentation de l'archivage
      console.log('Archivage de la demande:', demande.demandeStage.reference);
    }
  

  canViewCV(demande: any): boolean {
    return !!demande.demandeStage.cv;
  }

  canDownloadCV(demande: any): boolean {
    return !!demande.demandeStage.cv;
  }

  canViewCoverLetter(demande: any): boolean {
    return !!demande.demandeStage.coverLetter;
  }

  canDownloadCoverLetter(demande: any): boolean {
    return !!demande.demandeStage.coverLetter;
  }

// Méthode pour afficher le CV
viewCV(demande: any): void {
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
    window.open(url, '_blank'); // Ouvre dans un nouvel onglet
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
viewCoverLetter(demande: any): void {
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
    window.open(url, '_blank'); // Ouvre dans un nouvel onglet
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

  proposeToManager(demande: any): void {
    this.gwteService.setDemande(demande);
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

}
