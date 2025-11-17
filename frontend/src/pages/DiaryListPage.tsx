import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { diaryService } from '../services/diaryService';
import type { DiaryEntry } from '../types/index';

export default function DiaryListPage() {
  const navigate = useNavigate();
  const [diaries, setDiaries] = useState<DiaryEntry[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDiaries();
  }, []);

  const loadDiaries = async () => {
    try {
      const response = await diaryService.getDiaries();
      setDiaries(response.diaries);
    } catch (error) {
      console.error('Failed to load diaries:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRetry = async (e: React.MouseEvent, diaryId: number) => {
    e.stopPropagation(); // 카드 클릭 이벤트 방지
    try {
      await diaryService.retrySTT(diaryId);
      alert('재처리를 시작했습니다. 잠시 후 다시 확인해주세요.');
      // 3초 후 목록 새로고침
      setTimeout(() => {
        loadDiaries();
      }, 3000);
    } catch (error) {
      alert('재처리 요청에 실패했습니다.');
    }
  };

  const getEmotionColor = (score: number) => {
    if (score > 10) return 'text-gray-900';
    if (score > 0) return 'text-gray-700';
    if (score > -10) return 'text-gray-600';
    return 'text-gray-500';
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return <span className="px-2 py-1 bg-gray-200 text-gray-800 text-xs rounded-full">완료</span>;
      case 'PROCESSING':
        return <span className="px-2 py-1 bg-gray-100 text-gray-600 text-xs rounded-full">처리중</span>;
      case 'FAILED':
        return <span className="px-2 py-1 bg-gray-300 text-gray-700 text-xs rounded-full">실패</span>;
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <button
            onClick={() => navigate('/dashboard')}
            className="text-gray-800 hover:text-gray-900 font-semibold"
          >
            ← 대시보드
          </button>
        </div>
      </nav>

      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold text-gray-800">내 일기</h1>
            <button
              onClick={() => navigate('/record')}
              className="bg-gray-800 text-white px-6 py-2 rounded-lg hover:bg-gray-900 hover:shadow-lg transition"
            >
              + 새 일기 작성
            </button>
          </div>

          {loading ? (
            <div className="text-center py-16">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-800 mx-auto"></div>
              <p className="mt-4 text-gray-600">로딩 중...</p>
            </div>
          ) : diaries.length === 0 ? (
            <div className="bg-white rounded-xl shadow-lg p-16 text-center">
              <p className="text-xl text-gray-500 mb-4">작성된 일기가 없습니다</p>
              <p className="text-gray-400">첫 일기를 작성해보세요!</p>
            </div>
          ) : (
            <div className="space-y-4">
              {diaries.map((diary) => (
                <div
                  key={diary.id}
                  onClick={() => navigate(`/diary/${diary.id}`)}
                  className="bg-white rounded-lg shadow p-6 hover:shadow-xl transition cursor-pointer"
                >
                  <div className="flex justify-between items-start mb-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <span className="text-sm text-gray-500">
                          {new Date(diary.createdAt).toLocaleDateString('ko-KR', {
                            year: 'numeric',
                            month: 'long',
                            day: 'numeric',
                          })}
                        </span>
                        {getStatusBadge(diary.status)}
                        {diary.status === 'FAILED' && (
                          <button
                            onClick={(e) => handleRetry(e, diary.id)}
                            className="ml-2 px-3 py-1 bg-gray-700 text-white text-xs rounded hover:bg-gray-800 transition"
                          >
                            재처리
                          </button>
                        )}
                      </div>
                      <p className="text-gray-700 line-clamp-2">
                        {diary.editedText || diary.originalText || '처리 중...'}
                      </p>
                    </div>
                    {diary.emotionAnalysis && (
                      <div className="ml-4 text-right">
                        <div
                          className={`text-3xl font-bold ${getEmotionColor(
                            diary.emotionAnalysis.emotionScore
                          )}`}
                        >
                          {diary.emotionAnalysis.emotionScore > 0 ? '+' : ''}
                          {diary.emotionAnalysis.emotionScore}
                        </div>
                        <div className="text-sm text-gray-500">
                          {diary.emotionAnalysis.primaryEmotion}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
