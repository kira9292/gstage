import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ai-agent',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="ai-agent-container" [class.open]="isOpen">
      <button class="ai-agent-button" (click)="toggleAgent()">
        <i class="fas fa-robot"></i>
      </button>
      
      <div class="ai-agent-interface">
        <div class="ai-agent-header">
          <h3>AI Assistant</h3>
          <button (click)="toggleAgent()">×</button>
        </div>
        <div class="ai-agent-content">
          <!-- Contenu de l'interface AI -->
          <div class="messages">
            <!-- Messages ici -->
          </div>
          <div class="input-area">
            <input type="text" placeholder="Posez votre question..." [(ngModel)]="userInput">
            <button (click)="sendMessage()">Envoyer</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./ai-agent.component.scss']
})
export class AIAgentComponent {
  isOpen = false;
  userInput = '';

  toggleAgent() {
    this.isOpen = !this.isOpen;
  }

  sendMessage() {
    // Implémentation de l'envoi du message
    console.log('Message envoyé:', this.userInput);
    this.userInput = '';
  }
} 