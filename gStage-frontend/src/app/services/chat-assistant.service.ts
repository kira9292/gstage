// src/app/services/chat-assistant.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ChatMessage, UserRole, SuggestedQuestion } from '../models/chat-message';
import { v4 as uuidv4 } from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class ChatAssistantService {
  private readonly storageKey = 'gstage_chat_history';
  private readonly roleStorageKey = 'gstage_user_role';
  private readonly lastChatTimeKey = 'gstage_last_chat_time';

  private messagesSubject = new BehaviorSubject<ChatMessage[]>([]);
  private userRoleSubject = new BehaviorSubject<UserRole>('candidate');

  public messages$ = this.messagesSubject.asObservable();
  public userRole$ = this.userRoleSubject.asObservable();

  private isTyping = false;
  private typingSubject = new BehaviorSubject<boolean>(false);
  public typing$ = this.typingSubject.asObservable();

  constructor() {
    this.loadHistory();
    this.loadUserRole();
  }

  // Charger l'historique depuis localStorage
  private loadHistory(): void {
    const savedHistory = localStorage.getItem(this.storageKey);
    if (savedHistory) {
      try {
        const parsedHistory = JSON.parse(savedHistory);
        // Convertir les timestamps string en objets Date
        const messages = parsedHistory.map((msg: any) => ({
          ...msg,
          timestamp: new Date(msg.timestamp)
        }));
        this.messagesSubject.next(messages);
      } catch (e) {
        console.error('Error loading chat history', e);
      }
    }
  }

  // Charger le rôle utilisateur depuis localStorage
  private loadUserRole(): void {
    const savedRole = localStorage.getItem(this.roleStorageKey) as UserRole;
    if (savedRole) {
      this.userRoleSubject.next(savedRole);
    }
  }

  // Sauvegarder l'historique dans localStorage
  private saveHistory(): void {
    localStorage.setItem(this.storageKey, JSON.stringify(this.messagesSubject.value));
    // Enregistrer la dernière heure d'interaction
    localStorage.setItem(this.lastChatTimeKey, new Date().toISOString());
  }

  // Vérifier si une conversation récente existe (moins de 24h)
  hasRecentConversation(): boolean {
    const lastChatTime = localStorage.getItem(this.lastChatTimeKey);
    if (!lastChatTime) return false;

    const lastTime = new Date(lastChatTime).getTime();
    const currentTime = new Date().getTime();
    const hoursDiff = (currentTime - lastTime) / (1000 * 60 * 60);

    return this.messagesSubject.value.length > 0 && hoursDiff < 24;
  }

  // Changer le rôle utilisateur
  setUserRole(role: UserRole): void {
    if (this.userRoleSubject.value !== role) {
      this.userRoleSubject.next(role);
      localStorage.setItem(this.roleStorageKey, role);

      // Ajouter un message système pour indiquer le changement
      if (this.messagesSubject.value.length > 0) {
        const systemMessage: ChatMessage = {
          id: uuidv4(),
          content: `Mode changé pour "${role === 'candidate' ? 'Candidat' : role === 'evaluator' ? 'Évaluateur' : 'RH'}". Comment puis-je vous aider avec ${this.getRoleDescription(role)} ?`,
          sender: 'assistant',
          timestamp: new Date()
        };

        const currentMessages = [...this.messagesSubject.value, systemMessage];
        this.messagesSubject.next(currentMessages);
        this.saveHistory();
      }
    }
  }

  // Description texte pour chaque rôle
  private getRoleDescription(role: UserRole): string {
    switch (role) {
      case 'candidate': return 'votre candidature et le processus de postulation';
      case 'evaluator': return 'l\'évaluation des candidats et le workflow de sélection';
      case 'hr': return 'la gestion administrative des stages';
      default: return 'le processus de stage';
    }
  }

  // Envoyer un message utilisateur avec un meilleur traitement
  sendMessage(content: string): void {
    if (!content.trim()) return;

    const userMessage: ChatMessage = {
      id: uuidv4(),
      content: content.trim(),
      sender: 'user',
      timestamp: new Date()
    };

    const currentMessages = [...this.messagesSubject.value, userMessage];
    this.messagesSubject.next(currentMessages);
    this.saveHistory();

    // Simuler la réponse avec un délai plus réaliste
    this.getAssistantResponse(content);
  }

  // Simuler une réponse de l'assistant avec un délai plus réaliste
  private getAssistantResponse(userQuery: string): void {
    this.typingSubject.next(true);

    // Le délai varie en fonction de la complexité (estimée par la longueur)
    // de la réponse pour un comportement plus naturel
    const response = this.generateResponse(userQuery);
    const responseDelay = Math.min(500 + response.length * 5, 3000);

    of(response).pipe(
      delay(responseDelay)
    ).subscribe(response => {
      const assistantMessage: ChatMessage = {
        id: uuidv4(),
        content: response,
        sender: 'assistant',
        timestamp: new Date()
      };

      const currentMessages = [...this.messagesSubject.value, assistantMessage];
      this.messagesSubject.next(currentMessages);
      this.saveHistory();
      this.typingSubject.next(false);
    });
  }

  // Générer une réponse avec plus de contexte et de précision
  private generateResponse(query: string): string {
    const normalizedQuery = query.toLowerCase();
    const currentRole = this.userRoleSubject.value;
    const messageHistory = this.messagesSubject.value;

    // Tenir compte du contexte de conversation
    const conversationContext = this.getConversationContext(messageHistory);

    // Réponses spécifiques selon le rôle avec plus de détails
    if (currentRole === 'candidate') {
      if (normalizedQuery.includes('postuler') || normalizedQuery.includes('candidature')) {
        return "Pour postuler sur GStage, suivez ces étapes :\n\n1. Créez un compte sur la plateforme si ce n'est pas déjà fait\n2. Complétez votre profil avec vos informations personnelles\n3. Dans l'onglet 'Postuler', sélectionnez les offres qui vous intéressent\n4. Joignez votre CV et lettre de motivation\n5. Soumettez votre candidature et suivez son statut dans 'Mes candidatures'";
      } else if (normalizedQuery.includes('cv') || normalizedQuery.includes('curriculum') || normalizedQuery.includes('lettre de motivation')) {
        return "Votre CV et lettre de motivation sont essentiels. Voici quelques conseils :\n\n- Adaptez votre CV à chaque offre en mettant en avant les compétences pertinentes\n- Pour la lettre de motivation, personnalisez-la pour l'entreprise et le poste\n- Utilisez des mots-clés du secteur et de l'offre\n- Limitez votre CV à 1-2 pages maximum\n- Vérifiez l'orthographe et la grammaire";
      } else if (normalizedQuery.includes('entretien') || normalizedQuery.includes('interview')) {
        return "Pour réussir votre entretien de stage :\n\n- Recherchez l'entreprise et le poste en profondeur\n- Préparez des exemples concrets de vos compétences\n- Prévoyez des questions pertinentes à poser\n- Arrivez à l'heure (ou 10 minutes en avance)\n- Habillez-vous de façon professionnelle\n- Montrez votre motivation et votre intérêt pour le poste";
      }
    } else if (currentRole === 'evaluator') {
      if (normalizedQuery.includes('critères') || normalizedQuery.includes('évaluation')) {
        return "Les critères d'évaluation recommandés pour les candidats sont :\n\n- Compétences techniques en adéquation avec le poste\n- Expériences pertinentes (projets, stages précédents)\n- Formation académique\n- Qualités personnelles et soft skills\n- Motivation et connaissance de l'entreprise\n- Potentiel d'intégration à l'équipe\n\nVous pouvez créer une grille d'évaluation personnalisée dans l'outil.";
      } else if (normalizedQuery.includes('entretien') || normalizedQuery.includes('interview')) {
        return "Pour conduire un entretien efficace :\n\n- Préparez vos questions à l'avance et structurez l'entretien\n- Commencez par mettre le candidat à l'aise\n- Posez des questions ouvertes et comportementales\n- Utilisez la méthode STAR (Situation, Tâche, Action, Résultat)\n- Laissez au candidat l'opportunité de poser ses questions\n- Prenez des notes pendant l'entretien\n- Utilisez la grille d'évaluation disponible sur GStage";
      } else if (normalizedQuery.includes('feedback') || normalizedQuery.includes('retour')) {
        return "Pour donner un feedback constructif :\n\n1. Remplissez la grille d'évaluation dans l'onglet 'Candidatures'\n2. Soyez spécifique et objectif dans vos commentaires\n3. Équilibrez points forts et axes d'amélioration\n4. Soutenez vos observations par des exemples concrets\n5. Soumettez votre évaluation via la plateforme\n\nTous les évaluateurs recevront une notification pour consulter vos commentaires.";
      }
    } else if (currentRole === 'hr') {
      if (normalizedQuery.includes('contrat') || normalizedQuery.includes('convention')) {
        return "Pour générer une convention de stage :\n\n1. Accédez à l'onglet 'Conventions' dans votre espace RH\n2. Sélectionnez le candidat accepté\n3. Vérifiez/complétez les informations (dates, horaires, gratification)\n4. Générez le document au format PDF\n5. Envoyez-le pour signature via la plateforme ou par email\n\nLe système conserve un historique de toutes les conventions émises.";
      } else if (normalizedQuery.includes('signature') || normalizedQuery.includes('signer')) {
        return "Le processus de signature des conventions sur GStage :\n\n1. Générez la convention complète depuis l'onglet 'Conventions'\n2. Utilisez l'option 'Envoyer pour signature' pour initier le processus\n3. Le stagiaire, le tuteur et l'établissement recevront un email avec un lien sécurisé\n4. Chaque partie peut signer électroniquement le document\n5. Vous pouvez suivre l'état des signatures dans le tableau de bord\n6. Une fois toutes les signatures obtenues, la convention finale est automatiquement envoyée à tous";
      } else if (normalizedQuery.includes('gratification') || normalizedQuery.includes('rémunération')) {
        return "Concernant la gratification des stagiaires :\n\n- La gratification minimale légale est obligatoire pour les stages de plus de 2 mois\n- Le montant horaire minimal est de 15% du plafond horaire de la sécurité sociale\n- Vous pouvez configurer les montants standard dans 'Paramètres'\n- La plateforme calcule automatiquement le montant total en fonction de la durée\n- Les avantages supplémentaires (tickets restaurant, transport) peuvent être ajoutés dans la section dédiée";
      }
    }

    // Réponses générales si aucune correspondance spécifique n'est trouvée
    if (normalizedQuery.includes('bonjour') || normalizedQuery.includes('salut') || normalizedQuery.includes('hello')) {
      return `Bonjour ! Je suis l'assistant GStage. Comment puis-je vous aider avec ${this.getRoleDescription(currentRole)} ?`;
    } else if (normalizedQuery.includes('merci')) {
      return "Je vous en prie ! N'hésitez pas si vous avez d'autres questions.";
    } else if (normalizedQuery.includes('au revoir') || normalizedQuery.includes('à bientôt')) {
      return "Au revoir ! Bonne continuation dans vos démarches. N'hésitez pas à revenir si vous avez d'autres questions.";
    }

    // Réponse générique en dernier recours
    return `Je comprends votre question sur "${query.substring(0, 30)}${query.length > 30 ? '...' : ''}". Pour obtenir une assistance plus précise concernant ${this.getRoleDescription(currentRole)}, pourriez-vous reformuler ou préciser votre demande ?`;
  }

  // Analyser le contexte de la conversation pour réponses plus pertinentes
  private getConversationContext(messageHistory: ChatMessage[]): string {
    // Extraire les 3 derniers messages pour le contexte
    const recentMessages = messageHistory.slice(-3);

    // Si moins de 3 messages, conversation nouvelle
    if (recentMessages.length < 2) {
      return 'nouvelle_conversation';
    }

    // Détecter une série de questions sur un même sujet
    const userMessages = recentMessages.filter(msg => msg.sender === 'user');
    const userQueries = userMessages.map(msg => msg.content.toLowerCase());

    if (userQueries.some(q => q.includes('postuler') || q.includes('candidature'))) {
      return 'process_candidature';
    } else if (userQueries.some(q => q.includes('entretien') || q.includes('interview'))) {
      return 'preparation_entretien';
    } else if (userQueries.some(q => q.includes('cv') || q.includes('lettre'))) {
      return 'documents_candidature';
    } else if (userQueries.some(q => q.includes('contrat') || q.includes('convention'))) {
      return 'documents_administratifs';
    }

    return 'general';
  }

  // Obtenir des questions suggérées selon le rôle
  getSuggestedQuestions(): SuggestedQuestion[] {
    const currentRole = this.userRoleSubject.value;
    const allSuggestions = [
      // Questions pour les candidats
      {
        text: 'Comment postuler à un stage ?',
        forRoles: ['candidate'] as UserRole[]
      },
      {
        text: 'Comment préparer mon CV ?',
        forRoles: ['candidate'] as UserRole[]
      },
      {
        text: 'Comment se préparer à un entretien ?',
        forRoles: ['candidate'] as UserRole[]
      },
      {
        text: 'Quels sont les délais de réponse ?',
        forRoles: ['candidate'] as UserRole[]
      },

      // Questions pour les évaluateurs
      {
        text: 'Quels critères utiliser pour évaluer ?',
        forRoles: ['evaluator'] as UserRole[]
      },
      {
        text: 'Comment organiser un entretien efficace ?',
        forRoles: ['evaluator'] as UserRole[]
      },
      {
        text: 'Comment donner un feedback constructif ?',
        forRoles: ['evaluator'] as UserRole[]
      },
      {
        text: 'Comment comparer plusieurs candidats ?',
        forRoles: ['evaluator'] as UserRole[]
      },

      // Questions pour les RH
      {
        text: 'Comment générer une convention de stage ?',
        forRoles: ['hr'] as UserRole[]
      },
      {
        text: 'Quel est le processus de signature ?',
        forRoles: ['hr'] as UserRole[]
      },
      {
        text: 'Comment calculer la gratification ?',
        forRoles: ['hr'] as UserRole[]
      },
      {
        text: 'Comment gérer les absences d\'un stagiaire ?',
        forRoles: ['hr'] as UserRole[]
      },

      // Questions communes à tous les rôles
      {
        text: 'Quelles sont les fonctionnalités principales ?',
        forRoles: ['candidate', 'evaluator', 'hr'] as UserRole[]
      }
    ];

    // Filtrer les questions pertinentes pour le rôle actuel
    return allSuggestions.filter(q => q.forRoles.includes(currentRole));
  }

  // Effacer l'historique des conversations
  clearHistory(): void {
    this.messagesSubject.next([]);
    localStorage.removeItem(this.storageKey);
    localStorage.removeItem(this.lastChatTimeKey);

    // Ajouter un message de bienvenue après nettoyage
    const welcomeMessage: ChatMessage = {
      id: uuidv4(),
      content: `Bonjour ! Je suis l'assistant GStage. Comment puis-je vous aider avec ${this.getRoleDescription(this.userRoleSubject.value)} ?`,
      sender: 'assistant',
      timestamp: new Date()
    };

    this.messagesSubject.next([welcomeMessage]);
    this.saveHistory();
  }

  deleteMessage(message: ChatMessage): void {
    const currentMessages = this.messagesSubject.value.filter(msg => msg.id !== message.id);
    this.messagesSubject.next(currentMessages);
    this.saveHistory();
  }
}
