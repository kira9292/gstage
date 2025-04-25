import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ChatMessage, UserRole, SuggestedQuestion } from '../models/chat-message';
import { v4 as uuidv4 } from 'uuid';

@Injectable({
  providedIn: 'root'
})
export class ChatAssistantTempService {
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

  private cvAnalysisInProgress = false;
  private cvAnalysisResults: any = null;
  private conversationTopics = new Set<string>();
  private userInterests: string[] = [];
  private lastInteractionTime: Date | null = null;
  private conversationMood: 'neutral' | 'positive' | 'negative' = 'neutral';
  private userEngagementLevel = 0;

  constructor() {
    this.loadHistory();
    this.loadUserRole();
    this.initializeConversationTracking();
  }

  private initializeConversationTracking(): void {
    this.lastInteractionTime = new Date();
    setInterval(() => this.checkUserEngagement(), 60000);
  }

  private checkUserEngagement(): void {
    if (this.lastInteractionTime) {
      const minutesSinceLastInteraction = (new Date().getTime() - this.lastInteractionTime.getTime()) / (1000 * 60);
      if (minutesSinceLastInteraction > 5) {
        this.userEngagementLevel = Math.max(0, this.userEngagementLevel - 1);
      }
    }
  }

  private updateConversationMetrics(message: string): void {
    this.lastInteractionTime = new Date();
    this.userEngagementLevel = Math.min(5, this.userEngagementLevel + 1);
    this.analyzeMessageSentiment(message);
    this.extractTopics(message);
  }

  private analyzeMessageSentiment(message: string): void {
    const positiveWords = ['merci', 'super', 'génial', 'parfait', 'excellent', 'bravo'];
    const negativeWords = ['problème', 'erreur', 'difficile', 'compliqué', 'impossible'];

    const normalizedMessage = message.toLowerCase();
    const positiveCount = positiveWords.filter(word => normalizedMessage.includes(word)).length;
    const negativeCount = negativeWords.filter(word => normalizedMessage.includes(word)).length;

    if (positiveCount > negativeCount) {
      this.conversationMood = 'positive';
    } else if (negativeCount > positiveCount) {
      this.conversationMood = 'negative';
    } else {
      this.conversationMood = 'neutral';
    }
  }

  private extractTopics(message: string): void {
    const topics = ['candidature', 'entretien', 'cv', 'lettre', 'stage', 'entreprise', 'formation', 'compétences'];
    const normalizedMessage = message.toLowerCase();
    
    topics.forEach(topic => {
      if (normalizedMessage.includes(topic)) {
        this.conversationTopics.add(topic);
      }
    });
  }

  private getContextualSuggestions(query: string): string {
    const normalizedQuery = query.toLowerCase();
    const currentRole = this.userRoleSubject.value;

    if (currentRole === 'candidate') {
      if (normalizedQuery.includes('documents')) {
        return "Pour postuler, vous aurez besoin de :\n- Un CV à jour\n- Une lettre de motivation\n- Vos relevés de notes\n- Vos diplômes/certifications\n- Des références professionnelles\n\nVoulez-vous des conseils pour préparer l'un de ces documents ?";
      } else if (normalizedQuery.includes('entretien')) {
        return "Pour préparer votre entretien, je peux vous aider avec :\n- Les questions fréquentes\n- La gestion du stress\n- La présentation personnelle\n- Les questions à poser\n- Le dress code approprié\n\nSur quel aspect souhaitez-vous plus de détails ?";
      }
    } else if (currentRole === 'evaluator') {
      if (normalizedQuery.includes('évaluation')) {
        return "Pour l'évaluation des candidats, je peux vous aider avec :\n- Les critères d'évaluation\n- La grille de notation\n- Le feedback constructif\n- La comparaison des candidats\n- Les cas particuliers\n\nQuel aspect vous intéresse ?";
      }
    } else if (currentRole === 'hr') {
      if (normalizedQuery.includes('convention')) {
        return "Pour les conventions de stage, je peux vous aider avec :\n- La génération du document\n- Le processus de signature\n- La gratification\n- Les obligations légales\n- L'archivage\n\nQuelle information vous serait utile ?";
      }
    }

    return "Je peux vous aider avec :\n- Le processus de candidature\n- La préparation des documents\n- Les entretiens et évaluations\n- Les conventions et contrats\n- La gestion administrative\n\nQuel sujet vous intéresse ?";
  }

