import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy, HostListener, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClientModule } from '@angular/common/http';
import { Subject, BehaviorSubject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { ChatAssistantService } from '../../services/chat-assistant.service';
import { ChatMessage, UserRole, SuggestedQuestion } from '../../models/chat-message';
import { trigger, transition, style, animate } from '@angular/animations';

// Material Design Modules
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import {TextFieldModule} from "@angular/cdk/text-field";

// Types d'animations pour réutilisation
const ANIMATIONS = {
  fadeInOut: trigger('fadeInOut', [
    transition(':enter', [
      style({ opacity: 0, transform: 'translateY(40px) scale(0.95)' }),
      animate('300ms cubic-bezier(0.34, 1.56, 0.64, 1)', style({ opacity: 1, transform: 'translateY(0) scale(1)' }))
    ]),
    transition(':leave', [
      animate('200ms ease-out', style({ opacity: 0, transform: 'translateY(20px) scale(0.95)' }))
    ])
  ]),
  fadeIn: trigger('fadeIn', [
    transition(':enter', [
      style({ opacity: 0, transform: 'translateY(10px)' }),
      animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
    ])
  ]),
  messageAnimation: trigger('messageAnimation', [
    transition(':enter', [
      style({ opacity: 0, transform: 'translateY(10px)' }),
      animate('200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
    ])
  ]),
  slideInUp: trigger('slideInUp', [
    transition(':enter', [
      style({ opacity: 0, transform: 'translateY(15px)' }),
      animate('250ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
    ]),
    transition(':leave', [
      animate('200ms ease-in', style({ opacity: 0, transform: 'translateY(15px)' }))
    ])
  ])
};

// Configuration constantes
const CONFIG = {
  MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
  SUPPORTED_FILE_TYPES: ['image/jpeg', 'image/png', 'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'],
  DEFAULT_QUICK_REPLIES: ['D\'accord', 'Merci', 'Je comprends', 'Plus de détails', 'Autre question'],
  MAX_CONTEXT_MESSAGES: 5,
  QUICK_REPLIES_DELAY: 1000,
  QUICK_REPLIES_HIDE_DELAY: 30000,
  SCROLL_DELAY: 100,
  TYPING_SIMULATION_DELAY: 50 // ms par caractère pour simuler la frappe
};

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
    MatProgressBarModule,
    TextFieldModule
  ],
  templateUrl: './chat-assistant.component.html',
  styleUrls: ['./chat-assistant.component.scss'],
  animations: Object.values(ANIMATIONS)
})
export class ChatAssistantComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('messagesContainer') private messagesContainer: ElementRef | undefined;
  @ViewChild('chatBody') private chatBody: ElementRef | undefined;

  // État du chat
  isOpen = false;
  messages: ChatMessage[] = [];
  isTyping = false;
  currentMessage = '';
  userRole: UserRole = 'candidate';
  suggestedQuestions: SuggestedQuestion[] = [];
  isMobile = false;
  hasNewMessage = false;
  showQuickReplies = false;

  // État d'upload
  isUploading = false;
  uploadProgress = 0;

  // Quickreplies
  quickReplies: string[] = [];

  // Contrôle des cycles de vie
  private destroy$ = new Subject<void>();
  private timeouts: Record<string, any> = {
    scroll: null,
    quickReplies: null,
    quickRepliesHide: null,
    welcomeMessage: null
  };


  // État interne
  private messageHistory: string[] = [];
  private lastUserMessageTime: Date | null = null;
  private readonly uploadState$ = new BehaviorSubject<boolean>(false);
  private welcomeMessageSent = false;

  constructor(
    private chatService: ChatAssistantService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar
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
    this.initializeSubscriptions();
    this.initializeState();
  }

  private initializeSubscriptions(): void {
    this.chatService.messages$
      .pipe(
        takeUntil(this.destroy$),
        tap(messages => {
          const wasClosed = !this.isOpen;
          this.messages = messages;
          if (wasClosed && messages.length > 0) {
            this.hasNewMessage = true;
          }
          this.scheduleScrollToBottom();
          this.handleQuickRepliesDisplay();
        })
      )
      .subscribe();

    this.chatService.typing$
      .pipe(
        takeUntil(this.destroy$),
        tap(isTyping => {
          this.isTyping = isTyping;
          if (isTyping) {
            this.scheduleScrollToBottom();
            this.hideQuickReplies();
          }
        })
      )
      .subscribe();

    this.chatService.userRole$
      .pipe(
        takeUntil(this.destroy$),
        tap(role => {
          this.userRole = role;
          this.updateSuggestedQuestions();
        })
      )
      .subscribe();
  }

  private initializeState(): void {
    this.quickReplies = [...CONFIG.DEFAULT_QUICK_REPLIES];
    this.updateSuggestedQuestions();
    this.checkRecentActivity();

    if (this.chatService.hasRecentConversation()) {
      this.isOpen = true;
      this.welcomeMessageSent = true; // Si conversation existante, pas de message de bienvenue
    } else {
      // Envoyer un message de bienvenue automatique uniquement s'il n'y a pas déjà de conversation
      this.sendWelcomeMessage();
    }
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.clearAllTimeouts();
  }

  // Gestion des timeouts
  private clearTimeout(key: string): void {
    if (this.timeouts[key]) {
      clearTimeout(this.timeouts[key]);
      this.timeouts[key] = null;
    }
  }

  private clearAllTimeouts(): void {
    Object.keys(this.timeouts).forEach(key => this.clearTimeout(key));
  }

  private setTimout(key: string, fn: () => void, delay: number): void {
    this.clearTimeout(key);
    this.timeouts[key] = setTimeout(fn, delay);
  }

  // Gestion du chat
  toggleChat(): void {
    this.isOpen = !this.isOpen;
    if (this.isOpen) {
      this.hasNewMessage = false;
      this.setTimout('scroll', () => this.scrollToBottom(), 300);

      // Envoyer le message de bienvenue si c'est la première ouverture
      if (this.messages.length === 0 && !this.welcomeMessageSent) {
        this.sendWelcomeMessage();
      }
    }
  }

  onEnterPress(event: Event): void {
    const keyboardEvent = event as KeyboardEvent;

    if (keyboardEvent.key === 'Enter' && !keyboardEvent.shiftKey) {
      keyboardEvent.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage(predefinedMessage?: string): void {
    const messageToSend = predefinedMessage || this.currentMessage;
    if (messageToSend.trim()) {
      this.hideQuickReplies();
      this.updateMessageContext(messageToSend);
      this.chatService.sendMessage(messageToSend);
      this.currentMessage = '';
      this.scheduleScrollToBottom();
    }
  }

  sendQuickReply(reply: string): void {
    this.sendMessage(reply);
  }

  private updateMessageContext(message: string): void {
    this.lastUserMessageTime = new Date();
    localStorage.setItem('last_chat_activity', this.lastUserMessageTime.toISOString());

    this.messageHistory.push(message);
    if (this.messageHistory.length > CONFIG.MAX_CONTEXT_MESSAGES) {
      this.messageHistory.shift();
    }
  }

  private sendWelcomeMessage(): void {
    if (this.welcomeMessageSent) return;

    this.welcomeMessageSent = true;
    this.chatService.setTyping(true);

    const welcomeMessage = this.getPersonalizedWelcomeMessage();

    // Simuler un délai de frappe pour que le message semble plus naturel
    const typingDelay = Math.min(1500, welcomeMessage.length * CONFIG.TYPING_SIMULATION_DELAY);

    this.setTimout('welcomeMessage', () => {
      this.chatService.addAssistantMessage(welcomeMessage);
      this.chatService.setTyping(false);
      this.scheduleScrollToBottom();
    }, typingDelay);
  }


  getUserRoleDescription(): string {
    return this.chatService.getRoleDescription(this.userRole);
  }

  shouldShowSuggestions(): boolean {
    // Ne montrer les suggestions que s'il y a des messages et moins de 3
    return this.messages.length > 0 && this.messages.length < 3;
  }

  clearConversation(): void {
    this.chatService.clearHistory();
    this.welcomeMessageSent = false;
  }

  deleteMessage(message: ChatMessage): void {
    this.chatService.deleteMessage(message);
  }

  updateSuggestedQuestions(): void {
    this.suggestedQuestions = this.chatService.getSuggestedQuestions()
      .map(q => {
        const question = {...q};
        if (question.text.length > 45) {
          question.text = question.text.substring(0, 42) + '...';
        }
        return question;
      });
  }

  // Gestion du défilement
  private scheduleScrollToBottom(): void {
    this.setTimout('scroll', () => this.scrollToBottom(), CONFIG.SCROLL_DELAY);
  }

  private scrollToBottom(): void {
    if (this.chatBody) {
      const element = this.chatBody.nativeElement;
      element.scrollTop = element.scrollHeight;
    }
  }

  @HostListener('scroll', ['$event'])
  onScroll(): void {
    if (this.chatBody && this.chatBody.nativeElement.scrollTop > 50) {
      this.hideQuickReplies();
    }
  }

  // Gestion des fichiers
  handleFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];

      if (!this.validateFile(file)) {
        return;
      }

      this.startFileUpload(file);
    }
  }

  private validateFile(file: File): boolean {
    if (file.size > CONFIG.MAX_FILE_SIZE) {
      this.showError('Le fichier est trop volumineux. Taille maximale: 5MB');
      return false;
    }

    if (!CONFIG.SUPPORTED_FILE_TYPES.includes(file.type)) {
      this.showError('Type de fichier non supporté. Formats acceptés: JPG, PNG, PDF, DOC, DOCX');
      return false;
    }

    return true;
  }

  private startFileUpload(file: File): void {
    this.isUploading = true;
    this.uploadProgress = 0;
    this.uploadState$.next(true);

    const uploadSpeed = Math.max(100, Math.min(500, file.size / 10000));
    const interval = setInterval(() => {
      this.uploadProgress += 5;
      if (this.uploadProgress >= 100) {
        clearInterval(interval);
        this.completeFileUpload(file);
      }
    }, uploadSpeed);
  }



  private completeFileUpload(file: File): void {
    this.isUploading = false;
    this.uploadProgress = 0;
    this.uploadState$.next(false);
    this.chatService.sendMessage(`Fichier "${file.name}" envoyé avec succès`);
    this.scheduleScrollToBottom();
  }

  private getFileIcon(file: File): string {
    const fileExtension = file.name.split('.').pop()?.toLowerCase();

    switch (fileExtension) {
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
        return 'image';  // Utilise une icône d'image
      case 'pdf':
        return 'picture_as_pdf';  // Icône PDF
      case 'doc':
      case 'docx':
        return 'article';  // Icône Word
      case 'xls':
      case 'xlsx':
        return 'table_chart';  // Icône Excel
      case 'ppt':
      case 'pptx':
        return 'presentation';  // Icône PowerPoint
      default:
        return 'attach_file';  // Icône générique pour autres fichiers
    }
  }


  // Gestion des réponses rapides
  private handleQuickRepliesDisplay(): void {
    if (!this.isOpen || this.messages.length === 0) {
      this.hideQuickReplies();
      return;
    }

    const lastMessage = this.messages[this.messages.length - 1];
    if (lastMessage?.sender === 'assistant') {
      this.scheduleQuickRepliesDisplay();
    } else {
      this.hideQuickReplies();
    }
  }

  private scheduleQuickRepliesDisplay(): void {
    this.setTimout('quickReplies', () => {
      this.showQuickReplies = true;
      this.updateQuickRepliesContent();
      this.scheduleQuickRepliesHide();
      this.scheduleScrollToBottom();
    }, CONFIG.QUICK_REPLIES_DELAY);
  }

  private scheduleQuickRepliesHide(): void {
    this.setTimout('quickRepliesHide', () => {
      if (!this.isTyping && this.showQuickReplies) {
        this.hideQuickReplies();
      }
    }, CONFIG.QUICK_REPLIES_HIDE_DELAY);
  }

  hideQuickReplies(): void {
    this.showQuickReplies = false;
    this.clearTimeout('quickRepliesHide');
  }

  private updateQuickRepliesContent(): void {
    const lastMessage = this.messages[this.messages.length - 1];
    if (lastMessage && lastMessage.sender === 'assistant') {
      this.quickReplies = this.getContextualQuickReplies(lastMessage.content);
    }
  }

  private getContextualQuickReplies(message: string): string[] {
    const normalizedMessage = message.toLowerCase();
    let replies: string[] = [];

    if (normalizedMessage.includes('postuler') || normalizedMessage.includes('candidature')) {
      replies.push('Comment préparer mon CV ?', 'Documents nécessaires ?', 'Quelle procédure ?');
    } else if (normalizedMessage.includes('entretien')) {
      replies.push('Comment me préparer ?', 'Questions à poser ?', 'Comment me présenter ?');
    } else if (normalizedMessage.includes('convention') || normalizedMessage.includes('contrat')) {
      replies.push('Quels délais ?', 'Qui doit signer ?', 'Quelles conditions ?');
    }

    // Ajouter les réponses par défaut
    replies.push('Merci', 'D\'accord');

    // Tronquer les réponses trop longues
    return replies.map(reply =>
      reply.length > 30 ? reply.substring(0, 27) + '...' : reply
    );
  }




  // Gestion des messages personnalisés
  private getPersonalizedWelcomeMessage(): string {
    const timeOfDay = new Date().getHours();
    let greeting = 'Bonjour';

    if (timeOfDay < 12) {
      greeting = 'Bonjour';
    } else if (timeOfDay < 18) {
      greeting = 'Bon après-midi';
    } else {
      greeting = 'Bonsoir';
    }

    return `${greeting} ! Je suis l'assistant GStage. Comment puis-je vous aider avec ${this.getUserRoleDescription()} ?`;
  }

  private checkRecentActivity(): void {
    const lastActivity = localStorage.getItem('last_chat_activity');
    if (lastActivity) {
      const lastActivityDate = new Date(lastActivity);
      const hoursSinceLastActivity = (new Date().getTime() - lastActivityDate.getTime()) / (1000 * 60 * 60);

      if (hoursSinceLastActivity <= 24) {
        this.welcomeMessageSent = true; // Ne pas renvoyer un message de bienvenue si activité récente
      }
    }
  }

  // Fonctions utilitaires
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

  private showError(message: string): void {
    this.snackBar.open(message, 'OK', {
      duration: 3000,
      panelClass: ['error-snackbar']
    });
  }
}
