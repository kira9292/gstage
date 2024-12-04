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
    this.loadAttestations();
  }


  loadAttestations(): void {
    this.traineeService.getPresenceAttestations().subscribe({
      next: (data) => {
        this.attestations = data;
        console.log(this.attestations);
        this.applyFilters();
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des attestations de presence', err);
      }
    });
  }

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

  viewAttestation(attestation: any): void {
    if (attestation.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(attestation.docs);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);
      // Créer un Blob pour un fichier Word
      const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

      // Créer une URL pour le Blob
      const blobUrl = URL.createObjectURL(blob);

      // Ouvrir dans une nouvelle fenêtre
      window.open(blobUrl, '_blank');
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
      const fileName = this.generateDocumentFileName(attestation);

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

// Méthode pour générer un nom de fichier personnalisé pour le document
  generateDocumentFileName(attestation: any): string {
    // Vous pouvez personnaliser cette méthode selon vos besoins
    const prefix = 'Attestation';
    const date = new Date().toISOString().split('T')[0]; // Date au format AAAA-MM-JJ
    const identifier = attestation.id || 'sans_id'; // Utiliser un identifiant unique si disponible
    const type = attestation.type || 'presence'; // Type d'attestation si disponible

    return `${prefix}_${type}_${identifier}_${date}.docx`;
  }
  closeDialog(): void {
    this.showAttestationDialog = false;
    this.selectedAttestation = null;
    this.safeUrl = null;
  }


  getMonthFromDate(date: Date): string {
    const dateObj = new Date(date);
    return dateObj.toLocaleString('fr-FR', { month: 'long' });
  }
}
