import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { noWhitespaceValidator } from '../../../../../core/validators/names.validator';
import { Router } from '@angular/router';
import { SuccessModalComponent } from '../success-modal/success-modal.component';
import { DemandeStageService } from '../../serivices/demande-stage.service';

@Component({
  selector: 'app-demande-stage-form1',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SuccessModalComponent
  ],
  templateUrl: './demande-stage.component.html',
  styleUrl: './demande-stage.component.scss'
})
export class DemandeStageComponent {
  currentStep = 1; // Initialise à la première étape
  showModal = false;

  applicationForm: FormGroup;
  selectedCV: File | null = null;
  selectedMotivationLetter: File | null = null;
  cvError: string = '';
  motivationLetterError: string = '';

  constructor(
    private fb: FormBuilder, 
    private router: Router,
    private demandeStageService: DemandeStageService
  ) {
    this.applicationForm = this.fb.group({

      // Champs de la première étape
      firstName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      lastName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [
        Validators.required,
        Validators.pattern(/^(70|75|76|77|78)[0-9]{7}$/)
      ]],
      formation: [Validators.required],
      school: ['',[Validators.required, Validators.minLength(2), noWhitespaceValidator]],


      // Champs de la deuxième étape
      type: ['', Validators.required],
      direction: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    }, { validator: this.dateRangeValidator });
  }

  // Getters pour les messages d'erreur
  get firstNameError(): string {
    const control = this.applicationForm.get('firstName');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le prénom est requis';
      if (control.errors['minlength']) return 'Le prénom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le prénom ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  get lastNameError(): string {
    const control = this.applicationForm.get('lastName');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom est requis';
      if (control.errors['minlength']) return 'Le nom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  
  get formationError(): string {
    const control = this.applicationForm.get('formation');
    if (control?.errors && control.touched) {
     return 'La formation est requise';
    }
    return '';
  }

  
  get schoolError(): string {
    const control = this.applicationForm.get('school');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'L\'ecole est requise';
      if (control.errors['minlength']) return 'Le nom de l\'ecole doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom de l\'ecole ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  
  get emailError(): string {
    const control = this.applicationForm.get('email');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'L\'email est requis';
      if (control.errors['email']) return 'Format d\'email invalide';
    }
    return '';
  }

  get phoneError(): string {
    const control = this.applicationForm.get('phone');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le numéro de téléphone est requis';
      if (control.errors['pattern']) return 'Format invalide (70|75|76|77|78 + 7 chiffres)';
    }
    return '';
  }

 // Pour passer à l'étape suivante
 goToStep2() {
  if (this.applicationForm.get('firstName')?.valid &&
      this.applicationForm.get('lastName')?.valid &&
      this.applicationForm.get('email')?.valid &&
      this.applicationForm.get('phone')?.valid &&
      this.applicationForm.get('formation')?.valid &&
      this.applicationForm.get('school')?.valid) {
    this.currentStep = 2;
    
  }
  console.log("Step: " + this.currentStep);
  
}
  // Pour revenir à l'étape précédente

  goToStep1() {
    this.currentStep = 1;
  }


  ngOnInit(): void {}

  dateRangeValidator(group: FormGroup) {
    const startDate = group.get('startDate')?.value;
    const endDate = group.get('endDate')?.value;
    
    if (startDate && endDate && new Date(startDate) >= new Date(endDate)) {
      return { dateRange: true };
    }
    return null;
  }
  
 // Fonction de conversion d'un fichier en Base64 sans le préfixe
convertToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => resolve(reader.result as string);
    reader.onerror = (error) => reject(error);
    reader.readAsDataURL(file);
  });
}
  onFileSelected(event: Event, fileType: 'cv' | 'motivation'): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      ];
      
      if (allowedTypes.includes(file.type)) {
        if (fileType === 'cv') {
          this.selectedCV = file;
          this.cvError = '';
        } else {
          this.selectedMotivationLetter = file;
          this.motivationLetterError = '';
        }
      } else {
        const errorMessage = 'Format de fichier non valide. Veuillez utiliser PDF, DOC ou DOCX.';
        if (fileType === 'cv') {
          this.cvError = errorMessage;
          this.selectedCV = null;
        } else {
          this.motivationLetterError = errorMessage;
          this.selectedMotivationLetter = null;
        }
      }
    }
  }


// Fonction de soumission du formulaire
onSubmit(): void {
  if (this.applicationForm.valid && this.selectedCV && this.selectedMotivationLetter) {
    // Créer l'objet demandeStage avec les informations du formulaire
    const demandeStageData = {
      demandeStage: {
        creationDate: new Date().toISOString().split('T')[0], // Date actuelle
        internshipType: this.applicationForm.get('type')?.value, // Exemple de type de stage
        startDate: this.applicationForm.get('startDate')?.value,
        endDate: this.applicationForm.get('endDate')?.value,
        status: "EN_ATTENTE", // Statut initial
        validated: false, // Statut de validation
        // Champs pour le contenu Base64 des fichiers
        cv: '',
        cvContentType: this.selectedCV ? this.selectedCV.type : '', // Vérifie si selectedCV est défini
        coverLetter: '',
        coverLetterContentType: this.selectedMotivationLetter ? this.selectedMotivationLetter.type : '', // Vérifie si selectedMotivationLetter est défini
      },
      candidat: {
        firstName: this.applicationForm.get('firstName')?.value,
        lastName: this.applicationForm.get('lastName')?.value,
        birthDate: null, // Exemple de date de naissance
        nationality: 'Senegalese', // Exemple de nationalité
        birthPlace: null, // Exemple de lieu de naissance
        cni: null, // Exemple de numéro de CNI
        address: '', // Exemple d'adresse
        email: this.applicationForm.get('email')?.value,
        phone: this.applicationForm.get('phone')?.value,
        educationLevel: 'BAC_PLUS_2', // Exemple de niveau d'éducation
        school: this.applicationForm.get('school')?.value,
      }
    };

    // Vérification si les fichiers existent avant de les convertir en Base64
    if (this.selectedCV && this.selectedMotivationLetter) {
      // Convertir le CV en Base64
      this.convertToBase64(this.selectedCV as File).then((cvBase64) => {
        // Enlever le préfixe "data:application/pdf;base64," si présent
        demandeStageData.demandeStage.cv = cvBase64.split(',')[1] || cvBase64;

        // Convertir la lettre de motivation en Base64
        this.convertToBase64(this.selectedMotivationLetter as File).then((motivationLetterBase64) => {
          demandeStageData.demandeStage.coverLetter = motivationLetterBase64.split(',')[1] || motivationLetterBase64;

          // Appel au service pour soumettre les données
          console.log('Données à envoyer (avant submit):', demandeStageData);
          this.demandeStageService.submitDemandeStage(demandeStageData).subscribe(response => {
            this.showModal = true;
          }, error => {
            console.error("Erreur lors de l'envoi de la demande :", error);
          });
        }).catch((error) => {
          console.error('Erreur lors de la conversion en Base64 de la lettre de motivation:', error);
        });
      }).catch((error) => {
        console.error('Erreur lors de la conversion en Base64 du CV:', error);
      });
    }
  } else {
    console.warn("Le formulaire n'est pas valide ou les fichiers sont manquants.");
  }
}




  closeModal() {
    this.showModal = false;
    this.router.navigate(['/login'])
  }
}
