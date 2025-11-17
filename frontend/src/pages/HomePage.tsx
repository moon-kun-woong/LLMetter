import { useNavigate } from 'react-router-dom';

export default function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-700 via-gray-800 to-gray-900">
      <div className="container mx-auto px-4 py-16">
        <div className="text-center text-white mb-16">
          <h1 className="text-6xl font-bold mb-6 animate-fade-in">
            LLMetter
          </h1>
          <p className="text-2xl mb-4 opacity-90">
            음성으로 일기를 작성하고, AI가 감정을 분석합니다
          </p>
          <p className="text-lg opacity-75">
            당신의 감정 변화를 추적하고 시각화하세요
          </p>
        </div>

        <div className="max-w-4xl mx-auto grid md:grid-cols-3 gap-8 mb-16">
          <FeatureCard
            icon="🎤"
            title="음성 녹음"
            description="간편하게 음성으로 하루를 기록하세요"
          />
          <FeatureCard
            icon="🤖"
            title="AI 감정 분석"
            description="Claude AI가 12가지 감정을 정밀하게 분석합니다"
          />
          <FeatureCard
            icon="📊"
            title="감정 그래프"
            description="시간에 따른 감정 변화를 한눈에 확인하세요"
          />
        </div>

        <div className="text-center">
          <button
            onClick={() => navigate('/login')}
            className="bg-white text-gray-800 px-12 py-4 rounded-full text-xl font-bold hover:bg-opacity-90 transform hover:scale-105 transition shadow-2xl"
          >
            시작하기
          </button>
        </div>

        <div className="mt-16 text-center text-white text-sm opacity-75">
          <p>© 2025 LLMetter. Powered by Claude & Whisper AI</p>
        </div>
      </div>
    </div>
  );
}

function FeatureCard({ icon, title, description }: { icon: string; title: string; description: string }) {
  return (
    <div className="bg-white bg-opacity-10 backdrop-blur-lg rounded-xl p-6 text-white hover:bg-opacity-20 transition">
      <div className="text-5xl mb-4">{icon}</div>
      <h3 className="text-xl font-bold mb-2">{title}</h3>
      <p className="opacity-90">{description}</p>
    </div>
  );
}
