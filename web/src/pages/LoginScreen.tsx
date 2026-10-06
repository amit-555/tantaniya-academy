import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { PRIMARY_ADMIN_EMAIL } from '../firebase';
import { ShieldCheck, User, LogIn, Lock, ArrowLeft } from 'lucide-react';

interface LoginScreenProps {
  onClose: () => void;
}

export const LoginScreen: React.FC<LoginScreenProps> = ({ onClose }) => {
  const { loginAsPrimaryAdmin, loginAsStudent, loginWithEmail } = useAuth();
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim()) return;
    loginWithEmail(email.trim(), name.trim());
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-sm w-full p-6 shadow-2xl text-white">
        <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-800">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-amber-400 text-slate-950 flex items-center justify-center font-bold text-base">
              T
            </div>
            <div>
              <h3 className="text-sm font-bold leading-tight">Tantaniya Academy</h3>
              <p className="text-[10px] text-amber-300">સાઇન ઇન પોર્ટલ</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-white text-xs p-1"
          >
            ✕
          </button>
        </div>

        {/* Quick Role Selectors */}
        <div className="space-y-2 mb-5">
          <button
            onClick={() => {
              loginAsPrimaryAdmin();
              onClose();
            }}
            className="w-full p-3 rounded-2xl bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-400 hover:to-amber-500 text-slate-950 font-bold text-xs flex items-center justify-between shadow-lg shadow-amber-500/20 transition-all"
          >
            <div className="flex items-center gap-2">
              <ShieldCheck className="w-4 h-4" />
              <span>મુખ્ય એડમિન પ્રવેશ (Primary Admin)</span>
            </div>
            <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-950/20">ADMIN</span>
          </button>

          <button
            onClick={() => {
              loginAsStudent();
              onClose();
            }}
            className="w-full p-3 rounded-2xl bg-slate-800 hover:bg-slate-700 text-white font-medium text-xs flex items-center justify-between border border-slate-700/80 transition-all"
          >
            <div className="flex items-center gap-2">
              <User className="w-4 h-4 text-sky-400" />
              <span>સામાન્ય વિદ્યાર્થી પ્રવેશ (Student)</span>
            </div>
            <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-900 text-slate-400">USER</span>
          </button>
        </div>

        <div className="flex items-center gap-2 my-4">
          <div className="h-px bg-slate-800 flex-1"></div>
          <span className="text-[10px] text-slate-500 uppercase tracking-wider">અથવા ઇમેઇલ દ્વારા</span>
          <div className="h-px bg-slate-800 flex-1"></div>
        </div>

        {/* Custom Email Login Form */}
        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="text-[11px] text-slate-400 block mb-1">તમારું નામ (ઓપ્શનલ)</label>
            <input
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
              placeholder="દા.ત. પ્રવીણ સોલંકી"
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
            />
          </div>

          <div>
            <label className="text-[11px] text-slate-400 block mb-1">ઇમેઇલ સરનામું</label>
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="user@example.com"
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
              required
            />
          </div>

          <p className="text-[10px] text-slate-500 leading-tight">
            * નોંધ: સામાન્ય વપરાશકર્તાઓ આપોઆપ <span className="text-slate-300">USER</span> ભૂમિકા મેળવે છે. ફક્ત પૂર્વનિર્ધારિત મુખ્ય એડમિનને <span className="text-amber-400 font-bold">ADMIN</span> એક્સેસ મળે છે.
          </p>

          <button
            type="submit"
            className="w-full py-2.5 rounded-xl bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs shadow-md transition-colors"
          >
            લોગિન કરો
          </button>
        </form>
      </div>
    </div>
  );
};
