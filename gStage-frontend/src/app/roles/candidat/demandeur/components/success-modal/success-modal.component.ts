// success-modal.component.ts
import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-success-modal',
  template: `
    <div class="fixed inset-0 bg-gray-800 bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-lg p-8 w-96 animate-fadeIn">
        <div class="flex items-center justify-center mb-4">
          <div class="bg-green-100 rounded-full p-4">
            <i class="fas fa-check-circle text-green-500 text-4xl"></i>
          </div>
        </div>
        <h2 class="text-xl font-bold text-green-600 mb-4 text-center">Demande soumise avec succès !</h2>
        <p class="text-gray-700 mb-6 text-center">
          Votre demande de stage a été envoyée avec succès. Nous vous contacterons bientôt pour la suite.
        </p>
        <div class="flex justify-center">
          <button (click)="closeModal()"
                  class="bg-orange-500 text-white px-6 py-2 rounded-lg hover:bg-orange-600 transition-colors">
            OK
          </button>
        </div>
      </div>
    </div>
  `,
  imports: [],
  standalone: true,
  styles: [`
    .animate-fadeIn {
      animation: fadeIn 0.3s ease-in-out;
    }
    @keyframes fadeIn {
      from { opacity: 0; transform: scale(0.95); }
      to { opacity: 1; transform: scale(1); }
    }
  `]
})
export class SuccessModalComponent {
  @Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
