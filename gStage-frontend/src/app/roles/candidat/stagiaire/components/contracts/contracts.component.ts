// contracts.component.ts
import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Contract } from '../../interfaces/trainee.interface';
import { ContractStatus } from '../../enums/trainee.enum';
import { TraineeService } from '../../services/trainee.service';




// interface Contract {
//   id: number;
//   type: string;
//   name: string;
//   startDate: Date;
//   endDate: Date;
//   url: string;
// }

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './contracts.component.html',
  styleUrls: ['./contracts.component.scss']
})
export class ContractsComponent implements OnInit {
  contracts: Contract[] = [];
  filteredContracts: Contract[] = [];
  showContractDialog = false;
  selectedContract: Contract | null = null;
  safeUrl: SafeResourceUrl | null = null;
  
  statusFilter = '';
  searchTerm = '';
  
  contractStatuses = Object.values(ContractStatus);

  constructor(
    private sanitizer: DomSanitizer,
    private traineeService: TraineeService
  ) {}

  ngOnInit(): void {
    // Simuler le chargement des données
    this.loadContracts();
  }

  loadContracts(): void {
    // À remplacer par un appel API
    this.contracts = [
      {
        id: 1,
        reference: 'STAGE-2024-001',
        type: 'Stage',
        name: 'Contrat Initial',
        startDate: new Date('2024-01-01'),
        endDate: new Date('2024-03-31'),
        compensation: 150000,
        status: ContractStatus.SIGNE,
        assignmentSite: 'Sablux',
        signatureDate: new Date('2023-12-15'),
        url: '/assets/documents/eCommerce.pdf'
      },
      {
        id: 2,
        reference: 'STAGE-2024-002',
        type: 'Stage',
        name: 'Renouvellement',
        startDate: new Date('2024-04-01'),
        endDate: new Date('2024-06-30'),
        compensation: 150000,
        status: ContractStatus.EN_SIGNATURE,
        assignmentSite: 'Siege',
        url: '/assets/documents/contrat-2.pdf'
      }
    ];
    
    this.applyFilters();
  }

  // loadContracts(): void {
  //   this.traineeService.getContracts().subscribe({
  //     next: (data) => {
  //       this.contracts = data;
  //       this.applyFilters();
  //     },
  //     error: (err) => {
  //       console.error('Erreur lors de la récupération des contrats', err);
  //     }
  //   });
  // }

  applyFilters(): void {
    this.filteredContracts = this.contracts.filter(contract => {
      const matchesStatus = !this.statusFilter || contract.status === this.statusFilter;
      const searchLower = this.searchTerm.toLowerCase();
      const matchesSearch = !this.searchTerm || 
        contract.reference.toLowerCase().includes(searchLower) ||
        contract.name.toLowerCase().includes(searchLower) ||
        contract.assignmentSite.toLowerCase().includes(searchLower);
      
      return matchesStatus && matchesSearch;
    });
  }

  getStatusLabel(status: ContractStatus): string {
    const labels: Record<ContractStatus, string> = {
      EN_PREPARATION: 'En préparation',
      EN_SIGNATURE: 'En signature',
      SIGNE: 'Signé',
      TERMINE: 'Terminé',
      RESILIE: 'Résilié'
    };
    return labels[status] || status;
  }

  getStatusClass(status: ContractStatus): string {
    const baseClasses = 'px-2 py-1 text-xs font-medium rounded-full';
    const statusClasses: Record<ContractStatus, string> = {
      EN_PREPARATION: `${baseClasses} bg-gray-100 text-gray-800`,
      EN_SIGNATURE: `${baseClasses} bg-yellow-100 text-yellow-800`,
      SIGNE: `${baseClasses} bg-green-100 text-green-800`,
      TERMINE: `${baseClasses} bg-blue-100 text-blue-800`,
      RESILIE: `${baseClasses} bg-red-100 text-red-800`
    };
    return statusClasses[status] || `${baseClasses} bg-gray-100 text-gray-800`;
  }

  canViewContract(contract: Contract): boolean {
    return [ContractStatus.SIGNE, ContractStatus.TERMINE].includes(contract.status);
  }

  canDownloadContract(contract: Contract): boolean {
    return [ContractStatus.SIGNE, ContractStatus.TERMINE].includes(contract.status);
  }

  viewContract(contract: Contract): void {
    if (!this.canViewContract(contract)) return;
    
    this.selectedContract = contract;
    this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(contract.url);
    this.showContractDialog = true;
  }

  downloadDocument(contract: Contract): void {
    if (!this.canDownloadContract(contract)) return;
    window.open(contract.url, '_blank');
  }

  closeDialog(): void {
    this.showContractDialog = false;
    this.selectedContract = null;
    this.safeUrl = null;
  }
}