// manager-dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { InternshipStatus } from '../../../../enums/gstage.enum';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagerService } from '../../services/manager.service';
import { InternshipDetailModalComponent } from '../../../gwte/components/detail-demande/detail-demande.component';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-dashboard-manager',
  standalone: true,
  imports: [CommonModule, FormsModule, InternshipDetailModalComponent],
  templateUrl: './dashboard-manager.component.html',
  styleUrl: './dashboard-manager.component.scss'
})
export class DashboardManagerComponent implements OnInit {
  InternshipStatus = InternshipStatus;
  proposedInternships: any[] = [];
  filteredInternships: any[] = [];
  searchTerm: string = '';
  selectedDetailsModal: any = null;

  statsData = [
    {
      label: 'Total Propositions',
      value: 0,
      icon: 'fa-file-alt',
      borderColor: 'border-blue-500',  
      bgColor: 'bg-blue-100',
      iconColor: 'text-blue-500'
    },
    {
      label: 'Nouvelles Propositions',
      value: 0,
      icon: 'fa-paper-plane',
      borderColor: 'border-yellow-500',
      bgColor: 'bg-yellow-100',
      iconColor: 'text-yellow-500'
    }
  ];

  constructor(
    private managerService: ManagerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProposedInternships();
  }

  loadProposedInternships(): void {
    this.managerService.getManagerInternshipRequests().subscribe(
      (data) => {
        this.proposedInternships = data;
        this.applyFilters();
        this.updateStats();
      },
      (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Erreur',
          text: 'Impossible de charger les propositions de stage'
        });
      }
    );
  }

  applyFilters(): void {
    this.filteredInternships = this.proposedInternships.filter(internship => 
      !this.searchTerm || 
      internship.candidat.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      internship.candidat.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      internship.demandeStage.reference.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  updateStats(): void {
    this.statsData[0].value = this.proposedInternships.length;
    this.statsData[1].value = this.proposedInternships.filter(
      p => p.demandeStage.status === InternshipStatus.PROPOSE
    ).length;
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
            this.loadProposedInternships();
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
            this.loadProposedInternships();
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

  viewInternshipDetails(internship: any): void {
    this.selectedDetailsModal = internship;
  }

  getStatusClass(status: InternshipStatus): string {
    const statusClasses = {
      [InternshipStatus.PROPOSE]: 'bg-yellow-100 text-yellow-800',
      [InternshipStatus.ACCEPTE]: 'bg-green-100 text-green-800',
      [InternshipStatus.REFUSE]: 'bg-red-100 text-red-800'
    };
    return statusClasses[status as keyof typeof statusClasses] || 'bg-gray-100 text-gray-800';
  }

  closeDetailsModal(): void {
    this.selectedDetailsModal = null;
  }
}