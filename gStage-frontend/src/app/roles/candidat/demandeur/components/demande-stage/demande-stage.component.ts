import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { noWhitespaceValidator } from '../../../../../core/validators/names.validator';
import { Router } from '@angular/router';
import { SuccessModalComponent } from '../success-modal/success-modal.component';
import { DemandeStageService } from '../../services/demande-stage.service';

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
  verificationForm: FormGroup;
  selectedCV: File | null = null;
  selectedMotivationLetter: File | null = null;
  cvError: string = '';
  motivationLetterError: string = '';

  recipientEmail: string = ''; // Message d'erreur pour la vérification du code


  constructor(
    private fb: FormBuilder,
    private router: Router,
    private demandeStageService: DemandeStageService
  ) {
    this.applicationForm = this.fb.group({

      // Champs de la première étape
      firstName: ['',
        [
          Validators.required,
          Validators.minLength(2),
          noWhitespaceValidator
        ]],
      lastName: ['',
        [
          Validators.required,
          Validators.minLength(2),
          noWhitespaceValidator
        ]],
      email: ['',
        [
          Validators.required,
          Validators.email
        ]],
      phone: ['', [
        Validators.required,
        Validators.pattern(/^(70|75|76|77|78)[0-9]{7}$/)
      ]],
      formation: ['',
        [
          Validators.required
        ]],
      school: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        noWhitespaceValidator
      ]],

      // Champs de la deuxième étape
      type: ['', [
        Validators.required
      ]],
      direction: ['',
        [
          Validators.required
        ]],
      startDate: ['',
        [
          Validators.required
        ]],
      endDate: ['',
        [
          Validators.required
        ]],
      birthDate: ['', [
        Validators.required,
        this.dateOfBirthValidator
      ]],
      nationality: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        noWhitespaceValidator
      ]],
      birthPlace: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        noWhitespaceValidator
      ]],
      cni: ['', [
        Validators.required,
        Validators.pattern(/^\d{13}$/), // Exact 13 chiffres
      ]],
      address: ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(100),
        noWhitespaceValidator // Validateur personnalisé existant
      ]],
      educationLevel: ['', Validators.required]
    }, { validator: this.dateRangeValidator });


    // Initialisation du formulaire de code de vérification
    this.verificationForm = this.fb.group({
      verificationCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
    });
  }

  // Validateur personnalisé pour la date de naissance
dateOfBirthValidator(control: AbstractControl): { [key: string]: any } | null {
  const birthDate = new Date(control.value);
  const currentDate = new Date();
  const minAge = 16; // Âge minimum pour un stagiaire
  const maxAge = 35; // Âge maximum pour un stagiaire

  let age = currentDate.getFullYear() - birthDate.getFullYear();
  const monthDiff = currentDate.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && currentDate.getDate() < birthDate.getDate())) {
    age--;
  }

  if (age < minAge || age > maxAge) {
    return { 'invalidAge': true };
  }

  return null;
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

// Pour la date de naissance
get birthDateError(): string {
  const control = this.applicationForm.get('birthDate');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'La date de naissance est requise';
    if (control.errors['invalidAge']) return 'Vous devez avoir entre 16 et 35 ans pour ce stage';
  }
  return '';
}

// Pour le lieu de naissance
get birthPlaceError(): string {
  const control = this.applicationForm.get('birthPlace');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'Le lieu de naissance est requis';
    if (control.errors['minlength']) return 'Le lieu de naissance doit contenir au moins 2 caractères';
    if (control.errors['maxlength']) return 'Le lieu de naissance ne doit pas dépasser 50 caractères';
    if (control.errors['whitespace']) return 'Le lieu de naissance ne doit pas contenir de caractères spéciaux inutiles';
  }
  return '';
}

  get cniError(): string {
    const control = this.applicationForm.get('cni');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le numéro CNI est requis';
      if (control.errors['pattern']) return 'Le numéro CNI doit contenir 13 chiffres';
    }
    return '';
  }
// Pour la nationalité
get nationalityError(): string {
  const control = this.applicationForm.get('nationality');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'La nationalité est requise';
    if (control.errors['minlength']) return 'La nationalité doit contenir au moins 2 caractères';
    if (control.errors['maxlength']) return 'La nationalité ne doit pas dépasser 50 caractères';
    if (control.errors['whitespace']) return 'La nationalité ne doit pas contenir de caractères spéciaux inutiles';
  }
  return '';
}
// Pour l'adresse
get addressError(): string {
  const control = this.applicationForm.get('address');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'L\'adresse est requise';
    if (control.errors['minlength']) return 'L\'adresse doit contenir au moins 5 caractères';
    if (control.errors['maxlength']) return 'L\'adresse ne doit pas dépasser 100 caractères';
    if (control.errors['whitespace']) return 'L\'adresse ne doit pas contenir de caractères spéciaux inutiles';
  }
  return '';
}

// Pour les dates de stage
get dateRangeError(): string {
  const form = this.applicationForm;

  if (form.errors && form.errors['startDate']) {
    return 'La date de début doit être après la date actuelle';
  }

  if (form.errors && form.errors['dateRange']) {
    return 'La date de début doit être antérieure à la date de fin';
  }
  if (form.errors && form.errors['stageDuration']) {
    return 'La durée du stage doit être entre 1 et 6 mois';
  }
  return '';
}

 // Pour la formation
get formationError(): string {
  const control = this.applicationForm.get('formation');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'La formation est requise';
    if (control.errors['minlength']) return 'La formation doit contenir au moins 2 caractères';
    if (control.errors['maxlength']) return 'La formation ne doit pas dépasser 50 caractères';
  }
  return '';
}
 // Pour la niveau d'education
