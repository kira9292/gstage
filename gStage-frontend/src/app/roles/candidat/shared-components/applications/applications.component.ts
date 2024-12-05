import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import {TraineeService} from "../../stagiaire/services/trainee.service";
import {DemandeStageService} from "../../demandeur/services/demande-stage.service";

@Component({
  selector: 'app-applications',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './applications.component.html',
  styleUrl: './applications.component.scss'
})
export class ApplicationsComponent implements OnInit {
  applicationForm: FormGroup;
  // selectedCV: File | null = null;
  // selectedMotivationLetter: File | null = null;
  // cvError: string = '';
  // motivationLetterError: string = '';

  constructor(
    private fb: FormBuilder,
    private demandeStageService: DemandeStageService,
    ) {
    this.applicationForm = this.fb.group({
      type: ['', Validators.required],
      direction: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    }, { validator: this.dateRangeValidator });
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

  // onFileSelected(event: Event, fileType: 'cv' | 'motivation'): void {
  //   const input = event.target as HTMLInputElement;
  //   if (input.files && input.files.length > 0) {
  //     const file = input.files[0];
  //     const allowedTypes = [
  //       'application/pdf',
  //       'application/msword',
  //       'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  //     ];
  //
  //     if (allowedTypes.includes(file.type)) {
  //       if (fileType === 'cv') {
  //         this.selectedCV = file;
  //         this.cvError = '';
  //       } else {
  //         this.selectedMotivationLetter = file;
  //         this.motivationLetterError = '';
  //       }
  //     } else {
  //       const errorMessage = 'Format de fichier non valide. Veuillez utiliser PDF, DOC ou DOCX.';
  //       if (fileType === 'cv') {
  //         this.cvError = errorMessage;
  //         this.selectedCV = null;
  //       } else {
  //         this.motivationLetterError = errorMessage;
  //         this.selectedMotivationLetter = null;
  //       }
  //     }
  //   }
  // }

  onSubmit(): void {
    if (this.applicationForm.valid) {
      // Créer un FormData pour envoyer les fichiers
      const formData = new FormData();
      formData.append('type', this.applicationForm.get('type')?.value);
      formData.append('direction', this.applicationForm.get('direction')?.value);
      formData.append('duration', this.applicationForm.get('duration')?.value);
      formData.append('cv', '');
      formData.append('motivationLetter', '');

      this.demandeStageService.submitDemandeStage(formData).subscribe((response) => {
        console.log("Demande de Renouvellement soumise");
      }, (error) => {
        console.log(error);
      })
      console.log('Form Data:', formData);
    }
  }
}
