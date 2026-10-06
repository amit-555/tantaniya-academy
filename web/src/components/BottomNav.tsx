import React from 'react';
import { Home, ClipboardList, BookOpen, Compass, Shield } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export type NavTab = 'home' | 'tests' | 'ca' | 'gk' | 'materials' | 'admin';

interface BottomNavProps {
  currentTab: NavTab;
  onTabChange: (tab: NavTab) => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ currentTab, onTabChange }) => {
  const { isAdmin } = useAuth();

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-30 bg-slate-900/95 backdrop-blur-md border-t border-slate-800 text-slate-400 py-1.5 px-3">
      <div className="max-w-md mx-auto flex items-center justify-around">
        <button
          onClick={() => onTabChange('home')}
          className={`flex flex-col items-center gap-1 py-1 px-2 rounded-lg transition-colors ${
            currentTab === 'home' ? 'text-amber-400 font-semibold' : 'hover:text-slate-200'
          }`}
        >
          <Home className="w-5 h-5" />
          <span className="text-[10px]">મુખ્ય</span>
        </button>

        <button
          onClick={() => onTabChange('tests')}
          className={`flex flex-col items-center gap-1 py-1 px-2 rounded-lg transition-colors ${
            currentTab === 'tests' ? 'text-amber-400 font-semibold' : 'hover:text-slate-200'
          }`}
        >
          <ClipboardList className="w-5 h-5" />
          <span className="text-[10px]">મોક ટેસ્ટ</span>
        </button>

        <button
          onClick={() => onTabChange('ca')}
          className={`flex flex-col items-center gap-1 py-1 px-2 rounded-lg transition-colors ${
            currentTab === 'ca' ? 'text-amber-400 font-semibold' : 'hover:text-slate-200'
          }`}
        >
          <Compass className="w-5 h-5" />
          <span className="text-[10px]">કરંટ અફેર્સ</span>
        </button>

        <button
          onClick={() => onTabChange('materials')}
          className={`flex flex-col items-center gap-1 py-1 px-2 rounded-lg transition-colors ${
            currentTab === 'materials' ? 'text-amber-400 font-semibold' : 'hover:text-slate-200'
          }`}
        >
          <BookOpen className="w-5 h-5" />
          <span className="text-[10px]">મટીરીયલ</span>
        </button>

        {isAdmin && (
          <button
            onClick={() => onTabChange('admin')}
            className={`flex flex-col items-center gap-1 py-1 px-2 rounded-lg transition-colors ${
              currentTab === 'admin' ? 'text-amber-400 font-semibold' : 'text-amber-300/80 hover:text-amber-300'
            }`}
          >
            <Shield className="w-5 h-5" />
            <span className="text-[10px]">એડમિન</span>
          </button>
        )}
      </div>
    </nav>
  );
};
