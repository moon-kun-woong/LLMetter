import api from './api';
import type { ApiResponse, EmotionGraphResponse } from '../types/index';

export const emotionService = {
  getEmotionGraph: async (startDate: string, endDate: string): Promise<EmotionGraphResponse> => {
    const response = await api.get<ApiResponse<EmotionGraphResponse>>('/emotions/graph', {
      params: { startDate, endDate },
    });
    return response.data.data!;
  },

  getEmotionStatistics: async (startDate: string, endDate: string): Promise<any> => {
    const response = await api.get<ApiResponse<any>>('/emotions/statistics', {
      params: { startDate, endDate },
    });
    return response.data.data!;
  },
};
