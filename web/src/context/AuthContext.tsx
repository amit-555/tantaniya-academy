import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { PRIMARY_ADMIN_EMAIL, isPrimaryAdmin } from '../firebase';

interface AuthContextType {
  user: User | null;
  isAdmin: boolean;
  loginAsPrimaryAdmin: () => void;
  loginAsStudent: () => void;
  loginWithEmail: (email: string, name?: string) => void;
  logout: () => void;
}

const DEFAULT_ADMIN: User = {
  id: 1,
  name: "Amit Gangal (Primary Admin)",
  email: PRIMARY_ADMIN_EMAIL,
  role: 'ADMIN',
  targetExam: 'ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)',
  createdAt: Date.now()
};

const DEFAULT_STUDENT: User = {
  id: 2,
  name: "વિદ્યાર્થી મિત્ર (Student)",
  email: "student@tantaniya.in",
  role: 'USER',
  targetExam: 'ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)',
  createdAt: Date.now()
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('tantaniya_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        return DEFAULT_ADMIN;
      }
    }
    return DEFAULT_ADMIN;
  });

  useEffect(() => {
    if (user) {
      // Secure role check: ensure role is strictly ADMIN only for primary admin email
      const safeRole = isPrimaryAdmin(user.email) ? 'ADMIN' : 'USER';
      const safeUser = { ...user, role: safeRole };
      localStorage.setItem('tantaniya_user', JSON.stringify(safeUser));
    } else {
      localStorage.removeItem('tantaniya_user');
    }
  }, [user]);

  const loginAsPrimaryAdmin = () => {
    setUser(DEFAULT_ADMIN);
  };

  const loginAsStudent = () => {
    setUser(DEFAULT_STUDENT);
  };

  const loginWithEmail = (email: string, name?: string) => {
    const cleanEmail = email.trim();
    const role: 'ADMIN' | 'USER' = isPrimaryAdmin(cleanEmail) ? 'ADMIN' : 'USER';
    setUser({
      id: Date.now(),
      name: name?.trim() || (role === 'ADMIN' ? 'Amit Gangal (Primary Admin)' : 'વિદ્યાર્થી મિત્ર'),
      email: cleanEmail,
      role,
      targetExam: 'ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)',
      createdAt: Date.now()
    });
  };

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAdmin: user?.role === 'ADMIN' && isPrimaryAdmin(user?.email),
        loginAsPrimaryAdmin,
        loginAsStudent,
        loginWithEmail,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
