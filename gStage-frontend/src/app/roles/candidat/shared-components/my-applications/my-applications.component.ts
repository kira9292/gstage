import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { AuthService } from '../../../../core/services/auth.service';

enum InternshipType {
  ACADEMIQUE = 'ACADEMIQUE',
  PROFESSIONNEL = 'PROFESSIONNEL',
  PROJET_FIN_ETUDES = 'PROJET_FIN_ETUDES'
}

enum InternshipStatus {
  EN_ATTENTE = 'EN_ATTENTE',
  ACCEPTE = 'ACCEPTE',
  REFUSE = 'REFUSE',
  EN_COURS = 'EN_COURS',
  TERMINE = 'TERMINE'
}

enum EducationLevel {
  BAC = 'BAC',
  BAC1 = 'BAC1',
  BAC2 = 'BAC2',
  BAC3 = 'BAC3',
  BAC4 = 'BAC4',
  BAC5 = 'BAC5'
}

interface Candidat {
  id: number;
  firstName: string;
  lastName: string;
  birthDate: Date;
  nationality: string;
  birthPlace: string;
  idNumber: string;
  address: string;
  email: string;
  phone: string;
  educationLevel: EducationLevel;
  school: string;
  registrationNumber?: string;
  currentEducation: string;
}

interface DemandeStage {
  id: number;
  reference: string;
  creationDate: Date;
  status: InternshipStatus;
  description: string;
  internshipType: InternshipType;
  startDate: Date;
  endDate: Date;
  resume: string;
  coverLetter: string;
  validated: boolean;
  feedback?: string;
  interviewDate?: Date;
  processSteps: {
    step: string;
    status: 'pending' | 'completed' | 'rejected';
    date?: Date;
    comments?: string;
  }[];
}

@Component({
  selector: 'app-suivi-demande-stage',
  standalone: true,
  templateUrl: './my-applications.component.html',
  styleUrls: [
    './my-applications.component.scss'
  ],
  imports: [
    CommonModule,
    FormsModule
  ]
})

export class SuiviDemandeStageComponent implements OnInit {
  // Enums
  InternshipStatus = InternshipStatus;
  InternshipType = InternshipType;
  Math = Math;

  // Data
  userInfo: { firstName: string; name: string } | null = null;

  candidat: Candidat | null = null;
  demandes: DemandeStage[] = [];
  filteredDemandes: DemandeStage[] = [];
  paginatedDemandes: DemandeStage[] = [];
  internshipStatuses = Object.values(InternshipStatus);

  // Filters
  statusFilter: string = '';
  searchTerm: string = '';

  // Pagination
  pagination = {
    currentPage: 1,
    pageSize: 5,
    totalPages: 1
  };

  // Dialog
  showDemandeDialog = false;
  selectedDemande: DemandeStage | null = null;

  constructor(
    private sanitizer: DomSanitizer,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Simuler le chargement des données
    this.loadMockData();
    this.applyFilters();
     // S'abonner au BehaviorSubject pour récupérer les infos utilisateur
     this.authService.userInfo$.subscribe(userInfo => {
      this.userInfo = userInfo;
    });
  }

  loadMockData() {
    // Mock du candidat
    this.candidat = {
      id: 1,
      firstName: 'Mouhamadou Lamine',
      lastName: 'Ndiaye',
      birthDate: new Date('2002-09-18'),
      nationality: 'Sénégalaise',
      birthPlace: 'Dakar',
      idNumber: 'SN123456',
      address: '123 Rue de la Paix, Dakar',
      email: 'nidayeml@ept.sn',
      phone: '+221 77 123 45 67',
      educationLevel: EducationLevel.BAC3,
      school: 'Ecole Polytechnique de Thies',
      registrationNumber: '20230001',
      currentEducation: 'DIC1 Informatique'
    };

    // Mock des demandes
    this.demandes = [
      {
        id: 1,
        reference: 'STAGE-2024-001',
        creationDate: new Date('2024-01-15'),
        status: InternshipStatus.EN_ATTENTE,
        description: 'Stage en développement web fullstack',
        internshipType: InternshipType.ACADEMIQUE,
        startDate: new Date('2024-06-01'),
        endDate: new Date('2024-08-31'),
        resume: 'cv_john_doe.pdf',
        coverLetter: 'lm_john_doe.pdf',
        validated: false,
        processSteps: [
          { step: 'Soumission', status: 'completed', date: new Date('2024-01-15') },
          { step: 'Validation documents', status: 'pending' },
          { step: 'Entretien', status: 'pending' },
          { step: 'Décision finale', status: 'pending' }
        ]
      },
      // Ajoutez d'autres demandes mock si nécessaire
    ];
  }

  // Méthodes de formatage
  formatEducationLevel(level: EducationLevel | undefined): string {
    if (!level) return '';
    const formats: Record<EducationLevel, string> = {
      [EducationLevel.BAC]: 'Baccalauréat',
      [EducationLevel.BAC1]: 'Bac +1',
      [EducationLevel.BAC2]: 'Bac +2',
      [EducationLevel.BAC3]: 'Bac +3',
      [EducationLevel.BAC4]: 'Bac +4',
      [EducationLevel.BAC5]: 'Bac +5'
    };
    return formats[level];
  }

  formatStatus(status: InternshipStatus | undefined): string {
    if (!status) return '';
    const formats: Record<InternshipStatus, string> = {
      [InternshipStatus.EN_ATTENTE]: 'En attente',
      [InternshipStatus.ACCEPTE]: 'Accepté',
      [InternshipStatus.REFUSE]: 'Refusé',
      [InternshipStatus.EN_COURS]: 'En cours',
      [InternshipStatus.TERMINE]: 'Terminé'
    };
    return formats[status];
  }

