import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { noWhitespaceValidator } from '../../../../../core/validators/names.validator';

@Component({
  selector: 'app-demande-stage-form1',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './demande-stage.component.html',
  styleUrl: './demande-stage.component.scss'
})
export class DemandeStageComponent {
  currentStep = 1; // Initialise à la première étape
  

  applicationForm: FormGroup;
  selectedCV: File | null = null;
  selectedMotivationLetter: File | null = null;
  cvError: string = '';
  motivationLetterError: string = '';

  constructor(private fb: FormBuilder) {
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
    // Créer un FormData pour envoyer les fichiers et les autres informations
    const formData = new FormData();
    formData.append('firstName', this.applicationForm.get('firstName')?.value);
    formData.append('lastName', this.applicationForm.get('lastName')?.value);
    formData.append('email', this.applicationForm.get('email')?.value);
    formData.append('phoneNumber', this.applicationForm.get('phoneNumber')?.value);
    formData.append('formation', this.applicationForm.get('formation')?.value);
    formData.append('school', this.applicationForm.get('school')?.value);
    formData.append('type', this.applicationForm.get('type')?.value);
    formData.append('direction', this.applicationForm.get('direction')?.value);
    formData.append('startDate', this.applicationForm.get('startDate')?.value);
    formData.append('endDate', this.applicationForm.get('endDate')?.value);
    formData.append('cv', this.selectedCV);
    formData.append('motivationLetter', this.selectedMotivationLetter);


      // Appel au service pour envoyer les donnees
      console.log('Form Data:', formData);
    }
  }
}