get educationLevelError(): string {
  const control = this.applicationForm.get('educationLevel');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'Le niveau d\'etudes est requis';
  }
  return '';
}

// Pour l'école
get schoolError(): string {
  const control = this.applicationForm.get('school');
  if (control?.errors && control.touched) {
    if (control.errors['required']) return 'L\'école est requise';
    if (control.errors['minlength']) return 'Le nom de l\'école doit contenir au moins 2 caractères';
    if (control.errors['maxlength']) return 'Le nom de l\'école ne doit pas dépasser 100 caractères';
    if (control.errors['whitespace']) return 'Le nom de l\'école ne doit pas contenir de caractères spéciaux inutiles';
  }
  return '';
}

  // Pour l'email
  get emailError(): string {
    const control = this.applicationForm.get('email');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'L\'email est requis';
      if (control.errors['email']) return 'Format d\'email invalide';
    }
    return '';
  }


  // Pour le phone
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
      this.applicationForm.get('birthDate')?.valid &&
      this.applicationForm.get('birthPlace')?.valid &&
      this.applicationForm.get('cni')?.valid &&
      this.applicationForm.get('nationality')?.valid &&
      this.applicationForm.get('address')?.valid &&
      this.applicationForm.get('formation')?.valid &&
      this.applicationForm.get('school')?.valid &&
      this.applicationForm.get('educationLevel')?.valid)
      {
        this.currentStep = 2;
      }

}
  // Pour revenir à l'étape précédente

  goToStep1() {
    this.currentStep = 1;
  }


  ngOnInit(): void {}

  // dat
  dateRangeValidator(group: FormGroup) {
    const startDate = group.get('startDate')?.value;
    const endDate = group.get('endDate')?.value;

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Réinitialiser les heures pour une comparaison précise

    if (startDate && endDate) {
      const start = new Date(startDate);
      const end = new Date(endDate);
      const minDuration = 1; // Durée minimale du stage en mois
      const maxDuration = 6; // Durée maximale du stage en mois

      // Vérifier que la date de début est après aujourd'hui
      if (start <= today) {
        return { startDate: 'La date de début doit être postérieure à la date actuelle' };
      }

      // Vérifier que la date de début est avant la date de fin
      if (start >= end) {
        return { dateRange: 'La date de début doit être antérieure à la date de fin' };
      }

      // Calculer la durée du stage
      const monthDifference = (end.getFullYear() - start.getFullYear()) * 12 +
                              (end.getMonth() - start.getMonth());

      if (monthDifference < minDuration || monthDifference > maxDuration) {
        return {
          stageDuration: `La durée du stage doit être entre ${minDuration} et ${maxDuration} mois`
        };
      }
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
          reference: '',
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
          birthDate: this.applicationForm.get('birthDate')?.value, // Exemple de date de naissance
          nationality: this.applicationForm.get('nationality')?.value, // Exemple de nationalité
          birthPlace: this.applicationForm.get('birthPlace')?.value, // Exemple de lieu de naissance
          cni: this.applicationForm.get('cni')?.value, // Exemple de numéro de CNI
          address: this.applicationForm.get('address')?.value, // Exemple d'adresse
          email: this.applicationForm.get('email')?.value,
          phone: this.applicationForm.get('phone')?.value,
          educationLevel: this.applicationForm.get('educationLevel')?.value, // Exemple de niveau d'éducation
          school: this.applicationForm.get('school')?.value,
          formation: this.applicationForm.get('formation')?.value,
        }
      };

      this.convertToBase64(this.selectedCV as File)
        .then((cvBase64) => {
          demandeStageData.demandeStage.cv = cvBase64.split(',')[1] || cvBase64;
          return this.convertToBase64(this.selectedMotivationLetter as File);
        })
        .then((motivationLetterBase64) => {
          demandeStageData.demandeStage.coverLetter = motivationLetterBase64.split(',')[1] || motivationLetterBase64;

          this.recipientEmail = this.applicationForm.get('email')?.value;
          // Réinitialiser les champs du formulaire
          this.applicationForm.reset();
          // Réinitialiser les fichiers sélectionnés
          this.selectedCV = null;
          this.selectedMotivationLetter = null;

          // Appel au service pour soumettre les données et envoyer le code de vérification
          return this.demandeStageService.submitDemandeStage(demandeStageData).subscribe((response) => {
            this.showModal = true;

          },  (error) => {
            console.error("Erreur lors de la soumission:", error);
          })

        })
        .catch(error => {
          console.error("Erreur lors de la soumission :", error);
        });
    } else {
      console.warn("Le formulaire n'est pas valide ou les fichiers sont manquants.");
    }
  }



  // // Méthode pour vérifier le code de validation
  // verifyCode(): void {
  //   const verificationCode = this.verificationForm.get('verificationCode')?.value;
  //
  //   if (this.verificationForm.valid) {
  //     this.demandeStageService.verifyCode(verificationCode).subscribe(
  //       response => {
  //         this.showModal = true; // Affiche la modal de succès
  //       },
  //       error => {
  //         this.verificationError = 'Code invalide. Veuillez réessayer.';
  //         console.error('Erreur de vérification du code:', error);
  //       }
  //     );
  //   } else {
  //     this.verificationError = 'Veuillez entrer un code valide.';
  //   }
  // }


  closeModal() {
    this.showModal = false;
    this.router.navigate(['/login'])
  }
}