  private generateBaseResponse(query: string, role: UserRole): string {
    const normalizedQuery = query.toLowerCase();

    // Gérer les requêtes liées à l'analyse du CV
    if (normalizedQuery.includes('analyser') || normalizedQuery.includes('analyse') || normalizedQuery.includes('cv')) {
      if (this.cvAnalysisInProgress) {
        return "L'analyse de votre CV est en cours. Veuillez patienter quelques instants...";
      }

      if (this.cvAnalysisResults) {
        return this.formatCvAnalysisResults();
      }

      this.simulateCvAnalysis();
      return "Je vais analyser votre CV. Cela peut prendre quelques instants...";
    }

    // Réponses spécifiques selon le rôle
    if (role === 'candidate') {
      if (normalizedQuery.includes('postuler') || normalizedQuery.includes('candidature')) {
        return "Pour postuler sur GStage, suivez ces étapes :\n\n1. Créez un compte sur la plateforme\n2. Complétez votre profil\n3. Sélectionnez les offres\n4. Joignez vos documents\n5. Soumettez votre candidature\n\nConseil : Personnalisez votre candidature pour chaque offre.";
      } else if (normalizedQuery.includes('cv') || normalizedQuery.includes('lettre')) {
        return "Vos documents sont essentiels :\n\n- Adaptez votre CV à chaque offre\n- Personnalisez votre lettre\n- Utilisez des mots-clés\n- Limitez à 1-2 pages\n- Vérifiez l'orthographe\n- Incluez vos projets\n- Mentionnez vos compétences\n- Ajoutez des liens pertinents";
      } else if (normalizedQuery.includes('entretien')) {
        return "Pour réussir votre entretien :\n\n- Recherchez l'entreprise\n- Préparez des exemples\n- Prévoyez des questions\n- Arrivez à l'heure\n- Habillez-vous bien\n- Montrez votre motivation\n- Préparez les questions techniques\n- Ayez vos documents\n- Suivez l'actualité\n- Préparez votre pitch";
      } else if (normalizedQuery.includes('stage') || normalizedQuery.includes('durée')) {
        return "Concernant les stages :\n\n- Durée : 2 à 6 mois\n- Temps plein ou partiel\n- Gratification obligatoire\n- Plusieurs stages possibles\n- Présentiel/hybride/distance\n- Convention obligatoire\n- Conditions d'interruption";
      } else if (normalizedQuery.includes('compétences')) {
        return "Pour vos compétences :\n\n- Identifiez les besoins\n- Préparez des exemples\n- Mentionnez certifications\n- Décrivez vos projets\n- Mettez en avant soft skills\n- Adaptez au secteur\n- Soyez prêt à démontrer";
      } else if (normalizedQuery.includes('documents')) {
        return "Documents nécessaires :\n\n- CV à jour (PDF)\n- Lettre de motivation\n- Relevés de notes\n- Diplômes/certifications\n- Références\n- Portfolio si pertinent\n\nConseil : Préparez à l'avance.";
      } else if (normalizedQuery.includes('stress')) {
        return "Gestion du stress :\n\n- Préparez à l'avance\n- Respiration\n- Visualisation positive\n- Arrivez en avance\n- Attitude positive\n- Questions préparées\n- Activité relaxante\n- Bon sommeil\n- Repas léger";
      } else if (normalizedQuery.includes('questions')) {
        return "Questions à poser :\n\n- Missions principales\n- Journée type\n- Processus d'intégration\n- Formations\n- Suivi/évaluation\n- Perspectives\n- Travail en équipe\n- Projets en cours\n- Culture d'entreprise\n- Défis actuels";
      }
    } else if (role === 'evaluator') {
      if (normalizedQuery.includes('critères')) {
        return "Critères d'évaluation :\n\n- Compétences techniques\n- Expériences\n- Formation\n- Soft skills\n- Motivation\n- Intégration\n- Adaptation\n- Communication\n- Réalisations\n- Références";
      } else if (normalizedQuery.includes('entretien')) {
        return "Conduite d'entretien :\n\n- Questions préparées\n- Mise en confiance\n- Questions ouvertes\n- Méthode STAR\n- Questions du candidat\n- Prise de notes\n- Évaluation motivation\n- Tests techniques\n- Langage non-verbal\n- Informations claires";
      } else if (normalizedQuery.includes('feedback')) {
        return "Feedback constructif :\n\n1. Grille d'évaluation\n2. Spécificité\n3. Équilibre\n4. Exemples\n5. Soumission\n6. Langage pro\n7. Pistes d'amélioration\n8. Délais";
      } else if (normalizedQuery.includes('comparaison')) {
        return "Comparaison candidats :\n\n- Grille standardisée\n- Compétences\n- Expérience\n- Potentiel\n- Culture\n- Références\n- Rémunération\n- Disponibilité\n- Soft skills\n- Débriefing";
      }
    } else if (role === 'hr') {
      if (normalizedQuery.includes('contrat')) {
        return "Convention de stage :\n\n1. Onglet Conventions\n2. Sélection candidat\n3. Vérification infos\n4. Génération PDF\n5. Envoi signature\n6. Suivi signatures\n7. Archivage\n8. Conservation";
      } else if (normalizedQuery.includes('signature')) {
        return "Processus signature :\n\n1. Génération\n2. Envoi signature\n3. Emails sécurisés\n4. Signatures électroniques\n5. Suivi état\n6. Envoi final\n7. Archivage\n8. Export PDF";
      } else if (normalizedQuery.includes('gratification')) {
        return "Gratification :\n\n- Obligatoire > 2 mois\n- 15% plafond SS\n- Configuration standard\n- Calcul automatique\n- Avantages supplémentaires\n- Cotisations\n- Transport\n- Primes\n- Versement mensuel";
      } else if (normalizedQuery.includes('absences')) {
        return "Absences/congés :\n\n- 2,5 jours/mois\n- Justification\n- Suivi temps\n- Approbation tuteur\n- Sanctions\n- Jours fériés\n- Pas de RTT\n- Certificat médical\n- Examens";
      }
    }

    // Réponses générales
    if (normalizedQuery.includes('bonjour')) {
      return `Bonjour ! Je suis l'assistant GStage. Comment puis-je vous aider avec ${this.getRoleDescription(role)} ?`;
    } else if (normalizedQuery.includes('merci')) {
      return "Avec plaisir 😊 ! Si vous avez d'autres questions, je suis là.";
    } else if (normalizedQuery.includes('au revoir')) {
      return "Au revoir ! Bonne continuation. N'hésitez pas à revenir.";
    } else if (normalizedQuery.includes('aide')) {
      return `Je peux vous aider avec ${this.getRoleDescription(role)}. Voici quelques sujets :\n\n- Candidature\n- Documents\n- Entretiens\n- Conventions\n- Administration\n\nQuelle information souhaitez-vous ?`;
    }

    // Si aucune correspondance, donner des suggestions contextuelles
    return `Je comprends que vous souhaitez des informations sur "${query}". Voici ce que je peux vous dire :\n\n${this.getContextualSuggestions(query)}\n\nN'hésitez pas à me demander plus de détails.`;
  }

