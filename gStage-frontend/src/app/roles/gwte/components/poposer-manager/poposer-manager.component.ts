import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from "@angular/router";
import { AssistantgwteService } from "../../services/assistantgwte.service";
import { GwteService } from "../../services/gwte.service";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-poposer-manager',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './poposer-manager.component.html',
  styleUrls: ['./poposer-manager.component.scss']
})
export class PoposerManagerComponent implements OnInit {
  demandeForm: FormGroup;
  demande: any;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private GwteService: GwteService,
    private assistantService: AssistantgwteService
  ) {
    // Initialisation du formulaire sans 'nbreStagiaire'
    this.demandeForm = this.fb.group({
      demandeur: [
        '',
        [Validators.required]
      ],
      direction: [
        '',
        [Validators.required]
      ],
      profilFormation: [
        { value: '', disabled: true },
        [Validators.required]
      ],
      stagiaireSousRecomandation: [
        { value: '', disabled: true },
        [Validators.maxLength(255)]
      ],
      commentaire: [
        '',
        [Validators.maxLength(500)]
      ],
      motif: [
        '',
        [Validators.maxLength(500)]
      ],
      traitement: [
        '',
        [Validators.maxLength(500)]
      ]
    });
  }

  ngOnInit() {
    // Récupération de la demande et pré-remplissage du formulaire
    this.GwteService.currentDemande.subscribe(
      (demande) => {
        if (demande) {
          this.demande = demande;
          console.log('Demande récupérée :', this.demande);

          // Pré-remplir les champs avec les données du candidat
          const candidat = this.demande?.candidat;
          this.demandeForm.patchValue({
            profilFormation: candidat?.formation || '',
            stagiaireSousRecomandation: `${candidat?.firstName || ''} ${candidat?.lastName || ''}`
          });
        }
      }
    );
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
