import React from 'react';
import { Exam, Test, CurrentAffairsItem, GkItem } from '../types';
import { Play, Sparkles, BookOpen, Compass, Award, CheckCircle2, ChevronRight, ShieldCheck, Flame } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface HomeScreenProps {
  exams: Exam[];
  tests: Test[];
  currentAffairs: CurrentAffairsItem[];
  gkItems: GkItem[];
  selectedExamId: number;
  onSelectExam: (examId: number) => void;
  onStartTest: (test: Test) => void;
  onNavigateTab: (tab: any) => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  exams,
  tests,
  currentAffairs,
  gkItems,
  selectedExamId,
  onSelectExam,
  onStartTest,
  onNavigateTab
}) => {
  const { user, isAdmin } = useAuth();
  const activeExam = exams.find(e => e.id === selectedExamId) || exams[0];
  const activeTests = tests.filter(t => t.examId === selectedExamId);

  return (
    <div className="space-y-6 pb-24 max-w-4xl mx-auto px-4 py-4">
      {/* Hero Welcome Banner */}
      <div className="bg-gradient-to-br from-slate-900 via-blue-950 to-slate-900 border border-blue-800/40 rounded-3xl p-5 sm:p-6 text-white shadow-xl relative overflow-hidden">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-amber-400 text-slate-950">
                <Flame className="w-3 h-3 fill-slate-950" />
                મિશન ખાખી & વનરક્ષક 2026
              </span>
              {isAdmin && (
                <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500 text-white">
                  <ShieldCheck className="w-3 h-3" />
                  મુખ્ય એડમિન
                </span>
              )}
            </div>

            <h2 className="text-xl sm:text-2xl font-black text-white leading-tight">
              નમસ્તે, {user?.name || "સ્પર્ધાત્મક પરીક્ષા મિત્ર"}!
            </h2>
            <p className="text-xs sm:text-sm text-slate-300 mt-1 max-w-md">
              તન્તાણિયા એકેડેમી ઓનલાઇન મોક ટેસ્ટ પોર્ટલ પર તમારું સ્વાગત છે. CBRT પદ્ધતિથી તમારી તૈયારીને મજબૂત બનાવો.
            </p>
          </div>

          {activeTests.length > 0 && (
            <button
              onClick={() => onStartTest(activeTests[0])}
              className="inline-flex items-center justify-center gap-2 px-5 py-3 rounded-2xl bg-gradient-to-r from-amber-400 to-amber-500 hover:from-amber-300 hover:to-amber-400 text-slate-950 font-black text-xs sm:text-sm shadow-lg shadow-amber-500/25 transition-transform active:scale-95 shrink-0"
            >
              <Play className="w-4 h-4 fill-slate-950" />
              <span>ટેસ્ટ શરૂ કરો (200 પ્રશ્નો)</span>
            </button>
          )}
        </div>
      </div>

      {/* Dynamic Exam Selector Pills */}
      <div>
        <div className="flex items-center justify-between mb-2.5">
          <h3 className="font-bold text-sm text-slate-900 flex items-center gap-1.5">
            <Award className="w-4 h-4 text-blue-600" />
            <span>તમારી લક્ષ્ય પરીક્ષા પસંદ કરો</span>
          </h3>
          <span className="text-[11px] text-slate-500">કુલ {exams.length} પરીક્ષાઓ ઉપલબ્ધ</span>
        </div>

        <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-none">
          {exams.map(exam => {
            const isSelected = exam.id === selectedExamId;
            return (
              <button
                key={exam.id}
                onClick={() => onSelectExam(exam.id)}
                className={`px-4 py-2.5 rounded-2xl text-xs font-bold shrink-0 transition-all border ${
                  isSelected
                    ? 'bg-blue-600 text-white border-blue-600 shadow-md shadow-blue-500/20 scale-[1.02]'
                    : 'bg-white text-slate-700 border-slate-200 hover:bg-slate-50'
                }`}
              >
                {exam.title}
              </button>
            );
          })}
        </div>
      </div>

      {/* Selected Exam Information Card */}
      <div className="bg-white border border-slate-200/80 rounded-2xl p-4 shadow-sm">
        <div className="flex items-center justify-between mb-1.5">
          <h4 className="font-bold text-slate-900 text-sm">{activeExam?.title}</h4>
          <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-100 text-amber-800">
            {activeExam?.code}
          </span>
        </div>
        <p className="text-xs text-slate-600 mb-3">{activeExam?.description}</p>

        <div className="grid grid-cols-3 gap-2 text-center text-xs">
          <div className="p-2 bg-slate-50 rounded-xl border border-slate-100">
            <div className="text-[10px] text-slate-500">કુલ ગુણ</div>
            <div className="font-bold text-slate-900">{activeExam?.totalMarks}</div>
          </div>
          <div className="p-2 bg-slate-50 rounded-xl border border-slate-100">
            <div className="text-[10px] text-slate-500">સમય</div>
            <div className="font-bold text-slate-900">{activeExam?.durationMinutes} મિનિટ</div>
          </div>
          <div className="p-2 bg-slate-50 rounded-xl border border-slate-100">
            <div className="text-[10px] text-slate-500">નેગેટિવ</div>
            <div className="font-bold text-rose-600">-{activeExam?.negativeMarking}</div>
          </div>
        </div>
      </div>

      {/* Available CBRT Mock Tests List */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-bold text-sm text-slate-900 flex items-center gap-1.5">
            <Play className="w-4 h-4 text-emerald-600 fill-emerald-600" />
            <span>લાઈવ CBRT મોક ટેસ્ટ</span>
          </h3>
          <button
            onClick={() => onNavigateTab('tests')}
            className="text-xs text-blue-600 hover:underline flex items-center gap-0.5"
          >
            <span>બધા જુઓ</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>

        <div className="space-y-3">
          {activeTests.map(test => (
            <div
              key={test.id}
              className="bg-white border border-slate-200/90 rounded-2xl p-4 shadow-sm hover:shadow-md transition-shadow flex flex-col sm:flex-row sm:items-center justify-between gap-3"
            >
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-100 text-emerald-800">
                    CBRT પદ્ધતિ
                  </span>
                  <span className="text-[11px] text-slate-500">
                    {test.totalQuestions} પ્રશ્નો • {test.durationMinutes} મિનિટ
                  </span>
                </div>
                <h4 className="font-bold text-slate-900 text-sm">{test.title}</h4>
                <p className="text-xs text-slate-600 mt-0.5 line-clamp-1">{test.description}</p>
              </div>

              <button
                onClick={() => onStartTest(test)}
                className="px-4 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs flex items-center justify-center gap-1.5 shadow-sm transition-colors shrink-0"
              >
                <span>ટેસ્ટ આપો</span>
                <ChevronRight className="w-3.5 h-3.5" />
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Daily Current Affairs & GK Teaser */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        {/* Current Affairs Card */}
        <div className="bg-white border border-slate-200/90 rounded-2xl p-4 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="inline-flex items-center gap-1 text-xs font-bold text-blue-600">
                <Compass className="w-4 h-4" />
                તાજા વર્તમાન પ્રવાહો
              </span>
              <span className="text-[10px] text-slate-400">ઓક્ટોબર 2026</span>
            </div>
            {currentAffairs[0] && (
              <div>
                <h5 className="font-bold text-slate-900 text-xs line-clamp-2">
                  {currentAffairs[0].headline}
                </h5>
                <p className="text-[11px] text-slate-600 mt-1 line-clamp-2">
                  {currentAffairs[0].description}
                </p>
              </div>
            )}
          </div>
          <button
            onClick={() => onNavigateTab('ca')}
            className="mt-3 text-xs text-blue-600 hover:text-blue-700 font-semibold flex items-center gap-1 pt-2 border-t border-slate-100"
          >
            <span>વધુ વાંચો</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* GK One-Liners Card */}
        <div className="bg-white border border-slate-200/90 rounded-2xl p-4 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="inline-flex items-center gap-1 text-xs font-bold text-amber-600">
                <BookOpen className="w-4 h-4" />
                ગુજરાત જનરલ નોલેજ (GK)
              </span>
              <span className="text-[10px] text-slate-400">વનલાઇનર ફેક્ટ્સ</span>
            </div>
            {gkItems[0] && (
              <div>
                <h5 className="font-bold text-slate-900 text-xs line-clamp-2">
                  {gkItems[0].title}
                </h5>
                <p className="text-[11px] text-slate-600 mt-1 line-clamp-2">
                  {gkItems[0].oneLinerFact}
                </p>
              </div>
            )}
          </div>
          <button
            onClick={() => onNavigateTab('materials')}
            className="mt-3 text-xs text-amber-600 hover:text-amber-700 font-semibold flex items-center gap-1 pt-2 border-t border-slate-100"
          >
            <span>મટીરીયલ & નોટ્સ જુઓ</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  );
};
