import { useAuthStore } from '../store/authStore';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import EmotionGraph from '../components/EmotionGraph';

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user);
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await authService.logout();
      clearAuth();
      navigate('/login');
    } catch (err) {
      console.error('Logout failed:', err);
      clearAuth();
      navigate('/login');
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-purple-600">LLMetter</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-700">{user?.email}</span>
            <button
              onClick={handleLogout}
              className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition"
            >
              로그아웃
            </button>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <EmotionGraph />
        </div>

        <div className="flex justify-center gap-4">
          <button
            onClick={() => navigate('/record')}
            className="bg-gradient-to-r from-purple-500 to-pink-500 text-white px-8 py-4 rounded-full text-lg font-bold hover:shadow-xl transform hover:scale-105 transition"
          >
            일기 작성하기
          </button>
          <button
            onClick={() => navigate('/diaries')}
            className="bg-white border-2 border-purple-500 text-purple-500 px-8 py-4 rounded-full text-lg font-bold hover:shadow-xl transform hover:scale-105 transition"
          >
            일기 목록 보기
          </button>
        </div>
      </div>
    </div>
  );
}
