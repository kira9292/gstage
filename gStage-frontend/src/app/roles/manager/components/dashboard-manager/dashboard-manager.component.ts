// manager-dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { InternshipStatus } from '../../../../enums/gstage.enum';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Manager {
  id: string;
  firstName: string;
  lastName: string;
  department: string;
}

interface InternshipRequest {
  id: string;
  reference: string;
  candidate: {
    firstName: string;
    lastName: string;
    formation: string;
    school: string;
    cv?: string;
    coverLetter?: string;
  };
  department: string;
  status: InternshipStatus;
  proposedDate: Date;
  startDate?: Date;
  endDate?: Date;
  comments?: string;
}

@Component({
  selector: 'app-dashboard-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="min-h-screen bg-gray-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Header Section -->
        <div class="mb-8">
          <div class="flex items-center justify-between">
            <div>
              <h1 class="text-3xl font-bold text-gray-900">Dashboard Manager - {{ currentManager.department }}</h1>
              <p class="mt-2 text-sm text-gray-600">
                {{ filteredRequests.length }} demande(s) de stage à traiter
              </p>
            </div>

          </div>
        </div>

        <!-- Stats Cards -->
        <div class="grid grid-cols-1 gap-5 sm:grid-cols-3 mb-8">
          <div class="p-6 bg-white rounded-lg shadow-sm border-l-4 border-yellow-500">
            <div class="flex items-center">
              <div class="flex-1">
                <p class="text-sm font-medium text-gray-600">En attente de décision</p>
                <p class="mt-2 text-3xl font-semibold text-gray-900">
                  {{ getPendingRequestsCount() }}
                </p>
              </div>
              <div class="p-3 rounded-full bg-yellow-100">
                <i class="fas fa-clock text-xl text-yellow-600"></i>
              </div>
            </div>
          </div>

          <div class="p-6 bg-white rounded-lg shadow-sm border-l-4 border-green-500">
            <div class="flex items-center">
              <div class="flex-1">
                <p class="text-sm font-medium text-gray-600">Stages validés</p>
                <p class="mt-2 text-3xl font-semibold text-gray-900">
                  {{ getAcceptedRequestsCount() }}
                </p>
              </div>
              <div class="p-3 rounded-full bg-green-100">
                <i class="fas fa-check text-xl text-green-600"></i>
              </div>
            </div>
          </div>

          <div class="p-6 bg-white rounded-lg shadow-sm border-l-4 border-blue-500">
            <div class="flex items-center">
              <div class="flex-1">
                <p class="text-sm font-medium text-gray-600">Stages en cours</p>
                <p class="mt-2 text-3xl font-semibold text-gray-900">
                  {{ getOngoingRequestsCount() }}
                </p>
              </div>
              <div class="p-3 rounded-full bg-blue-100">
                <i class="fas fa-user-graduate text-xl text-blue-600"></i>
              </div>
            </div>
          </div>
        </div>

        <!-- Filters -->
        <div class="bg-white rounded-lg shadow-sm p-6 mb-8">
          <div class="flex flex-col md:flex-row gap-4 items-center justify-between">
            <div class="w-full md:w-1/3">
              <div class="relative">
                <span class="absolute inset-y-0 left-0 pl-3 flex items-center">
                  <i class="fas fa-search text-gray-400"></i>
                </span>
                <input 
                  type="text"
                  [(ngModel)]="searchTerm"
                  (input)="applyFilters()"
                  placeholder="Rechercher par nom, référence..."
                  class="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
              </div>
            </div>
            
            <div class="w-full md:w-1/4">
              <select 
                [(ngModel)]="statusFilter"
                (change)="applyFilters()"
                class="w-full border border-gray-200 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                <option value="">Tous les statuts</option>
                <option value="PROPOSE">Proposé</option>
                <option value="ACCEPTE">Accepté</option>
                <option value="REFUSE">Refusé</option>
                <option value="EN_COURS">En cours</option>
              </select>
            </div>
          </div>
        </div>

        <!-- Internship Requests List -->
        <div class="space-y-6">
          <div 
            *ngFor="let request of filteredRequests" 
            class="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200">
            <div class="p-6">
              <div class="flex justify-between items-start">
                <div>
                  <div class="flex items-center space-x-2">
                    <h3 class="text-lg font-semibold text-gray-900">
                      {{ request.candidate.firstName }} {{ request.candidate.lastName }}
                    </h3>
                    <span 
                      [ngClass]="getStatusClass(request.status)"
                      class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium">
                      {{ getStatusLabel(request.status) }}
                    </span>
                  </div>
                  <p class="text-sm text-gray-600 mt-1">Réf: {{ request.reference }}</p>
                  <p class="text-sm text-gray-600 mt-1">Proposé le: {{ request.proposedDate | date:'dd/MM/yyyy' }}</p>
                </div>

                <!-- Action Buttons for PROPOSE status -->
                <div *ngIf="request.status === 'PROPOSE'" class="flex space-x-3">
                  <button 
                    (click)="acceptRequest(request)"
                    class="inline-flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors">
                    <i class="fas fa-check mr-2"></i>
                    Accepter
                  </button>
                  <button 
                    (click)="rejectRequest(request)"
                    class="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors">
                    <i class="fas fa-times mr-2"></i>
                    Refuser
                  </button>
                </div>
              </div>

              <!-- Candidate Details -->
              <div class="mt-4 grid grid-cols-2 gap-4">
                <div class="flex items-center">
                  <i class="fas fa-graduation-cap text-gray-400 w-5"></i>
                  <span class="ml-2 text-sm text-gray-600">{{ request.candidate.formation }}</span>
                </div>
                <div class="flex items-center">
                  <i class="fas fa-school text-gray-400 w-5"></i>
                  <span class="ml-2 text-sm text-gray-600">{{ request.candidate.school }}</span>
                </div>
              </div>

              <!-- Documents -->
              <div class="mt-4 flex space-x-4">
                <button 
                  *ngIf="request.candidate.cv"
                  (click)="viewDocument(request.candidate.cv)"
                  class="flex items-center text-sm text-blue-600 hover:text-blue-800">
                  <i class="fas fa-file-pdf mr-2"></i>
                  Voir CV
                </button>
                <button 
                  *ngIf="request.candidate.coverLetter"
                  (click)="viewDocument(request.candidate.coverLetter)"
                  class="flex items-center text-sm text-blue-600 hover:text-blue-800">
                  <i class="fas fa-envelope mr-2"></i>
                  Voir Lettre de motivation
                </button>
              </div>

              <!-- Comments section for PROPOSE status -->
              <div *ngIf="request.status === 'PROPOSE'" class="mt-4">
                <textarea
                  [(ngModel)]="request.comments"
                  placeholder="Ajouter un commentaire sur la décision..."
                  class="w-full p-3 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  rows="2"
                ></textarea>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div 
            *ngIf="filteredRequests.length === 0" 
            class="text-center py-12 bg-white rounded-lg shadow-sm">
            <div class="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
              <i class="fas fa-folder-open text-gray-400 text-xl"></i>
            </div>
            <h3 class="text-lg font-medium text-gray-900">Aucune demande trouvée</h3>
            <p class="mt-2 text-sm text-gray-500">Aucune demande ne correspond à vos critères de recherche.</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class DashboardManagerComponent implements OnInit {
  currentManager: Manager = {
    id: '1',
    firstName: 'Seydina',
    lastName: 'Dia',
    department: 'Informatique'
  };

  internshipRequests: InternshipRequest[] = [];
  filteredRequests: InternshipRequest[] = [];
  searchTerm: string = '';
  statusFilter: string = '';

  constructor() {}

  ngOnInit() {
    // Simuler le chargement des données
    this.loadInternshipRequests();
    this.applyFilters();
  }

  loadInternshipRequests() {
    // À remplacer par un vrai appel API
    this.internshipRequests = [
      // Exemple de données
    ];
    this.filteredRequests = [...this.internshipRequests];
  }

  applyFilters() {
    this.filteredRequests = this.internshipRequests.filter(request => {
      const matchesSearch = !this.searchTerm || 
        request.candidate.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        request.candidate.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        request.reference.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesStatus = !this.statusFilter || request.status === this.statusFilter;

      return matchesSearch && matchesStatus;
    });
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

  async acceptRequest(request: InternshipRequest) {
    try {
      // Appel API pour accepter la demande
      request.status = InternshipStatus.ACCEPTE;
      // Rafraîchir les données
      this.applyFilters();
    } catch (error) {
      console.error('Error accepting request:', error);
    }
  }

  async rejectRequest(request: InternshipRequest) {
    try {
      // Appel API pour refuser la demande
      request.status = InternshipStatus.REFUSE;
      // Rafraîchir les données
      this.applyFilters();
    } catch (error) {
      console.error('Error rejecting request:', error);
    }
  }

  viewDocument(documentUrl: string) {
    // Implémenter la logique pour visualiser les documents
    window.open(documentUrl, '_blank');
  }
}