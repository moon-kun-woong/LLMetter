import api from './api';
import type { ApiResponse, DiaryEntry } from '../types/index';

export const diaryService = {
  uploadVoice: async (audioBlob: Blob): Promise<DiaryEntry> => {
    const formData = new FormData();
    formData.append('audio', audioBlob, 'recording.webm');

    const response = await api.post<ApiResponse<DiaryEntry>>('/diaries', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data.data!;
  },

  getDiaries: async (page = 0, size = 20): Promise<any> => {
    const response = await api.get<ApiResponse<any>>('/diaries', {
      params: { page, size },
    });
    return response.data.data!;
  },

  getDiary: async (id: number): Promise<DiaryEntry> => {
    const response = await api.get<ApiResponse<DiaryEntry>>(`/diaries/${id}`);
    return response.data.data!;
  },

  updateDiary: async (id: number, editedText: string): Promise<DiaryEntry> => {
    const response = await api.patch<ApiResponse<DiaryEntry>>(`/diaries/${id}`, {
      editedText,
    });
    return response.data.data!;
  },

  deleteDiary: async (id: number): Promise<void> => {
    await api.delete(`/diaries/${id}`);
  },

  retrySTT: async (id: number): Promise<void> => {
    await api.post(`/diaries/${id}/retry-stt`);
  },
};
