// src/app/models/chat-message.ts
export interface ChatMessage {
  id: string;
  content: string;
  sender: 'user' | 'assistant';
  timestamp: Date;
  showFull?: boolean;
}

export type UserRole = 'candidate' | 'evaluator' | 'hr';

export interface SuggestedQuestion {
  text: string;
  forRoles: UserRole[];
}
