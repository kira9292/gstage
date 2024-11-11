// success-modal.component.ts

import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-success-modal',
  imports: [],
  standalone: true,
  templateUrl: './success-modal.component.html',
  styleUrl: './success-modal.component.scss',
})

export class SuccessModalComponent {
  @Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