  formatInternshipType(type: InternshipType | undefined): string {
    if (!type) return '';
    const formats: Record<InternshipType, string> = {
      [InternshipType.ACADEMIQUE]: 'Stage académique',
      [InternshipType.PROFESSIONNEL]: 'Stage professionnel',
      [InternshipType.PROJET_FIN_ETUDES]: 'Projet de fin d\'études'
    };
    return formats[type];
  }

  // Classes dynamiques
  getStatusClass(status: InternshipStatus | undefined): string {
    if (!status) return '';
    const classes: Record<InternshipStatus, string> = {
      [InternshipStatus.EN_ATTENTE]: 'text-yellow-600 bg-yellow-100 px-2 py-1 rounded-full text-sm',
      [InternshipStatus.ACCEPTE]: 'text-green-600 bg-green-100 px-2 py-1 rounded-full text-sm',
      [InternshipStatus.REFUSE]: 'text-red-600 bg-red-100 px-2 py-1 rounded-full text-sm',
      [InternshipStatus.EN_COURS]: 'text-blue-600 bg-blue-100 px-2 py-1 rounded-full text-sm',
      [InternshipStatus.TERMINE]: 'text-gray-600 bg-gray-100 px-2 py-1 rounded-full text-sm'
    };
    return classes[status];
  }

  getStepStatusClass(status: 'pending' | 'completed' | 'rejected'): string {
    const baseClasses = 'w-8 h-8 rounded-full flex items-center justify-center';
    const statusClasses = {
      pending: `${baseClasses} bg-gray-200 text-gray-500`,
      completed: `${baseClasses} bg-green-500 text-white`,
      rejected: `${baseClasses} bg-red-500 text-white`
    };
    return statusClasses[status];
  }

  getStepTextClass(status: 'pending' | 'completed' | 'rejected'): string {
    const statusClasses = {
      pending: 'text-gray-500',
      completed: 'text-green-600',
      rejected: 'text-red-600'
    };
    return statusClasses[status];
  }

  getStepIcon(status: 'pending' | 'completed' | 'rejected'): string {
    const icons = {
      pending: 'fa-clock',
      completed: 'fa-check',
      rejected: 'fa-times'
    };
    return icons[status];
  }

  // Gestion des filtres et pagination
  applyFilters() {
    this.filteredDemandes = this.demandes.filter(demande => {
      const matchStatus = !this.statusFilter || demande.status === this.statusFilter;
      const matchSearch = !this.searchTerm ||
        demande.reference.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        demande.description.toLowerCase().includes(this.searchTerm.toLowerCase());
      return matchStatus && matchSearch;
    });

    this.pagination.totalPages = Math.ceil(this.filteredDemandes.length / this.pagination.pageSize);
    this.pagination.currentPage = 1;
    this.updatePaginatedDemandes();
  }

  updatePaginatedDemandes() {
    const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
    const end = start + this.pagination.pageSize;
    this.paginatedDemandes = this.filteredDemandes.slice(start, end);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.pagination.totalPages) {
      this.pagination.currentPage = page;
      this.updatePaginatedDemandes();
    }
  }

  // Actions
  viewDemande(demande: DemandeStage) {
    this.selectedDemande = demande;
    this.showDemandeDialog = true;
  }

  closeDialog() {
    this.showDemandeDialog = false;
    this.selectedDemande = null;
  }


  viewFeedback(demande: DemandeStage) {

    }

  downloadResume(demande: DemandeStage) {
    window.open('assets/documents/CV_Mouhamadou_L_NDIAYE.pdf', '_blank');
  }

  downloadCoverLetter(demande: DemandeStage) {
    window.open('assets/documents/eCommerce.pdf', '_blank');
  }

  getNextStep(demande: DemandeStage): { step: string; message: string } | null {
    const pendingStep = demande.processSteps.find(step => step.status === 'pending');
    if (!pendingStep) return null;

    const messages: Record<string, string> = {
      'Soumission': 'Votre demande est en cours de soumission',
      'Validation documents': 'Vos documents sont en cours de validation',
      'Entretien': 'Vous serez bientôt contacté pour un entretien',
      'Décision finale': 'Votre dossier est en cours d\'évaluation finale'
    };

    return {
      step: pendingStep.step,
      message: messages[pendingStep.step] || 'Étape en cours de traitement'
    };
  }

  get totalDemandes(): number {
      return this.demandes?.length ?? 0;
  }

  get pendingDemandesCount(): number {
      return this.demandes?.filter(d => d.status === InternshipStatus.EN_ATTENTE).length ?? 0;
  }

  get acceptedDemandesCount(): number {
      return this.demandes?.filter(d => d.status === InternshipStatus.ACCEPTE).length ?? 0;
  }

  get inProgressDemandesCount(): number {
      return this.demandes?.filter(d => d.status === InternshipStatus.EN_COURS).length ?? 0;
  }

  getProgressWidth(index: number, demande: DemandeStage): string {
    const totalSteps = demande.processSteps.length;

    if (index >= totalSteps - 1) {
      return '0%'; // Pas de progression après la dernière étape
    }
    // getProgressWidth(index: number): string {
    //   return ((index + 1) / this.demande.processSteps.length) * 100 + '%';
    // }

    const currentStep = demande.processSteps[index];
    const nextStep = demande.processSteps[index + 1];

    if (currentStep.status === 'completed') {
      return '100%'; // Étape complètement remplie si terminée
    } else if (currentStep.status === 'pending' && nextStep.status === 'pending') {
      return '50%'; // Étape à moitié remplie si en attente
    } else {
      return '0%'; // Aucun remplissage sinon
    }
  }


}
