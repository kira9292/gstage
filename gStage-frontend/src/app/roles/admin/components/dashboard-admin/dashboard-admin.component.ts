import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from "@angular/common";
import {AdminServiceService} from "../../services/admin-service.service";

export interface User {
  id: number;
  username: string;
  email: string;
  name: string;
  role: string;
  enabled: boolean;
  accountNonExpired: boolean;
  credentialsNonExpired: boolean;
  accountNonLocked: boolean;
  authorities: string[];
}


@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './dashboard-admin.component.html',
  styleUrl: './dashboard-admin.component.scss'
})
export class DashboardAdminComponent implements OnInit {
  users: User[] = [];
  searchTerm: string = '';
  isModalOpen: boolean = false;
  selectedUser: User | null = null;

  constructor(private adminService: AdminServiceService) { } // Injecter le service

  ngOnInit() {
    this.fetchUsers(); // Appeler la méthode pour récupérer les données
  }

  // Méthode pour récupérer les utilisateurs depuis le service
  fetchUsers() {
    this.adminService.getAppUsers().subscribe(
      (data: User[]) => {
        this.users = data; // Mettre à jour la liste des utilisateurs
      },
      (error) => {
        console.error('Erreur lors du chargement des utilisateurs:', error);
      }
    );
  }

  // Méthode pour filtrer les utilisateurs
  filteredUsers() {
    return this.users.filter(user =>
      user.name.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  // Gestion du modal
  formData: any;
  openModal(user?: User) {
    this.selectedUser = user || null;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedUser = null;
  }

  saveUser() {
    if (this.selectedUser) {
      // Mise à jour utilisateur
    } else {
      // Ajouter un utilisateur
    }
    this.closeModal();
  }

  deleteUser(id: number) {
    this.users = this.users.filter(user => user.id !== id);
  }

}
