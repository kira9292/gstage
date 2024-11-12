import { Component, OnInit } from '@angular/core';
import { TraineeService } from '../../../services/trainee.service';

@Component({
  selector: 'app-ending-internship-attestation',
  standalone: true,
  imports: [],
  templateUrl: './ending-internship-attestation.component.html',
  styleUrl: './ending-internship-attestation.component.scss'
})
export class EndingInternshipAttestationComponent implements OnInit {
  
  private endingInternshipAttestation: any;

  ngOnInit(): void {
    this.loadPayments();
  }

  constructor(private traineeService: TraineeService){}

  loadPayments(): void {
        this.traineeService.getEndingInternAttestation().subscribe({
      next: (data) => {
        this.endingInternshipAttestation = data;        
      },
      error: (err) => {
        console.error('Erreur lors de la récupération de l\'attestation de fin de stage', err);
      }
    });
  }
}
