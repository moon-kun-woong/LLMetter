import { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { emotionService } from '../services/emotionService';
import type { EmotionGraphPoint } from '../types/index';

export default function EmotionGraph() {
  const [data, setData] = useState<EmotionGraphPoint[]>([]);
  const [loading, setLoading] = useState(true);
  const [period, setPeriod] = useState<'week' | 'month' | 'all'>('week');

  useEffect(() => {
    loadGraphData();
  }, [period]);

  const loadGraphData = async () => {
    setLoading(true);
    try {
      // 로컬 시간대의 날짜 문자열 생성 함수
      const toLocalDateTimeString = (date: Date): string => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hour = String(date.getHours()).padStart(2, '0');
        const minute = String(date.getMinutes()).padStart(2, '0');
        const second = String(date.getSeconds()).padStart(2, '0');
        const ms = String(date.getMilliseconds()).padStart(3, '0');
        return `${year}-${month}-${day}T${hour}:${minute}:${second}.${ms}`;
      };

      // endDate를 오늘 23:59:59로 설정하여 모든 일기 포함
      const now = new Date();
      const endOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59, 999);
      const endDate = toLocalDateTimeString(endOfDay);

      let startDate: string;

      switch (period) {
        case 'week':
          const weekStart = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
          weekStart.setHours(0, 0, 0, 0);
          startDate = toLocalDateTimeString(weekStart);
          break;
        case 'month':
          const monthStart = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          monthStart.setHours(0, 0, 0, 0);
          startDate = toLocalDateTimeString(monthStart);
          break;
        case 'all':
          const yearStart = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
          yearStart.setHours(0, 0, 0, 0);
          startDate = toLocalDateTimeString(yearStart);
          break;
      }

      const response = await emotionService.getEmotionGraph(startDate, endDate);
      setData(response.data);
    } catch (error) {
      console.error('Failed to load emotion graph:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatData = data.map((point) => {
    const dateObj = new Date(point.date);
    return {
      date: dateObj.toLocaleString('ko-KR', {
        month: 'numeric',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).replace(/\. /g, '/').replace(' ', ' '),
      fullDate: dateObj.toLocaleString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }),
      score: point.emotionScore,
      emotion: point.primaryEmotion,
    };
  });

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-800">감정 변화 그래프</h2>
        <div className="flex gap-2">
          {(['week', 'month', 'all'] as const).map((p) => (
            <button
              key={p}
              onClick={() => setPeriod(p)}
              className={`px-4 py-2 rounded-lg text-sm font-semibold transition ${
                period === p
                  ? 'bg-gray-900 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {p === 'week' ? '이번 주' : p === 'month' ? '이번 달' : '전체'}
            </button>
          ))}
        </div>
      </div>

      {loading ? (
        <div className="h-64 flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-800"></div>
        </div>
      ) : data.length === 0 ? (
        <div className="h-64 flex items-center justify-center text-gray-500">
          <p>아직 데이터가 없습니다. 일기를 작성해보세요!</p>
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={formatData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis domain={[-20, 20]} />
            <Tooltip
              content={({ active, payload }) => {
                if (active && payload && payload.length) {
                  return (
                    <div className="bg-white p-3 border border-gray-200 rounded-lg shadow-lg">
                      <p className="font-semibold text-sm">{payload[0].payload.fullDate}</p>
                      <p className="text-gray-800 font-bold mt-1">점수: {payload[0].value}</p>
                      <p className="text-sm text-gray-600 mt-1">{payload[0].payload.emotion}</p>
                    </div>
                  );
                }
                return null;
              }}
            />
            <Line
              type="monotone"
              dataKey="score"
              stroke="#1f2937"
              strokeWidth={2}
              dot={{ fill: '#1f2937', r: 4 }}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
