import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Document } from '../dashboard/dashboard.component';

@Component({
  selector: 'app-attestations',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './attestations.component.html',
  styleUrl: './attestations.component.scss'
})
export class AttestationsComponent {
  attestations: Document[] = [
    {
      id: 1,
      name: 'Attestation de Présence',
      date: new Date('2024-01-01'),
      url: '/assets/documents/attestation-janvier.pdf'
    },
    {
      id: 2,
      name: 'Attestation de Présence',
      date: new Date('2024-02-01'),
      url: '/assets/documents/attestation-fevrier.pdf'
    },
    // ... autres attestations
  ];

  downloadDocument(document: Document) {
    window.open(document.url, '_blank');
  }
}

