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
  selectedDemandeur: any = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private GwteService: GwteService,
    private assistantService: AssistantgwteService
  ) {
    // Initialisation du formulaire sans 'nbreStagiaire'
    this.demandeForm = this.fb.group({
      demandeur: ['', [Validators.required]],
      direction: [{ value: '', disabled: true }, [Validators.maxLength(255)]],
      profilFormation: [{ value: '', disabled: true }, [Validators.required]],
      stagiaire: [{ value: '1', disabled: true }, [Validators.maxLength(255)]],
      commentaire: ['', [Validators.maxLength(500)]],
      motif: ['', [Validators.maxLength(500)]],
      traitement: ['ok', [Validators.maxLength(500)]]
    });
  }

  ngOnInit() {

    //
    this.GwteService.getManagers().subscribe(
      (data) => {
        this.demandeurs = data;
        console.log('Liste des demandeurs récupérés :', this.demandeurs);
      },
      (error) => {
        console.error('Erreur lors de la récupération des demandeurs :', error);
      }
    );


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


      const demandeur = formData.demandeur;
      const demandeStageId = this.demande?.demandeStage?.id;


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

      const payload1 = {
        "appUser": {
          "email": demandeur
        },
        "demandeStage": {
          "id": demandeStageId
        }
      };
      this.assistantService.postDemande1(payload1).subscribe(
        response => {
          console.log('Réponse de l\'API pour payload1 :', response);

          // Appel à postDemande pour envoyer les autres informations
          this.assistantService.postDemande(payload).subscribe(
            response => {
              console.log('Réponse de l\'API pour payload :', response);
              this.demandeForm.reset();
              this.router.navigate(['/login']); // Redirection après soumission
            },
            error => {
              console.error('Erreur lors de l\'envoi des données (payload) :', error);
            }
          );
        },
        error => {
          console.error('Erreur lors de l\'envoi des données (payload1) :', error);
        }
      );
    } else {
      console.log('Formulaire invalide');
    }

  }






  onDemandeurChange(demandeurEmail: string) {
    this.selectedDemandeur = this.demandeurs.find(d => d.email === demandeurEmail);
    // Mettre à jour le champ direction avec le serviceName du demandeur
    if (this.selectedDemandeur) {
      this.demandeForm.patchValue({
        direction: this.selectedDemandeur.serviceName
      });
    }
  }

  goToBack() {
    this.router.navigate(['dashboard-gwte']);
  }
}
