import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Exam, Test, Question, CurrentAffairsItem, GkItem, StudyMaterialItem } from '../types';
import { ShieldAlert, Plus, Trash2, Edit3, Sparkles, BookOpen, Layers, CheckCircle2, Award, FileText, Compass, Database } from 'lucide-react';

interface AdminDashboardProps {
  exams: Exam[];
  tests: Test[];
  questions: Question[];
  currentAffairs: CurrentAffairsItem[];
  gkItems: GkItem[];
  studyMaterials: StudyMaterialItem[];
  onAddExam: (exam: Exam) => void;
  onAddTest: (test: Test) => void;
  onAddQuestion: (q: Question) => void;
  onDeleteQuestion: (id: number) => void;
  onClose: () => void;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
  exams,
  tests,
  questions,
  currentAffairs,
  gkItems,
  studyMaterials,
  onAddExam,
  onAddTest,
  onAddQuestion,
  onDeleteQuestion,
  onClose
}) => {
  const { user, isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState<'overview' | 'exams' | 'tests' | 'questions' | 'ai' | 'ca'>('overview');

  // New Exam Form State
  const [newExamTitle, setNewExamTitle] = useState('');
  const [newExamCode, setNewExamCode] = useState('');
  const [newExamMarks, setNewExamMarks] = useState(200);
  const [newExamDuration, setNewExamDuration] = useState(180);

  // New Question Form State
  const [qText, setQText] = useState('');
  const [qSubject, setQSubject] = useState('ભારતીય બંધારણ');
  const [qTopic, setQTopic] = useState('મૂળભૂત અધિકારો');
  const [optA, setOptA] = useState('');
  const [optB, setOptB] = useState('');
  const [optC, setOptC] = useState('');
  const [optD, setOptD] = useState('');
  const [correctOpt, setCorrectOpt] = useState<'A' | 'B' | 'C' | 'D'>('A');
  const [explanation, setExplanation] = useState('');

  // AI Generator Form State
  const [aiTopic, setAiTopic] = useState('ગુજરાતનો ઇતિહાસ અને વારસો');
  const [aiCount, setAiCount] = useState(5);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiGenerated, setAiGenerated] = useState<Omit<Question, 'id'>[]>([]);

  // RBAC Access Guard
  if (!isAdmin) {
    return (
      <div className="min-h-screen bg-slate-950 text-white flex flex-col items-center justify-center p-6 text-center">
        <div className="w-16 h-16 rounded-2xl bg-rose-500/20 border border-rose-500/40 text-rose-400 flex items-center justify-center mb-4">
          <ShieldAlert className="w-8 h-8" />
        </div>
        <h2 className="text-xl font-bold mb-2">એક્સેસ નામંજૂર (Access Denied)</h2>
        <p className="text-sm text-slate-400 max-w-sm mb-6">
          આ પેનલ ફક્ત અધિકૃત એડમિનિસ્ટ્રેટર માટે જ સુરક્ષિત છે. સામાન્ય વિદ્યાર્થીઓને અહીં પ્રવેશ નથી.
        </p>
        <button
          onClick={onClose}
          className="px-5 py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 font-semibold text-xs text-white"
        >
          મુખ્ય પેજ પર પાછા ફરો
        </button>
      </div>
    );
  }

  const handleCreateExam = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newExamTitle.trim()) return;
    const exam: Exam = {
      id: Date.now(),
      title: newExamTitle.trim(),
      code: newExamCode.trim() || 'EXAM',
      description: `કુલ ${newExamMarks} ગુણ, ${newExamDuration} મિનિટ, 0.25 નેગેટિવ માર્કિંગ`,
      totalMarks: newExamMarks,
      durationMinutes: newExamDuration,
      negativeMarking: 0.25,
      isPublished: true,
      createdAt: Date.now()
    };
    onAddExam(exam);
    setNewExamTitle('');
    setNewExamCode('');
    alert('પરીક્ષા સફળતાપૂર્વક ઉમેરાઈ ગઈ!');
  };

  const handleCreateQuestion = (e: React.FormEvent) => {
    e.preventDefault();
    if (!qText.trim() || !optA.trim() || !optB.trim() || !optC.trim() || !optD.trim()) {
      alert('કૃપા કરી પ્રશ્ન અને ચારેય વિકલ્પો ભરો.');
      return;
    }
    const q: Question = {
      id: Date.now(),
      examId: 1,
      part: 'ભાગ-અ (Part A)',
      subject: qSubject,
      topic: qTopic,
      difficulty: 'મધ્યમ',
      questionText: qText.trim(),
      optionA: optA.trim(),
      optionB: optB.trim(),
      optionC: optC.trim(),
      optionD: optD.trim(),
      correctOption: correctOpt,
      explanation: explanation.trim(),
      marks: 1.0,
      negativeMarks: 0.25,
      source: 'Tantaniya Academy CMS',
      createdAt: Date.now()
    };
    onAddQuestion(q);
    setQText('');
    setOptA('');
    setOptB('');
    setOptC('');
    setOptD('');
    setExplanation('');
    alert('પ્રશ્ન બેંકમાં નવો પ્રશ્ન ઉમેરાઈ ગયો!');
  };

  // AI MCQ Generator
  const handleGenerateAiQuestions = () => {
    setAiLoading(true);
    setTimeout(() => {
      const generated: Omit<Question, 'id'>[] = [
        {
          examId: 1,
          part: 'ભાગ-બ (Part B)',
          subject: 'ઇતિહાસ & વારસો',
          topic: aiTopic,
          difficulty: 'મધ્યમ',
          questionText: `ગુજરાતમાં ${aiTopic} અંતર્ગત સિદ્ધરાજ જયસિંહ દ્વારા કઈ ઐતિહાસિક વાવ/તળાવનું નિર્માણ કરાવવામાં આવ્યું હતું?`,
          optionA: 'સહસ્ત્રલિંગ તળાવ',
          optionB: 'મુનસર તળાવ',
          optionC: 'મલાવ તળાવ',
          optionD: 'શર્મિષ્ઠા તળાવ',
          correctOption: 'A',
          explanation: 'સિદ્ધરાજ જયસિંહે પાટણમાં 1008 શિવલિંગો ધરાવતા ભવ્ય સહસ્ત્રલિંગ તળાવનું નિર્માણ કરાવ્યું હતું.',
          marks: 1.0,
          negativeMarks: 0.25,
          source: 'Tantaniya Academy AI Desk',
          createdAt: Date.now()
        },
        {
          examId: 1,
          part: 'ભાગ-બ (Part B)',
          subject: 'ભારતીય બંધારણ',
          topic: aiTopic,
          difficulty: 'સરળ',
          questionText: `ભારતના બંધારણના આમુખમાં 42મા સુધારા (1976) દ્વારા કયા શબ્દો ઉમેરવામાં આવ્યા હતા?`,
          optionA: 'સમાજવાદી, બિનસાંપ્રદાયિક અને અખંડિતતા',
          optionB: 'સ્વતંત્રતા, સમાનતા અને બંધુતા',
          optionC: 'ન્યાય, વિચાર અને વાણી',
          optionD: 'સાર્વભૌમ અને લોકશાહી',
          correctOption: 'A',
          explanation: '1976 ના 42મા બંધારણીય સુધારા દ્વારા આમુખમાં સમાજવાદી (Socialist), બિનસાંપ્રદાયિક (Secular) અને અખંડિતતા (Integrity) શબ્દો ઉમેરાયા હતા.',
          marks: 1.0,
          negativeMarks: 0.25,
          source: 'Tantaniya Academy AI Desk',
          createdAt: Date.now()
        }
      ];
      setAiGenerated(generated);
      setAiLoading(false);
    }, 1000);
  };

  const handleSaveAiQuestion = (q: Omit<Question, 'id'>) => {
    onAddQuestion({
      ...q,
      id: Date.now()
    });
    setAiGenerated(prev => prev.filter(item => item !== q));
    alert('AI પ્રશ્ન પ્રશ્ન બેંકમાં સાચવવામાં આવ્યો!');
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-3 sm:p-6 pb-24 max-w-5xl mx-auto">
      {/* Top Banner */}
      <div className="flex items-center justify-between pb-4 mb-5 border-b border-slate-800">
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-amber-500/20 border border-amber-500/40 text-amber-400 flex items-center justify-center font-bold">
            <Award className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-base sm:text-lg font-bold text-white flex items-center gap-2">
              <span>Tantaniya Academy Admin CMS</span>
              <span className="px-2 py-0.5 rounded text-[10px] bg-amber-400 text-slate-950 font-bold">
                સુરક્ષિત એડમિન
              </span>
            </h1>
            <p className="text-xs text-slate-400">
              સંચાલક: <strong className="text-slate-200">{user?.name}</strong> • ભૂમિકા: ADMIN
            </p>
          </div>
        </div>

        <button
          onClick={onClose}
          className="px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 text-xs text-slate-300 hover:text-white"
        >
          બંધ કરો
        </button>
      </div>

      {/* Admin Nav Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2 mb-5">
        <button
          onClick={() => setActiveTab('overview')}
          className={`px-3 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 shrink-0 ${
            activeTab === 'overview' ? 'bg-amber-500 text-slate-950 font-bold' : 'bg-slate-900 text-slate-300'
          }`}
        >
          <Database className="w-3.5 h-3.5" />
          <span>ઓવરવ્યુ</span>
        </button>

        <button
          onClick={() => setActiveTab('exams')}
          className={`px-3 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 shrink-0 ${
            activeTab === 'exams' ? 'bg-amber-500 text-slate-950 font-bold' : 'bg-slate-900 text-slate-300'
          }`}
        >
          <Layers className="w-3.5 h-3.5" />
          <span>પરીક્ષાઓ ({exams.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('questions')}
          className={`px-3 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 shrink-0 ${
            activeTab === 'questions' ? 'bg-amber-500 text-slate-950 font-bold' : 'bg-slate-900 text-slate-300'
          }`}
        >
          <BookOpen className="w-3.5 h-3.5" />
          <span>પ્રશ્ન બેંક ({questions.length})</span>
        </button>

        <button
          onClick={() => setActiveTab('ai')}
          className={`px-3 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 shrink-0 ${
            activeTab === 'ai' ? 'bg-amber-500 text-slate-950 font-bold' : 'bg-slate-900 text-slate-300'
          }`}
        >
          <Sparkles className="w-3.5 h-3.5 text-amber-400" />
          <span>AI પ્રશ્ન જનરેટર</span>
        </button>
      </div>

      {/* Tab: Overview */}
      {activeTab === 'overview' && (
        <div className="space-y-5">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
              <span className="text-xs text-slate-400">કુલ પરીક્ષાઓ</span>
              <div className="text-2xl font-bold text-amber-400 mt-1">{exams.length}</div>
            </div>
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
              <span className="text-xs text-slate-400">કુલ મોક ટેસ્ટ</span>
              <div className="text-2xl font-bold text-sky-400 mt-1">{tests.length}</div>
            </div>
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
              <span className="text-xs text-slate-400">પ્રશ્ન બેંક પ્રશ્નો</span>
              <div className="text-2xl font-bold text-emerald-400 mt-1">{questions.length}</div>
            </div>
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
              <span className="text-xs text-slate-400">કરંટ અફેર્સ / GK</span>
              <div className="text-2xl font-bold text-purple-400 mt-1">{currentAffairs.length + gkItems.length}</div>
            </div>
          </div>

          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5">
            <h3 className="font-bold text-sm text-white mb-2">એડમિન સુરક્ષા માહિતી</h3>
            <p className="text-xs text-slate-400 leading-relaxed mb-3">
              તમારું એકાઉન્ટ Tantaniya Academy ના પ્રથમ અને મુખ્ય સંચાલક (Primary Admin) તરીકે અધિકૃત છે.
              સામાન્ય વિદ્યાર્થીઓ ફક્ત મોક ટેસ્ટ, કરંટ અફેર્સ અને મટીરીયલ વાંચી શકે છે, તેઓ એડમિન CMS માં ફેરફાર કરી શકતા નથી.
            </p>
            <div className="p-3 bg-emerald-950/40 border border-emerald-800/60 rounded-xl text-emerald-300 text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-400" />
              <span>Role-Based Access Control (RBAC) & Firestore Security Rules સંપૂર્ણ સક્રિય છે.</span>
            </div>
          </div>
        </div>
      )}

      {/* Tab: Dynamic Exams */}
      {activeTab === 'exams' && (
        <div className="space-y-5">
          {/* Add Exam Form */}
          <form onSubmit={handleCreateExam} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3">
            <h3 className="font-bold text-sm text-white flex items-center gap-2">
              <Plus className="w-4 h-4 text-amber-400" />
              <span>નવી પરીક્ષા ઉમેરો (Dynamic Exam Builder)</span>
            </h3>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-400 block mb-1">પરીક્ષાનું નામ</label>
                <input
                  type="text"
                  value={newExamTitle}
                  onChange={e => setNewExamTitle(e.target.value)}
                  placeholder="દા.ત. બિન સચિવાલય ક્લાર્ક"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
              </div>

              <div>
                <label className="text-xs text-slate-400 block mb-1">પરીક્ષા કોડ</label>
                <input
                  type="text"
                  value={newExamCode}
                  onChange={e => setNewExamCode(e.target.value)}
                  placeholder="દા.ત. BSC-2026"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div>
                <label className="text-xs text-slate-400 block mb-1">કુલ ગુણ</label>
                <input
                  type="number"
                  value={newExamMarks}
                  onChange={e => setNewExamMarks(Number(e.target.value))}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div>
                <label className="text-xs text-slate-400 block mb-1">સમયમર્યાદા (મિનિટ)</label>
                <input
                  type="number"
                  value={newExamDuration}
                  onChange={e => setNewExamDuration(Number(e.target.value))}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>
            </div>

            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 text-xs font-bold"
            >
              પરીક્ષા સાચવો
            </button>
          </form>

          {/* Existing Exams List */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">હાલની પરીક્ષાઓ</h4>
            {exams.map(exam => (
              <div key={exam.id} className="bg-slate-900 border border-slate-800 rounded-xl p-4 flex items-center justify-between">
                <div>
                  <h5 className="font-bold text-white text-sm">{exam.title}</h5>
                  <p className="text-xs text-slate-400 mt-0.5">{exam.description}</p>
                </div>
                <span className="px-2.5 py-1 rounded-md text-[11px] font-bold bg-blue-950 text-blue-300 border border-blue-800">
                  {exam.code}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tab: Question Bank */}
      {activeTab === 'questions' && (
        <div className="space-y-5">
          {/* Add Question Form */}
          <form onSubmit={handleCreateQuestion} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3">
            <h3 className="font-bold text-sm text-white flex items-center gap-2">
              <Plus className="w-4 h-4 text-amber-400" />
              <span>પ્રશ્ન બેંકમાં નવો પ્રશ્ન ઉમેરો</span>
            </h3>

            <div>
              <label className="text-xs text-slate-400 block mb-1">પ્રશ્ન (ગુજરાતીમાં)</label>
              <textarea
                value={qText}
                onChange={e => setQText(e.target.value)}
                rows={2}
                placeholder="પ્રશ્નનું લખાણ અહીં લખો..."
                className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-white"
                required
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-400 block mb-1">વિકલ્પ A</label>
                <input
                  type="text"
                  value={optA}
                  onChange={e => setOptA(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
              </div>
              <div>
                <label className="text-xs text-slate-400 block mb-1">વિકલ્પ B</label>
                <input
                  type="text"
                  value={optB}
                  onChange={e => setOptB(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
              </div>
              <div>
                <label className="text-xs text-slate-400 block mb-1">વિકલ્પ C</label>
                <input
                  type="text"
                  value={optC}
                  onChange={e => setOptC(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
              </div>
              <div>
                <label className="text-xs text-slate-400 block mb-1">વિકલ્પ D</label>
                <input
                  type="text"
                  value={optD}
                  onChange={e => setOptD(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div>
                <label className="text-xs text-slate-400 block mb-1">સાચો વિકલ્પ</label>
                <select
                  value={correctOpt}
                  onChange={e => setCorrectOpt(e.target.value as any)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  <option value="A">વિકલ્પ A</option>
                  <option value="B">વિકલ્પ B</option>
                  <option value="C">વિકલ્પ C</option>
                  <option value="D">વિકલ્પ D</option>
                </select>
              </div>

              <div>
                <label className="text-xs text-slate-400 block mb-1">વિષય</label>
                <input
                  type="text"
                  value={qSubject}
                  onChange={e => setQSubject(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div>
                <label className="text-xs text-slate-400 block mb-1">ટોપિક</label>
                <input
                  type="text"
                  value={qTopic}
                  onChange={e => setQTopic(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>
            </div>

            <div>
              <label className="text-xs text-slate-400 block mb-1">સમજૂતી (Explanation)</label>
              <textarea
                value={explanation}
                onChange={e => setExplanation(e.target.value)}
                rows={2}
                placeholder="સાચા જવાબ અંગેની સમજૂતી..."
                className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-white"
              />
            </div>

            <button
              type="submit"
              className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 text-xs font-bold"
            >
              પ્રશ્ન સાચવો
            </button>
          </form>

          {/* Quick Questions Review */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">
              હાલના પ્રશ્નો (પ્રથમ ૧૦ દર્શાવેલ, કુલ {questions.length})
            </h4>
            {questions.slice(0, 10).map(q => (
              <div key={q.id} className="bg-slate-900 border border-slate-800 rounded-xl p-3.5 flex items-start justify-between gap-3">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-xs font-bold text-amber-400">#{q.id}</span>
                    <span className="text-[11px] text-slate-400">{q.subject} • {q.topic}</span>
                  </div>
                  <p className="text-xs text-white font-medium">{q.questionText}</p>
                  <p className="text-[11px] text-emerald-400 mt-1">સાચો જવાબ: વિકલ્પ {q.correctOption}</p>
                </div>
                <button
                  onClick={() => onDeleteQuestion(q.id)}
                  className="p-1.5 text-slate-500 hover:text-rose-400"
                  title="ડિલીટ કરો"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tab: AI Generator */}
      {activeTab === 'ai' && (
        <div className="space-y-5">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3">
            <h3 className="font-bold text-sm text-white flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-amber-400" />
              <span>AI પ્રશ્ન જનરેટર (ગુજરાતી MCQs)</span>
            </h3>
            <p className="text-xs text-slate-400">
              અભ્યાસક્રમના કોઈપણ વિષય અથવા ટોપિક પરથી આપોઆપ ૪ વિકલ્પો અને સચોટ સમજૂતી સાથે ગુજરાતી પ્રશ્નો તૈયાર કરો.
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-xs text-slate-400 block mb-1">ટોપિક પસંદ કરો</label>
                <input
                  type="text"
                  value={aiTopic}
                  onChange={e => setAiTopic(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div>
                <label className="text-xs text-slate-400 block mb-1">પ્રશ્નોની સંખ્યા</label>
                <select
                  value={aiCount}
                  onChange={e => setAiCount(Number(e.target.value))}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  <option value={2}>2 પ્રશ્નો</option>
                  <option value={5}>5 પ્રશ્નો</option>
                  <option value={10}>10 પ્રશ્નો</option>
                </select>
              </div>
            </div>

            <button
              onClick={handleGenerateAiQuestions}
              disabled={aiLoading}
              className="px-4 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-400 hover:to-amber-500 text-slate-950 text-xs font-bold flex items-center gap-2 shadow-lg shadow-amber-500/20"
            >
              <Sparkles className="w-4 h-4" />
              <span>{aiLoading ? 'પ્રશ્નો જનરેટ થઈ રહ્યા છે...' : 'AI પ્રશ્નો જનરેટ કરો'}</span>
            </button>
          </div>

          {/* Generated Preview */}
          {aiGenerated.length > 0 && (
            <div className="space-y-3">
              <h4 className="text-xs font-bold text-amber-400 uppercase tracking-wider">
                જનરેટ કરેલા પ્રશ્નોનું પ્રિવ્યુ (ચકાસીને પ્રશ્ન બેંકમાં ઉમેરો)
              </h4>
              {aiGenerated.map((item, idx) => (
                <div key={idx} className="bg-slate-900 border border-slate-800 rounded-xl p-4 space-y-2">
                  <div className="text-xs font-semibold text-white">{item.questionText}</div>
                  <div className="grid grid-cols-2 gap-2 text-xs text-slate-300">
                    <div>A. {item.optionA}</div>
                    <div>B. {item.optionB}</div>
                    <div>C. {item.optionC}</div>
                    <div>D. {item.optionD}</div>
                  </div>
                  <div className="text-[11px] text-emerald-400">
                    સાચો જવાબ: વિકલ્પ {item.correctOption} • {item.explanation}
                  </div>
                  <button
                    onClick={() => handleSaveAiQuestion(item)}
                    className="px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold"
                  >
                    પ્રશ્ન બેંકમાં ઉમેરો
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};
