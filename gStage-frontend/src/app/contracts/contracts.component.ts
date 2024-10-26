import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Document } from '../dashboard/dashboard.component';

@Component({
  selector: 'app-contracts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './contracts.component.html',
  styleUrl: './contracts.component.scss'
})
export class ContractsComponent {
  contracts: Document[] = [
    {
      id: 1,
      name: 'Contrat Initial',
      date: new Date('2024-01-01'),
      url: '/assets/documents/contrat-1.pdf'
    },
    {
      id: 2,
      name: 'Renouvellement',
      date: new Date('2024-04-01'),
      url: '/assets/documents/contrat-2.pdf'
    }
  ];
  downloadDocument(document: Document) {
    // Implémentation du téléchargement
    window.open(document.url, '_blank');
  }
}