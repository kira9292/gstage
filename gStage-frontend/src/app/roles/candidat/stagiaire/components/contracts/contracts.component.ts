// contracts.component.ts
import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Contract } from '../../interfaces/trainee.interface';
import { ContractStatus } from '../../enums/trainee.enum';
import { TraineeService } from '../../services/trainee.service';


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
  contract: any;
  showContractDialog = false;
  selectedContract: any | null = null;
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
    this.loadContract();
  }

  loadContract(): void {
    this.traineeService.getContract().subscribe({
      next: (data) => {
        this.contract = data;
        console.log("Contrat" + JSON.stringify(this.contract));
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des contrats', err);
      }
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

  viewContract(contrat: any): void {
    // // if (!this.canViewContract(contract)) return;
    //
    // this.selectedContract = contract;
    // this.safeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(contract.url);
    // this.showContractDialog = true;

    if (contrat.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(contrat.docs);
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

  downloadDocument(contrat: any): void {
    // Décommenter cette ligne si vous avez une logique de vérification spécifique
    // if (!this.canDownloadContract(contrat)) return;

    if (contrat.docs) {
      // Convertir la chaîne Base64 en Blob
      const byteCharacters = atob(contrat.docs);
      const byteNumbers = new Array(byteCharacters.length).fill(0).map((_, i) => byteCharacters.charCodeAt(i));
      const byteArray = new Uint8Array(byteNumbers);

      // Créer un Blob pour un fichier Word
      const blob = new Blob([byteArray], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

      // Générer un nom de fichier personnalisé
      const fileName = this.generateDocumentFileName(contrat);

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
      console.error('Document Base64 non trouvé.');
    }
  }

// Méthode pour générer un nom de fichier personnalisé pour le document
  generateDocumentFileName(contrat: any): string {
    // Vous pouvez personnaliser cette méthode selon vos besoins
    const prefix = 'Contrat';
    const date = new Date().toISOString().split('T')[0]; // Date au format AAAA-MM-JJ
    const identifier = contrat.id || 'sans_id'; // Utiliser un identifiant unique si disponible
    const type = contrat.type || 'stage'; // Type de document si disponible

    return `${prefix}_${type}_${identifier}_${date}.docx`;
  }

  closeDialog(): void {
    this.showContractDialog = false;
    this.selectedContract = null;
    this.safeUrl = null;
  }


  startDate: string | null = null;
  endDate: string | null = null;


}
