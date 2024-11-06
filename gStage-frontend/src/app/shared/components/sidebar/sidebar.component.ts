import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NavigationService } from '../../../services/navigation.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    RouterLink, 
    CommonModule, 
    ReactiveFormsModule, 
    RouterLinkActive
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})


export class SidebarComponent {
  @Input() userType: 'intern' | 'candidate' = 'intern';
  activeTab: string = 'dashboard';
  constructor(private navigationService: NavigationService) {}

  ngOnInit() {
    this.navigationService.activeTab$.subscribe(tab => {
      this.activeTab = tab;
    });
  }

  logout() {
    // Implement logout logic here
    console.log('Logout clicked');
  }
  @Input() isSidebarOpen: boolean = false;
  @Output() sidebarClose = new EventEmitter<void>();
  
  closeSidebar() {
    this.sidebarClose.emit();
  }
}
