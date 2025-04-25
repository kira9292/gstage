// src/app/services/chat-assistant.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { ChatMessage, UserRole, SuggestedQuestion } from '../models/chat-message';
import { v4 as uuidv4 } from 'uuid';

// Déplacer l'interface au début du fichier, avant la classe
interface FollowUpQuestions {
  [key: string]: {
    [key: string]: string[];
  };
}

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

  // Ajouter une propriété pour suivre l'état de l'analyse du CV
  private cvAnalysisInProgress = false;
  private cvAnalysisResults: any = null;

  // Nouvelles propriétés pour les fonctionnalités avancées
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
    setInterval(() => this.checkUserEngagement(), 60000); // Vérifier toutes les minutes
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

  // Rendre la méthode publique
  public getRoleDescription(role: UserRole): string {
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

    // Mettre à jour les métriques de conversation
    this.updateConversationMetrics(content);

    const userMessage: ChatMessage = {
      id: uuidv4(),
      content: content.trim(),
      sender: 'user',
      timestamp: new Date(),
      metadata: {
        sentiment: this.conversationMood,
        topics: Array.from(this.conversationTopics),
        engagementLevel: this.userEngagementLevel
      }
    };

    const currentMessages = [...this.messagesSubject.value, userMessage];
    this.messagesSubject.next(currentMessages);
    this.saveHistory();

    // Générer une réponse plus contextuelle
    this.getAssistantResponse(content);
  }

  // Simuler une réponse de l'assistant avec un délai plus réaliste
  private getAssistantResponse(userQuery: string): void {
    this.typingSubject.next(true);

    const response = this.generateEnhancedResponse(userQuery);
    const responseDelay = this.calculateTypingDelay(response);

    of(response).pipe(
      delay(responseDelay)
    ).subscribe(response => {
      const assistantMessage: ChatMessage = {
        id: uuidv4(),
        content: response,
        sender: 'assistant',
        timestamp: new Date(),
        metadata: {
          sentiment: this.conversationMood,
          topics: Array.from(this.conversationTopics),
          engagementLevel: this.userEngagementLevel
        }
      };

      const currentMessages = [...this.messagesSubject.value, assistantMessage];
      this.messagesSubject.next(currentMessages);
      this.saveHistory();
      this.typingSubject.next(false);
    });
  }

  private calculateTypingDelay(response: string): number {
    // Délai de base + temps supplémentaire selon la longueur
    const baseDelay = 500;
    const lengthDelay = response.length * 5;
    const moodDelay = this.conversationMood === 'positive' ? -200 : this.conversationMood === 'negative' ? 200 : 0;

    return Math.min(baseDelay + lengthDelay + moodDelay, 3000);
  }

  private generateEnhancedResponse(query: string): string {
    const normalizedQuery = query.toLowerCase();
    const currentRole = this.userRoleSubject.value;
    const recentTopics = Array.from(this.conversationTopics);

    // Générer une réponse basée sur le contexte et l'humeur
    let response = this.generateBaseResponse(normalizedQuery, currentRole);

    // Personnaliser la réponse selon l'humeur
    if (this.conversationMood === 'positive') {
      response = this.addPositiveTone(response);
    } else if (this.conversationMood === 'negative') {
      response = this.addSupportiveTone(response);
    }

    // Ajouter des suggestions contextuelles
    if (this.userEngagementLevel > 3 && recentTopics.length > 0) {
      response += this.getContextualSuggestions(normalizedQuery);
    }

    // Ajouter des questions de suivi pertinentes
    response += this.getFollowUpQuestions(normalizedQuery, currentRole);

    return response;
  }

  private generateBaseResponse(query: string, role: UserRole): string {
    const normalizedQuery = query.toLowerCase();

    // Gérer les requêtes liées à l'analyse du CV
    if (normalizedQuery.includes('analyser') || normalizedQuery.includes('analyse')) {
      if (this.cvAnalysisInProgress) {
        return "L'analyse de votre CV est en cours. Veuillez patienter quelques instants...";
      }

      if (this.cvAnalysisResults) {
        return this.formatCvAnalysisResults();
      }

      // Simuler l'analyse du CV
      this.simulateCvAnalysis();
      return "Je vais analyser votre CV. Cela peut prendre quelques instants...";
    }

    // Réponses spécifiques selon le rôle avec plus de détails
    if (role === 'candidate') {
      if (normalizedQuery.includes('postuler') || normalizedQuery.includes('candidature')) {
        return "Pour postuler sur GStage, suivez ces étapes :\n\n1. Créez un compte sur la plateforme si ce n'est pas déjà fait\n2. Complétez votre profil avec vos informations personnelles\n3. Dans l'onglet 'Postuler', sélectionnez les offres qui vous intéressent\n4. Joignez votre CV et lettre de motivation\n5. Soumettez votre candidature et suivez son statut dans 'Mes candidatures'\n\nConseil : Personnalisez votre candidature pour chaque offre en mettant en avant les compétences pertinentes.";
      } else if (normalizedQuery.includes('cv') || normalizedQuery.includes('curriculum') || normalizedQuery.includes('lettre de motivation')) {
        return "Votre CV et lettre de motivation sont essentiels. Voici quelques conseils :\n\n- Adaptez votre CV à chaque offre en mettant en avant les compétences pertinentes\n- Pour la lettre de motivation, personnalisez-la pour l'entreprise et le poste\n- Utilisez des mots-clés du secteur et de l'offre\n- Limitez votre CV à 1-2 pages maximum\n- Vérifiez l'orthographe et la grammaire\n- Incluez vos projets personnels et réalisations\n- Mentionnez vos compétences techniques et soft skills\n- Ajoutez des liens vers votre portfolio ou GitHub si pertinent";
      } else if (normalizedQuery.includes('entretien') || normalizedQuery.includes('interview')) {
        return "Pour réussir votre entretien de stage :\n\n- Recherchez l'entreprise et le poste en profondeur\n- Préparez des exemples concrets de vos compétences\n- Prévoyez des questions pertinentes à poser\n- Arrivez à l'heure (ou 10 minutes en avance)\n- Habillez-vous de façon professionnelle\n- Montrez votre motivation et votre intérêt pour le poste\n- Préparez-vous aux questions techniques\n- Ayez une copie de votre CV et de vos références\n- Suivez l'actualité de l'entreprise\n- Préparez votre présentation personnelle (pitch)";
      } else if (normalizedQuery.includes('stage') || normalizedQuery.includes('durée') || normalizedQuery.includes('période')) {
        return "Concernant les stages sur GStage :\n\n- La durée minimale est de 2 mois\n- La durée maximale est de 6 mois\n- Les stages peuvent être à temps plein ou partiel\n- La gratification est obligatoire pour les stages de plus de 2 mois\n- Vous pouvez effectuer plusieurs stages dans l'année\n- Les stages peuvent être en présentiel, hybride ou à distance\n- La convention de stage est obligatoire\n- Vous pouvez interrompre votre stage sous certaines conditions";
      } else if (normalizedQuery.includes('compétences') || normalizedQuery.includes('skills')) {
        return "Pour mettre en valeur vos compétences :\n\n- Identifiez les compétences techniques requises pour le poste\n- Préparez des exemples concrets de vos réalisations\n- Mentionnez vos certifications et formations\n- Décrivez vos projets personnels ou académiques\n- Mettez en avant vos soft skills (communication, travail d'équipe, etc.)\n- Adaptez votre discours selon le secteur d'activité\n- Soyez prêt à démontrer vos compétences techniques si nécessaire";
      } else if (normalizedQuery.includes('documents') || normalizedQuery.includes('pièces') || normalizedQuery.includes('nécessaires')) {
        return "Pour postuler, vous aurez besoin des documents suivants :\n\n- Un CV à jour (format PDF de préférence)\n- Une lettre de motivation\n- Vos relevés de notes\n- Vos diplômes/certifications\n- Des références professionnelles (si disponibles)\n- Un portfolio ou des exemples de travaux (selon le domaine)\n\nConseil : Préparez ces documents à l'avance et gardez-les à jour.";
      } else if (normalizedQuery.includes('stress') || normalizedQuery.includes('anxiété') || normalizedQuery.includes('nerveux')) {
        return "Pour gérer le stress avant un entretien :\n\n- Préparez-vous bien à l'avance\n- Faites des exercices de respiration\n- Visualisez le déroulement positif de l'entretien\n- Arrivez en avance sur les lieux\n- Ayez une attitude positive\n- Rappelez-vous que l'entreprise est aussi intéressée par vous\n- Préparez des questions à poser\n- Faites une activité relaxante avant l'entretien\n- Dormez bien la veille\n- Mangez léger avant l'entretien";
      } else if (normalizedQuery.includes('questions') || normalizedQuery.includes('poser') || normalizedQuery.includes('demander')) {
        return "Questions pertinentes à poser en entretien :\n\n- Quelles seront mes principales missions ?\n- Comment se déroule une journée type ?\n- Quel est le processus d'intégration ?\n- Quelles sont les opportunités de formation ?\n- Comment se passe le suivi et l'évaluation ?\n- Quelles sont les perspectives d'évolution ?\n- Comment se déroule le travail en équipe ?\n- Quels sont les projets en cours ?\n- Quelle est la culture d'entreprise ?\n- Quels sont les défis actuels de l'entreprise ?";
      }
    }

    // Réponses générales si aucune correspondance spécifique n'est trouvée
    if (normalizedQuery.includes('bonjour') || normalizedQuery.includes('salut') || normalizedQuery.includes('hello')) {
      return `Bonjour ! Je suis l'assistant GStage. Comment puis-je vous aider avec ${this.getRoleDescription(role)} ?`;
    } else if (normalizedQuery.includes('merci') || normalizedQuery.includes('d\'accord')) {
      return "Avec plaisir 😊 ! Si vous avez d'autres questions ou besoin d'assistance, je suis toujours là.";
    } else if (normalizedQuery.includes('au revoir') || normalizedQuery.includes('à bientôt')) {
      return "Au revoir ! Bonne continuation dans vos démarches. N'hésitez pas à revenir si vous avez d'autres questions. Je reste à votre disposition.";
    } else if (normalizedQuery.includes('aide') || normalizedQuery.includes('help')) {
      return `Je peux vous aider avec ${this.getRoleDescription(role)}. Voici quelques sujets que je peux aborder :\n\n- Processus de candidature\n- Préparation des documents\n- Entretiens et évaluations\n- Conventions et contrats\n- Gestion administrative\n- Questions fréquentes\n\nPour quelle information spécifique souhaitez-vous de l'aide ?`;
    }

    // Si la requête contient des mots-clés de suggestions, donner une réponse appropriée
    if (normalizedQuery.includes('préparer') && normalizedQuery.includes('entretien')) {
      return "Pour préparer votre entretien :\n\n1. Recherchez l'entreprise et le poste\n2. Préparez votre présentation personnelle\n3. Anticipez les questions courantes\n4. Préparez des exemples concrets\n5. Habillez-vous de façon professionnelle\n6. Arrivez en avance\n7. Préparez vos questions\n8. Entraînez-vous avec un ami\n9. Vérifiez vos documents\n10. Détendez-vous avant l'entretien";
    } else if (normalizedQuery.includes('lettre') && normalizedQuery.includes('motivation')) {
      return "Pour rédiger une lettre de motivation percutante :\n\n1. Structurez votre lettre en 3 parties\n2. Personnalisez pour chaque entreprise\n3. Mettez en avant vos atouts\n4. Soyez concis et clair\n5. Utilisez un ton professionnel\n6. Évitez les formules toutes faites\n7. Relisez attentivement\n8. Faites relire par quelqu'un\n9. Adaptez au format demandé\n10. Envoyez au bon destinataire";
    } else if (normalizedQuery.includes('compétences') && normalizedQuery.includes('techniques')) {
      return "Pour mettre en valeur vos compétences techniques :\n\n1. Listez vos compétences principales\n2. Donnez des exemples concrets\n3. Mentionnez vos certifications\n4. Décrivez vos projets\n5. Utilisez des mots-clés du secteur\n6. Adaptez au poste visé\n7. Quantifiez vos réalisations\n8. Montrez votre progression\n9. Incluez vos outils maîtrisés\n10. Préparez des démonstrations";
    }

    // Réponse générique en dernier recours avec une suggestion contextuelle
    const suggestions = this.getContextualSuggestions(normalizedQuery);
    return `Je comprends que vous souhaitez des informations sur "${query}". Voici ce que je peux vous dire :\n\n${suggestions}\n\nN'hésitez pas à me demander plus de détails sur l'un de ces aspects.`;
  }

  private getContextualSuggestions(query: string): string {
    const normalizedQuery = query.toLowerCase();
    const currentRole = this.userRoleSubject.value;
    const recentTopics = Array.from(this.conversationTopics);

    // Suggestions basées sur le rôle et les sujets récents
    if (currentRole === 'candidate') {
      if (recentTopics.includes('candidature')) {
        return "Je peux vous aider avec :\n- La préparation de votre CV\n- La rédaction de votre lettre de motivation\n- La recherche d'offres pertinentes\n- Le suivi de vos candidatures\n- La préparation aux entretiens\n\nQuel aspect vous intéresse le plus ?";
      } else if (recentTopics.includes('entretien')) {
        return "Pour votre entretien, je peux vous conseiller sur :\n- Les questions fréquentes\n- La présentation personnelle\n- La gestion du stress\n- Les questions à poser\n- Le dress code approprié\n\nSur quel point souhaitez-vous des conseils ?";
      } else if (recentTopics.includes('cv')) {
        return "Concernant votre CV, je peux vous aider à :\n- Structurer votre CV\n- Mettre en valeur vos compétences\n- Adapter votre CV au poste\n- Choisir le bon format\n- Optimiser votre présentation\n\nQuel aspect voulez-vous améliorer ?";
      }
    }
    // Suggestions générales basées sur la requête
    if (normalizedQuery.includes('postuler') || normalizedQuery.includes('candidature')) {
      return "Pour postuler, vous aurez besoin de :\n- Un CV à jour\n- Une lettre de motivation\n- Vos relevés de notes\n- Vos diplômes/certifications\n- Des références professionnelles\n\nVoulez-vous des conseils pour préparer l'un de ces documents ?";
    } else if (normalizedQuery.includes('entretien')) {
      return "Pour préparer votre entretien, je peux vous aider avec :\n- Les questions fréquentes\n- La gestion du stress\n- La présentation personnelle\n- Les questions à poser\n- Le dress code approprié\n\nSur quel aspect souhaitez-vous plus de détails ?";
    } else if (normalizedQuery.includes('contrat') || normalizedQuery.includes('convention')) {
      return "Pour les conventions de stage, je peux vous aider avec :\n- La génération du document\n- Le processus de signature\n- La gratification\n- Les obligations légales\n- L'archivage\n\nQuelle information vous serait utile ?";
    }

    // Suggestions par défaut
    return "Je peux vous aider avec :\n- Le processus de candidature\n- La préparation des documents\n- Les entretiens et évaluations\n- Les conventions et contrats\n- La gestion administrative\n\nQuel sujet vous intéresse ?";
  }

  private simulateCvAnalysis(): void {
    this.cvAnalysisInProgress = true;

    // Simuler un délai d'analyse
    setTimeout(() => {
      this.cvAnalysisResults = {
        overallScore: 85,
        strengths: [
          'Formation académique solide',
          'Expériences pertinentes',
          'Compétences techniques bien présentées',
          'Projets personnels intéressants'
        ],
        weaknesses: [
          'Peu d\'expérience en entreprise',
          'Certaines compétences pourraient être plus détaillées',
          'Peu de références professionnelles'
        ],
        recommendations: [
          'Ajouter plus de détails sur vos réalisations',
          'Mentionner des résultats concrets',
          'Développer la section des soft skills',
          'Ajouter des certifications pertinentes'
        ],
        sections: {
          formation: 90,
          experience: 75,
          competences: 85,
          projets: 80,
          langues: 70
        }
      };

      this.cvAnalysisInProgress = false;

      // Envoyer les résultats
      const analysisMessage = this.formatCvAnalysisResults();
      this.sendMessage(analysisMessage);
    }, 3000); // 3 secondes de simulation
  }

  private formatCvAnalysisResults(): string {
    if (!this.cvAnalysisResults) return "Aucune analyse de CV disponible.";

    const { overallScore, strengths, weaknesses, recommendations, sections } = this.cvAnalysisResults;

    return `Voici l'analyse de votre CV :

📊 Note globale : ${overallScore}/100

✅ Points forts :
${strengths.map((s: string) => `- ${s}`).join('\n')}

⚠️ Points à améliorer :
${weaknesses.map((w: string) => `- ${w}`).join('\n')}

💡 Recommandations :
${recommendations.map((r: string) => `- ${r}`).join('\n')}

📝 Détail par section :
- Formation : ${sections.formation}/100
- Expérience : ${sections.experience}/100
- Compétences : ${sections.competences}/100
- Projets : ${sections.projets}/100
- Langues : ${sections.langues}/100

Voulez-vous des conseils spécifiques pour améliorer une section en particulier ?`;
  }

  // Analyser le contexte de la conversation pour réponses plus pertinentes


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
        text: 'Comment préparer mon CV et ma lettre de motivation ?',
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
      {
        text: 'Quelle est la durée minimale d\'un stage ?',
        forRoles: ['candidate'] as UserRole[]
      },
      {
        text: 'Comment mettre en valeur mes compétences ?',
        forRoles: ['candidate'] as UserRole[]
      },
      {
        text: 'Quels documents dois-je fournir ?',
        forRoles: ['candidate'] as UserRole[]
      },
      {
        text: 'Comment suivre l\'état de ma candidature ?',
        forRoles: ['candidate'] as UserRole[]
      },
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

  resetChat(): void {
    this.userRoleSubject.next('candidate');
    localStorage.removeItem(this.storageKey);
    localStorage.removeItem(this.roleStorageKey);
    localStorage.removeItem(this.lastChatTimeKey);

    const welcomeMessage: ChatMessage = {
      id: uuidv4(),
      content: `👋 Bonjour et bienvenue sur GStage ! Je suis là pour vous accompagner dans vos démarches de stage. N'hésitez pas à me poser vos questions.`,
      sender: 'assistant',
      timestamp: new Date()
    };

    this.messagesSubject.next([welcomeMessage]);
    this.saveHistory();
  }

  private addPositiveTone(response: string): string {
    const positivePhrases = [
      "\n\nJe suis ravi de pouvoir vous aider !",
      "\n\nN'hésitez pas si vous avez d'autres questions !",
      "\n\nC'est un plaisir de vous accompagner !"
    ];
    return response + positivePhrases[Math.floor(Math.random() * positivePhrases.length)];
  }

  private addSupportiveTone(response: string): string {
    const supportivePhrases = [
      "\n\nJe comprends que cela peut être compliqué. Je suis là pour vous aider.",
      "\n\nNe vous inquiétez pas, nous allons trouver une solution ensemble.",
      "\n\nPrenez votre temps, je suis là pour vous accompagner."
    ];
    return response + supportivePhrases[Math.floor(Math.random() * supportivePhrases.length)];
  }

  private addContextualSuggestions(topics: string[]): string {
    if (topics.length === 0) return '';

    const suggestions: { [key: string]: string[] } = {
      'candidature': [
        "\n\n💡 Vous pourriez aussi être intéressé par : Comment préparer votre entretien ?",
        "\n\n💡 Avez-vous pensé à : Comment rédiger une lettre de motivation percutante ?"
      ],
      'entretien': [
        "\n\n💡 Pour vous préparer : Quelles questions sont fréquemment posées en entretien ?",
        "\n\n💡 Conseil : Comment gérer le stress avant un entretien ?"
      ],
      'cv': [
        "\n\n💡 Astuce : Comment mettre en valeur vos compétences techniques ?",
        "\n\n💡 Saviez-vous que : La structure de votre CV est cruciale ?"
      ]
    };

    const relevantTopic = topics.find(topic => topic in suggestions);
    if (relevantTopic) {
      return suggestions[relevantTopic][Math.floor(Math.random() * suggestions[relevantTopic].length)];
    }

    return '';
  }

  // Réinitialiser complètement la discussion
  resetDiscussion(): void {
    // Supprimer toutes les données du localStorage
    localStorage.removeItem(this.storageKey);
    localStorage.removeItem(this.roleStorageKey);
    localStorage.removeItem(this.lastChatTimeKey);

    // Réinitialiser les sujets de conversation
    this.conversationTopics.clear();
    this.userInterests = [];
    this.conversationMood = 'neutral';
    this.userEngagementLevel = 0;
    this.lastInteractionTime = null;

    // Réinitialiser l'analyse du CV
    this.cvAnalysisInProgress = false;
    this.cvAnalysisResults = null;

    // Réinitialiser les messages
    this.messagesSubject.next([]);

    // Ajouter un message de bienvenue
    const welcomeMessage: ChatMessage = {
      id: uuidv4(),
      content: "👋 Bonjour ! Je suis l'assistant GStage. Comment puis-je vous aider aujourd'hui ?",
      sender: 'assistant',
      timestamp: new Date()
    };

    this.messagesSubject.next([welcomeMessage]);
    this.saveHistory();
  }

  // Ajouter les méthodes manquantes
  public setTyping(isTyping: boolean): void {
    this.typingSubject.next(isTyping);
  }

  public addAssistantMessage(content: string): void {
    const message: ChatMessage = {
      id: uuidv4(),
      content,
      sender: 'assistant',
      timestamp: new Date(),
      metadata: {
        sentiment: this.conversationMood,
        topics: Array.from(this.conversationTopics),
        engagementLevel: this.userEngagementLevel
      }
    };

    const currentMessages = [...this.messagesSubject.value, message];
    this.messagesSubject.next(currentMessages);
    this.saveHistory();
  }

  private getFollowUpQuestions(query: string, role: UserRole): string {
    const followUps: FollowUpQuestions = {
      'candidate': {
        'postuler': [
          "\n\nAvez-vous besoin d'aide pour :",
          "- Préparer votre CV ?",
          "- Rédiger votre lettre de motivation ?",
          "- Vous préparer à l'entretien ?"
        ],
        'entretien': [
          "\n\nSouhaitez-vous des conseils sur :",
          "- Les questions fréquentes ?",
          "- La gestion du stress ?",
          "- La présentation personnelle ?"
        ],
        'cv': [
          "\n\nVoulez-vous des conseils pour :",
          "- Structurer votre CV ?",
          "- Mettre en valeur vos compétences ?",
          "- Adapter votre CV au poste ?"
        ]
      },
      'evaluator': {
        'évaluation': [
          "\n\nAvez-vous besoin d'aide pour :",
          "- Créer une grille d'évaluation ?",
          "- Donner un feedback constructif ?",
          "- Comparer les candidats ?"
        ],
        'entretien': [
          "\n\nSouhaitez-vous des conseils sur :",
          "- Les questions à poser ?",
          "- La structure de l'entretien ?",
          "- L'évaluation des réponses ?"
        ]
      },
      'hr': {
        'convention': [
          "\n\nAvez-vous besoin d'aide pour :",
          "- Générer la convention ?",
          "- Gérer les signatures ?",
          "- Calculer la gratification ?"
        ],
        'gestion': [
          "\n\nSouhaitez-vous des conseils sur :",
          "- La gestion des absences ?",
          "- Les obligations légales ?",
          "- L'archivage des documents ?"
        ]
      }
    };

    const roleFollowUps = followUps[role];
    if (!roleFollowUps) return '';

    const mainTopic = Object.keys(roleFollowUps).find(topic =>
      query.includes(topic)
    );

    if (mainTopic && roleFollowUps[mainTopic]) {
      return roleFollowUps[mainTopic].join('\n');
    }

    return '';
  }

}
