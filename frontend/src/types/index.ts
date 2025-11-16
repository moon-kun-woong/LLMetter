export interface User {
  id: number;
  email: string;
  provider: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: {
    code: string;
    message: string;
    details?: Record<string, any>;
  };
}

export interface DiaryEntry {
  id: number;
  originalText: string | null;
  editedText: string | null;
  status: 'PROCESSING' | 'COMPLETED' | 'FAILED';
  createdAt: string;
  updatedAt: string;
  emotionAnalysis: EmotionAnalysis | null;
}

export interface EmotionAnalysis {
  id: number;
  emotionScore: number;
  primaryEmotion: string;
  emotionDistribution: Record<string, number>;
  keywords: string[];
  summary: string | null;
  createdAt: string;
}

export interface EmotionGraphPoint {
  date: string;
  emotionScore: number;
  primaryEmotion: string;
}

export interface EmotionGraphResponse {
  data: EmotionGraphPoint[];
  averageScore: number | null;
  period: string;
}
