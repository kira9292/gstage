import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NavigationService } from '../services/navigation.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink, 
    CommonModule, 
    ReactiveFormsModule, 
    RouterLinkActive
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})


export class NavbarComponent {
  @Input() userType: 'intern' | 'candidate' = 'intern';
  activeTab: string = 'dashboard';
  constructor(private navigationService: NavigationService) {}

  ngOnInit() {
    this.navigationService.activeTab$.subscribe(tab => {
      this.activeTab = tab;
    });
  }
  @Output() menuToggle = new EventEmitter<void>(); // Émet un événement pour toggler le menu

  toggleMenu() {
    this.menuToggle.emit(); // Émettre un événement pour signaler que le menu doit être togglé
  }
}
