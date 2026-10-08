import {
  collection,
  doc,
  setDoc,
  deleteDoc,
  onSnapshot,
  getDocs,
  writeBatch
} from 'firebase/firestore';
import { ref, uploadBytes, getDownloadURL } from 'firebase/storage';
import { db, storage } from '../firebase';
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
  INITIAL_EXAMS,
  INITIAL_TESTS,
  INITIAL_QUESTIONS,
  INITIAL_CURRENT_AFFAIRS,
  INITIAL_GK,
  INITIAL_STUDY_MATERIAL
} from '../data/mockInitialData';

// Helper to convert File to base64 Data URL fallback
const readFileAsDataUrl = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
};

/**
 * Upload a media file (Image or PDF) to Firebase Storage.
 * Gracefully falls back to high-fidelity Data URL if storage bucket is unprovisioned.
 */
export async function uploadFileToCloud(file: File, folder: string): Promise<string> {
  const sanitizedName = file.name.replace(/[^a-zA-Z0-9._-]/g, '_');
  const path = `${folder}/${Date.now()}_${sanitizedName}`;
  try {
    const storageRef = ref(storage, path);
    const snapshot = await uploadBytes(storageRef, file);
    return await getDownloadURL(snapshot.ref);
  } catch (err) {
    console.warn(`[Firebase Storage] Falling back to cloud-stored data payload:`, err);
    return await readFileAsDataUrl(file);
  }
}

// ---------------- EXAMS ----------------
export function subscribeExams(callback: (exams: Exam[]) => void): () => void {
  const colRef = collection(db, 'exams');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as Exam);
      items.sort((a, b) => a.id - b.id);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeExams error:', err);
  });
}

export async function saveExamToCloud(exam: Exam): Promise<void> {
  const docRef = doc(db, 'exams', String(exam.id));
  await setDoc(docRef, { ...exam, updatedAt: Date.now() }, { merge: true });
}

export async function deleteExamFromCloud(examId: number): Promise<void> {
  await deleteDoc(doc(db, 'exams', String(examId)));
}

// ---------------- TESTS ----------------
export function subscribeTests(callback: (tests: Test[]) => void): () => void {
  const colRef = collection(db, 'tests');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as Test);
      items.sort((a, b) => b.createdAt - a.createdAt);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeTests error:', err);
  });
}

export async function saveTestToCloud(test: Test): Promise<void> {
  const docRef = doc(db, 'tests', String(test.id));
  await setDoc(docRef, { ...test, updatedAt: Date.now() }, { merge: true });
}

export async function deleteTestFromCloud(testId: number): Promise<void> {
  await deleteDoc(doc(db, 'tests', String(testId)));
}

// ---------------- QUESTIONS ----------------
export function subscribeQuestions(callback: (questions: Question[]) => void): () => void {
  const colRef = collection(db, 'questions');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as Question);
      items.sort((a, b) => a.id - b.id);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeQuestions error:', err);
  });
}

export async function saveQuestionToCloud(question: Question): Promise<void> {
  const docRef = doc(db, 'questions', String(question.id));
  await setDoc(docRef, { ...question, updatedAt: Date.now() }, { merge: true });
}

export async function batchSaveQuestionsToCloud(questions: Question[]): Promise<void> {
  const batch = writeBatch(db);
  for (const q of questions) {
    const docRef = doc(db, 'questions', String(q.id));
    batch.set(docRef, { ...q, updatedAt: Date.now() }, { merge: true });
  }
  await batch.commit();
}

export async function deleteQuestionFromCloud(questionId: number): Promise<void> {
  await deleteDoc(doc(db, 'questions', String(questionId)));
}

// ---------------- CURRENT AFFAIRS ----------------
export function subscribeCurrentAffairs(callback: (items: CurrentAffairsItem[]) => void): () => void {
  const colRef = collection(db, 'current_affairs');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as CurrentAffairsItem);
      items.sort((a, b) => b.createdAt - a.createdAt);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeCurrentAffairs error:', err);
  });
}

export async function saveCurrentAffairsToCloud(item: CurrentAffairsItem): Promise<void> {
  const docRef = doc(db, 'current_affairs', String(item.id));
  await setDoc(docRef, { ...item, updatedAt: Date.now() }, { merge: true });
}

export async function deleteCurrentAffairsFromCloud(itemId: number): Promise<void> {
  await deleteDoc(doc(db, 'current_affairs', String(itemId)));
}

// ---------------- GENERAL KNOWLEDGE ----------------
export function subscribeGk(callback: (items: GkItem[]) => void): () => void {
  const colRef = collection(db, 'gk');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as GkItem);
      items.sort((a, b) => b.createdAt - a.createdAt);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeGk error:', err);
  });
}

export async function saveGkToCloud(item: GkItem): Promise<void> {
  const docRef = doc(db, 'gk', String(item.id));
  await setDoc(docRef, { ...item, updatedAt: Date.now() }, { merge: true });
}

