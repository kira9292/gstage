import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { DemandeStage } from '../../../candidat/stagiaire/interfaces/trainee.interface'; 
import { EducationLevel, InternshipStatus, InternshipType } from '../../../../enums/gstage.enum';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Formation } from '../../../candidat/stagiaire/enums/trainee.enum';

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
  demandesStage: DemandeStage[] = [
    {
      id: 1,
      reference: 'REF1234',
      internshipType: InternshipType.ACADEMIQUE,
      internshipStatus: InternshipStatus.EN_ATTENTE,
      startDate: new Date('2024-12-01'),
      endDate: new Date('2025-06-30'),
      cv: 'https://www.example.com/cv1234.pdf',
      coverLetter: 'https://www.example.com/coverletter1234.pdf',
      validated: false,
      candidat: {
        id: 1,
        firstName: 'Lamine',
        lastName: 'Ndiaye',
        email: 'lndiaye@gmail.com',
        formation: Formation.INFORMATIQUE_SI,
        school: 'Université de Dakar',
        cni: '1234567890',
        address: 'Dakar, Sénégal',
        phone: '774807241',
        educationalLevel: EducationLevel.BAC_PLUS_3,
      }
    },
    {
      id: 2,
      reference: 'REF5678',
      internshipType: InternshipType.PROFESSIONNEL,
      internshipStatus: InternshipStatus.ACCEPTE,
      startDate: new Date('2024-10-01'),
      endDate: new Date('2025-04-01'),
      cv: 'https://www.example.com/cv5678.pdf',
      coverLetter: 'https://www.example.com/coverletter5678.pdf',
      validated: true,
      candidat: {
        id: 2,
        firstName: 'Alice',
        lastName: 'Martin',
        email: 'alice.martin@example.com',
        formation: Formation.MARKETING_COMMUNICATION,
        school: 'Université de Paris',
        cni: '0987654321',
        address: 'Paris, France',
        phone: '07654321',
        educationalLevel: EducationLevel.BAC_PLUS_4,
      }
    },
  ];

  filteredDemandesStage: DemandeStage[] = [];
  statusFilter: InternshipStatus | null = null;
  searchTerm: string = '';
  internshipStatuses = Object.values(InternshipStatus);
  internshipTypes = Object.values(InternshipType);

  constructor(
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = this.demandesStage;

    if (this.statusFilter) {
      filtered = filtered.filter(demande => demande.internshipStatus === this.statusFilter);
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

  canViewCV(demande: DemandeStage): boolean {
    return !!demande.cv;
  }

  canDownloadCV(demande: DemandeStage): boolean {
    return !!demande.cv;
  }

  canViewCoverLetter(demande: DemandeStage): boolean {
    return !!demande.coverLetter;
  }

  canDownloadCoverLetter(demande: DemandeStage): boolean {
    return !!demande.coverLetter;
  }

  viewCV(demande: DemandeStage): void {
    window.open(demande.cv, '_blank');
  }

  downloadCV(demande: DemandeStage): void {
    window.open(demande.cv, '_blank');
  }

  viewCoverLetter(demande: DemandeStage): void {
    window.open(demande.coverLetter, '_blank');
  }

  downloadCoverLetter(demande: DemandeStage): void {
    window.open(demande.coverLetter, '_blank');
  }

  proposeToManager(demande: DemandeStage): void {
    alert(`Demande de stage ${demande.reference} proposée au manager.`);
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
        return 'bg-yellow-500 text-white'; // Jaune pour "En attente"
      case InternshipStatus.ACCEPTE:
        return 'bg-green-500 text-white';   // Vert pour "Accepté"
      case InternshipStatus.REFUSE:
        return 'bg-red-500 text-white';     // Rouge pour "Rejeté"
      case InternshipStatus.EN_COURS:
        return 'bg-blue-500 text-white';    // Bleu pour "En cours"
      case InternshipStatus.TERMINER:
        return 'bg-gray-500 text-white';    // Gris pour "Terminé"
      default:
        return 'bg-gray-200 text-black';    // Gris par défaut pour un statut inconnu
    }
  }
  
}
