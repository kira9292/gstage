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
    if (normalizedQuery.includes('analyser') || normalizedQuery.includes('analyse') || normalizedQuery.includes('cv')) {
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
    } else if (role === 'evaluator') {
      if (normalizedQuery.includes('critères') || normalizedQuery.includes('évaluation')) {
        return "Les critères d'évaluation recommandés pour les candidats sont :\n\n- Compétences techniques en adéquation avec le poste\n- Expériences pertinentes (projets, stages précédents)\n- Formation académique\n- Qualités personnelles et soft skills\n- Motivation et connaissance de l'entreprise\n- Potentiel d'intégration à l'équipe\n- Capacité d'adaptation et d'apprentissage\n- Qualité de la communication\n- Réalisations et projets personnels\n- Références et recommandations\n\nVous pouvez créer une grille d'évaluation personnalisée dans l'outil.";
      } else if (normalizedQuery.includes('entretien') || normalizedQuery.includes('interview')) {
        return "Pour conduire un entretien efficace :\n\n- Préparez vos questions à l'avance et structurez l'entretien\n- Commencez par mettre le candidat à l'aise\n- Posez des questions ouvertes et comportementales\n- Utilisez la méthode STAR (Situation, Tâche, Action, Résultat)\n- Laissez au candidat l'opportunité de poser ses questions\n- Prenez des notes pendant l'entretien\n- Évaluez la motivation et l'intérêt pour le poste\n- Testez les compétences techniques si nécessaire\n- Observez le langage non-verbal\n- Donnez des informations claires sur le poste et l'entreprise";
      } else if (normalizedQuery.includes('feedback') || normalizedQuery.includes('retour')) {
        return "Pour donner un feedback constructif :\n\n1. Remplissez la grille d'évaluation dans l'onglet 'Candidatures'\n2. Soyez spécifique et objectif dans vos commentaires\n3. Équilibrez points forts et axes d'amélioration\n4. Soutenez vos observations par des exemples concrets\n5. Soumettez votre évaluation via la plateforme\n6. Utilisez un langage professionnel et constructif\n7. Proposez des pistes d'amélioration\n8. Respectez les délais impartis\n\nTous les évaluateurs recevront une notification pour consulter vos commentaires.";
      } else if (normalizedQuery.includes('comparaison') || normalizedQuery.includes('sélection')) {
        return "Pour comparer efficacement les candidats :\n\n- Utilisez la grille d'évaluation standardisée\n- Comparez les compétences techniques requises\n- Évaluez l'expérience et les réalisations\n- Considérez le potentiel d'évolution\n- Prenez en compte la culture d'entreprise\n- Analysez les références et recommandations\n- Comparez les attentes salariales\n- Évaluez la disponibilité et la flexibilité\n- Considérez les soft skills et l'adaptabilité\n- Organisez des réunions de débriefing avec l'équipe";
      } else if (normalizedQuery.includes('questions') || normalizedQuery.includes('poser') || normalizedQuery.includes('entretien')) {
        return "Questions recommandées pour l'entretien :\n\n- Parlez-moi de votre parcours académique\n- Quelles sont vos principales réalisations ?\n- Comment gérez-vous les situations difficiles ?\n- Pourquoi souhaitez-vous ce stage ?\n- Quelles sont vos attentes ?\n- Comment travaillez-vous en équipe ?\n- Quels sont vos points forts et axes d'amélioration ?\n- Où vous voyez-vous dans 5 ans ?\n- Qu'avez-vous appris de vos expériences précédentes ?\n- Comment vous organisez-vous face aux deadlines ?";
      } else if (normalizedQuery.includes('cas') || normalizedQuery.includes('particulier') || normalizedQuery.includes('difficile')) {
        return "Pour gérer les cas particuliers :\n\n- Consultez la politique de l'entreprise\n- Documentez tous les cas particuliers\n- Impliquez les RH si nécessaire\n- Respectez la confidentialité\n- Suivez les procédures établies\n- Communiquez clairement avec le candidat\n- Gardez une trace écrite des décisions\n- Consultez les précédents similaires\n- Respectez les délais légaux\n- Assurez-vous de l'équité du processus";
      }
    } else if (role === 'hr') {
      if (normalizedQuery.includes('contrat') || normalizedQuery.includes('convention')) {
        return "Pour générer une convention de stage :\n\n1. Accédez à l'onglet 'Conventions' dans votre espace RH\n2. Sélectionnez le candidat accepté\n3. Vérifiez/complétez les informations (dates, horaires, gratification)\n4. Générez le document au format PDF\n5. Envoyez-le pour signature via la plateforme ou par email\n6. Suivez l'état d'avancement des signatures\n7. Conservez une copie numérique dans le système\n8. Archivez les documents selon la réglementation\n\nLe système conserve un historique de toutes les conventions émises.";
      } else if (normalizedQuery.includes('signature') || normalizedQuery.includes('signer')) {
        return "Le processus de signature des conventions sur GStage :\n\n1. Générez la convention complète depuis l'onglet 'Conventions'\n2. Utilisez l'option 'Envoyer pour signature' pour initier le processus\n3. Le stagiaire, le tuteur et l'établissement recevront un email avec un lien sécurisé\n4. Chaque partie peut signer électroniquement le document\n5. Vous pouvez suivre l'état des signatures dans le tableau de bord\n6. Une fois toutes les signatures obtenues, la convention finale est automatiquement envoyée à tous\n7. Les documents signés sont archivés automatiquement\n8. Vous pouvez exporter les conventions signées au format PDF";
      } else if (normalizedQuery.includes('gratification') || normalizedQuery.includes('rémunération')) {
        return "Concernant la gratification des stagiaires :\n\n- La gratification minimale légale est obligatoire pour les stages de plus de 2 mois\n- Le montant horaire minimal est de 15% du plafond horaire de la sécurité sociale\n- Vous pouvez configurer les montants standard dans 'Paramètres'\n- La plateforme calcule automatiquement le montant total en fonction de la durée\n- Les avantages supplémentaires (tickets restaurant, transport) peuvent être ajoutés dans la section dédiée\n- La gratification est soumise à cotisations sociales\n- Les frais de transport peuvent être remboursés\n- Des primes de fin de stage peuvent être prévues\n- La gratification est versée mensuellement";
      } else if (normalizedQuery.includes('absences') || normalizedQuery.includes('congés')) {
        return "Gestion des absences et congés des stagiaires :\n\n- Les stagiaires ont droit à 2,5 jours de congés par mois\n- Les absences doivent être justifiées et déclarées\n- Utilisez l'onglet 'Gestion du temps' pour suivre les absences\n- Les congés doivent être approuvés par le tuteur\n- Les absences non justifiées peuvent être sanctionnées\n- Les jours fériés sont chômés\n- Les RTT ne s'appliquent pas aux stagiaires\n- Les absences pour maladie doivent être justifiées par un certificat médical\n- Les absences pour examens sont autorisées sur présentation d'un justificatif";
      } else if (normalizedQuery.includes('obligations') || normalizedQuery.includes('légales') || normalizedQuery.includes('réglementation')) {
        return "Obligations légales pour les stages :\n\n- Convention de stage obligatoire\n- Gratification minimale pour stages > 2 mois\n- Assurance responsabilité civile\n- Respect du code du travail\n- Horaires conformes à la réglementation\n- Conditions de travail décentes\n- Suivi et évaluation obligatoires\n- Respect de la confidentialité\n- Protection des données personnelles\n- Égalité de traitement";
      } else if (normalizedQuery.includes('archivage') || normalizedQuery.includes('documents') || normalizedQuery.includes('conservation')) {
        return "Archivage des documents :\n\n- Conservation des conventions pendant 5 ans\n- Archivage électronique sécurisé\n- Classement par année et type de document\n- Accès restreint aux documents sensibles\n- Sauvegarde régulière des données\n- Respect du RGPD\n- Traçabilité des modifications\n- Historique des versions\n- Export possible au format PDF\n- Destruction sécurisée après délai légal";
      }
    }

    // Réponses générales si aucune correspondance spécifique n'est trouvée
    if (normalizedQuery.includes('bonjour') || normalizedQuery.includes('salut') || normalizedQuery.includes('hello')) {
      return `Bonjour ! Je suis l'assistant GStage. Comment puis-je vous aider avec ${this.getRoleDescription(role)} ?`;
    } else if (normalizedQuery.includes('merci')) {
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
    } else if (currentRole === 'evaluator') {
      if (recentTopics.includes('évaluation')) {
        return "Pour l'évaluation, je peux vous aider avec :\n- La grille d'évaluation\n- Les critères de sélection\n- Le feedback constructif\n- La comparaison des candidats\n- Les cas particuliers\n\nQuel aspect vous intéresse ?";
      } else if (recentTopics.includes('entretien')) {
        return "Pour l'entretien, je peux vous conseiller sur :\n- Les questions à poser\n- La structure de l'entretien\n- L'évaluation des réponses\n- La prise de notes\n- Le debriefing\n\nSur quel point souhaitez-vous des conseils ?";
      }
    } else if (currentRole === 'hr') {
      if (recentTopics.includes('convention')) {
        return "Pour les conventions, je peux vous aider avec :\n- La génération du document\n- Le processus de signature\n- La gratification\n- Les obligations légales\n- L'archivage\n\nQuelle information vous serait utile ?";
      } else if (recentTopics.includes('gestion')) {
        return "Pour la gestion administrative, je peux vous aider avec :\n- Les absences et congés\n- La gratification\n- Les documents obligatoires\n- Le suivi des stagiaires\n- L'archivage\n\nQuel aspect vous intéresse ?";
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
      {
        text: 'Quelles questions poser en entretien ?',
        forRoles: ['evaluator'] as UserRole[]
      },
      {
        text: 'Comment évaluer les compétences techniques ?',
        forRoles: ['evaluator'] as UserRole[]
      },
      {
        text: 'Comment gérer les cas particuliers ?',
        forRoles: ['evaluator'] as UserRole[]
      },
      {
        text: 'Comment collaborer avec les autres évaluateurs ?',
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
      {
        text: 'Quelles sont les obligations légales ?',
        forRoles: ['hr'] as UserRole[]
      },
      {
        text: 'Comment gérer les congés des stagiaires ?',
        forRoles: ['hr'] as UserRole[]
      },
      {
        text: 'Comment archiver les documents ?',
        forRoles: ['hr'] as UserRole[]
      },
      {
        text: 'Comment gérer les cas particuliers ?',
        forRoles: ['hr'] as UserRole[]
      },

      // Questions communes à tous les rôles
      {
        text: 'Quelles sont les fonctionnalités principales ?',
        forRoles: ['candidate', 'evaluator', 'hr'] as UserRole[]
      },
      {
        text: 'Comment contacter le support ?',
        forRoles: ['candidate', 'evaluator', 'hr'] as UserRole[]
      },
      {
        text: 'Où trouver l\'aide en ligne ?',
        forRoles: ['candidate', 'evaluator', 'hr'] as UserRole[]
      },
      {
        text: 'Comment mettre à jour mon profil ?',
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
