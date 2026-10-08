export interface User {
  id: number | string;
  name: string;
  email: string;
  role: 'ADMIN' | 'USER';
  targetExam: string;
  createdAt: number;
}

export interface Exam {
  id: number;
  title: string;
  code: string;
  description: string;
  totalMarks: number;
  durationMinutes: number;
  negativeMarking: number;
  isPublished: boolean;
  createdAt: number;
}

export interface SyllabusItem {
  id: number;
  examId: number;
  partName: string;
  subjectName: string;
  topicName: string;
  weightageMarks: number;
}

export interface Test {
  id: number;
  examId: number;
  title: string;
  description: string;
  durationMinutes: number;
  totalQuestions: number;
  totalMarks: number;
  negativeMarking: number;
  passingMarks: number;
  instructions: string;
  isPublished: boolean;
  createdAt: number;
}

export interface Question {
  id: number;
  examId: number;
  part: string;
  subject: string;
  topic: string;
  difficulty: 'સરળ' | 'મધ્યમ' | 'કઠિન';
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  correctOption: 'A' | 'B' | 'C' | 'D';
  explanation: string;
  marks: number;
  negativeMarks: number;
  source: string;
  createdAt: number;
}

export interface TestAttempt {
  id: number;
  userId: number | string;
  userName: string;
  testId: number;
  testTitle: string;
  examTitle: string;
  totalQuestions: number;
  attemptedCount: number;
  correctCount: number;
  incorrectCount: number;
  unattemptedCount: number;
  positiveMarks: number;
  negativeMarks: number;
  finalScore: number;
  percentage: number;
  accuracy: number;
  timeUsedSeconds: number;
  submittedAt: number;
  answers: Record<number, string>; // questionId -> selectedOption ('A'|'B'|'C'|'D')
  subjectPerformance: Record<string, { correct: number; total: number }>;
}

export interface CurrentAffairsItem {
  id: number;
  dateText: string;
  headline: string;
  description: string;
  category: string;
  imageUrl?: string;
  importantFacts: string;
  source: string;
  relatedExam: string;
  relatedSubject: string;
  mcqQuestion?: string;
  mcqOptionA?: string;
  mcqOptionB?: string;
  mcqOptionC?: string;
  mcqOptionD?: string;
  mcqCorrect?: string;
  isPublished: boolean;
  createdAt: number;
}

export interface GkItem {
  id: number;
  category: string;
  title: string;
  content: string;
  oneLinerFact: string;
  question?: string;
  answer?: string;
  explanation?: string;
  exam: string;
  subject: string;
  isPublished: boolean;
  createdAt: number;
}

export interface StudyMaterialItem {
  id: number;
  examId: number;
  subject: string;
  topic: string;
  title: string;
  description: string;
  type: 'નોટ્સ' | 'PDF' | 'શોર્ટ ટ્રીક્સ';
  contentText: string;
  fileUrl?: string;
  fileSize?: string;
  isPublished: boolean;
  createdAt: number;
}

export interface ImageLibraryItem {
  id: number;
  title: string;
  category: string;
  description?: string;
  imageUrl: string;
  isPublished: boolean;
  createdAt: number;
}
