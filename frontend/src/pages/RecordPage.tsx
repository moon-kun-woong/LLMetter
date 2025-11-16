import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAudioRecorder } from '../hooks/useAudioRecorder';
import { diaryService } from '../services/diaryService';

export default function RecordPage() {
  const navigate = useNavigate();
  const {
    isRecording,
    recordingTime,
    audioBlob,
    startRecording,
    stopRecording,
    resetRecording,
  } = useAudioRecorder();

  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleUpload = async () => {
    if (!audioBlob) return;

    setUploading(true);
    setError('');

    try {
      await diaryService.uploadVoice(audioBlob);
      alert('일기가 업로드되었습니다! AI가 분석 중입니다.');
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.error?.message || '업로드에 실패했습니다.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="container mx-auto px-4 py-4">
          <button
            onClick={() => navigate('/dashboard')}
            className="text-purple-600 hover:text-purple-700 font-semibold"
          >
            ← 대시보드로 돌아가기
          </button>
        </div>
      </nav>

      <div className="container mx-auto px-4 py-8">
        <div className="max-w-2xl mx-auto">
          <div className="bg-white rounded-2xl shadow-lg p-8">
            <h1 className="text-3xl font-bold text-gray-800 mb-6 text-center">
              음성 일기 작성
            </h1>

            {/* Recording Visualizer */}
            <div className="mb-8 p-8 bg-gradient-to-br from-purple-50 to-pink-50 rounded-xl">
              <div className="text-center mb-4">
                {isRecording ? (
                  <div className="flex items-center justify-center gap-2">
                    <div className="w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
                    <span className="text-2xl font-mono font-bold text-gray-800">
                      {formatTime(recordingTime)}
                    </span>
                  </div>
                ) : audioBlob ? (
                  <div className="text-green-600 font-semibold">
                    녹음 완료! ({formatTime(recordingTime)})
                  </div>
                ) : (
                  <div className="text-gray-500">
                    녹음 버튼을 눌러 시작하세요
                  </div>
                )}
              </div>

              {/* Waveform visualization placeholder */}
              {isRecording && (
                <div className="flex items-center justify-center gap-1 h-20">
                  {[...Array(20)].map((_, i) => (
                    <div
                      key={i}
                      className="w-1 bg-purple-500 rounded-full animate-pulse"
                      style={{
                        height: `${Math.random() * 60 + 20}px`,
                        animationDelay: `${i * 0.1}s`,
                      }}
                    ></div>
                  ))}
                </div>
              )}
            </div>

            {/* Controls */}
            <div className="flex flex-col items-center gap-4">
              {!audioBlob ? (
                <button
                  onClick={isRecording ? stopRecording : startRecording}
                  className={`w-24 h-24 rounded-full text-white font-bold text-lg shadow-lg transform transition hover:scale-105 ${
                    isRecording
                      ? 'bg-red-500 hover:bg-red-600'
                      : 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600'
                  }`}
                >
                  {isRecording ? '중지' : '녹음'}
                </button>
              ) : (
                <div className="flex gap-4">
                  <button
                    onClick={resetRecording}
                    className="px-6 py-3 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition"
                  >
                    다시 녹음
                  </button>
                  <button
                    onClick={handleUpload}
                    disabled={uploading}
                    className="px-8 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg hover:from-purple-600 hover:to-pink-600 transition disabled:opacity-50"
                  >
                    {uploading ? '업로드 중...' : '일기 저장'}
                  </button>
                </div>
              )}

              {error && (
                <div className="text-red-600 text-sm">{error}</div>
              )}
            </div>

            {/* Instructions */}
            <div className="mt-8 p-4 bg-blue-50 rounded-lg">
              <h3 className="font-semibold text-blue-900 mb-2">사용 방법</h3>
              <ul className="text-sm text-blue-800 space-y-1">
                <li>• 녹음 버튼을 눌러 하루를 이야기하세요</li>
                <li>• 최대 10분까지 녹음 가능합니다</li>
                <li>• 중지 후 일기 저장을 누르면 AI가 감정을 분석합니다</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
