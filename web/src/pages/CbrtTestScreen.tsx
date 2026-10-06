import React, { useState, useEffect, useMemo } from 'react';
import { Test, Question, TestAttempt } from '../types';
import { Clock, CheckCircle2, Bookmark, ArrowLeft, ArrowRight, RotateCcw, Send, AlertTriangle, Layers, Grid } from 'lucide-react';

interface CbrtTestScreenProps {
  test: Test;
  questions: Question[];
  onFinishTest: (attempt: TestAttempt) => void;
  onExit: () => void;
}

export const CbrtTestScreen: React.FC<CbrtTestScreenProps> = ({
  test,
  questions,
  onFinishTest,
  onExit
}) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [reviewed, setReviewed] = useState<Set<number>>(new Set());
  const [visited, setVisited] = useState<Set<number>>(new Set([0]));
  const [timeLeft, setTimeLeft] = useState(test.durationMinutes * 60);
  const [showSubmitModal, setShowSubmitModal] = useState(false);
  const [showPaletteDrawer, setShowPaletteDrawer] = useState(false);

  // Countdown timer
  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          handleSubmitTest();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const currentQ = questions[currentIndex] || questions[0];

  const handleSelectOption = (opt: string) => {
    if (!currentQ) return;
    setAnswers(prev => ({
      ...prev,
      [currentQ.id]: opt
    }));
  };

  const handleClearResponse = () => {
    if (!currentQ) return;
    setAnswers(prev => {
      const copy = { ...prev };
      delete copy[currentQ.id];
      return copy;
    });
  };

  const handleToggleReview = () => {
    if (!currentQ) return;
    setReviewed(prev => {
      const next = new Set(prev);
      if (next.has(currentQ.id)) {
        next.delete(currentQ.id);
      } else {
        next.add(currentQ.id);
      }
      return next;
    });
  };

  const handleNext = () => {
    if (currentIndex < questions.length - 1) {
      const nextIdx = currentIndex + 1;
      setCurrentIndex(nextIdx);
      setVisited(prev => new Set(prev).add(nextIdx));
    }
  };

  const handlePrev = () => {
    if (currentIndex > 0) {
      const prevIdx = currentIndex - 1;
      setCurrentIndex(prevIdx);
      setVisited(prev => new Set(prev).add(prevIdx));
    }
  };

  const handleJumpToQuestion = (idx: number) => {
    setCurrentIndex(idx);
    setVisited(prev => new Set(prev).add(idx));
    setShowPaletteDrawer(false);
  };

  // Evaluation & Results
  const handleSubmitTest = () => {
    let correct = 0;
    let incorrect = 0;
    let unattempted = 0;
    const subjectStats: Record<string, { correct: number; total: number }> = {};

    questions.forEach(q => {
      const userAns = answers[q.id];
      if (!subjectStats[q.subject]) {
        subjectStats[q.subject] = { correct: 0, total: 0 };
      }
      subjectStats[q.subject].total += 1;

      if (!userAns) {
        unattempted += 1;
      } else if (userAns === q.correctOption) {
        correct += 1;
        subjectStats[q.subject].correct += 1;
      } else {
        incorrect += 1;
      }
    });

    const attemptedCount = correct + incorrect;
    const positiveMarks = correct * (test.totalMarks / test.totalQuestions);
    const negativeMarks = incorrect * test.negativeMarking;
    const finalScore = Math.max(0, parseFloat((positiveMarks - negativeMarks).toFixed(2)));
    const percentage = parseFloat(((finalScore / test.totalMarks) * 100).toFixed(2));
    const accuracy = attemptedCount > 0 ? parseFloat(((correct / attemptedCount) * 100).toFixed(2)) : 0;
    const timeUsedSeconds = test.durationMinutes * 60 - timeLeft;

    const attempt: TestAttempt = {
      id: Date.now(),
      userId: 1,
      userName: "વિદ્યાર્થી",
      testId: test.id,
      testTitle: test.title,
      examTitle: "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
      totalQuestions: questions.length,
      attemptedCount,
      correctCount: correct,
      incorrectCount: incorrect,
      unattemptedCount: unattempted,
      positiveMarks,
      negativeMarks,
      finalScore,
      percentage,
      accuracy,
      timeUsedSeconds,
      submittedAt: Date.now(),
      answers,
      subjectPerformance: subjectStats
    };

    onFinishTest(attempt);
  };

  // Palette status calculator
  const getStatus = (qId: number, idx: number) => {
    const isAnswered = !!answers[qId];
    const isRev = reviewed.has(qId);
    const isVis = visited.has(idx);

    if (isAnswered && isRev) return 'answered_review';
    if (isAnswered) return 'answered';
    if (isRev) return 'review';
    if (isVis) return 'not_answered';
    return 'not_visited';
  };

  const answeredCount = Object.keys(answers).length;
  const markedReviewCount = reviewed.size;

  const formatTime = (secs: number) => {
    const h = Math.floor(secs / 3600);
    const m = Math.floor((secs % 3600) / 60);
    const s = secs % 60;
    if (h > 0) {
      return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    }
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans select-none">
      {/* Top CBRT Control Bar */}
      <div className="bg-slate-900 border-b border-slate-800 px-3 py-2.5 flex items-center justify-between sticky top-0 z-20 shadow-md">
        <div className="flex items-center gap-2">
          <button
            onClick={onExit}
            className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 transition-colors"
            title="બહાર નીકળો"
          >
            <ArrowLeft className="w-4 h-4" />
          </button>
          <div>
            <h2 className="text-xs sm:text-sm font-bold text-white max-w-[200px] sm:max-w-xs truncate">
              {test.title}
            </h2>
            <div className="text-[10px] text-amber-400 font-medium">
              પ્રશ્ન {currentIndex + 1} / {questions.length} • ગુણ: {currentQ?.marks || 1}
            </div>
          </div>
        </div>

        {/* Live Timer & Controls */}
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1.5 bg-slate-800/90 border border-amber-500/40 px-2.5 py-1 rounded-lg text-amber-400 font-mono font-bold text-xs sm:text-sm shadow-inner">
            <Clock className="w-3.5 h-3.5 animate-pulse text-amber-400" />
            <span>{formatTime(timeLeft)}</span>
          </div>

          <button
            onClick={() => setShowPaletteDrawer(!showPaletteDrawer)}
            className="p-1.5 rounded-lg bg-blue-600/30 border border-blue-500/40 text-blue-300 text-xs font-semibold flex items-center gap-1 hover:bg-blue-600/50 transition-colors"
          >
            <Grid className="w-4 h-4" />
            <span className="hidden sm:inline">પેલેટ</span>
          </button>

          <button
            onClick={() => setShowSubmitModal(true)}
            className="px-3 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs shadow-md transition-colors"
          >
            સબમિટ
          </button>
        </div>
      </div>

      {/* Main Examination Work Area */}
      <div className="flex-1 max-w-4xl w-full mx-auto p-3 sm:p-5 flex flex-col pb-24">
        {/* Section & Metadata Pill */}
        <div className="flex flex-wrap items-center justify-between gap-2 mb-3 bg-slate-900/60 p-2 rounded-xl border border-slate-800/80">
          <div className="flex items-center gap-2">
            <span className="px-2.5 py-1 rounded-md text-[11px] font-bold bg-blue-950 text-blue-300 border border-blue-800">
              {currentQ?.part}
            </span>
            <span className="px-2 py-0.5 rounded text-[11px] bg-slate-800 text-slate-300 font-medium">
              {currentQ?.subject}
            </span>
          </div>
          <span className="text-[11px] text-slate-400">
            નેગેટિવ માર્કિંગ: <strong className="text-rose-400">-{test.negativeMarking}</strong>
          </span>
        </div>

        {/* Question Text Box (Pure White, High Contrast) */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 sm:p-5 shadow-xl mb-4">
          <div className="text-white text-base sm:text-lg font-semibold leading-relaxed tracking-wide">
            {currentQ?.questionText}
          </div>
        </div>

        {/* Options List (Accessible, High Contrast, Touch-Optimized) */}
        <div className="space-y-3 mb-6">
          {(['A', 'B', 'C', 'D'] as const).map(opt => {
            const optKey = `option${opt}` as keyof Question;
            const optText = currentQ ? (currentQ[optKey] as string) : '';
            const isSelected = answers[currentQ?.id] === opt;

            return (
              <button
                key={opt}
                onClick={() => handleSelectOption(opt)}
                className={`w-full text-left p-3.5 sm:p-4 rounded-xl border-2 transition-all flex items-start gap-3.5 ${
                  isSelected
                    ? 'bg-blue-950/80 border-sky-400 text-white shadow-lg shadow-sky-500/10'
                    : 'bg-slate-900/80 border-slate-800/80 text-slate-200 hover:border-slate-700 hover:bg-slate-850'
                }`}
              >
                <span
                  className={`w-7 h-7 rounded-lg flex items-center justify-center font-bold text-sm shrink-0 ${
                    isSelected
                      ? 'bg-sky-400 text-slate-950'
                      : 'bg-slate-800 text-slate-300 border border-slate-700'
                  }`}
                >
                  {opt}
                </span>
                <span className="text-sm sm:text-base pt-0.5 font-medium leading-relaxed">
                  {optText}
                </span>
              </button>
            );
          })}
        </div>

        {/* Bottom Action Bar for Current Question */}
        <div className="mt-auto flex flex-wrap items-center justify-between gap-2 pt-3 border-t border-slate-800/80">
          <div className="flex items-center gap-2">
            <button
              onClick={handleToggleReview}
              className={`px-3 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 transition-colors border ${
                reviewed.has(currentQ?.id)
                  ? 'bg-purple-900/60 border-purple-500 text-purple-200'
                  : 'bg-slate-800/80 border-slate-700 text-slate-300 hover:bg-slate-700'
              }`}
            >
              <Bookmark className="w-3.5 h-3.5" />
              <span>{reviewed.has(currentQ?.id) ? 'ચિહ્નિત કરેલ' : 'રિવ્યુ માટે ચિહ્નિત'}</span>
            </button>

            <button
              onClick={handleClearResponse}
              disabled={!answers[currentQ?.id]}
              className="px-3 py-2 rounded-xl text-xs font-semibold bg-slate-800/80 border border-slate-700 text-slate-400 hover:text-slate-200 disabled:opacity-40 transition-colors flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              <span>સાફ કરો</span>
            </button>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={handlePrev}
              disabled={currentIndex === 0}
              className="px-3.5 py-2 rounded-xl text-xs font-semibold bg-slate-800 border border-slate-700 text-slate-300 hover:bg-slate-700 disabled:opacity-40 transition-colors flex items-center gap-1"
            >
              <ArrowLeft className="w-3.5 h-3.5" />
              <span>પાછળ</span>
            </button>

            <button
              onClick={handleNext}
              disabled={currentIndex === questions.length - 1}
              className="px-4 py-2 rounded-xl text-xs font-bold bg-amber-500 hover:bg-amber-400 text-slate-950 transition-colors flex items-center gap-1 shadow-md shadow-amber-500/20"
            >
              <span>સાચવો & આગળ</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>

      {/* Question Palette Drawer (Mobile Friendly Modal) */}
      {showPaletteDrawer && (
        <div className="fixed inset-0 z-40 bg-black/70 backdrop-blur-sm flex justify-end">
          <div className="w-full max-w-sm bg-slate-900 border-l border-slate-800 h-full p-4 flex flex-col overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-800 mb-3">
              <h3 className="font-bold text-white text-sm">પ્રશ્ન પેલેટ (200 પ્રશ્નો)</h3>
              <button
                onClick={() => setShowPaletteDrawer(false)}
                className="text-slate-400 hover:text-white p-1 text-sm"
              >
                ✕
              </button>
            </div>

            {/* Status Legend */}
            <div className="grid grid-cols-2 gap-2 text-[11px] mb-4 p-2.5 bg-slate-800/60 rounded-xl border border-slate-700/60">
              <div className="flex items-center gap-2">
                <span className="w-4 h-4 rounded bg-emerald-600"></span>
                <span>જવાબ આપ્યો ({answeredCount})</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="w-4 h-4 rounded bg-rose-600"></span>
                <span>જવાબ નથી આપ્યો</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="w-4 h-4 rounded bg-purple-600"></span>
                <span>રિવ્યુ ({markedReviewCount})</span>
              </div>
              <div className="flex items-center gap-2">
                <span className="w-4 h-4 rounded bg-slate-700"></span>
                <span>મુલાકાત નથી લીધી</span>
              </div>
            </div>

            {/* 200 Questions Grid */}
            <div className="grid grid-cols-5 gap-2 flex-1 overflow-y-auto pr-1">
              {questions.map((q, idx) => {
                const status = getStatus(q.id, idx);
                let colorClass = "bg-slate-800 text-slate-300 border-slate-700";
                if (status === 'answered') colorClass = "bg-emerald-600 text-white border-emerald-500 font-bold";
                else if (status === 'answered_review') colorClass = "bg-purple-600 text-white border-amber-400 font-bold ring-2 ring-amber-400";
                else if (status === 'review') colorClass = "bg-purple-600 text-white border-purple-500";
                else if (status === 'not_answered') colorClass = "bg-rose-600 text-white border-rose-500";

                const isCurrent = idx === currentIndex;

                return (
                  <button
                    key={q.id}
                    onClick={() => handleJumpToQuestion(idx)}
                    className={`h-9 rounded-lg border text-xs flex items-center justify-center font-medium transition-transform active:scale-95 ${colorClass} ${
                      isCurrent ? 'ring-2 ring-sky-400 scale-105' : ''
                    }`}
                  >
                    {idx + 1}
                  </button>
                );
              })}
            </div>

            <div className="pt-3 border-t border-slate-800 mt-2">
              <button
                onClick={() => {
                  setShowPaletteDrawer(false);
                  setShowSubmitModal(true);
                }}
                className="w-full py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs"
              >
                ટેસ્ટ સબમિટ કરો
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Submit Confirmation Modal */}
      {showSubmitModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl max-w-sm w-full p-5 shadow-2xl">
            <div className="w-12 h-12 rounded-full bg-amber-500/20 text-amber-400 flex items-center justify-center mx-auto mb-3">
              <AlertTriangle className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-center text-white mb-2">
              ટેસ્ટ પૂર્ણ કરીને સબમિટ કરવો છે?
            </h3>
            <p className="text-xs text-slate-400 text-center mb-4">
              એકવાર સબમિટ કર્યા પછી ઉત્તરો બદલી શકાશે નહીં. તમારું પરિણામ તરત જ વિશ્લેષણ સાથે દર્શાવવામાં આવશે.
            </p>

            <div className="bg-slate-800/80 rounded-xl p-3 mb-4 space-y-1.5 text-xs">
              <div className="flex justify-between text-slate-300">
                <span>કુલ પ્રશ્નો:</span>
                <span className="font-bold text-white">{questions.length}</span>
              </div>
              <div className="flex justify-between text-emerald-400">
                <span>જવાબ આપેલ પ્રશ્નો:</span>
                <span className="font-bold">{answeredCount}</span>
              </div>
              <div className="flex justify-between text-rose-400">
                <span>જવાબ આપ્યા વિનાના:</span>
                <span className="font-bold">{questions.length - answeredCount}</span>
              </div>
              <div className="flex justify-between text-purple-400">
                <span>રિવ્યુ માટે ચિહ્નિત:</span>
                <span className="font-bold">{markedReviewCount}</span>
              </div>
            </div>

            <div className="flex gap-2.5">
              <button
                onClick={() => setShowSubmitModal(false)}
                className="flex-1 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold"
              >
                ચાલુ રાખો
              </button>
              <button
                onClick={handleSubmitTest}
                className="flex-1 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold shadow-lg shadow-emerald-600/30"
              >
                હા, સબમિટ કરો
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
