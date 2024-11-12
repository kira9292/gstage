// attestation-presence.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { AttestationPresence, PaginationInfo } from '../../interfaces/trainee.interface';
import { TraineeService } from '../../services/trainee.service';


@Component({
  selector: 'app-attestation-presence',
  standalone: true,
  templateUrl: './attestations.component.html',
  styleUrls: ['./attestations.component.scss'],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class AttestationsComponent implements OnInit {
  attestations: AttestationPresence[] = [];
  filteredAttestations: AttestationPresence[] = [];
  paginatedAttestations: AttestationPresence[] = [];
  
  showAttestationDialog = false;
  selectedAttestation: AttestationPresence | null = null;
  safeUrl: SafeResourceUrl | null = null;
  statusFilter = '';
  searchTerm = '';
  monthFilter = '';

  availableMonths: string[] = [];


  pagination: PaginationInfo = {
    currentPage: 1,
    pageSize: 5,
    totalItems: 0,
    totalPages: 0
  };

  constructor(
    private sanitizer: DomSanitizer,
    private traineeService: TraineeService
  ) {}

  ngOnInit(): void {
    // Simuler le chargement des données
    this.loadAttestations();
  }

  loadAttestations(): void {
    // À remplacer par un appel API
    this.attestations = [

      {
        id: 1,
        reference: 'ATT-2024-001',
        month: '2024-01',
        startDate: new Date('2024-01-01'),
        endDate: new Date('2024-01-31'),
        signatureDate: new Date('2024-02-01'),
        status: true,
        comments: 'Attestation de janvier 2024',
        url: '/assets/documents/attestation-1.pdf'
      },
      {
        id: 2,
        reference: 'ATT-2024-002',
        month: '2024-02',
        startDate: new Date('2024-02-01'),
        endDate: new Date('2024-02-29'),
        signatureDate: new Date('2024-03-01'),
        status: true,
        comments: 'Attestation de février 2024',
        url: '/assets/documents/attestation-2.pdf'
      },
      {
        id: 3,
        reference: 'ATT-2024-003',
        month: '2024-03',
        startDate: new Date('2024-03-01'),
        endDate: new Date('2024-03-31'),
        signatureDate: new Date('2024-03-31'),
        status: false,
        comments: 'En attente de signature',
        url: '/assets/documents/attestation-3.pdf'
      },
      {
        id: 4,
        reference: 'ATT-2024-004',
        month: '2024-04',
        startDate: new Date('2024-04-01'),
        endDate: new Date('2024-04-30'),
        signatureDate: new Date('2024-05-01'),
        status: true,
        comments: 'Attestation de janvier 2024',
        url: '/assets/documents/attestation-1.pdf'
      },
      {
        id: 5,
        reference: 'ATT-2024-005',
        month: '2024-05',
        startDate: new Date('2024-05-01'),
        endDate: new Date('2024-05-31'),
        signatureDate: new Date('2024-06-01'),
        status: true,
        comments: 'Attestation de février 2024',
        url: '/assets/documents/attestation-2.pdf'
      },
      {
        id: 6,
        reference: 'ATT-2024-006',
        month: '2024-06',
        startDate: new Date('2024-06-01'),
        endDate: new Date('2024-06-30'),
        signatureDate: new Date('2024-07-01'),
        status: false,
        comments: 'En attente de signature',
        url: '/assets/documents/attestation-3.pdf'
      },
      {
        id: 7,
        reference: 'ATT-2024-007',
        month: '2024-07',
        startDate: new Date('2024-07-01'),
        endDate: new Date('2024-07-31'),
        signatureDate: new Date('2024-08-01'),
        status: true,
        comments: 'Attestation de janvier 2024',
        url: '/assets/documents/attestation-1.pdf'
      },
      {
        id: 8,
        reference: 'ATT-2024-008',
        month: '2024-08',
        startDate: new Date('2024-08-01'),
        endDate: new Date('2024-08-31'),
        signatureDate: new Date('2024-09-01'),
        status: true,
        comments: 'Attestation de février 2024',
        url: '/assets/documents/attestation-2.pdf'
      },
      {
        id: 9,
        reference: 'ATT-2024-009',
        month: '2024-09',
        startDate: new Date('2024-09-01'),
        endDate: new Date('2024-09-30'),
        signatureDate: new Date('2024-09-30'),
        status: false,
        comments: 'En attente de signature',
        url: '/assets/documents/attestation-3.pdf'
      },
      {
        id: 10,
        reference: 'ATT-2024-010',
        month: '2024-10',
        startDate: new Date('2024-10-01'),
        endDate: new Date('2024-10-31'),
        signatureDate: new Date('2024-10-31'),
        status: false,
        comments: 'En attente de signature',
        url: '/assets/documents/attestation-3.pdf'
      },
      {
        id: 11,
        reference: 'ATT-2024-011',
        month: '2024-11',
        startDate: new Date('2024-11-01'),
        endDate: new Date('2024-11-30'),
        signatureDate: new Date('2024-11-30'),
        status: false,
        comments: 'En attente de signature',
        url: '/assets/documents/attestation-3.pdf'
      },
    ];
    
      // Extraire les mois uniques disponibles
      this.availableMonths = Array.from(new Set(this.attestations.map(a => a.month))).sort();
          
      this.applyFilters();
  }

  // loadAttestations(): void {
  //   this.traineeService.getPresenceAttestations().subscribe({
  //     next: (data) => {
  //       this.attestations = data;        
  //       this.applyFilters();
  //     },
  //     error: (err) => {
  //       console.error('Erreur lors de la récupération des attestations de presence', err);
  //     }
  //   });
  // }

  formatMonth(monthStr?: string): string {
    if (!monthStr) return '';
    const date = new Date(monthStr + '-01');
    return date.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
  }


  applyFilters(): void {
    this.filteredAttestations = this.attestations.filter(attestation => {
      const matchesStatus = this.statusFilter === '' || 
        attestation.status === (this.statusFilter === 'true');
      const matchesMonth = !this.monthFilter || 
        attestation.month === this.monthFilter;
      const searchLower = this.searchTerm.toLowerCase();
      const matchesSearch = !this.searchTerm || 
        attestation.reference.toLowerCase().includes(searchLower) ||
        this.formatMonth(attestation.month).toLowerCase().includes(searchLower);
      
      return matchesStatus && matchesMonth && matchesSearch;
    });

    this.updatePagination();
  }

  updatePagination(): void {
    this.pagination.totalItems = this.filteredAttestations.length;
    this.pagination.totalPages = Math.ceil(this.pagination.totalItems / this.pagination.pageSize);
    
    if (this.pagination.currentPage > this.pagination.totalPages) {
      this.pagination.currentPage = this.pagination.totalPages;
    }
    if (this.pagination.currentPage < 1) {
      this.pagination.currentPage = 1;
    }

    const startIndex = (this.pagination.currentPage - 1) * this.pagination.pageSize;
    const endIndex = startIndex + this.pagination.pageSize;
    this.paginatedAttestations = this.filteredAttestations.slice(startIndex, endIndex);
  }


  goToPage(page: number): void {
    if (page >= 1 && page <= this.pagination.totalPages) {
      this.pagination.currentPage = page;
      this.updatePagination();
    }
  }

  onPageSizeChange(): void {
    this.pagination.currentPage = 1;
    this.updatePagination();
  }

  getStatusLabel(status: boolean): string {
    return status ? 'Signée' : 'En attente';
  }

  getStatusClass(status: boolean): string {
    const baseClasses = 'px-2 py-1 text-xs font-medium rounded-full';
    return status 
      ? `${baseClasses} bg-green-100 text-green-800`
      : `${baseClasses} bg-yellow-100 text-yellow-800`;
  }

  viewAttestation(attestation: AttestationPresence): void {
    if (!attestation.status) return;
    
    this.selectedAttestation = attestation;
    this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(attestation.url);
    this.showAttestationDialog = true;
  }

  downloadDocument(attestation: AttestationPresence): void {
    if (!attestation.status) return;
    window.open(attestation.url, '_blank');
  }

  closeDialog(): void {
    this.showAttestationDialog = false;
    this.selectedAttestation = null;
    this.safeUrl = null;
  }
}