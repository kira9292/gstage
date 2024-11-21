import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from "@angular/router";
import { AssistantgwteService } from "../../services/assistantgwte.service";
import { GwteService } from "../../services/gwte.service";
import {CommonModule, NgClass, NgIf} from "@angular/common";

@Component({
  selector: 'app-poposer-manager',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgClass,CommonModule,],
  templateUrl: './poposer-manager.component.html',
  styleUrls: ['./poposer-manager.component.scss']
})
export class PoposerManagerComponent implements OnInit {
  demandeForm: FormGroup;
  demandeurs: any[] = []; // Tableau pour stocker les demandeurs récupérés
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
      stagiaire: [
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

    //
    // this.GwteService.getManagers().subscribe(
    //   (demandes) => {
    //     // Récupérer les demandeurs (ici vous pouvez les adapter selon vos données)
    //     this.demandeurs = demandes; // Assurez-vous que l'API renvoie les bons objets
    //     console.log('Liste des demandeurs récupérés :', this.demandeurs);
    //   },
    //   (error) => {
    //     console.error('Erreur lors de la récupération des demandeurs :', error);
    //   }
    // );
















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
            stagiaire: `${candidat?.firstName || ''} ${candidat?.lastName || ''}`
          });
        }
      }
    );
  }

  onSubmit() {
    if (this.demandeForm.valid) {
      const formData = this.demandeForm.value;

      // Création des données dans le format exact demandé
      const payload = {
        "demandeur": formData.demandeur, // Exemple : "Jean Dupont Direction Technique"
        "direction": formData.direction, // Exemple : "for gurn consequently"
        "nbreStagiaire": formData.nbreStagiaire, // Exemple : 16387
        "profilFormation": formData.profilFormation, // Exemple : "oh behind glum"
        "stagiaieSousRecomandation": formData.stagiaire, // Exemple : "outnumber"
        "commentaire": formData.commentaire, // Exemple : "CD following nervously"
        "motif": formData.motif, // Exemple : "while fairly"
        "traitement": formData.traitement // Exemple : "near shrilly"
      };

      console.log('Données préparées pour l\'API :', JSON.stringify(payload));

      // Appel du service pour envoyer les données
      this.assistantService.postDemande(payload).subscribe(
        response => {
          console.log('Réponse de l\'API :', response);
          this.demandeForm.reset();
          this.router.navigate(['/login']); // Redirection après soumission
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
