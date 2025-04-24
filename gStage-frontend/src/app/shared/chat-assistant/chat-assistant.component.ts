// src/app/shared/chat-assistant/chat-assistant.component.ts
import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable, Subject, of } from 'rxjs';
import { catchError, debounceTime, distinctUntilChanged, switchMap, takeUntil } from 'rxjs/operators';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { ChatAssistantService } from '../../services/chat-assistant.service';
import { ChatMessage, UserRole, SuggestedQuestion } from '../../models/chat-message';
import { trigger, transition, style, animate, state } from '@angular/animations';

// Material Design Modules
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-chat-assistant',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatDialogModule,
    MatTooltipModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    HttpClientModule,
    MatSelectModule,
    MatMenuModule,
    MatFormFieldModule,
    MatProgressBarModule
  ],
  templateUrl: './chat-assistant.component.html',
  styleUrls: ['./chat-assistant.component.scss'],
  animations: [
      trigger('fadeInOut', [
        transition(':enter', [
          style({ opacity: 0, transform: 'translateY(40px) scale(0.95)' }),
          animate('300ms cubic-bezier(0.34, 1.56, 0.64, 1)', style({ opacity: 1, transform: 'translateY(0) scale(1)' }))
        ]),
        transition(':leave', [
          animate('200ms ease-out', style({ opacity: 0, transform: 'translateY(20px) scale(0.95)' }))
        ])
      ]),
      trigger('fadeIn', [
        transition(':enter', [
          style({ opacity: 0, transform: 'translateY(10px)' }),
          animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
        ])
      ]),
      trigger('messageAnimation', [
        transition(':enter', [
          style({ opacity: 0, transform: 'translateY(10px)' }),
          animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
        ])
      ])
    ]
  })
export class ChatAssistantComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('messagesContainer') private messagesContainer: ElementRef | undefined;
  @ViewChild('chatBody') private chatBody: ElementRef | undefined;

  isOpen = false;
  messages: ChatMessage[] = [];
  isTyping = false;
  currentMessage = '';
  userRole: UserRole = 'candidate';
  suggestedQuestions: SuggestedQuestion[] = [];
  isMobile: boolean = false;

  private destroy$ = new Subject<void>();
  private scrollTimeout: any;

  // Nouvelles propriétés
  quickReplies: string[] = [];
  isUploading = false;
  uploadProgress = 0;
  selectedFile: File | null = null;
  maxFileSize = 5 * 1024 * 1024; // 5MB
  supportedFileTypes = ['image/jpeg', 'image/png', 'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];

  constructor(
    private chatService: ChatAssistantService,
    private dialog: MatDialog
  ) {
    this.checkScreenSize();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  checkScreenSize() {
    this.isMobile = window.innerWidth < 768;
  }

  ngOnInit(): void {
    // S'abonner aux messages
    this.chatService.messages$
      .pipe(takeUntil(this.destroy$))
      .subscribe(messages => {
        this.messages = messages;
        this.scheduleScrollToBottom();
      });

    // S'abonner à l'état de frappe
    this.chatService.typing$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isTyping => {
        this.isTyping = isTyping;
        if (isTyping) {
          this.scheduleScrollToBottom();
        }
      });

    // S'abonner au rôle utilisateur
    this.chatService.userRole$
      .pipe(takeUntil(this.destroy$))
      .subscribe(role => {
        this.userRole = role;
        this.updateSuggestedQuestions();
      });

    // Initialiser les questions suggérées
    this.updateSuggestedQuestions();

    // Initialiser les réponses rapides
    this.initializeQuickReplies();

    // Vérifier s'il y a déjà des messages et ouvrir automatiquement
    if (this.chatService.hasRecentConversation()) {
      this.isOpen = true;
    }
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.scrollTimeout) {
      clearTimeout(this.scrollTimeout);
    }
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      setTimeout(() => this.scrollToBottom(), 300);
    }
  }

  sendMessage(predefinedMessage?: string): void {
    const messageToSend = predefinedMessage || this.currentMessage;
    if (messageToSend.trim()) {
      this.chatService.sendMessage(messageToSend);
      this.currentMessage = '';
    }
  }

  changeUserRole(role: UserRole): void {
    this.chatService.setUserRole(role);
  }

  getUserRoleDescription(): string {
    switch (this.userRole) {
      case 'candidate': return 'votre candidature et le processus de postulation';
      case 'evaluator': return 'l\'évaluation des candidats et le workflow de sélection';
      case 'hr': return 'la gestion administrative des stages';
      default: return 'le processus de stage';
    }
  }

  shouldShowSuggestions(): boolean {
    // Montrer les suggestions s'il n'y a pas ou peu de messages
    return this.messages.length < 3;
  }

  clearConversation(): void {
    this.chatService.clearHistory();
  }

  deleteMessage(message: ChatMessage): void {
    this.chatService.deleteMessage(message);
  }

  private updateSuggestedQuestions(): void {
    this.suggestedQuestions = this.chatService.getSuggestedQuestions();
  }

  private scheduleScrollToBottom(): void {
    if (this.scrollTimeout) {
      clearTimeout(this.scrollTimeout);
    }
    this.scrollTimeout = setTimeout(() => this.scrollToBottom(), 100);
  }

  private scrollToBottom(): void {
    if (this.chatBody) {
      const element = this.chatBody.nativeElement;
      element.scrollTop = element.scrollHeight;
    }
  }

  // Nouvelle méthode pour initialiser les réponses rapides
  private initializeQuickReplies(): void {
    this.quickReplies = [
      'D\'accord',
      'Merci',
      'Je comprends',
      'Plus de détails',
      'Autre question'
    ];
  }

  // Nouvelle méthode pour gérer l'upload de fichiers
  handleFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      
      // Vérifier la taille du fichier
      if (file.size > this.maxFileSize) {
        this.chatService.sendMessage('Le fichier est trop volumineux. Taille maximale: 5MB');
        return;
      }

      // Vérifier le type de fichier
      if (!this.supportedFileTypes.includes(file.type)) {
        this.chatService.sendMessage('Type de fichier non supporté. Formats acceptés: JPG, PNG, PDF, DOC, DOCX');
        return;
      }

      this.selectedFile = file;
      this.isUploading = true;
      this.uploadProgress = 0;
      
      // Simuler la progression de l'upload
      const interval = setInterval(() => {
        this.uploadProgress += 10;
        if (this.uploadProgress >= 100) {
          clearInterval(interval);
          this.isUploading = false;
          this.uploadProgress = 0;
          this.chatService.sendMessage(`Fichier "${file.name}" envoyé avec succès`);
          this.selectedFile = null;
          // Réinitialiser l'input file
          input.value = '';
        }
      }, 200);
    }
  }

  // Nouvelle méthode pour envoyer une réponse rapide
  sendQuickReply(reply: string): void {
    this.sendMessage(reply);
  }

  // Nouvelle méthode pour formater les messages longs
  formatLongMessage(message: string): string {
    if (message.length > 500) {
      return message.substring(0, 500) + '...';
    }
    return message;
  }

  confirmClearConversation(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Effacer la conversation',
        message: 'Êtes-vous sûr de vouloir effacer toute la conversation ? Cette action est irréversible.',
        confirmText: 'Effacer',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.clearConversation();
      }
    });
  }
}
