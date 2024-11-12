export interface RegisterData {
    appUser: {
      username: string;
      email: string;
      password: string;
      name: string;
      firstName: string;
    },
    role: {
      name: string; // Utiliser le rôle sélectionné dans le formulaire
    }
  }
  
  export interface LoginData {
    username: string;
    password: string;
  }

  export interface PasswordCriteria {
    length: boolean;
    uppercase: boolean;
    lowercase: boolean;
    number: boolean;
    special: boolean;
  }
  