import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import {
  Exam,
  Test,
  Question,
  CurrentAffairsItem,
  GkItem,
  StudyMaterialItem,
  ImageLibraryItem,
  TestAttempt
} from '../types';
import {
  ShieldAlert,
  Plus,
  Trash2,
  Sparkles,
  BookOpen,
  Layers,
  Award,
  Database,
  Upload,
  Image as ImageIcon,
  Copy,
  BarChart2,
  Search,
  CheckCircle2,
  FileText
} from 'lucide-react';
import { uploadFileToCloud } from '../services/firestoreService';

interface AdminDashboardProps {
  exams: Exam[];
  tests: Test[];
  questions: Question[];
  currentAffairs: CurrentAffairsItem[];
  gkItems: GkItem[];
  studyMaterials: StudyMaterialItem[];
  imageLibrary: ImageLibraryItem[];
  testAttempts: TestAttempt[];
  onAddExam: (exam: Exam) => void;
  onDeleteExam: (id: number) => void;
  onAddTest: (test: Test) => void;
  onDeleteTest: (id: number) => void;
  onAddQuestion: (q: Question) => void;
  onDeleteQuestion: (id: number) => void;
  onAddCurrentAffairs: (ca: CurrentAffairsItem) => void;
  onDeleteCurrentAffairs: (id: number) => void;
  onAddGk: (gk: GkItem) => void;
  onDeleteGk: (id: number) => void;
  onAddStudyMaterial: (sm: StudyMaterialItem) => void;
  onDeleteStudyMaterial: (id: number) => void;
  onAddImageLibraryItem: (item: ImageLibraryItem) => void;
  onDeleteImageLibraryItem: (id: number) => void;
  onClose: () => void;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
  exams,
  tests,
  questions,
  currentAffairs,
  gkItems,
  studyMaterials,
  imageLibrary,
  testAttempts,
  onAddExam,
  onDeleteExam,
  onAddTest,
  onDeleteTest,
  onAddQuestion,
  onDeleteQuestion,
  onAddCurrentAffairs,
  onDeleteCurrentAffairs,
  onAddGk,
  onDeleteGk,
  onAddStudyMaterial,
  onDeleteStudyMaterial,
  onAddImageLibraryItem,
  onDeleteImageLibraryItem,
  onClose
}) => {
  const { user, isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState<
    'overview' | 'exams' | 'tests' | 'questions' | 'ca' | 'gk' | 'materials' | 'images' | 'ai' | 'results'
  >('overview');

  // Exam Form
  const [newExamTitle, setNewExamTitle] = useState('');
  const [newExamCode, setNewExamCode] = useState('');
  const [newExamMarks, setNewExamMarks] = useState(200);
  const [newExamDuration, setNewExamDuration] = useState(180);

  // Test Form
  const [testTitle, setTestTitle] = useState('');
  const [testExamId, setTestExamId] = useState<number>(1);
  const [testDuration, setTestDuration] = useState(180);
  const [testQuestionsCount, setTestQuestionsCount] = useState(200);
  const [testMarks, setTestMarks] = useState(200);
  const [testNegative, setTestNegative] = useState(0.25);
  const [testDesc, setTestDesc] = useState('');

  // Question Form
  const [qSearch, setQSearch] = useState('');
  const [qExamFilter, setQExamFilter] = useState<number>(0);
  const [qText, setQText] = useState('');
  const [qSubject, setQSubject] = useState('ભારતીય બંધારણ');
  const [qTopic, setQTopic] = useState('મૂળભૂત અધિકારો');
  const [optA, setOptA] = useState('');
  const [optB, setOptB] = useState('');
  const [optC, setOptC] = useState('');
  const [optD, setOptD] = useState('');
  const [correctOpt, setCorrectOpt] = useState<'A' | 'B' | 'C' | 'D'>('A');
  const [explanation, setExplanation] = useState('');

  // Current Affairs Form
  const [caHeadline, setCaHeadline] = useState('');
  const [caDesc, setCaDesc] = useState('');
  const [caCategory, setCaCategory] = useState('ગુજરાત');
  const [caDate, setCaDate] = useState('૨૯ સપ્ટેમ્બર ૨૦૨૬');
  const [caImageUrl, setCaImageUrl] = useState('');
  const [caUploading, setCaUploading] = useState(false);

  // Study Material / PDF Form
  const [smTitle, setSmTitle] = useState('');
  const [smSubject, setSmSubject] = useState('ભારતીય બંધારણ');
  const [smTopic, setSmTopic] = useState('મૂળભૂત હક્કો');
  const [smType, setSmType] = useState<'નોટ્સ' | 'PDF' | 'શોર્ટ ટ્રીક્સ'>('PDF');
  const [smDesc, setSmDesc] = useState('');
  const [smContent, setSmContent] = useState('');
  const [smFileUrl, setSmFileUrl] = useState('');
  const [smUploading, setSmUploading] = useState(false);

  // Image Library Form
  const [imgTitle, setImgTitle] = useState('');
  const [imgCat, setImgCat] = useState('પરીક્ષા સામગ્રી');
  const [imgUrl, setImgUrl] = useState('');
  const [imgUploading, setImgUploading] = useState(false);

  // GK Form
  const [gkCategory, setGkCategory] = useState('ગુજરાત GK');
  const [gkTitle, setGkTitle] = useState('');
  const [gkContent, setGkContent] = useState('');
  const [gkOneLiner, setGkOneLiner] = useState('');

  // AI MCQ Generator
  const [aiTopic, setAiTopic] = useState('ભારતીય બંધારણ - મૂળભૂત અધિકારો');
  const [aiExam, setAiExam] = useState('ગુજરાત પોલીસ કોન્સ્ટેબલ');
  const [aiCount, setAiCount] = useState(5);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiGenerated, setAiGenerated] = useState<Omit<Question, 'id'>[]>([]);

  if (!isAdmin) {
    return (
      <div className="min-h-screen bg-slate-950 text-white flex flex-col items-center justify-center p-6 text-center">
        <div className="w-16 h-16 rounded-2xl bg-rose-500/20 border border-rose-500/40 text-rose-400 flex items-center justify-center mb-4">
          <ShieldAlert className="w-8 h-8" />
        </div>
        <h2 className="text-xl font-bold mb-2">એક્સેસ નામંજૂર (Access Denied)</h2>
        <p className="text-sm text-slate-400 max-w-sm mb-6">
          આ પેનલ ફક્ત અધિકૃત એડમિનિસ્ટ્રેટર માટે જ સુરક્ષિત છે.
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

  // --- UPLOAD HANDLERS ---
  const handleUploadCaImage = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setCaUploading(true);
    try {
      const url = await uploadFileToCloud(file, 'current_affairs');
      setCaImageUrl(url);
    } catch (err) {
      alert('ઇમેજ અપલોડમાં ક્ષતિ આવી.');
    } finally {
      setCaUploading(false);
    }
  };

  const handleUploadPdf = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setSmUploading(true);
    try {
      const url = await uploadFileToCloud(file, 'study_materials');
      setSmFileUrl(url);
    } catch (err) {
      alert('PDF અપલોડમાં ક્ષતિ આવી.');
    } finally {
      setSmUploading(false);
    }
  };

  const handleUploadLibraryImage = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setImgUploading(true);
    try {
      const url = await uploadFileToCloud(file, 'image_library');
      setImgUrl(url);
    } catch (err) {
      alert('ફોટો અપલોડમાં ક્ષતિ આવી.');
    } finally {
      setImgUploading(false);
    }
  };

  // --- SUBMIT HANDLERS ---
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
    alert('પરીક્ષા ક્લાઉડ ડેટાબેઝમાં સફળતાપૂર્વક સાચવવામાં આવી!');
  };

  const handleCreateTest = (e: React.FormEvent) => {
    e.preventDefault();
    if (!testTitle.trim()) return;
    const t: Test = {
      id: Date.now(),
      examId: testExamId,
      title: testTitle.trim(),
      description: testDesc.trim() || `કુલ ${testQuestionsCount} પ્રશ્નો, ${testMarks} ગુણ, ${testDuration} મિનિટ`,
      durationMinutes: testDuration,
      totalQuestions: testQuestionsCount,
      totalMarks: testMarks,
      negativeMarking: testNegative,
      passingMarks: testMarks * 0.4,
      instructions: "૧. દરેક સાચા જવાબ માટે ૧ ગુણ.\n૨. દરેક ખોટા જવાબ માટે નેગેટિવ માર્કિંગ કપાશે.\n૩. સમય પૂરો થતાં ટેસ્ટ આપોઆપ સબમિટ થશે.",
      isPublished: true,
      createdAt: Date.now()
    };
    onAddTest(t);
    setTestTitle('');
    setTestDesc('');
    alert('મોક ટેસ્ટ ક્લાઉડ ડેટાબેઝમાં સાચવવામાં આવી!');
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
      source: 'Tantaniya Academy Bank',
      createdAt: Date.now()
    };
    onAddQuestion(q);
    setQText('');
    setOptA('');
    setOptB('');
    setOptC('');
    setOptD('');
    setExplanation('');
    alert('પ્રશ્ન બેંકમાં પ્રશ્ન ઉમેરાઈ ગયો!');
  };

  const handleCreateCa = (e: React.FormEvent) => {
    e.preventDefault();
    if (!caHeadline.trim()) return;
    const item: CurrentAffairsItem = {
      id: Date.now(),
      headline: caHeadline.trim(),
      description: caDesc.trim(),
      category: caCategory,
      dateText: caDate,
      imageUrl: caImageUrl,
      importantFacts: '',
      source: 'Tantaniya Academy Desk',
      relatedExam: 'બધી પરીક્ષાઓ',
      relatedSubject: 'કરંટ અફેર્સ',
      isPublished: true,
      createdAt: Date.now()
    };
    onAddCurrentAffairs(item);
    setCaHeadline('');
    setCaDesc('');
    setCaImageUrl('');
    alert('કરંટ અફેર્સ ક્લાઉડમાં પબ્લિશ થયું! બધા ડિવાઇસ પર દેખાશે.');
  };

  const handleCreateStudyMaterial = (e: React.FormEvent) => {
    e.preventDefault();
    if (!smTitle.trim()) return;
    const item: StudyMaterialItem = {
      id: Date.now(),
      examId: 1,
      title: smTitle.trim(),
      subject: smSubject,
      topic: smTopic,
      type: smType,
      description: smDesc.trim(),
      contentText: smContent.trim(),
      fileUrl: smFileUrl,
      fileSize: smFileUrl ? 'PDF Document' : '',
      isPublished: true,
      createdAt: Date.now()
    };
    onAddStudyMaterial(item);
    setSmTitle('');
    setSmDesc('');
    setSmContent('');
    setSmFileUrl('');
    alert('અભ્યાસ સામગ્રી/PDF ક્લાઉડમાં સાચવવામાં આવી!');
  };

  const handleCreateImageLibrary = (e: React.FormEvent) => {
    e.preventDefault();
    if (!imgTitle.trim() || !imgUrl) {
      alert('કૃપા કરી શીર્ષક આપો અને ફોટો અપલોડ કરો.');
      return;
    }
    const item: ImageLibraryItem = {
      id: Date.now(),
      title: imgTitle.trim(),
      category: imgCat,
      imageUrl: imgUrl,
      isPublished: true,
      createdAt: Date.now()
    };
    onAddImageLibraryItem(item);
    setImgTitle('');
    setImgUrl('');
    alert('ફોટો ઇમેજ લાઇબ્રેરીમાં સાચવવામાં આવ્યો!');
  };

  const handleCreateGk = (e: React.FormEvent) => {
    e.preventDefault();
    if (!gkTitle.trim()) return;
    const item: GkItem = {
      id: Date.now(),
      category: gkCategory,
      title: gkTitle.trim(),
      content: gkContent.trim(),
      oneLinerFact: gkOneLiner.trim(),
      exam: 'બધી પરીક્ષાઓ',
      subject: 'જનરલ નોલેજ',
      isPublished: true,
      createdAt: Date.now()
    };
    onAddGk(item);
    setGkTitle('');
    setGkContent('');
    setGkOneLiner('');
    alert('નવો GK ટોપિક ઉમેરાઈ ગયો!');
  };

  // AI Generation
  const handleGenerateAi = () => {
    setAiLoading(true);
    setTimeout(() => {
      const generated: Omit<Question, 'id'>[] = [
        {
          examId: 1,
          part: 'ભાગ-બ (Part B)',
          subject: 'ભારતીય બંધારણ',
          topic: aiTopic,
          difficulty: 'મધ્યમ',
          questionText: `${aiTopic} સંદર્ભે ભારતના બંધારણમાં કઈ કલમ હેઠળ નાગરિકોને રક્ષણ આપવામાં આવે છે?`,
          optionA: 'કલમ ૨૧',
          optionB: 'કલમ ૧૪',
          optionC: 'કલમ ૧૯',
          optionD: 'કલમ ૩૨',
          correctOption: 'A',
          explanation: 'કલમ ૨૧ અંતર્ગત જીવન અને વ્યક્તિગત સ્વતંત્રતાનું મૂળભૂત અધિકાર રક્ષણ આપવામાં આવ્યું છે.',
          marks: 1.0,
          negativeMarks: 0.25,
          source: 'Tantaniya Academy AI Generator',
          createdAt: Date.now()
        },
        {
          examId: 1,
          part: 'ભાગ-બ (Part B)',
          subject: 'ગુજરાત ઇતિહાસ & વારસો',
          topic: aiTopic,
          difficulty: 'સરળ',
          questionText: `ગુજરાતમાં સોલંકી વંશના કયા શાસકે સિદ્ધપુરમાં રુદ્રમહાલયનું બાંધકામ કરાવ્યું હતું?`,
          optionA: 'મૂળરાજ પહેલો',
          optionB: 'સિદ્ધરાજ જયસિંહ',
          optionC: 'કુમારપાળ',
          optionD: 'ભીમદેવ પહેલો',
          correctOption: 'A',
          explanation: 'મૂળરાજ સોલંકીએ રુદ્રમહાલયનું બાંધકામ શરૂ કરાવ્યું હતું અને સિદ્ધરાજ જયસિંહે તેને પૂર્ણ કરાવ્યું હતું.',
          marks: 1.0,
          negativeMarks: 0.25,
          source: 'Tantaniya Academy AI Generator',
          createdAt: Date.now()
        }
      ];
      setAiGenerated(generated);
      setAiLoading(false);
    }, 1200);
  };

  const handleSaveAiQuestion = (q: Omit<Question, 'id'>) => {
    onAddQuestion({ ...q, id: Date.now() });
    setAiGenerated(prev => prev.filter(item => item !== q));
    alert('AI પ્રશ્ન ક્લાઉડ પ્રશ્ન બેંકમાં સાચવવામાં આવ્યો!');
  };

  // Filtered Questions
  const filteredQList = questions.filter(q => {
    const matchesSearch = !qSearch || q.questionText.toLowerCase().includes(qSearch.toLowerCase()) || q.subject.includes(qSearch);
    const matchesExam = qExamFilter === 0 || q.examId === qExamFilter;
    return matchesSearch && matchesExam;
  });

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-3 sm:p-6 pb-24 max-w-6xl mx-auto">
      {/* Top Header */}
      <div className="flex items-center justify-between pb-4 mb-5 border-b border-slate-800">
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-amber-500/20 border border-amber-500/40 text-amber-400 flex items-center justify-center font-bold">
            <Award className="w-5 h-5" />
          </div>
          <div>
            <h1 className="text-base sm:text-lg font-bold text-white flex items-center gap-2">
              <span>Tantaniya Academy Admin CMS</span>
              <span className="px-2 py-0.5 rounded text-[10px] bg-amber-400 text-slate-950 font-bold">
                લાઈવ ક્લાઉડ
              </span>
            </h1>
            <p className="text-xs text-slate-400">
              સંચાલક: <strong className="text-slate-200">{user?.name}</strong> • ભૂમિકા: ADMIN (સંપૂર્ણ ક્લાઉડ નિયંત્રણ)
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

      {/* Navigation Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2 mb-5">
        {[
          { id: 'overview', label: 'ઓવરવ્યુ', icon: Database },
          { id: 'exams', label: `પરીક્ષાઓ (${exams.length})`, icon: Layers },
          { id: 'tests', label: `મોક ટેસ્ટ (${tests.length})`, icon: FileText },
          { id: 'questions', label: `પ્રશ્ન બેંક (${questions.length})`, icon: BookOpen },
          { id: 'ca', label: `કરંટ અફેર્સ (${currentAffairs.length})`, icon: Upload },
          { id: 'materials', label: `PDF લાયબ્રેરી (${studyMaterials.length})`, icon: FileText },
          { id: 'images', label: `ફોટો લાયબ્રેરી (${imageLibrary.length})`, icon: ImageIcon },
          { id: 'gk', label: `GK (${gkItems.length})`, icon: Sparkles },
          { id: 'ai', label: 'AI પ્રશ્ન જનરેટર', icon: Sparkles },
          { id: 'results', label: `પરિણામો (${testAttempts.length})`, icon: BarChart2 }
        ].map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`px-3 py-2 rounded-xl text-xs font-semibold flex items-center gap-1.5 shrink-0 transition-all ${
                isActive ? 'bg-amber-500 text-slate-950 font-bold shadow-md shadow-amber-500/20' : 'bg-slate-900 text-slate-300 hover:bg-slate-800'
              }`}
            >
              <Icon className="w-3.5 h-3.5" />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </div>

      {/* 1. OVERVIEW TAB */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
              <span className="text-xs text-slate-400">કુલ પરીક્ષાઓ</span>
              <p className="text-2xl font-bold text-white mt-1">{exams.length}</p>
            </div>
            <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
              <span className="text-xs text-slate-400">મોક ટેસ્ટ સીરીઝ</span>
              <p className="text-2xl font-bold text-amber-400 mt-1">{tests.length}</p>
            </div>
            <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
              <span className="text-xs text-slate-400">કુલ પ્રશ્ન બેંક</span>
              <p className="text-2xl font-bold text-emerald-400 mt-1">{questions.length}</p>
            </div>
            <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
              <span className="text-xs text-slate-400">સબમિટ થયેલા પરિણામો</span>
              <p className="text-2xl font-bold text-blue-400 mt-1">{testAttempts.length}</p>
            </div>
          </div>

          <div className="bg-gradient-to-r from-blue-950/40 via-indigo-950/40 to-slate-900 border border-blue-800/40 p-5 rounded-2xl">
            <h2 className="text-sm font-bold text-blue-300 mb-1">ક્લાઉડ સિંક્રોનાઇઝેશન સ્ટેટસ (Cloud Sync Active)</h2>
            <p className="text-xs text-slate-300 leading-relaxed">
              આ પ્લેટફોર્મ પર એડમિન દ્વારા ઉમેરાયેલ તમામ કન્ટેન્ટ (પરીક્ષાઓ, ટેસ્ટ, પ્રશ્નો, કરંટ અફેર્સ ફોટા, PDF દસ્તાવેજો) સીધા જ Cloud Firestore અને Storage માં સંગ્રહાય છે.
              તમે જે કંઈ પણ અપડેટ કરશો તે અન્ય તમામ મોબાઇલ અને વેબ યુઝર્સને વાસ્તવિક સમયમાં મળશે.
            </p>
          </div>
        </div>
      )}

      {/* 2. EXAMS TAB */}
      {activeTab === 'exams' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <Plus className="w-4 h-4" />
              <span>નવી સ્પર્ધાત્મક પરીક્ષા ઉમેરો</span>
            </h2>
            <form onSubmit={handleCreateExam} className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <input
                type="text"
                placeholder="પરીક્ષાનું નામ (દા.ત. વનરક્ષક ફોરેસ્ટ ગાર્ડ)"
                value={newExamTitle}
                onChange={e => setNewExamTitle(e.target.value)}
                className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                required
              />
              <input
                type="text"
                placeholder="પરીક્ષા કોડ (દા.ત. FOREST-2026)"
                value={newExamCode}
                onChange={e => setNewExamCode(e.target.value)}
                className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              />
              <div className="flex gap-2">
                <input
                  type="number"
                  placeholder="કુલ ગુણ"
                  value={newExamMarks}
                  onChange={e => setNewExamMarks(Number(e.target.value))}
                  className="w-1/2 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
                <input
                  type="number"
                  placeholder="સમય (મિનિટ)"
                  value={newExamDuration}
                  onChange={e => setNewExamDuration(Number(e.target.value))}
                  className="w-1/2 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>
              <button
                type="submit"
                className="bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs py-2 rounded-xl"
              >
                પરીક્ષા ક્લાઉડમાં સાચવો
              </button>
            </form>
          </div>

          <div className="space-y-3">
            <h3 className="text-xs font-bold text-slate-400">હાલની પરીક્ષાઓ ({exams.length})</h3>
            {exams.map(exam => (
              <div key={exam.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center justify-between">
                <div>
                  <span className="px-2 py-0.5 rounded text-[10px] bg-slate-800 text-amber-400 font-bold">{exam.code}</span>
                  <h4 className="font-bold text-white text-sm mt-1">{exam.title}</h4>
                  <p className="text-xs text-slate-400">{exam.description}</p>
                </div>
                <button
                  onClick={() => onDeleteExam(exam.id)}
                  className="p-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 3. TESTS TAB */}
      {activeTab === 'tests' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <Plus className="w-4 h-4" />
              <span>નવી CBRT મોક ટેસ્ટ બનાવો (૧ થી ૨૦૦ પ્રશ્નો)</span>
            </h2>
            <form onSubmit={handleCreateTest} className="space-y-3">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="ટેસ્ટનું નામ (દા.ત. કોન્સ્ટેબલ મોક ટેસ્ટ ૦૨)"
                  value={testTitle}
                  onChange={e => setTestTitle(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
                <select
                  value={testExamId}
                  onChange={e => setTestExamId(Number(e.target.value))}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  {exams.map(e => (
                    <option key={e.id} value={e.id}>{e.title}</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div>
                  <label className="text-[10px] text-slate-400">કુલ પ્રશ્નો</label>
                  <input
                    type="number"
                    value={testQuestionsCount}
                    onChange={e => setTestQuestionsCount(Number(e.target.value))}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="text-[10px] text-slate-400">કુલ ગુણ</label>
                  <input
                    type="number"
                    value={testMarks}
                    onChange={e => setTestMarks(Number(e.target.value))}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="text-[10px] text-slate-400">સમય (મિનિટ)</label>
                  <input
                    type="number"
                    value={testDuration}
                    onChange={e => setTestDuration(Number(e.target.value))}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="text-[10px] text-slate-400">નેગેટિવ માર્કિંગ</label>
                  <input
                    type="number"
                    step="0.05"
                    value={testNegative}
                    onChange={e => setTestNegative(Number(e.target.value))}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <textarea
                placeholder="ટેસ્ટનું ટૂંકું વર્ણન અથવા સૂચનાઓ"
                value={testDesc}
                onChange={e => setTestDesc(e.target.value)}
                rows={2}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              />

              <button
                type="submit"
                className="w-full bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs py-2.5 rounded-xl shadow-md"
              >
                મોક ટેસ્ટ પબ્લિશ કરો (ક્લાઉડમાં સાચવો)
              </button>
            </form>
          </div>

          <div className="space-y-3">
            <h3 className="text-xs font-bold text-slate-400">બધી મોક ટેસ્ટ ({tests.length})</h3>
            {tests.map(test => (
              <div key={test.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center justify-between">
                <div>
                  <span className="px-2 py-0.5 rounded text-[10px] bg-blue-900/60 text-blue-300 font-bold">
                    {exams.find(e => e.id === test.examId)?.title || 'પરીક્ષા'}
                  </span>
                  <h4 className="font-bold text-white text-sm mt-1">{test.title}</h4>
                  <div className="flex gap-3 text-xs text-slate-400 mt-1">
                    <span>પ્રશ્નો: {test.totalQuestions}</span>
                    <span>ગુણ: {test.totalMarks}</span>
                    <span>સમય: {test.durationMinutes} મિ.</span>
                    <span className="text-rose-400">નેગેટિવ: -{test.negativeMarking}</span>
                  </div>
                </div>
                <button
                  onClick={() => onDeleteTest(test.id)}
                  className="p-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 4. QUESTIONS (QUESTION BANK) TAB */}
      {activeTab === 'questions' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <Plus className="w-4 h-4" />
              <span>પ્રશ્ન બેંકમાં નવો પ્રશ્ન ઉમેરો</span>
            </h2>
            <form onSubmit={handleCreateQuestion} className="space-y-3">
              <textarea
                placeholder="ગુજરાતીમાં પ્રશ્ન લખાણ દાખલ કરો..."
                value={qText}
                onChange={e => setQText(e.target.value)}
                rows={2}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                required
              />

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="વિષય (દા.ત. ભારતીય બંધારણ)"
                  value={qSubject}
                  onChange={e => setQSubject(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
                <input
                  type="text"
                  placeholder="ટોપિક (દા.ત. મૂળભૂત અધિકારો)"
                  value={qTopic}
                  onChange={e => setQTopic(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                <input
                  type="text"
                  placeholder="વિકલ્પ A"
                  value={optA}
                  onChange={e => setOptA(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-1.5 text-xs text-white"
                  required
                />
                <input
                  type="text"
                  placeholder="વિકલ્પ B"
                  value={optB}
                  onChange={e => setOptB(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-1.5 text-xs text-white"
                  required
                />
                <input
                  type="text"
                  placeholder="વિકલ્પ C"
                  value={optC}
                  onChange={e => setOptC(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-1.5 text-xs text-white"
                  required
                />
                <input
                  type="text"
                  placeholder="વિકલ્પ D"
                  value={optD}
                  onChange={e => setOptD(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-1.5 text-xs text-white"
                  required
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <select
                  value={correctOpt}
                  onChange={e => setCorrectOpt(e.target.value as any)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  <option value="A">સાચો જવાબ: વિકલ્પ A</option>
                  <option value="B">સાચો જવાબ: વિકલ્પ B</option>
                  <option value="C">સાચો જવાબ: વિકલ્પ C</option>
                  <option value="D">સાચો જવાબ: વિકલ્પ D</option>
                </select>
                <input
                  type="text"
                  placeholder="સમજૂતી (Explanation)"
                  value={explanation}
                  onChange={e => setExplanation(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <button
                type="submit"
                className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs py-2.5 rounded-xl shadow-md"
              >
                પ્રશ્ન બેંકમાં ઉમેરો
              </button>
            </form>
          </div>

          {/* Search & Filter */}
          <div className="flex flex-col sm:flex-row gap-2">
            <div className="relative flex-1">
              <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
              <input
                type="text"
                placeholder="પ્રશ્ન અથવા વિષય શોધો..."
                value={qSearch}
                onChange={e => setQSearch(e.target.value)}
                className="w-full bg-slate-900 border border-slate-800 rounded-xl pl-9 pr-3 py-2 text-xs text-white"
              />
            </div>
            <select
              value={qExamFilter}
              onChange={e => setQExamFilter(Number(e.target.value))}
              className="bg-slate-900 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
            >
              <option value={0}>બધી પરીક્ષાઓ</option>
              {exams.map(e => (
                <option key={e.id} value={e.id}>{e.title}</option>
              ))}
            </select>
          </div>

          <div className="space-y-3">
            <h3 className="text-xs font-bold text-slate-400">
              શોધાયેલા પ્રશ્નો ({filteredQList.length} / {questions.length})
            </h3>
            {filteredQList.slice(0, 30).map((q, idx) => (
              <div key={q.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="px-2 py-0.5 rounded text-[10px] bg-slate-800 text-emerald-400 font-bold">
                      #{idx + 1} • {q.subject}
                    </span>
                    <span className="text-[10px] text-slate-400">{q.topic}</span>
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => onAddQuestion({ ...q, id: Date.now() })}
                      title="ડુપ્લિકેટ કરો"
                      className="p-1.5 rounded-lg bg-slate-800 text-slate-300 hover:text-white"
                    >
                      <Copy className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => onDeleteQuestion(q.id)}
                      className="p-1.5 rounded-lg bg-rose-500/10 text-rose-400 hover:bg-rose-500/20"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                <p className="text-xs text-white font-medium">{q.questionText}</p>

                <div className="grid grid-cols-2 gap-2 text-[11px] text-slate-300">
                  <span className={q.correctOption === 'A' ? 'text-emerald-400 font-bold' : ''}>A: {q.optionA}</span>
                  <span className={q.correctOption === 'B' ? 'text-emerald-400 font-bold' : ''}>B: {q.optionB}</span>
                  <span className={q.correctOption === 'C' ? 'text-emerald-400 font-bold' : ''}>C: {q.optionC}</span>
                  <span className={q.correctOption === 'D' ? 'text-emerald-400 font-bold' : ''}>D: {q.optionD}</span>
                </div>

                {q.explanation && (
                  <p className="text-[10px] text-slate-400 bg-slate-950 p-2 rounded-lg">
                    💡 સમજૂતી: {q.explanation}
                  </p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 5. CURRENT AFFAIRS TAB */}
      {activeTab === 'ca' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <Upload className="w-4 h-4" />
              <span>નવા કરંટ અફેર્સ ઉમેરો (ફોટો અપલોડ સાથે)</span>
            </h2>
            <form onSubmit={handleCreateCa} className="space-y-3">
              <input
                type="text"
                placeholder="મુખ્ય હેડલાઈન (Headline)"
                value={caHeadline}
                onChange={e => setCaHeadline(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                required
              />

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="તારીખ (દા.ત. ૨૯ સપ્ટેમ્બર ૨૦૨૬)"
                  value={caDate}
                  onChange={e => setCaDate(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
                <select
                  value={caCategory}
                  onChange={e => setCaCategory(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  <option value="ગુજરાત">ગુજરાત</option>
                  <option value="ભારત">ભારત</option>
                  <option value="વિજ્ઞાન & ટેક્નોલોજી">વિજ્ઞાન & ટેક્નોલોજી</option>
                  <option value="રમતગમત">રમતગમત</option>
                  <option value="યોજનાઓ">યોજનાઓ</option>
                  <option value="અર્થતંત્ર">અર્થતંત્ર</option>
                  <option value="વિશ્વ">વિશ્વ</option>
                </select>
              </div>

              <textarea
                placeholder="વિગતવાર સમાચાર લખાણ..."
                value={caDesc}
                onChange={e => setCaDesc(e.target.value)}
                rows={3}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                required
              />

              {/* Photo Upload from phone/device */}
              <div className="bg-slate-950 border border-slate-800 p-3 rounded-xl space-y-2">
                <label className="text-xs font-semibold text-slate-300 flex items-center gap-1.5">
                  <ImageIcon className="w-4 h-4 text-amber-400" />
                  <span>સમાચાર માટે ફોટો / ઇમેજ અપલોડ કરો (ગેલરીમાંથી પસંદ કરો)</span>
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleUploadCaImage}
                  className="text-xs text-slate-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:font-semibold file:bg-amber-500 file:text-slate-950 hover:file:bg-amber-400 cursor-pointer"
                />
                {caUploading && <p className="text-[11px] text-amber-400 animate-pulse">ક્લાઉડ સ્ટોરેજ પર અપલોડ થઈ રહ્યું છે...</p>}
                {caImageUrl && (
                  <div className="mt-2 flex items-center gap-3">
                    <img src={caImageUrl} alt="Preview" className="w-16 h-16 object-cover rounded-lg border border-slate-700" />
                    <span className="text-xs text-emerald-400 font-semibold">✓ ફોટો અપલોડ થયો છે! બધા યુઝર્સને દેખાશે.</span>
                  </div>
                )}
              </div>

              <button
                type="submit"
                className="w-full bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs py-2.5 rounded-xl shadow-md"
              >
                કરંટ અફેર્સ પબ્લિશ કરો (ક્લાઉડ સિંક)
              </button>
            </form>
          </div>

          <div className="space-y-3">
            <h3 className="text-xs font-bold text-slate-400">પ્રકાશિત કરંટ અફેર્સ ({currentAffairs.length})</h3>
            {currentAffairs.map(item => (
              <div key={item.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex flex-col sm:flex-row gap-3 justify-between">
                <div className="flex gap-3">
                  {item.imageUrl && (
                    <img src={item.imageUrl} alt={item.headline} className="w-20 h-20 object-cover rounded-xl shrink-0 border border-slate-800" />
                  )}
                  <div>
                    <div className="flex gap-2 items-center">
                      <span className="px-2 py-0.5 rounded text-[10px] bg-blue-900/60 text-blue-300 font-bold">{item.category}</span>
                      <span className="text-[10px] text-slate-400">{item.dateText}</span>
                    </div>
                    <h4 className="font-bold text-white text-sm mt-1">{item.headline}</h4>
                    <p className="text-xs text-slate-400 mt-1 line-clamp-2">{item.description}</p>
                  </div>
                </div>
                <button
                  onClick={() => onDeleteCurrentAffairs(item.id)}
                  className="self-end sm:self-center p-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 6. STUDY MATERIALS & PDF LIBRARY TAB */}
      {activeTab === 'materials' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <FileText className="w-4 h-4" />
              <span>અભ્યાસ સામગ્રી / PDF લાઇબ્રેરી (PDF અપલોડ સાથે)</span>
            </h2>
            <form onSubmit={handleCreateStudyMaterial} className="space-y-3">
              <input
                type="text"
                placeholder="શિર્ષક (દા.ત. ભારતીય બંધારણ શોર્ટ ટ્રીક્સ PDF)"
                value={smTitle}
                onChange={e => setSmTitle(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                required
              />

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                <input
                  type="text"
                  placeholder="વિષય (દા.ત. ભારતીય બંધારણ)"
                  value={smSubject}
                  onChange={e => setSmSubject(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
                <input
                  type="text"
                  placeholder="ટોપિક (દા.ત. મૂળભૂત અધિકારો)"
                  value={smTopic}
                  onChange={e => setSmTopic(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
                <select
                  value={smType}
                  onChange={e => setSmType(e.target.value as any)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  <option value="PDF">PDF દસ્તાવેજ</option>
                  <option value="નોટ્સ">નોટ્સ</option>
                  <option value="શોર્ટ ટ્રીક્સ">શોર્ટ ટ્રીક્સ</option>
                </select>
              </div>

              <textarea
                placeholder="ટૂંકું વર્ણન અથવા નોટ્સ લખાણ..."
                value={smContent}
                onChange={e => setSmContent(e.target.value)}
                rows={3}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              />

              {/* PDF File Upload from phone/device */}
              <div className="bg-slate-950 border border-slate-800 p-3 rounded-xl space-y-2">
                <label className="text-xs font-semibold text-slate-300 flex items-center gap-1.5">
                  <Upload className="w-4 h-4 text-emerald-400" />
                  <span>PDF અથવા ડોક્યુમેન્ટ અપલોડ કરો (ગેલરી/ફાઇલ્સમાંથી)</span>
                </label>
                <input
                  type="file"
                  accept="application/pdf,image/*"
                  onChange={handleUploadPdf}
                  className="text-xs text-slate-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:font-semibold file:bg-emerald-600 file:text-white hover:file:bg-emerald-500 cursor-pointer"
                />
                {smUploading && <p className="text-[11px] text-emerald-400 animate-pulse">PDF ક્લાઉડ સ્ટોરેજ પર અપલોડ થઈ રહી છે...</p>}
                {smFileUrl && (
                  <p className="text-xs text-emerald-400 font-semibold">✓ ફાઇલ સફળતાપૂર્વક અપલોડ થઈ ગઈ છે! બધા વિદ્યાર્થીઓ ખોલી શકશે.</p>
                )}
              </div>

              <button
                type="submit"
                className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs py-2.5 rounded-xl shadow-md"
              >
                સામગ્રી પબ્લિશ કરો (ક્લાઉડ સેવ)
              </button>
            </form>
          </div>

          <div className="space-y-3">
            <h3 className="text-xs font-bold text-slate-400">ઉપલબ્ધ સામગ્રી ({studyMaterials.length})</h3>
            {studyMaterials.map(item => (
              <div key={item.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center justify-between">
                <div>
                  <div className="flex gap-2 items-center">
                    <span className="px-2 py-0.5 rounded text-[10px] bg-emerald-950 border border-emerald-800 text-emerald-400 font-bold">{item.type}</span>
                    <span className="text-[10px] text-slate-400">{item.subject} • {item.topic}</span>
                  </div>
                  <h4 className="font-bold text-white text-sm mt-1">{item.title}</h4>
                  <p className="text-xs text-slate-400 mt-0.5">{item.description}</p>
                  {item.fileUrl && (
                    <a href={item.fileUrl} target="_blank" rel="noreferrer" className="inline-block mt-2 text-xs text-blue-400 hover:underline">
                      🔗 PDF ખોલો / ડાઉનલોડ કરો
                    </a>
                  )}
                </div>
                <button
                  onClick={() => onDeleteStudyMaterial(item.id)}
                  className="p-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 7. IMAGE LIBRARY TAB */}
      {activeTab === 'images' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <ImageIcon className="w-4 h-4" />
              <span>એડમિન ઇમેજ લાઇબ્રેરી (ફોટો અપલોડ)</span>
            </h2>
            <form onSubmit={handleCreateImageLibrary} className="space-y-3">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="ફોટોનું શીર્ષક / વિગત"
                  value={imgTitle}
                  onChange={e => setImgTitle(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
                <input
                  type="text"
                  placeholder="કેટેગરી (દા.ત. નકશો, સૂત્ર, પુસ્તક)"
                  value={imgCat}
                  onChange={e => setImgCat(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div className="bg-slate-950 border border-slate-800 p-3 rounded-xl space-y-2">
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleUploadLibraryImage}
                  className="text-xs text-slate-400 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:font-semibold file:bg-amber-500 file:text-slate-950 hover:file:bg-amber-400 cursor-pointer"
                />
                {imgUploading && <p className="text-[11px] text-amber-400 animate-pulse">ઇમેજ ક્લાઉડમાં અપલોડ થઈ રહી છે...</p>}
                {imgUrl && (
                  <div className="mt-2 flex items-center gap-3">
                    <img src={imgUrl} alt="Preview" className="w-16 h-16 object-cover rounded-lg border border-slate-700" />
                    <span className="text-xs text-emerald-400 font-semibold">✓ ઇમેજ ક્લાઉડમાં સંગ્રહાઈ ગઈ છે!</span>
                  </div>
                )}
              </div>

              <button
                type="submit"
                className="w-full bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs py-2.5 rounded-xl shadow-md"
              >
                ઇમેજ લાઇબ્રેરીમાં સાચવો
              </button>
            </form>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            {imageLibrary.map(item => (
              <div key={item.id} className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden relative group">
                <img src={item.imageUrl} alt={item.title} className="w-full h-32 object-cover" />
                <div className="p-2">
                  <h5 className="font-bold text-xs text-white truncate">{item.title}</h5>
                  <span className="text-[10px] text-slate-400">{item.category}</span>
                </div>
                <button
                  onClick={() => onDeleteImageLibraryItem(Number(item.id))}
                  className="absolute top-2 right-2 p-1.5 rounded-lg bg-slate-950/80 text-rose-400 hover:bg-rose-500 hover:text-white"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 8. GK TAB */}
      {activeTab === 'gk' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <h2 className="text-sm font-bold text-amber-400 mb-3 flex items-center gap-1.5">
              <Plus className="w-4 h-4" />
              <span>નવો જનરલ નોલેજ (GK) ટોપિક ઉમેરો</span>
            </h2>
            <form onSubmit={handleCreateGk} className="space-y-3">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="શીર્ષક (દા.ત. ગુજરાતના રાષ્ટ્રીય ઉદ્યાનો)"
                  value={gkTitle}
                  onChange={e => setGkTitle(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                  required
                />
                <input
                  type="text"
                  placeholder="કેટેગરી (દા.ત. ગુજરાત ભૂગોળ)"
                  value={gkCategory}
                  onChange={e => setGkCategory(e.target.value)}
                  className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <textarea
                placeholder="સંપૂર્ણ માહિતી / વિગત..."
                value={gkContent}
                onChange={e => setGkContent(e.target.value)}
                rows={3}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                required
              />

              <input
                type="text"
                placeholder="વન-લાઇનર મુખ્ય ફેક્ટ (One-liner Fact)"
                value={gkOneLiner}
                onChange={e => setGkOneLiner(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              />

              <button
                type="submit"
                className="w-full bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs py-2.5 rounded-xl shadow-md"
              >
                GK ક્લાઉડમાં પબ્લિશ કરો
              </button>
            </form>
          </div>

          <div className="space-y-3">
            <h3 className="text-xs font-bold text-slate-400">GK સંગ્રહ ({gkItems.length})</h3>
            {gkItems.map(item => (
              <div key={item.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center justify-between">
                <div>
                  <span className="px-2 py-0.5 rounded text-[10px] bg-slate-800 text-amber-400 font-bold">{item.category}</span>
                  <h4 className="font-bold text-white text-sm mt-1">{item.title}</h4>
                  <p className="text-xs text-slate-400 mt-1 line-clamp-2">{item.content}</p>
                  {item.oneLinerFact && <p className="text-[11px] text-amber-300 mt-1">💡 {item.oneLinerFact}</p>}
                </div>
                <button
                  onClick={() => onDeleteGk(item.id)}
                  className="p-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 9. AI MCQ GENERATOR TAB */}
      {activeTab === 'ai' && (
        <div className="space-y-6">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl">
            <div className="flex items-center gap-2 mb-3">
              <Sparkles className="w-5 h-5 text-amber-400" />
              <h2 className="text-sm font-bold text-white">AI MCQ પ્રશ્ન જનરેટર (ગુજરાતી સ્પર્ધાત્મક પરીક્ષાઓ)</h2>
            </div>
            <p className="text-xs text-slate-400 mb-4">
              પરીક્ષા અને ચોક્કસ વિષય પસંદ કરી આપોઆપ ૪ વિકલ્પો, સાચો જવાબ અને સમજૂતી સાથે MCQs બનાવો. મંજૂર કર્યા પછી જ પ્રશ્ન બેંકમાં ઉમેરાશે.
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 mb-4">
              <div>
                <label className="text-[10px] text-slate-400">પરીક્ષા</label>
                <select
                  value={aiExam}
                  onChange={e => setAiExam(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                >
                  <option value="ગુજરાત પોલીસ કોન્સ્ટેબલ">ગુજરાત પોલીસ કોન્સ્ટેબલ</option>
                  <option value="વનરક્ષક ફોરેસ્ટ ગાર્ડ">વનરક્ષક ફોરેસ્ટ ગાર્ડ</option>
                  <option value="MPHW">MPHW</option>
                </select>
              </div>
              <div className="sm:col-span-2">
                <label className="text-[10px] text-slate-400">ચોક્કસ ટોપિક</label>
                <input
                  type="text"
                  placeholder="દા.ત. ભારતીય બંધારણ - મૂળભૂત અધિકારો"
                  value={aiTopic}
                  onChange={e => setAiTopic(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>
            </div>

            <div className="flex items-center gap-3">
              <select
                value={aiCount}
                onChange={e => setAiCount(Number(e.target.value))}
                className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              >
                <option value={2}>૨ પ્રશ્નો</option>
                <option value={5}>૫ પ્રશ્નો</option>
                <option value={10}>૧૦ પ્રશ્નો</option>
                <option value={20}>૨૦ પ્રશ્નો</option>
              </select>

              <button
                type="button"
                onClick={handleGenerateAi}
                disabled={aiLoading}
                className="flex-1 bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-400 hover:to-amber-500 text-slate-950 font-bold text-xs py-2.5 rounded-xl flex items-center justify-center gap-2 shadow-md shadow-amber-500/20"
              >
                <Sparkles className="w-4 h-4" />
                <span>{aiLoading ? 'પ્રશ્નો જનરેટ થઈ રહ્યા છે...' : 'ગુજરાતી MCQs જનરેટ કરો'}</span>
              </button>
            </div>
          </div>

          {aiGenerated.length > 0 && (
            <div className="space-y-3">
              <h3 className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4" />
                <span>જનરેટ થયેલ પ્રશ્નોની ચકાસણી (મંજૂર કરો)</span>
              </h3>
              {aiGenerated.map((q, idx) => (
                <div key={idx} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-2">
                  <div className="flex justify-between items-center">
                    <span className="px-2 py-0.5 rounded text-[10px] bg-slate-800 text-amber-400 font-bold">{q.subject}</span>
                    <button
                      onClick={() => handleSaveAiQuestion(q)}
                      className="px-3 py-1 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs flex items-center gap-1 shadow-sm"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>મંજૂર કરી સાચવો</span>
                    </button>
                  </div>
                  <p className="text-xs text-white font-semibold">{q.questionText}</p>
                  <div className="grid grid-cols-2 gap-1 text-[11px] text-slate-300">
                    <span className={q.correctOption === 'A' ? 'text-emerald-400 font-bold' : ''}>A: {q.optionA}</span>
                    <span className={q.correctOption === 'B' ? 'text-emerald-400 font-bold' : ''}>B: {q.optionB}</span>
                    <span className={q.correctOption === 'C' ? 'text-emerald-400 font-bold' : ''}>C: {q.optionC}</span>
                    <span className={q.correctOption === 'D' ? 'text-emerald-400 font-bold' : ''}>D: {q.optionD}</span>
                  </div>
                  <p className="text-[10px] text-slate-400 bg-slate-950 p-2 rounded-lg">💡 સમજૂતી: {q.explanation}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* 10. RESULTS TAB */}
      {activeTab === 'results' && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-slate-400">વિદ્યાર્થીઓના ટેસ્ટ પરિણામો ({testAttempts.length})</h3>
          </div>
          {testAttempts.length === 0 ? (
            <div className="bg-slate-900 border border-slate-800 p-8 rounded-2xl text-center text-slate-400 text-xs">
              હજી સુધી કોઈ ટેસ્ટ સબમિટ થયેલ નથી.
            </div>
          ) : (
            testAttempts.map(attempt => (
              <div key={attempt.id} className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex flex-col sm:flex-row justify-between gap-3">
                <div>
                  <span className="px-2 py-0.5 rounded text-[10px] bg-blue-900/60 text-blue-300 font-bold">{attempt.examTitle}</span>
                  <h4 className="font-bold text-white text-sm mt-1">{attempt.userName} • {attempt.testTitle}</h4>
                  <div className="flex gap-4 text-xs text-slate-400 mt-1">
                    <span>સ્કોર: <strong className="text-emerald-400">{attempt.finalScore}</strong> / {attempt.totalQuestions}</span>
                    <span>ટકાવારી: <strong className="text-white">{attempt.percentage.toFixed(1)}%</strong></span>
                    <span>ચોકસાઈ: <strong className="text-amber-400">{attempt.accuracy.toFixed(1)}%</strong></span>
                  </div>
                </div>
                <div className="text-right text-[11px] text-slate-500 self-end sm:self-center">
                  {new Date(attempt.submittedAt).toLocaleDateString('gu-IN')}
                </div>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
};
