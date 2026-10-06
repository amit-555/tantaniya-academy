import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Header } from './components/Header';
import { BottomNav, NavTab } from './components/BottomNav';
import { HomeScreen } from './pages/HomeScreen';
import { CbrtTestScreen } from './pages/CbrtTestScreen';
import { TestResultScreen } from './pages/TestResultScreen';
import { AdminDashboard } from './pages/AdminDashboard';
import { CurrentAffairsScreen } from './pages/CurrentAffairsScreen';
import { StudyMaterialScreen } from './pages/StudyMaterialScreen';
import { LoginScreen } from './pages/LoginScreen';
import { UserProfileSettingsScreen } from './pages/UserProfileSettingsScreen';
import {
  INITIAL_EXAMS,
  INITIAL_TESTS,
  INITIAL_QUESTIONS,
  INITIAL_CURRENT_AFFAIRS,
  INITIAL_GK,
  INITIAL_STUDY_MATERIAL
} from './data/mockInitialData';
import { Exam, Test, Question, TestAttempt, CurrentAffairsItem, GkItem, StudyMaterialItem } from './types';

function MainApp() {
  const { isAdmin } = useAuth();

  // App Data State
  const [exams, setExams] = useState<Exam[]>(INITIAL_EXAMS);
  const [tests, setTests] = useState<Test[]>(INITIAL_TESTS);
  const [questions, setQuestions] = useState<Question[]>(INITIAL_QUESTIONS);
  const [currentAffairs, setCurrentAffairs] = useState<CurrentAffairsItem[]>(INITIAL_CURRENT_AFFAIRS);
  const [gkItems, setGkItems] = useState<GkItem[]>(INITIAL_GK);
  const [studyMaterials, setStudyMaterials] = useState<StudyMaterialItem[]>(INITIAL_STUDY_MATERIAL);

  // Navigation & View State
  const [currentTab, setCurrentTab] = useState<NavTab>('home');
  const [selectedExamId, setSelectedExamId] = useState<number>(1);
  const [activeTest, setActiveTest] = useState<Test | null>(null);
  const [lastAttempt, setLastAttempt] = useState<TestAttempt | null>(null);
  const [showAdminModal, setShowAdminModal] = useState<boolean>(false);
  const [showLoginModal, setShowLoginModal] = useState<boolean>(false);
  const [showProfileModal, setShowProfileModal] = useState<boolean>(false);

  // Test Handlers
  const handleStartTest = (test: Test) => {
    setActiveTest(test);
    setLastAttempt(null);
  };

  const handleFinishTest = (attempt: TestAttempt) => {
    setLastAttempt(attempt);
    setActiveTest(null);
  };

  const handleRetakeTest = () => {
    if (lastAttempt) {
      const test = tests.find(t => t.id === lastAttempt.testId) || tests[0];
      setLastAttempt(null);
      setActiveTest(test);
    }
  };

  // CMS Handlers
  const handleAddExam = (newExam: Exam) => {
    setExams(prev => [newExam, ...prev]);
  };

  const handleAddTest = (newTest: Test) => {
    setTests(prev => [newTest, ...prev]);
  };

  const handleAddQuestion = (newQ: Question) => {
    setQuestions(prev => [newQ, ...prev]);
  };

  const handleDeleteQuestion = (qId: number) => {
    setQuestions(prev => prev.filter(q => q.id !== qId));
  };

  // 1. If currently inside active CBRT Test
  if (activeTest) {
    const testQuestions = questions.filter(q => q.examId === activeTest.examId);
    return (
      <CbrtTestScreen
        test={activeTest}
        questions={testQuestions.length > 0 ? testQuestions : questions}
        onFinishTest={handleFinishTest}
        onExit={() => setActiveTest(null)}
      />
    );
  }

  // 2. If viewing Test Results Scorecard
  if (lastAttempt) {
    const testQuestions = questions.filter(q => q.examId === (tests.find(t => t.id === lastAttempt.testId)?.examId || 1));
    return (
      <TestResultScreen
        attempt={lastAttempt}
        questions={testQuestions.length > 0 ? testQuestions : questions}
        onBackToHome={() => setLastAttempt(null)}
        onRetakeTest={handleRetakeTest}
      />
    );
  }

  // 3. If Admin tab selected or Admin modal open
  if (currentTab === 'admin' || showAdminModal) {
    return (
      <AdminDashboard
        exams={exams}
        tests={tests}
        questions={questions}
        currentAffairs={currentAffairs}
        gkItems={gkItems}
        studyMaterials={studyMaterials}
        onAddExam={handleAddExam}
        onAddTest={handleAddTest}
        onAddQuestion={handleAddQuestion}
        onDeleteQuestion={handleDeleteQuestion}
        onClose={() => {
          setShowAdminModal(false);
          if (currentTab === 'admin') setCurrentTab('home');
        }}
      />
    );
  }

  return (
    <div className="min-h-screen bg-slate-100 text-slate-900 font-sans flex flex-col">
      <Header
        onOpenLogin={() => setShowLoginModal(true)}
        onOpenAdmin={() => setShowAdminModal(true)}
        onOpenProfile={() => setShowProfileModal(true)}
      />

      <main className="flex-1">
        {currentTab === 'home' && (
          <HomeScreen
            exams={exams}
            tests={tests}
            currentAffairs={currentAffairs}
            gkItems={gkItems}
            selectedExamId={selectedExamId}
            onSelectExam={setSelectedExamId}
            onStartTest={handleStartTest}
            onNavigateTab={setCurrentTab}
          />
        )}

        {currentTab === 'tests' && (
          <div className="max-w-4xl mx-auto px-4 py-4 pb-24 space-y-4">
            <h2 className="text-base sm:text-lg font-bold text-slate-900">
              તમામ CBRT મોક ટેસ્ટ ({tests.length})
            </h2>
            <div className="space-y-3">
              {tests.map(test => (
                <div
                  key={test.id}
                  className="bg-white border border-slate-200/90 rounded-2xl p-4 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-3"
                >
                  <div>
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-blue-100 text-blue-800">
                      {exams.find(e => e.id === test.examId)?.title || 'પરીક્ષા'}
                    </span>
                    <h3 className="font-bold text-slate-900 text-sm mt-1">{test.title}</h3>
                    <p className="text-xs text-slate-600 mt-0.5">{test.description}</p>
                    <div className="flex gap-3 text-[11px] text-slate-500 mt-2">
                      <span>પ્રશ્નો: {test.totalQuestions}</span>
                      <span>ગુણ: {test.totalMarks}</span>
                      <span>સમય: {test.durationMinutes} મિનિટ</span>
                      <span className="text-rose-600">નેગેટિવ: -{test.negativeMarking}</span>
                    </div>
                  </div>
                  <button
                    onClick={() => handleStartTest(test)}
                    className="px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs shrink-0 shadow-sm"
                  >
                    ટેસ્ટ શરૂ કરો
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {currentTab === 'ca' && (
          <CurrentAffairsScreen items={currentAffairs} />
        )}

        {currentTab === 'materials' && (
          <StudyMaterialScreen materials={studyMaterials} gkItems={gkItems} />
        )}
      </main>

      <BottomNav currentTab={currentTab} onTabChange={setCurrentTab} />

      {showLoginModal && (
        <LoginScreen onClose={() => setShowLoginModal(false)} />
      )}

      {showProfileModal && (
        <UserProfileSettingsScreen onClose={() => setShowProfileModal(false)} />
      )}
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <MainApp />
    </AuthProvider>
  );
}
