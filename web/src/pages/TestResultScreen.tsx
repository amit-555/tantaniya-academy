import React, { useState } from 'react';
import { TestAttempt, Question } from '../types';
import { Trophy, CheckCircle, XCircle, MinusCircle, Clock, Target, ArrowLeft, RotateCw, BookOpen, ChevronDown, ChevronUp } from 'lucide-react';

interface TestResultScreenProps {
  attempt: TestAttempt;
  questions: Question[];
  onBackToHome: () => void;
  onRetakeTest: () => void;
}

export const TestResultScreen: React.FC<TestResultScreenProps> = ({
  attempt,
  questions,
  onBackToHome,
  onRetakeTest
}) => {
  const [filter, setFilter] = useState<'all' | 'correct' | 'incorrect' | 'unattempted'>('all');
  const [expandedQId, setExpandedQId] = useState<number | null>(null);

  const filteredQuestions = questions.filter(q => {
    const userAns = attempt.answers[q.id];
    if (filter === 'correct') return userAns === q.correctOption;
    if (filter === 'incorrect') return userAns && userAns !== q.correctOption;
    if (filter === 'unattempted') return !userAns;
    return true;
  });

  const isPassed = attempt.finalScore >= 80;

  const formatSeconds = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m} મિનિટ ${s} સેકન્ડ`;
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-3 sm:p-5 pb-20 max-w-4xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-4 pb-3 border-b border-slate-800">
        <button
          onClick={onBackToHome}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 text-xs text-slate-300 hover:text-white"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>મુખ્ય પેજ</span>
        </button>
        <h2 className="text-sm sm:text-base font-bold text-white">ટેસ્ટ પરિણામ & પૃથ્થકરણ</h2>
        <button
          onClick={onRetakeTest}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-amber-500 hover:bg-amber-400 text-slate-950 text-xs font-bold"
        >
          <RotateCw className="w-3.5 h-3.5" />
          <span>ફરી આપો</span>
        </button>
      </div>

      {/* Main Scorecard Banner */}
      <div className="bg-gradient-to-br from-slate-900 via-blue-950 to-slate-900 border border-blue-900/50 rounded-2xl p-5 shadow-2xl mb-5 text-center relative overflow-hidden">
        <div className="w-16 h-16 mx-auto rounded-full bg-amber-400/20 border border-amber-400/40 flex items-center justify-center text-amber-400 mb-3 shadow-lg">
          <Trophy className="w-8 h-8" />
        </div>

        <h1 className="text-xl sm:text-2xl font-black text-white mb-1">
          {attempt.testTitle}
        </h1>
        <p className="text-xs text-amber-300 font-medium mb-4">
          Tantaniya Academy CBRT સ્કોરકાર્ડ
        </p>

        <div className="inline-block px-5 py-2.5 rounded-2xl bg-slate-900/90 border border-amber-500/40 shadow-inner mb-4">
          <div className="text-3xl sm:text-4xl font-extrabold text-amber-400">
            {attempt.finalScore} <span className="text-base text-slate-400 font-normal">/ {attempt.totalQuestions} ગુણ</span>
          </div>
          <div className="text-[11px] text-slate-400 mt-0.5">
            ટકાવારી: <span className="text-white font-bold">{attempt.percentage}%</span> • સચોટતા: <span className="text-sky-300 font-bold">{attempt.accuracy}%</span>
          </div>
        </div>

        <div className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold ${
          isPassed ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40' : 'bg-rose-500/20 text-rose-300 border border-rose-500/40'
        }`}>
          {isPassed ? '✓ ક્વોલિફાઇડ (પાસ)' : 'વધુ પ્રેક્ટિસની જરૂર છે'}
        </div>
      </div>

      {/* Statistics Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5 mb-5">
        <div className="bg-slate-900/90 border border-emerald-900/40 rounded-xl p-3 flex flex-col items-center">
          <CheckCircle className="w-5 h-5 text-emerald-400 mb-1" />
          <span className="text-lg font-bold text-emerald-400">{attempt.correctCount}</span>
          <span className="text-[11px] text-slate-400">સાચા ઉત્તર (+{attempt.positiveMarks})</span>
        </div>

        <div className="bg-slate-900/90 border border-rose-900/40 rounded-xl p-3 flex flex-col items-center">
          <XCircle className="w-5 h-5 text-rose-400 mb-1" />
          <span className="text-lg font-bold text-rose-400">{attempt.incorrectCount}</span>
          <span className="text-[11px] text-slate-400">ખોટા ઉત્તર (-{attempt.negativeMarks})</span>
        </div>

        <div className="bg-slate-900/90 border border-slate-800 rounded-xl p-3 flex flex-col items-center">
          <MinusCircle className="w-5 h-5 text-slate-400 mb-1" />
          <span className="text-lg font-bold text-slate-300">{attempt.unattemptedCount}</span>
          <span className="text-[11px] text-slate-400">ખાલી રાખેલ (0)</span>
        </div>

        <div className="bg-slate-900/90 border border-sky-900/40 rounded-xl p-3 flex flex-col items-center">
          <Clock className="w-5 h-5 text-sky-400 mb-1" />
          <span className="text-xs font-bold text-sky-300 pt-1">{formatSeconds(attempt.timeUsedSeconds)}</span>
          <span className="text-[11px] text-slate-400 mt-1">સમય ઉપયોગ</span>
        </div>
      </div>

      {/* Subject-Wise Performance Breakdown */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4 mb-5">
        <h3 className="font-bold text-white text-xs sm:text-sm mb-3 flex items-center gap-2">
          <Target className="w-4 h-4 text-amber-400" />
          <span>વિષયવાર પર્ફોર્મન્સ પૃથ્થકરણ (Subject Performance)</span>
        </h3>

        <div className="space-y-3">
          {Object.entries(attempt.subjectPerformance).map(([subject, stats]) => {
            const pct = Math.round((stats.correct / stats.total) * 100) || 0;
            return (
              <div key={subject}>
                <div className="flex justify-between text-xs mb-1">
                  <span className="text-slate-300 font-medium">{subject}</span>
                  <span className="text-slate-400">
                    <strong className="text-emerald-400">{stats.correct}</strong> / {stats.total} ({pct}%)
                  </span>
                </div>
                <div className="w-full bg-slate-800 rounded-full h-2 overflow-hidden">
                  <div
                    className={`h-full rounded-full transition-all ${
                      pct >= 70 ? 'bg-emerald-500' : pct >= 40 ? 'bg-amber-500' : 'bg-rose-500'
                    }`}
                    style={{ width: `${pct}%` }}
                  ></div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Question Filter Tabs */}
      <div className="flex items-center gap-1.5 overflow-x-auto pb-2 mb-3">
        <button
          onClick={() => setFilter('all')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold shrink-0 transition-colors ${
            filter === 'all' ? 'bg-blue-600 text-white' : 'bg-slate-900 text-slate-400 hover:text-white'
          }`}
        >
          બધા પ્રશ્નો ({questions.length})
        </button>
        <button
          onClick={() => setFilter('correct')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold shrink-0 transition-colors ${
            filter === 'correct' ? 'bg-emerald-600 text-white' : 'bg-slate-900 text-emerald-400 hover:bg-slate-800'
          }`}
        >
          સાચા ({attempt.correctCount})
        </button>
        <button
          onClick={() => setFilter('incorrect')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold shrink-0 transition-colors ${
            filter === 'incorrect' ? 'bg-rose-600 text-white' : 'bg-slate-900 text-rose-400 hover:bg-slate-800'
          }`}
        >
          ખોટા ({attempt.incorrectCount})
        </button>
        <button
          onClick={() => setFilter('unattempted')}
          className={`px-3 py-1.5 rounded-lg text-xs font-semibold shrink-0 transition-colors ${
            filter === 'unattempted' ? 'bg-slate-700 text-white' : 'bg-slate-900 text-slate-400 hover:bg-slate-800'
          }`}
        >
          ખાલી રાખેલ ({attempt.unattemptedCount})
        </button>
      </div>

      {/* Question Review List */}
      <div className="space-y-3">
        {filteredQuestions.map((q) => {
          const userAns = attempt.answers[q.id];
          const isCorrect = userAns === q.correctOption;
          const isUnattempted = !userAns;
          const isExpanded = expandedQId === q.id;

          let badgeColor = "bg-slate-800 text-slate-400 border-slate-700";
          let badgeText = "ખાલી રાખેલ";
          if (isCorrect) {
            badgeColor = "bg-emerald-950/80 text-emerald-300 border-emerald-800";
            badgeText = "સાચો ઉત્તર (+1.0)";
          } else if (!isUnattempted) {
            badgeColor = "bg-rose-950/80 text-rose-300 border-rose-800";
            badgeText = `ખોટો ઉત્તર (-${q.negativeMarks})`;
          }

          return (
            <div
              key={q.id}
              className="bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-md transition-all"
            >
              <div className="flex items-start justify-between gap-2 mb-2">
                <span className="text-xs text-amber-400 font-bold">
                  પ્રશ્ન {q.id} • {q.subject}
                </span>
                <span className={`px-2 py-0.5 rounded text-[10px] font-bold border ${badgeColor}`}>
                  {badgeText}
                </span>
              </div>

              <div className="text-white text-sm font-semibold mb-3 leading-relaxed">
                {q.questionText}
              </div>

              {/* Options */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs mb-3">
                {(['A', 'B', 'C', 'D'] as const).map(opt => {
                  const optKey = `option${opt}` as keyof Question;
                  const text = q[optKey] as string;
                  const isUserPick = userAns === opt;
                  const isCorrectPick = q.correctOption === opt;

                  let optClass = "bg-slate-800/60 border-slate-700/60 text-slate-300";
                  if (isCorrectPick) {
                    optClass = "bg-emerald-950/80 border-emerald-500 text-emerald-200 font-bold";
                  } else if (isUserPick) {
                    optClass = "bg-rose-950/80 border-rose-500 text-rose-200 font-bold";
                  }

                  return (
                    <div
                      key={opt}
                      className={`p-2.5 rounded-lg border flex items-center gap-2 ${optClass}`}
                    >
                      <span className="w-5 h-5 rounded font-bold text-[11px] flex items-center justify-center bg-slate-800 shrink-0">
                        {opt}
                      </span>
                      <span>{text}</span>
                    </div>
                  );
                })}
              </div>

              {/* Explanation Toggle */}
              {q.explanation && (
                <div>
                  <button
                    onClick={() => setExpandedQId(isExpanded ? null : q.id)}
                    className="text-xs text-amber-400 hover:text-amber-300 font-medium flex items-center gap-1 transition-colors"
                  >
                    <BookOpen className="w-3.5 h-3.5" />
                    <span>{isExpanded ? 'સમજૂતી છુપાવો' : 'વિગતવાર સમજૂતી જુઓ'}</span>
                    {isExpanded ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
                  </button>

                  {isExpanded && (
                    <div className="mt-2.5 p-3 rounded-lg bg-slate-950 border border-slate-800 text-xs text-slate-300 leading-relaxed">
                      <strong className="text-amber-400">સાચો જવાબ: વિકલ્પ {q.correctOption}</strong>
                      <p className="mt-1">{q.explanation}</p>
                    </div>
                  )}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
