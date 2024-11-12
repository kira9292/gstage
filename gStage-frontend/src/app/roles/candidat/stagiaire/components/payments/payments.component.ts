import { Component, OnInit } from '@angular/core';
import { TraineeService } from '../../services/trainee.service';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [],
  templateUrl: './payments.component.html',
  styleUrl: './payments.component.scss'
})
export class PaymentsComponent implements OnInit {

  private payments: any[] = [];

  ngOnInit(): void {
    this.loadPayments();
  }

  constructor(private traineeService: TraineeService){}

  loadPayments(): void {
        this.traineeService.getPayments().subscribe({
      next: (data) => {
        this.payments = data;        
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des paiements', err);
      }
    });
  }
}
