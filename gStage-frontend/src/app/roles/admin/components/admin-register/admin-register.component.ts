import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLinkActive} from '@angular/router';
import {Departement, Service, User} from "../dashboard-admin/dashboard-admin.component";
import {AdminServiceService} from "../../services/admin-service.service";
import {noWhitespaceValidator} from "../../../../core/validators/names.validator";






export interface Candidat {
  id?: number;
  firstName: string;
  name: string;
  email: string;
  educationLevel: string;
  validationStatus: 'Non validé' | 'En cours' | 'Validé';
}




@Component({
  selector: 'app-admin-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLinkActive,
    FormsModule
  ],
  templateUrl: './admin-register.component.html',
  styleUrls: ['./admin-register.component.scss']
})
export class AdminRegisterComponent implements OnInit {
  candidates: Candidat[] = [];

  searchTerm: string = '';
  statusFilter: string = '';
  selectedCandidate: Candidat | null = null;
  isModalOpen: boolean=false;
  candidatForm!: FormGroup;
  isSubmitting = false;


  constructor(
    private adminService: AdminServiceService,
    private fb: FormBuilder,
  ) {
    this.candidatForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      name: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.email ,noWhitespaceValidator]],
      username: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/)
      ]],

    });





  }

  ngOnInit() {
    this.fetchCandidat()

  }


  filteredCandidates(): Candidat[] {
    return this.candidates.filter(candidate =>
      (!this.searchTerm ||
        candidate.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        candidate.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        candidate.email.toLowerCase().includes(this.searchTerm.toLowerCase())
      ) &&
      (!this.statusFilter || candidate.validationStatus === this.statusFilter)
    );
  }

  fetchCandidat(): void {
    this.adminService.getCandidats().subscribe(
      (data: Candidat[]) => {
        this.candidates = data;
        console.log(data);
      },
      (error) => {
        console.error('Erreur lors du chargement des services:', error);
      }
    );
  }

  openAddModal(candidat?: Candidat): void {
    this.isModalOpen = true;
    if (candidat) {
      this.selectedCandidate = candidat;
      this.candidatForm.patchValue(candidat);
    } else {
      this.selectedCandidate = null;
      this.candidatForm.reset({ enabled: true });
    }
  }
  closeModal(): void {
    this.isModalOpen = false;
    this.selectedCandidate = null;
    console.log(this.candidatForm.value)

    this.candidatForm.reset();
  }




  deleteCandidate(candidate: Candidat) {
    this.candidates = this.candidates.filter(c => c.id !== candidate.id);
  }

  viewDetails(candidate: Candidat) {
    this.selectedCandidate = candidate;
  }

  saveUser(): void {
    console.log(this.candidatForm.value)
    if (this.candidatForm.valid) {
      const formData = this.candidatForm.value;
      const payload = {
        "candidat":{
          "id":this.selectedCandidate ? this.selectedCandidate.id : null,
        },
        "appUser": {
          "username":formData.username,
          "email": formData.email,
          "firstName": formData.firstName,
          "name": formData.name,
          "password": formData.password,

        },


      }
      console.log('Données préparées pour l\'API :', JSON.stringify(payload));

      this.adminService.saveCandiat(payload).subscribe(
        (response) => {
          this.isSubmitting=true;
          console.log('Utilisateur sauvegardé avec succès:', response);

          this.candidatForm.reset();
          this.closeModal();
          this.fetchCandidat();
        },
        (error) => {
          console.error('Erreur lors de la sauvegarde de l\'utilisateur :', error);
        }
      );
    }else {
      console.log('formulaire invalide')
    }

  }



  get firstNameError(): string {
    const control = this.candidatForm.get('firstName');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le prénom est requis';
      if (control.errors['minlength']) return 'Le prénom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le prénom ne doit pas contenir de caractere speciaux inutiles';
    }
    return '';
  }

  get lastNameError(): string {
    const control = this.candidatForm.get('name');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom est requis';
      if (control.errors['minlength']) return 'Le nom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }
  get usernameError(): string {
    const control = this.candidatForm.get('username');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom d\'utilisateur est requis';
      if (control.errors['minlength']) return 'Le nom d\'utilisateur doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom d\'utilisateur ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  get emailError(): string {
    const control = this.candidatForm.get('email');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'L\'email est requis';
      if (control.errors['email']) return 'Format d\'email invalide';
    }
    return '';
  }

  get passwordError(): string {
    const control = this.candidatForm.get('password');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le mot de passe est requis';
      if (control.errors['minlength']) return 'Le mot de passe doit contenir au moins 8 caractères';
      if (control.errors['pattern']) return 'Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial';
    }
    return '';
  }
}
