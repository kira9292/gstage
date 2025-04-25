// src/app/models/chat-message.ts
export interface ChatMessage {
  id: string;
  content: string;
  sender: 'user' | 'assistant';
  timestamp: Date;
  showFull?: boolean;
  metadata?: {
    sentiment: 'neutral' | 'positive' | 'negative';
    topics: string[];
    engagementLevel: number;
  };
}

export type UserRole = 'candidate' | 'evaluator' | 'hr';

export interface SuggestedQuestion {
  text: string;
  forRoles: UserRole[];
}
