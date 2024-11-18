import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {Router} from "@angular/router";
import {AssistantgwteService} from "../../services/assistantgwte.service";

@Component({
  selector: 'app-poposer-manager',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './poposer-manager.component.html',
  styleUrls: ['./poposer-manager.component.scss']
})
export class PoposerManagerComponent {
  demandeForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private assistantService: AssistantgwteService
  ) {


    this.demandeForm = this.fb.group({
      demandeur: [
        '',
        [Validators.required] // Only alphabets and spaces
      ],
      direction: [
        '',
        [Validators.required] // Only alphabets and spaces
      ],
      nbreStagiaire: [
        1,
        [Validators.required, Validators.min(1)] // Positive integer
      ],
      profilFormation: [
        '',
        [Validators.required] // Only alphabets and spaces
      ],
      stagiaieSousRecomandation: [
        '',
        [Validators.maxLength(255)] // Optional, max length validation
      ],
      commentaire: [
        '',
        [Validators.maxLength(500)] // Optional, max length validation
      ],
      motif: [
        '',
        [Validators.maxLength(500)] // Optional, max length validation
      ],
      traitement: [
        '',
        [Validators.maxLength(500)] // Optional, max length validation
      ]
    });
  }

  onSubmit() {
    if (this.demandeForm.valid) {
      const formData = this.demandeForm.value;
      console.log('Données du formulaire :', formData);

      // Appel du service pour envoyer les données à l'API
      this.assistantService.postDemande(formData).subscribe(
        response => {
          console.log('Réponse de l\'API :', response);
          this.demandeForm.reset();
          this.router.navigate(['/login']);  // Redirection après la soumission
        },
        error => {
          console.error('Erreur lors de l\'envoi des données :', error);
        }
      );
    } else {
      console.log('Formulaire invalide');
    }
  }

}
