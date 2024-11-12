import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NavigationService } from '../../../../../services/navigation.service';
import { TraineeService } from '../../services/trainee.service';


export interface DashboardCard {
  title: string;
  description: string;
  icon: string;
  count?: number;
  route: string;
  type: string;
}

export interface Document {
  id: number;
  name: string;
  date: Date;
  url: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterLink, 
    CommonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  constructor(
    private router: Router,
    private navigationService: NavigationService,
    private traineeService: TraineeService
  ) {}
  ngOnInit(): void {
    // this.loadDocumentCounts();
  }
  cards: DashboardCard[] = [
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
    {
      title: 'Mes Paiements',
      description: 'Historique de vos indemnités de stage',
      icon: 'credit-card',
      route: '/payments',
      count: 5,
      type: 'paiement'
    }
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

  //   // Charger le nombre de paiements
  //   this.traineeService.getPayments().subscribe({
  //     next: (payments) => {
  //       this.cards.find(card => card.type === 'paiement')!.count = payments.length;
  //     },
  //     error: (err) => {
  //       console.error('Erreur lors de la récupération des paiements', err);
  //     }
  //   });
  // }


  onCardClick(card: any) {
    if(card.type != 'paiement'){
    // Mettre à jour l'onglet actif
    this.navigationService.setActiveTab('documents');
    // Naviguer vers la page des documents
    this.router.navigate([`/documents${card.route}`]);
    }else {
      this.router.navigate([`${card.route}`]);
    }
  }
}