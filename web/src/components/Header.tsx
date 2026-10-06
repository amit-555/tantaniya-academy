import React from 'react';
import { ShieldCheck, LogIn, LogOut, Award, UserCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface HeaderProps {
  onOpenLogin: () => void;
  onOpenAdmin: () => void;
  onOpenProfile: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onOpenLogin, onOpenAdmin, onOpenProfile }) => {
  const { user, isAdmin, logout } = useAuth();

  return (
    <header className="sticky top-0 z-30 bg-gradient-to-r from-slate-900 via-blue-950 to-slate-900 border-b border-blue-900/40 text-white shadow-md">
      <div className="max-w-4xl mx-auto px-4 py-3 flex items-center justify-between">
        {/* Brand identity */}
        <div className="flex items-center gap-2.5">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-amber-400 to-amber-600 flex items-center justify-center shadow-lg shadow-amber-500/20 text-slate-950 font-bold text-xl border border-amber-300">
            T
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <h1 className="font-bold text-lg leading-tight tracking-wide text-white">
                Tantaniya Academy
              </h1>
              {isAdmin && (
                <span className="inline-flex items-center gap-0.5 px-1.5 py-0.5 rounded text-[10px] font-bold bg-amber-400 text-slate-950 shadow-sm">
                  <ShieldCheck className="w-3 h-3" />
                  ADMIN
                </span>
              )}
            </div>
            <p className="text-[11px] text-amber-300/90 font-medium">
              તન્તાણિયા એકેડેમી • સરકારી ભરતી પોર્ટલ
            </p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-2">
          {isAdmin && (
            <button
              onClick={onOpenAdmin}
              className="hidden sm:inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-amber-500 hover:bg-amber-400 text-slate-950 shadow transition-colors"
            >
              <Award className="w-3.5 h-3.5" />
              એડમિન પેનલ
            </button>
          )}

          {user ? (
            <div className="flex items-center gap-1.5 bg-slate-800/80 border border-slate-700/60 rounded-full pl-2 pr-1 py-1 text-xs">
              <button
                onClick={onOpenProfile}
                className="text-slate-300 hover:text-white font-medium max-w-[100px] truncate flex items-center gap-1 transition-colors"
                title="પ્રોફાઇલ & સેટિંગ્સ"
              >
                <span>{isAdmin ? "Admin" : user.name.split(' ')[0]}</span>
                <span className="text-[10px] text-amber-400">⚙️</span>
              </button>
              <button
                onClick={logout}
                title="સાઇન આઉટ"
                className="p-1 rounded-full text-slate-400 hover:text-red-400 hover:bg-slate-700/60 transition-colors"
              >
                <LogOut className="w-3.5 h-3.5" />
              </button>
            </div>
          ) : (
            <button
              onClick={onOpenLogin}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold bg-blue-600 hover:bg-blue-500 text-white shadow transition-colors"
            >
              <LogIn className="w-3.5 h-3.5" />
              સાઇન ઇન
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
