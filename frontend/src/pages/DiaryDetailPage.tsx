import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { diaryService } from '../services/diaryService';
import type { DiaryEntry } from '../types/index';

export default function DiaryDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [diary, setDiary] = useState<DiaryEntry | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [editText, setEditText] = useState('');

  useEffect(() => {
    if (id) {
      loadDiary(parseInt(id));
    }
  }, [id]);

  const loadDiary = async (diaryId: number) => {
    try {
      const data = await diaryService.getDiary(diaryId);
      setDiary(data);
      setEditText(data.editedText || data.originalText || '');
    } catch (error) {
      console.error('Failed to load diary:', error);
      alert('일기를 불러오는데 실패했습니다.');
      navigate('/diaries');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    if (!diary) return;

    try {
      await diaryService.updateDiary(diary.id, editText);
      setEditing(false);
      loadDiary(diary.id);
    } catch (error) {
      alert('수정에 실패했습니다.');
    }
  };

  const handleDelete = async () => {
    if (!diary || !confirm('정말 삭제하시겠습니까?')) return;

    try {
      await diaryService.deleteDiary(diary.id);
      alert('일기가 삭제되었습니다.');
      navigate('/diaries');
    } catch (error) {
      alert('삭제에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-500"></div>
      </div>
    );
  }

  if (!diary) {
    return <div>일기를 찾을 수 없습니다.</div>;
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <button
            onClick={() => navigate('/diaries')}
            className="text-purple-600 hover:text-purple-700 font-semibold"
          >
            ← 목록으로
          </button>
        </div>
      </nav>

      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto grid md:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="md:col-span-2">
            <div className="bg-white rounded-xl shadow-lg p-8">
              <div className="flex justify-between items-start mb-6">
                <div>
                  <h1 className="text-2xl font-bold text-gray-800 mb-2">
                    {new Date(diary.createdAt).toLocaleDateString('ko-KR', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                    })}
                  </h1>
                  <p className="text-sm text-gray-500">
                    {new Date(diary.createdAt).toLocaleTimeString('ko-KR')}
                  </p>
                </div>
                <div className="flex gap-2">
                  {!editing && (
                    <>
                      <button
                        onClick={() => setEditing(true)}
                        className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition text-sm"
                      >
                        수정
                      </button>
                      <button
                        onClick={handleDelete}
                        className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition text-sm"
                      >
                        삭제
                      </button>
                    </>
                  )}
                </div>
              </div>

              {editing ? (
                <div>
                  <textarea
                    value={editText}
                    onChange={(e) => setEditText(e.target.value)}
                    className="w-full h-64 p-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 outline-none"
                  />
                  <div className="flex gap-2 mt-4">
                    <button
                      onClick={handleSave}
                      className="px-6 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition"
                    >
                      저장
                    </button>
                    <button
                      onClick={() => {
                        setEditing(false);
                        setEditText(diary.editedText || diary.originalText || '');
                      }}
                      className="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition"
                    >
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <div className="prose max-w-none">
                  <p className="text-gray-700 whitespace-pre-wrap leading-relaxed">
                    {diary.editedText || diary.originalText || '처리 중입니다...'}
                  </p>
                </div>
              )}
            </div>
          </div>

          {/* Sidebar - Emotion Analysis */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-xl shadow-lg p-6 sticky top-4">
              <h2 className="text-xl font-bold text-gray-800 mb-4">감정 분석</h2>

              {diary.emotionAnalysis ? (
                <div className="space-y-4">
                  <div className="text-center p-6 bg-gradient-to-br from-purple-50 to-pink-50 rounded-lg">
                    <div className="text-5xl font-bold text-purple-600 mb-2">
                      {diary.emotionAnalysis.emotionScore > 0 ? '+' : ''}
                      {diary.emotionAnalysis.emotionScore}
                    </div>
                    <div className="text-sm text-gray-600">감정 점수</div>
                  </div>

                  <div>
                    <h3 className="font-semibold text-gray-700 mb-2">주요 감정</h3>
                    <div className="px-4 py-2 bg-purple-100 text-purple-700 rounded-lg text-center font-semibold">
                      {diary.emotionAnalysis.primaryEmotion}
                    </div>
                  </div>

                  {diary.emotionAnalysis.keywords && diary.emotionAnalysis.keywords.length > 0 && (
                    <div>
                      <h3 className="font-semibold text-gray-700 mb-2">키워드</h3>
                      <div className="flex flex-wrap gap-2">
                        {diary.emotionAnalysis.keywords.map((keyword, idx) => (
                          <span
                            key={idx}
                            className="px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm"
                          >
                            {keyword}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {diary.emotionAnalysis.summary && (
                    <div>
                      <h3 className="font-semibold text-gray-700 mb-2">요약</h3>
                      <p className="text-sm text-gray-600 leading-relaxed">
                        {diary.emotionAnalysis.summary}
                      </p>
                    </div>
                  )}
                </div>
              ) : (
                <div className="text-center py-8 text-gray-500">
                  <p>감정 분석 처리 중...</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
