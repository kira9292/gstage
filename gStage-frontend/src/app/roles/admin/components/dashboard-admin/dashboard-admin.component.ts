import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { AdminServiceService } from "../../services/admin-service.service";
import {Router} from "@angular/router";
import {noWhitespaceValidator} from "../../../../core/validators/names.validator";

export interface User {
  id: number;
  username: string;
  email: string;
  name: string;
  firstName: string;
  phone: string | null;
  roleName: string;
  serviceName: string | null;
  enabled: boolean;
  accountNonExpired: boolean;
  credentialsNonExpired: boolean;
  accountNonLocked: boolean;
  authorities: string[];
}
export interface Service{
  id: number;
  name: string;
  description: string;
  departmentId:number;
  departmentName: string;

}

export interface Departement{
  id: number;
  name: string;
  description: string;

}

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './dashboard-admin.component.html',
  styleUrls: ['./dashboard-admin.component.scss']
})
export class DashboardAdminComponent implements OnInit {
  users: User[] = [];
  searchTerm: string = '';
  isModalOpen: boolean = false;
  selectedUser: User | null = null;
  userForm!: FormGroup;
  isSubmitting = false;
  services: Service[]=[];
  depts: Departement[]=[];
  activeTab: string = 'users';
  serviceForm!: FormGroup;
  departementsForm!: FormGroup;
  isServiceModalOpen: boolean = false;
  isDepartementsModalOpen: boolean = false;
  selectedService: any = null;

  isModalOpen1 = false;
  resetPasswordForm!: FormGroup;
   selectedDept: any=null;


