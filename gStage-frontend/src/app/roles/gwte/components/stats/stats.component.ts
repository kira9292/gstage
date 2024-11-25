// stats.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart } from 'chart.js/auto';
import { GwteService } from '../../services/gwte.service';
import { InternshipStatus } from '../../../../enums/gstage.enum';

interface StatsData {
  label: string;
  value: number;
  icon: string;
  borderColor: string;
  bgColor: string;
  iconColor: string;
}

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./stats.component.html",
  styleUrl: "./stats.component.scss"
})

export class StatsComponent implements OnInit {
  demandesStage: any[] = [];
  statusChartInstance: any;
  trendsChartInstance: any;
  processingChartInstance: any;
  conversionChartInstance: any;

  statsData: StatsData[] = [
    {
      label: 'Total Demandes',
      value: 0,
      icon: 'fa-file-alt',
      borderColor: 'border-blue-500',
      bgColor: 'bg-blue-100',
      iconColor: 'text-blue-500'
    },
    {
      label: 'En Attente',
      value: 0,
      icon: 'fa-clock',
      borderColor: 'border-yellow-500',
      bgColor: 'bg-yellow-100',
      iconColor: 'text-yellow-500'
    },
    {
      label: 'Acceptées',
      value: 0,
      icon: 'fa-check-circle',
      borderColor: 'border-green-500',
      bgColor: 'bg-green-100',
      iconColor: 'text-green-500'
    },
    {
      label: 'Archivées',
      value: 0,
      icon: 'fa-archive',
      borderColor: 'border-gray-500',
      bgColor: 'bg-gray-100',
      iconColor: 'text-gray-500'
    }
  ];

  constructor(private gwteService: GwteService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.gwteService.getDemandesStages().subscribe({
      next: (data) => {
        this.demandesStage = data;
        
        this.updateStats();
        this.initCharts();
      },
      error: (error) => {
        console.error('Erreur lors de la récupération des données:', error);
      }
    });
  }

  updateStats(): void {
    this.statsData[0].value = this.demandesStage.length;
    this.statsData[1].value = this.demandesStage.filter(d => 
      d.demandeStage.status === InternshipStatus.EN_ATTENTE
    ).length;
    this.statsData[2].value = this.demandesStage.filter(d => 
      d.demandeStage.status === InternshipStatus.ACCEPTE
    ).length;
    this.statsData[3].value = this.demandesStage.filter(d => 
      d.demandeStage.status === InternshipStatus.ARCHIVE
    ).length;
  }

  initCharts(): void {
    this.initStatusChart();
    this.initTrendsChart();
    this.initProcessingChart();
    this.initConversionChart();
  }

  initStatusChart(): void {
    const statuses = [
      InternshipStatus.EN_ATTENTE,
      InternshipStatus.ACCEPTE,
      InternshipStatus.REFUSE,
      InternshipStatus.EN_COURS,
      InternshipStatus.TERMINE,
      InternshipStatus.ARCHIVE
    ];

    const statusCounts = statuses.map(status => 
      this.demandesStage.filter(d => d.demandeStage.status === status).length
    );

    const statusLabels = [
      'En Attente',
      'Accepté',
      'Refusé',
      'En Cours',
      'Terminé',
      'Archivé'
    ];

    const ctx = document.getElementById('statusChart') as HTMLCanvasElement;
    this.statusChartInstance?.destroy();
    this.statusChartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: statusLabels,
        datasets: [{
          data: statusCounts,
          backgroundColor: [
            '#FCD34D', // yellow
            '#34D399', // green
            '#F87171', // red
            '#60A5FA', // blue
            '#A78BFA', // purple
            '#9CA3AF'  // gray
          ]
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'right'
          }
        }
      }
    });
  }

  initTrendsChart(): void {
    const monthlyData = this.getMonthlyTrends();

    const ctx = document.getElementById('trendsChart') as HTMLCanvasElement;
    this.trendsChartInstance?.destroy();
    this.trendsChartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: monthlyData.labels,
        datasets: [
          {
            label: 'Total',
            data: monthlyData.total,
            borderColor: '#3B82F6',
            tension: 0.1
          },
          {
            label: 'Acceptées',
            data: monthlyData.accepted,
            borderColor: '#34D399',
            tension: 0.1
          },
          {
            label: 'Refusées',
            data: monthlyData.rejected,
            borderColor: '#F87171',
            tension: 0.1
          }
        ]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  initProcessingChart(): void {
    const processingTimes = this.getProcessingTimes();

    const ctx = document.getElementById('processingChart') as HTMLCanvasElement;
    this.processingChartInstance?.destroy();
    this.processingChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['< 1 jour', '1-3 jours', '3-7 jours', '> 7 jours'],
        datasets: [{
          label: 'Nombre de demandes',
          data: processingTimes,
          backgroundColor: '#60A5FA'
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  initConversionChart(): void {
    const conversionRates = this.getConversionRates();

    const ctx = document.getElementById('conversionChart') as HTMLCanvasElement;
    this.conversionChartInstance?.destroy();
    this.conversionChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Taux Acceptation', 'Taux Complétion', 'Taux Archivage'],
        datasets: [{
          label: 'Pourcentage',
          data: conversionRates,
          backgroundColor: ['#34D399', '#60A5FA', '#9CA3AF']
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            max: 100
          }
        }
      }
    });
  }

  getMonthlyTrends() {
    // Logique pour obtenir les tendances mensuelles
    // À adapter selon votre structure de données
    return {
      labels: ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin'],
      total: [10, 15, 20, 25, 30, 35],
      accepted: [5, 8, 12, 15, 18, 20],
      rejected: [2, 3, 4, 5, 6, 7]
    };
  }

  getProcessingTimes() {
    // Logique pour calculer les temps de traitement
    return [10, 25, 15, 5];
  }

  getConversionRates() {
    const total = this.demandesStage.length;
    if (total === 0) return [0, 0, 0];

    const accepted = this.demandesStage.filter(d => 
      d.demandeStage.status === InternshipStatus.ACCEPTE
    ).length;
    
    const completed = this.demandesStage.filter(d => 
      d.demandeStage.status === InternshipStatus.TERMINE
    ).length;
    
    const archived = this.demandesStage.filter(d => 
      d.demandeStage.status === InternshipStatus.ARCHIVE
    ).length;

    return [
      (accepted / total) * 100,
      (completed / accepted) * 100,
      (archived / total) * 100
    ];
  }

  exportStats(): void {
    // Implementer la logique d'export
    console.log('Exporting stats...');
  }

  ngOnDestroy(): void {
    // Cleanup charts
    this.statusChartInstance?.destroy();
    this.trendsChartInstance?.destroy();
    this.processingChartInstance?.destroy();
    this.conversionChartInstance?.destroy();
  }
}