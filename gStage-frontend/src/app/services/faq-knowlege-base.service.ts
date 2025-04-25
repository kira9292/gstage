// faq-knowledge-base.service.ts
import { Injectable } from '@angular/core';

export interface FAQItem {
  keywords: string[];
  question: string;
  answer: string;
  role: string[]; // Les rôles pour lesquels cette FAQ est pertinente
}

@Injectable({
  providedIn: 'root'
})
export class FAQKnowledgeBaseService {
  private faqDatabase: FAQItem[] = [
    // Questions sur la candidature
    {
      keywords: ['postuler', 'candidature', 'comment postuler', 'soumettre', 'soumettre cv'],
      question: 'Comment postuler à un stage?',
      answer: 'Pour postuler à un stage sur GStage, cliquez sur "Nouvelle candidature" dans votre tableau de bord. Vous devrez fournir votre CV, une lettre de motivation, et les informations sur votre formation actuelle. Assurez-vous que tous les documents sont au format PDF pour une meilleure lisibilité.',
      role: ['candidate']
    },
    {
      keywords: ['cv', 'curriculum', 'curriculum vitae', 'format cv'],
      question: 'Quel format de CV est recommandé?',
      answer: 'Nous recommandons un CV au format PDF d\'une ou deux pages maximum. Mettez en avant vos compétences techniques, vos formations et vos expériences pertinentes. N\'oubliez pas d\'inclure vos coordonnées et votre niveau de langue.',
      role: ['candidate']
    },
    {
      keywords: ['lettre', 'motivation', 'lettre de motivation'],
      question: 'Comment rédiger une bonne lettre de motivation?',
      answer: 'Une bonne lettre de motivation doit être personnalisée pour le stage visé. Expliquez pourquoi vous êtes intéressé par ce stage, ce que vous pouvez apporter, et comment il s\'inscrit dans votre projet professionnel. Gardez-la concise (une page maximum) et assurez-vous qu\'elle soit bien structurée.',
      role: ['candidate']
    },

    // Questions sur le processus
    {
      keywords: ['délai', 'délais', 'temps', 'réponse', 'attente'],
      question: 'Quel est le délai de réponse après ma candidature?',
      answer: 'Le délai de traitement des candidatures est généralement de 1 à 2 semaines. Vous recevrez une notification par email à chaque étape du processus. Si vous n\'avez pas de nouvelles après 2 semaines, vous pouvez nous contacter via le formulaire de contact.',
      role: ['candidate']
    },
    {
      keywords: ['entretien', 'interview', 'préparer entretien'],
      question: 'Comment me préparer à l\'entretien?',
      answer: 'Pour bien vous préparer à l\'entretien, renseignez-vous sur notre entreprise et le département qui vous intéresse. Relisez votre CV et votre lettre de motivation. Préparez des exemples concrets de projets ou d\'expériences pertinentes. Prévoyez également quelques questions à poser sur le stage et l\'entreprise.',
      role: ['candidate']
    },

    // Questions sur les documents administratifs
    {
      keywords: ['convention', 'convention de stage', 'contrat', 'documents administratifs'],
      question: 'Comment obtenir ma convention de stage?',
      answer: 'Une fois votre candidature acceptée, la convention de stage sera générée automatiquement dans votre espace personnel. Vous devrez la télécharger, la faire signer par votre établissement d\'enseignement, puis la téléverser sur la plateforme. Notre service RH la validera ensuite.',
      role: ['candidate']
    },
    {
      keywords: ['attestation', 'attestation de stage', 'fin de stage'],
      question: 'Comment obtenir mon attestation de fin de stage?',
      answer: 'À la fin de votre stage, votre attestation sera automatiquement générée et mise à disposition dans votre espace personnel sous l\'onglet "Documents". Si elle n\'apparaît pas dans les 7 jours suivant la fin de votre stage, contactez votre maître de stage ou le service RH.',
      role: ['candidate']
    },

    // Questions pratiques
    {
      keywords: ['horaires', 'temps de travail', 'heures'],
      question: 'Quels sont les horaires habituels de stage?',
      answer: 'Les horaires standards sont de 9h à 17h30 du lundi au vendredi, avec 1h30 de pause déjeuner. Ces horaires peuvent varier selon le service et les besoins spécifiques du stage. Tout aménagement d\'horaire doit être discuté et validé avec votre maître de stage.',
      role: ['candidate']
    },
    {
      keywords: ['gratification', 'rémunération', 'indemnité', 'paiement'],
      question: 'Comment fonctionne la gratification de stage?',
      answer: 'La gratification est versée mensuellement et calculée selon la législation en vigueur. Pour les stages de plus de 2 mois, elle correspond à environ 15% du plafond horaire de la sécurité sociale. Les versements sont effectués après validation des attestations de présence mensuelles par votre maître de stage.',
      role: ['candidate']
    },

    // Questions sur les problèmes techniques
    {
      keywords: ['problème', 'technique', 'erreur', 'bug', 'connexion'],
      question: 'Que faire en cas de problème technique sur la plateforme?',
      answer: 'En cas de problème technique, essayez d\'abord de rafraîchir la page ou de vous déconnecter puis reconnecter. Si le problème persiste, contactez notre support technique via le formulaire "Support" accessible depuis votre tableau de bord, en décrivant précisément l\'erreur rencontrée.',
      role: ['candidate']
    },

    // Questions sur l'évaluation
    {
      keywords: ['évaluation', 'note', 'rapport', 'soutenance'],
      question: 'Comment se déroule l\'évaluation de fin de stage?',
      answer: 'L\'évaluation de fin de stage comprend généralement trois éléments : une évaluation de votre maître de stage via un formulaire sur la plateforme, une auto-évaluation que vous remplirez, et des commentaires sur votre rapport de stage si votre formation l\'exige. Ces évaluations seront accessibles dans votre espace personnel.',
      role: ['candidate']
    }
  ];

  constructor() { }

  getFAQItems(): FAQItem[] {
    return this.faqDatabase;
  }

  getFAQsByRole(role: string): FAQItem[] {
    return this.faqDatabase.filter(item => item.role.includes(role));
  }

  findMatchingFAQ(message: string, role: string): FAQItem | null {
    const normalizedMessage = message.toLowerCase();

    // Chercher une correspondance par mots-clés
    const matchedItem = this.faqDatabase.find(item => {
      if (!item.role.includes(role)) return false;

      return item.keywords.some(keyword =>
        normalizedMessage.includes(keyword.toLowerCase())
      );
    });

    return matchedItem || null;
  }
}
