import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NavigationService } from '../services/navigation.service';


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
export class DashboardComponent {
  constructor(
    private router: Router,
    private navigationService: NavigationService
  ) {}
  @Input() userType: 'intern' | 'candidate' = 'intern';
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
      type: 'paiement'
    }
  ];

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