  private loadHistory(): void {
    const savedHistory = localStorage.getItem(this.storageKey);
    if (savedHistory) {
      const messages = JSON.parse(savedHistory);
      this.messagesSubject.next(messages);
    }
  }

  private loadUserRole(): void {
    const savedRole = localStorage.getItem(this.roleStorageKey) as UserRole;
    if (savedRole) {
      this.userRoleSubject.next(savedRole);
    }
  }

  private getRoleDescription(role: UserRole): string {
    switch (role) {
      case 'candidate':
        return 'votre candidature et votre recherche de stage';
      case 'evaluator':
        return "l'évaluation des candidats";
      case 'hr':
        return 'la gestion des conventions et des stages';
      default:
        return 'vos questions';
    }
  }

  private simulateCvAnalysis(): void {
    this.cvAnalysisInProgress = true;
    setTimeout(() => {
      this.cvAnalysisResults = {
        overallScore: 85,
        strengths: [
          'Formation solide',
          'Expériences pertinentes',
          'Compétences techniques',
          'Projets personnels'
        ],
        weaknesses: [
          'Peu d\'expérience en entreprise',
          'Langues à améliorer',
          'Soft skills à développer'
        ],
        recommendations: [
          'Ajouter plus de détails sur les projets',
          'Mettre en avant les réalisations',
          'Développer la section compétences'
        ],
        sections: {
          formation: 90,
          experience: 80,
          competences: 85,
          langues: 70,
          projets: 90
        }
      };
      this.cvAnalysisInProgress = false;
    }, 3000);
  }

  private formatCvAnalysisResults(): string {
    if (!this.cvAnalysisResults) return '';

    const results = this.cvAnalysisResults;
    return `Voici l'analyse de votre CV :\n\n` +
      `Score global : ${results.overallScore}/100\n\n` +
      `Points forts :\n${results.strengths.map((s: string) => `- ${s}`).join('\n')}\n\n` +
      `Points à améliorer :\n${results.weaknesses.map((w: string) => `- ${w}`).join('\n')}\n\n` +
      `Recommandations :\n${results.recommendations.map((r: string) => `- ${r}`).join('\n')}\n\n` +
      `Détail par section :\n` +
      Object.entries(results.sections)
        .map(([section, score]) => `- ${section.charAt(0).toUpperCase() + section.slice(1)}: ${score}/100`)
        .join('\n') +
      `\n\nVoulez-vous des conseils pour améliorer une section en particulier ?`;
  }

  // ... rest of the existing methods ...
} 