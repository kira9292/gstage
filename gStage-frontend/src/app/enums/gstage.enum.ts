export enum InternshipType {
    ACADEMIQUE = 'ACADEMIQUE',
    PROFESSIONNEL = 'PROFESSIONNEL',
    PROJET_FIN_ETUDES = 'PROJET_FIN_ETUDES'
  }
  
  export enum InternshipStatus {
    EN_ATTENTE = 'EN_ATTENTE',       // En attente de confirmation
    ACCEPTE = 'ACCEPTE',             // Accepté
    EN_COURS = 'EN_COURS',           // En cours de réalisation
    TERMINER = 'TERMINER',             // Terminé
    REFUSE = 'REFUSE',                // Refusé
    PROPOSE = 'PROPOSE'
  }
  // Niveau d'éducation
  export enum EducationLevel {
    BAC = 'Bac',                      // Niveau Bac
    BAC_PLUS_2 = 'Bac +2',            // Niveau Bac+2
    BAC_PLUS_3 = 'Bac +3',            // Niveau Bac+3
    BAC_PLUS_4 = 'Bac +4',            // Niveau Bac+4
    BAC_PLUS_5 = 'Bac +5'             // Niveau Bac+5
  }
  
  // Statut du contrat
  export enum ContractStatus {
    EN_PREPARATION = 'En préparation', // Contrat en préparation
    EN_SIGNATURE = 'En signature',     // Contrat en cours de signature
    SIGNE = 'Signé',                   // Contrat signé
    TERMINE = 'Terminé',               // Contrat terminé
    RESILIE = 'Résilié'                // Contrat résilié
  }
  
  // Rôle des utilisateurs
  export enum ERole {
    ADMIN = 'Admin',                  // Rôle administrateur
    MANAGER = 'Manager',              // Rôle manager
    RH = 'RH',                        // Rôle ressources humaines
    STAGIAIRE = 'Stagiaire',          // Rôle stagiaire
    DFC = 'DFC',                      // Rôle financier
    ASSISTANT_GWTE = 'Assistant GWTE' // Rôle assistant
  }
