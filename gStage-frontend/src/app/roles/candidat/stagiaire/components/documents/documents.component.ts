import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NavigationService } from '../../../../../services/navigation.service';
import { DocumentsCard } from '../../interfaces/trainee.interface';
import { TraineeService } from '../../services/trainee.service';




export interface Document {
  id: number;
  name: string;
  date: Date;
  url: string;
}

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [
    RouterLink,
    CommonModule
  ],
  templateUrl: './documents.component.html',
  styleUrl: './documents.component.scss'
})
export class DocumentsComponent implements OnInit {
  constructor(
    private router: Router,
    private navigationService: NavigationService,
    private traineeService: TraineeService
  ) {}




  cards: DocumentsCard[] = [
    {
      title: 'Mon Contrat',
      description: 'Consultez et téléchargez votre contrat de stage',
      icon: 'file-text',
      route: '/contracts',
      count: 1,
      type: 'contract'
    },
    {
      title: 'Mes Présences',
      description: 'Suivez vos attestations de présence',
      icon: 'clipboard-list',
      route: '/attestations',
      count: 1 ,
      type: 'attestation'
    },
  ];




  onCardClick(card: any) {
    // Mettre à jour l'onglet actif
    this.navigationService.setActiveTab('documents');
    // Naviguer vers la page des documents
    this.router.navigate([`/documents${card.route}`]);
  }



  ngOnInit(): void {
    this.loadDocumentCounts();
  }

  loadDocumentCounts(): void {


    // Charger le nombre d'attestations
    this.traineeService.getPresenceAttestations().subscribe({
      next: (attestations) => {
        const attestationCard = this.cards.find(card => card.type === 'attestation');
        if (attestationCard) {
          attestationCard.count = attestations.length;
        }
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des attestations', err);
      }
    });
  }

}
