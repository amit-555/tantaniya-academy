import { initializeApp, getApps, getApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';

// Environment-configured Firebase credentials or production defaults
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyDummyKeyForTantaniyaAcademy",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "tantaniya-academy.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "tantaniya-academy",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "tantaniya-academy.appspot.com",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "377806462018",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:377806462018:web:tantaniya-academy"
};

// Initialize Firebase safely
export const app = !getApps().length ? initializeApp(firebaseConfig) : getApp();
export const auth = getAuth(app);
export const db = getFirestore(app);

export const PRIMARY_ADMIN_EMAIL = "gangalamit005@gmail.com";
export const LEGACY_ADMIN_EMAIL = "admin@studypro.in";

export const isPrimaryAdmin = (email?: string | null): boolean => {
  if (!email) return false;
  const clean = email.trim().toLowerCase();
  return clean === PRIMARY_ADMIN_EMAIL.toLowerCase() || clean === LEGACY_ADMIN_EMAIL.toLowerCase();
};
