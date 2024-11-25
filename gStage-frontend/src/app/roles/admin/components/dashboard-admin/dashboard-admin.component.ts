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
  activeTab: string = 'users';



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
      username: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/)
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
  }

  ngOnInit(): void {
    this.fetchUsers();
    this.fetchServices();
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


  closeModal(): void {
    this.isModalOpen = false;
    this.selectedUser = null;
    this.userForm.reset();
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

  openServiceModal(service?: any) {
    this.selectedService = service ? { ...service } : null;
    this.isModalOpen = true;
  }


  selectedService: any = null;

  deleteService(serviceId: number) {
    this.services = this.services.filter(service => service.id !== serviceId);
    console.log('Service supprimé:', serviceId);
  }
  openRoleModal(role?: any) {
    console.log('Rôle modal ouvert', role);
  }

}