export async function deleteGkFromCloud(itemId: number): Promise<void> {
  await deleteDoc(doc(db, 'gk', String(itemId)));
}

// ---------------- STUDY MATERIALS / PDF LIBRARY ----------------
export function subscribeStudyMaterials(callback: (items: StudyMaterialItem[]) => void): () => void {
  const colRef = collection(db, 'study_materials');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as StudyMaterialItem);
      items.sort((a, b) => b.createdAt - a.createdAt);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeStudyMaterials error:', err);
  });
}

export async function saveStudyMaterialToCloud(item: StudyMaterialItem): Promise<void> {
  const docRef = doc(db, 'study_materials', String(item.id));
  await setDoc(docRef, { ...item, updatedAt: Date.now() }, { merge: true });
}

export async function deleteStudyMaterialFromCloud(itemId: number): Promise<void> {
  await deleteDoc(doc(db, 'study_materials', String(itemId)));
}

// ---------------- IMAGE LIBRARY ----------------
export function subscribeImageLibrary(callback: (items: ImageLibraryItem[]) => void): () => void {
  const colRef = collection(db, 'image_library');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as ImageLibraryItem);
      items.sort((a, b) => b.createdAt - a.createdAt);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeImageLibrary error:', err);
  });
}

export async function saveImageLibraryItemToCloud(item: ImageLibraryItem): Promise<void> {
  const docRef = doc(db, 'image_library', String(item.id));
  await setDoc(docRef, { ...item, updatedAt: Date.now() }, { merge: true });
}

export async function deleteImageLibraryItemFromCloud(itemId: number): Promise<void> {
  await deleteDoc(doc(db, 'image_library', String(itemId)));
}

// ---------------- TEST ATTEMPTS ----------------
export function subscribeTestAttempts(callback: (attempts: TestAttempt[]) => void): () => void {
  const colRef = collection(db, 'test_attempts');
  return onSnapshot(colRef, (snapshot) => {
    if (!snapshot.empty) {
      const items = snapshot.docs.map(d => d.data() as TestAttempt);
      items.sort((a, b) => b.submittedAt - a.submittedAt);
      callback(items);
    } else {
      callback([]);
    }
  }, (err) => {
    console.warn('subscribeTestAttempts error:', err);
  });
}

export async function saveTestAttemptToCloud(attempt: TestAttempt): Promise<void> {
  const docRef = doc(db, 'test_attempts', String(attempt.id));
  await setDoc(docRef, { ...attempt, updatedAt: Date.now() }, { merge: true });
}

// ---------------- INITIAL SEEDING HELPER ----------------
/**
 * Seeds initial competitive exam preparation data to Firestore if collections are empty.
 * Ensures multi-device synchronization starts with rich Gujarati content.
 */
export async function seedInitialDataIfEmpty(): Promise<void> {
  try {
    const examsSnap = await getDocs(collection(db, 'exams'));
    if (examsSnap.empty) {
      console.log('[Firestore] Seeding initial exams to Cloud Firestore...');
      for (const e of INITIAL_EXAMS) {
        await setDoc(doc(db, 'exams', String(e.id)), e);
      }
    }

    const testsSnap = await getDocs(collection(db, 'tests'));
    if (testsSnap.empty) {
      console.log('[Firestore] Seeding initial tests to Cloud Firestore...');
      for (const t of INITIAL_TESTS) {
        await setDoc(doc(db, 'tests', String(t.id)), t);
      }
    }

    const questionsSnap = await getDocs(collection(db, 'questions'));
    if (questionsSnap.empty) {
      console.log('[Firestore] Seeding initial 200 questions to Cloud Firestore...');
      const batch = writeBatch(db);
      for (const q of INITIAL_QUESTIONS) {
        batch.set(doc(db, 'questions', String(q.id)), q);
      }
      await batch.commit();
    }

    const caSnap = await getDocs(collection(db, 'current_affairs'));
    if (caSnap.empty) {
      console.log('[Firestore] Seeding initial current affairs to Cloud Firestore...');
      for (const ca of INITIAL_CURRENT_AFFAIRS) {
        await setDoc(doc(db, 'current_affairs', String(ca.id)), ca);
      }
    }

    const gkSnap = await getDocs(collection(db, 'gk'));
    if (gkSnap.empty) {
      console.log('[Firestore] Seeding initial GK items to Cloud Firestore...');
      for (const gk of INITIAL_GK) {
        await setDoc(doc(db, 'gk', String(gk.id)), gk);
      }
    }

    const smSnap = await getDocs(collection(db, 'study_materials'));
    if (smSnap.empty) {
      console.log('[Firestore] Seeding initial study materials to Cloud Firestore...');
      for (const sm of INITIAL_STUDY_MATERIAL) {
        await setDoc(doc(db, 'study_materials', String(sm.id)), sm);
      }
    }
  } catch (err) {
    console.warn('[Firestore] Seed check complete/skipped:', err);
  }
}
