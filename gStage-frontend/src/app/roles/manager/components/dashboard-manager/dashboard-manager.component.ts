// manager-dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { InternshipStatus } from '../../../../enums/gstage.enum';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagerService } from '../../services/manager.service';


@Component({
  selector: 'app-dashboard-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-manager.component.html',
  styleUrl: './dashboard-manager.component.scss'
})
export class DashboardManagerComponent implements OnInit {
  currentManagerID: string = '1';


  internshipRequests: any[] = [];
  filteredRequests: any[] = [];
  searchTerm: string = '';
  statusFilter: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';


  constructor(private managerService: ManagerService) {}

  ngOnInit() {
    // Simuler le chargement des données
    this.loadInternshipRequests();
  }

  loadInternshipRequests() {
    this.isLoading = true;
    this.errorMessage = '';

    this.managerService.getManagerInternshipRequests().subscribe({
      next: (requests) => {
        this.internshipRequests = requests;
        console.log("Demandes: ", this.internshipRequests);
        
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erreur de chargement:', error);
        this.errorMessage = 'Impossible de charger les demandes de stage';
        this.isLoading = false;
      }
    });
  }

  applyFilters() {
    this.filteredRequests = this.internshipRequests.filter(request => {
      const matchesSearch = !this.searchTerm || 
        request.candidat.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        request.candidat.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        request.candidat.formation.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesStatus = !this.statusFilter || 
        request.demandeStage.status === this.statusFilter;

      return matchesSearch && matchesStatus;
    });
  }

  validateRequest(request: any, validate: boolean) {
    this.managerService.validateInternshipRequest(request.demandeStage.id, validate).subscribe({
      next: (updatedRequest) => {
        // Mettre à jour la liste des demandes
        const index = this.internshipRequests.findIndex(
          r => r.demandeStage.id === request.demandeStage.id
        );
        if (index !== -1) {
          this.internshipRequests[index] = updatedRequest;
          this.applyFilters();
        }
      },
      error: (error) => {
        console.error('Erreur de validation:', error);
        this.errorMessage = validate 
          ? 'Impossible d\'accepter la demande' 
          : 'Impossible de refuser la demande';
      }
    });
  }

  viewDocument(documentBase64: string, contentType: string): void {
      const byteCharacters = atob(documentBase64);
      const byteArrays = [];
      for (let offset = 0; offset < byteCharacters.length; offset += 1024) {
        const slice = byteCharacters.slice(offset, offset + 1024);
        const byteNumbers = new Array(slice.length);
        for (let i = 0; i < slice.length; i++) {
          byteNumbers[i] = slice.charCodeAt(i);
        }
        byteArrays.push(new Uint8Array(byteNumbers));
      }
      const blob = new Blob(byteArrays, { type: contentType });
      const url = URL.createObjectURL(blob);
      window.open(url, '_blank');
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PROPOSE':
        return 'bg-yellow-100 text-yellow-800';
      case 'ACCEPTE':
        return 'bg-green-100 text-green-800';
      case 'REFUSE':
        return 'bg-red-100 text-red-800';
      case 'EN_COURS':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'PROPOSE': 'Proposé',
      'ACCEPTE': 'Accepté',
      'REFUSE': 'Refusé',
      'EN_COURS': 'En cours'
    };
    return labels[status] || status;
  }

  getPendingRequestsCount(): number {
    return this.internshipRequests.filter(r => r.status === 'PROPOSE').length;
  }

  getAcceptedRequestsCount(): number {
    return this.internshipRequests.filter(r => r.status === 'ACCEPTE').length;
  }

  getOngoingRequestsCount(): number {
    return this.internshipRequests.filter(r => r.status === 'EN_COURS').length;
  }

  async acceptRequest(request: any) {
    try {
      // Appel API pour accepter la demande
      request.status = InternshipStatus.ACCEPTE;
      // Rafraîchir les données
      this.applyFilters();
    } catch (error) {
      console.error('Error accepting request:', error);
    }
  }

  async rejectRequest(request: any) {
    try {
      // Appel API pour refuser la demande
      request.status = InternshipStatus.REFUSE;
      // Rafraîchir les données
      this.applyFilters();
    } catch (error) {
      console.error('Error rejecting request:', error);
    }
  }

}