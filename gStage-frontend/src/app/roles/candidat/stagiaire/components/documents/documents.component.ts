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


  ngOnInit(): void {
    // this.loadDocumentCounts();
  }

  cards: DocumentsCard[] = [
    {
      title: 'Mon Contrat',
      description: 'Consultez et téléchargez votre contrat de stage',
      icon: 'file-text',
      route: '/contracts',
      count: 2, // Exemple: nombre de contrats disponibles
      type: 'contrat'
    },
    {
      title: 'Mes Présences',
      description: 'Suivez vos attestations de présence',
      icon: 'clipboard-list',
      route: '/attestations',
      count: 6 ,// Exemple: nombre d'attestations disponibles
      type: 'attestation'
    },
  ];

  // loadDocumentCounts(): void {
  //   // Charger le nombre de contrats
  //   this.traineeService.getContracts().subscribe({
  //     next: (contracts) => {
  //       this.cards.find(card => card.type === 'contrat')!.count = contracts.length;
  //     },
  //     error: (err) => {
  //       console.error('Erreur lors de la récupération des contrats', err);
  //     }
  //   });

  //   // Charger le nombre d'attestations
  //   this.traineeService.getPresenceAttestations().subscribe({
  //     next: (attestations) => {
  //       this.cards.find(card => card.type === 'attestation')!.count = attestations.length;
  //     },
  //     error: (err) => {
  //       console.error('Erreur lors de la récupération des attestations', err);
  //     }
  //   });
  // }

  onCardClick(card: any) {  
    // Mettre à jour l'onglet actif
    this.navigationService.setActiveTab('documents');
    // Naviguer vers la page des documents
    this.router.navigate([`/documents${card.route}`]);
  }
}