  constructor(
    private adminService: AdminServiceService,
    private fb: FormBuilder,
    private router: Router

  ) {
    // Initialisation du formulaire avec les validations
    this.userForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      name: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
      ]],
      phone: [''],
      roleName: ['', Validators.required],
      serviceName: [''],
      enabled: [true]
    });

    this.userForm.get('roleName')?.valueChanges.subscribe(role => {
      const serviceControl = this.userForm.get('serviceName');
      if (role === 'MANAGER') {
        serviceControl?.setValidators([Validators.required]);
      } else {
        serviceControl?.clearValidators();
      }
      serviceControl?.updateValueAndValidity();
    });


    this.serviceForm = this.fb.group({
      name: ['',[Validators.required, Validators.minLength(2)]],
      description: [''],
      departmentId: ['',[Validators.required]],

    });
    this.departementsForm = this.fb.group({
      name: ['',[Validators.required, Validators.minLength(2)]],
      descriptions: [''],

    });

    this.resetPasswordForm = this.fb.group(
      {
        newPassword: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', Validators.required]
      },
      { validators: this.passwordMatchValidator }
    );

  }

  ngOnInit(): void {
    this.fetchUsers();
    this.fetchServices();
    this.fetchDepartement();

  }

  fetchServices(): void {
    this.adminService.getServices().subscribe(
      (data: Service[]) => {
        this.services = data;
        console.log(data);
      },
      (error) => {
        console.error('Erreur lors du chargement des services:', error);
      }
    );
  }
  fetchDepartement(): void {
    this.adminService.getDepartement().subscribe(
      (data: Departement[]) => {
        this.depts = data;
        console.log(data);
      },
      (error) => {
        console.error('Erreur lors du chargement des services:', error);
      }
    );
  }
  // Récupérer les utilisateurs depuis le service
  fetchUsers(): void {
    this.adminService.getAppUsers().subscribe(
      (data: User[]) => {
        this.users = data;
      },
      (error) => {
        console.error('Erreur lors du chargement des utilisateurs:', error);
      }
    );
  }

  // Filtrer les utilisateurs
  filteredUsers(): User[] {
    if (!this.searchTerm) return this.users;

    return this.users.filter(user =>
      user.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      user.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  // Ouvrir le modal pour modifier un utilisateur
  openModal(user?: User): void {
    this.isModalOpen = true;
    if (user) {
      this.selectedUser = user;
      this.userForm.patchValue(user);
    } else {
      this.selectedUser = null;
      this.userForm.reset({ enabled: true });
    }
  }


  openServiceModal(service?: Service): void {
    this.isServiceModalOpen = true;
    if (service) {
      this.selectedService = service;
      this.serviceForm.patchValue(service);
    } else {
      this.selectedService = null;
      this.serviceForm.reset();
    }
  }
  openDeptModal(dept?: Departement): void {
    this.isDepartementsModalOpen = true;
    this.isSubmitting=false;
    if (dept) {
      this.selectedDept = dept;
      this.departementsForm.patchValue(dept);
    } else {
      this.selectedDept = null;
      this.departementsForm.reset();
    }
  }

  openModal1(userId: number) {
    this.isModalOpen1 = true;
    // Vous pouvez enregistrer l'ID utilisateur si nécessaire
    console.log('Utilisateur à réinitialiser :', userId);
  }
  closeModal1() {
    this.isModalOpen1 = false;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedUser = null;
    this.userForm.reset();
  }

  closeServiceModal(): void {
    this.isServiceModalOpen = false;
    this.selectedService = null;
    this.serviceForm.reset();
  }
  closeDeptModal(): void {
    this.isDepartementsModalOpen = false;
    this.selectedDept = null;
    this.departementsForm.reset();
  }


  // Marquer tous les champs du formulaire comme touchés
  markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }


  submitPassword() {
    if (this.resetPasswordForm.valid) {
      const newPassword = this.resetPasswordForm.value.newPassword;
      console.log('Nouveau mot de passe soumis :', newPassword);
      // Ajoutez ici la logique pour appeler votre API ou gérer la réinitialisation
      this.closeModal();
    }
  }



  // Supprimer un utilisateur
  deleteUser(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
      this.adminService.deleteUser(id).subscribe(
        () => {
          this.users = this.users.filter(user => user.id !== id);
        },
        (error) => {
          console.error('Erreur lors de la suppression de l\'utilisateur:', error);
        }
      );
    }
  }

  getUserStatusClass(user: User): string {
    return user.serviceName ? 'text-green-500' : 'text-red-500';
  }




  saveUser(): void {
    const usernameControl = this.userForm.get('username');
    console.log('Username valide ?', usernameControl?.valid);
    console.log('Erreurs du champ username :', usernameControl?.errors);

    if (this.userForm.valid) {
      const formData = this.userForm.value;
      const payload = {
        "appUser": {
          "username":formData.username,
          "email": formData.email,
          "firstName": formData.firstName,
          "name": formData.name,
          "password": formData.password,

        },
        "role":{
        "name":formData.roleName,
      },
        "service":{
          "id":formData.serviceName,
        },
      }
      console.log('Données préparées pour l\'API :', JSON.stringify(payload));

      // Appel au service pour envoyer les données à l'API
      this.adminService.saveUser(payload).subscribe(
        (response) => {
          console.log('Utilisateur sauvegardé avec succès:', response);
          // Optionnel: Réinitialiser le formulaire ou rediriger l'utilisateur
          this.userForm.reset();
          this.closeModal();
          this.fetchUsers();
          },
        (error) => {
          console.error('Erreur lors de la sauvegarde de l\'utilisateur :', error);
        }
      );
    }else {
      console.log('formulaire invalide')
    }





  }






  saveService(): void {
    console.log(this.serviceForm.valid);
    console.log(this.serviceForm.value);

    if (this.serviceForm.valid) {
      const serviceData = this.serviceForm.value;

      if (this.selectedService) {
        // Modification d'un service existant
        const updatedService = { ...this.selectedService, ...serviceData };
        this.adminService.createService(updatedService).subscribe(
          () => {
            this.fetchServices();
            this.closeServiceModal();
          },
          error => {
            console.error('Erreur lors de la mise à jour du service:', error);
          }
        );
      } else {
        // Ajout d'un nouveau service
        this.adminService.createService(serviceData).subscribe(
          () => {
            this.fetchServices();
            this.closeServiceModal();
          },
          error => {
            console.error('Erreur lors de la création du service:', error);
          }
        );
      }
    }
  }

  saveDept(): void {
    console.log(this.departementsForm.valid);
    console.log(this.departementsForm.value);

    if (this.departementsForm.valid) {
      const DeptData = this.departementsForm.value;

      if (this.selectedDept) {
        this.isSubmitting=true;
        // Modification d'un service existant
        const updatedDept = { ...this.selectedDept, ...DeptData };
        this.adminService.updateDept(updatedDept).subscribe(
          () => {
            this.fetchDepartement();
            this.closeDeptModal();
          },
          error => {
            console.error('Erreur lors de la mise à jour du service:', error);
          }
        );
      } else {
        // Ajout d'un nouveau service
        this.adminService.createDept(DeptData).subscribe(
          () => {
            this.fetchDepartement();
            this.closeDeptModal();
          },
          error => {
            console.error('Erreur lors de la création du service:', error);
          }
        );
      }
    }
  }



  deleteService(serviceId: number): void {
    this.adminService.deleteService(serviceId).subscribe({
      next: () => {
        this.services = this.services.filter(service => service.id !== serviceId);
        console.log('Service supprimé:', serviceId);
      },
      error: (err) => {
        console.error('Erreur lors de la suppression du service:', err);
      }
    });
  }
  deleteDept(DeptId: number): void {
    this.adminService.deleteDept(DeptId).subscribe({
      next: () => {
        this.depts = this.depts.filter(dept => dept.id !== DeptId);
        console.log('Service supprimé:', DeptId);
      },
      error: (err) => {
        console.error('Erreur lors de la suppression du service:', err);
      }
    });
  }
  openRoleModal(role?: any) {
    console.log('Rôle modal ouvert', role);
  }


  get firstNameError(): string {
    const control = this.userForm.get('firstName');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le prénom est requis';
      if (control.errors['minlength']) return 'Le prénom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le prénom ne doit pas contenir de caractere speciaux inutiles';
    }
    return '';
  }

  get lastNameError(): string {
    const control = this.userForm.get('name');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom est requis';
      if (control.errors['minlength']) return 'Le nom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  get roleError():string {
    const control = this.userForm.get('role');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le role de l\'utilisateur est requis';
    }
    return '';
  }

  get usernameError(): string {
    const control = this.userForm.get('username');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom d\'utilisateur est requis';
      if (control.errors['minlength']) return 'Le nom d\'utilisateur doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom d\'utilisateur ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  get emailError(): string {
    const control = this.userForm.get('email');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'L\'email est requis';
      if (control.errors['email']) return 'Format d\'email invalide';
    }
    return '';
  }

  get passwordError(): string {
    const control = this.userForm.get('password');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le mot de passe est requis';
      if (control.errors['minlength']) return 'Le mot de passe doit contenir au moins 8 caractères';
      if (control.errors['pattern']) return 'Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial';
    }
    return '';
  }

  get serviceNameError(): string {
    const control = this.serviceForm.get('name');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom est requis';
      if (control.errors['minlength']) return 'Le nom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  resetPassword(id: number) {

  }
}
