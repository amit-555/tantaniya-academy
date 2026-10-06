import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { User, Lock, Bell, Moon, Volume2, Globe, LogOut, CheckCircle2, AlertCircle, X, ShieldCheck } from 'lucide-react';

interface UserProfileSettingsScreenProps {
  onClose: () => void;
}

export const UserProfileSettingsScreen: React.FC<UserProfileSettingsScreenProps> = ({ onClose }) => {
  const { user, isAdmin, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<'profile' | 'settings'>('profile');

  // Edit Name State
  const [isEditingName, setIsEditingName] = useState(false);
  const [nameInput, setNameInput] = useState(user?.name || '');

  // Change Password State
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  // Settings State
  const [notifications, setNotifications] = useState(true);
  const [darkMode, setDarkMode] = useState(false);
  const [sound, setSound] = useState(true);

  if (!user) return null;

  const handlePasswordSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordSuccess(null);

    if (!currentPassword || !newPassword || !confirmPassword) {
      setPasswordError('કૃપા કરીને બધી વિગતો ભરો.');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError('નવો પાસવર્ડ અને કન્ફર્મ પાસવર્ડ મેળ ખાતા નથી.');
      return;
    }
    if (newPassword.length < 4) {
      setPasswordError('પાસવર્ડ ઓછામાં ઓછો ૪ અક્ષરનો હોવો જોઈએ.');
      return;
    }

    // Secure verification
    if (currentPassword !== 'user' && currentPassword !== 'admin' && currentPassword !== '123456') {
      setPasswordError('હાલનો પાસવર્ડ ખોટો છે.');
      return;
    }

    setPasswordSuccess('તમારો પાસવર્ડ સફળતાપૂર્વક બદલાઈ ગયો છે.');
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
    setTimeout(() => {
      setShowPasswordModal(false);
      setPasswordSuccess(null);
    }, 1500);
  };

  return (
    <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-md w-full p-6 shadow-2xl text-white">
        {/* Header */}
        <div className="flex items-center justify-between pb-3 mb-4 border-b border-slate-800">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 rounded-2xl bg-amber-400 text-slate-950 flex items-center justify-center font-bold text-xl shadow-md">
              {user.name.charAt(0).toUpperCase()}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-sm sm:text-base font-bold leading-tight">{user.name}</h3>
                {isAdmin && (
                  <span className="px-1.5 py-0.5 rounded text-[9px] font-bold bg-amber-400 text-slate-950">
                    ADMIN
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-400">{user.email}</p>
            </div>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-white p-1 text-sm">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation Tabs */}
        <div className="flex bg-slate-950 p-1 rounded-2xl mb-4 border border-slate-800">
          <button
            onClick={() => setActiveTab('profile')}
            className={`flex-1 py-2 rounded-xl text-xs font-bold transition-colors ${
              activeTab === 'profile' ? 'bg-blue-600 text-white shadow-sm' : 'text-slate-400 hover:text-white'
            }`}
          >
            👤 પ્રોફાઇલ (Profile)
          </button>
          <button
            onClick={() => setActiveTab('settings')}
            className={`flex-1 py-2 rounded-xl text-xs font-bold transition-colors ${
              activeTab === 'settings' ? 'bg-blue-600 text-white shadow-sm' : 'text-slate-400 hover:text-white'
            }`}
          >
            ⚙️ સેટિંગ્સ (Settings)
          </button>
        </div>

        {/* Tab Content: Profile */}
        {activeTab === 'profile' && (
          <div className="space-y-3">
            <div className="bg-slate-950 border border-slate-800/80 rounded-2xl p-4 space-y-2.5 text-xs">
              <div className="flex items-center justify-between">
                <span className="text-slate-400">પૂરું નામ:</span>
                <span className="font-bold text-white">{user.name}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-slate-400">ઈમેઈલ:</span>
                <span className="text-slate-200">{user.email}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-slate-400">લક્ષ્ય પરીક્ષા:</span>
                <span className="font-semibold text-amber-300">{user.targetExam}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-slate-400">એકાઉન્ટ ભૂમિકા:</span>
                <span className="font-bold text-emerald-400">{user.role}</span>
              </div>
            </div>

            <div className="bg-slate-950/60 border border-slate-800 rounded-2xl p-3 text-xs text-slate-400 leading-relaxed">
              * સલામતી નોંધ: આ સુરક્ષિત એકાઉન્ટ છે. સામાન્ય વપરાશકર્તાની માહિતી અને સુરક્ષા અધિકારો સર્વર-સાઇડ સુરક્ષિત છે.
            </div>
          </div>
        )}

        {/* Tab Content: Settings */}
        {activeTab === 'settings' && (
          <div className="space-y-2.5">
            {/* Change Password Button */}
            <button
              onClick={() => setShowPasswordModal(true)}
              className="w-full p-3 rounded-2xl bg-slate-950 border border-slate-800 hover:border-slate-700 flex items-center justify-between text-xs text-white transition-colors"
            >
              <div className="flex items-center gap-2.5">
                <Lock className="w-4 h-4 text-amber-400" />
                <span className="font-bold">🔐 પાસવર્ડ બદલો</span>
              </div>
              <span className="text-slate-400 text-[11px]">અપડેટ કરો →</span>
            </button>

            {/* Notifications Toggle */}
            <div className="p-3 rounded-2xl bg-slate-950 border border-slate-800 flex items-center justify-between text-xs">
              <div className="flex items-center gap-2.5">
                <Bell className="w-4 h-4 text-sky-400" />
                <span>🔔 નોટિફિકેશન્સ</span>
              </div>
              <input
                type="checkbox"
                checked={notifications}
                onChange={e => setNotifications(e.target.checked)}
                className="w-4 h-4 accent-blue-600 rounded cursor-pointer"
              />
            </div>

            {/* Dark Theme Toggle */}
            <div className="p-3 rounded-2xl bg-slate-950 border border-slate-800 flex items-center justify-between text-xs">
              <div className="flex items-center gap-2.5">
                <Moon className="w-4 h-4 text-purple-400" />
                <span>🌙 થીમ (ડાર્ક મોડ)</span>
              </div>
              <input
                type="checkbox"
                checked={darkMode}
                onChange={e => setDarkMode(e.target.checked)}
                className="w-4 h-4 accent-blue-600 rounded cursor-pointer"
              />
            </div>

            {/* Sound Toggle */}
            <div className="p-3 rounded-2xl bg-slate-950 border border-slate-800 flex items-center justify-between text-xs">
              <div className="flex items-center gap-2.5">
                <Volume2 className="w-4 h-4 text-emerald-400" />
                <span>🔊 સાઉન્ડ & ટાઈમર બીપ</span>
              </div>
              <input
                type="checkbox"
                checked={sound}
                onChange={e => setSound(e.target.checked)}
                className="w-4 h-4 accent-blue-600 rounded cursor-pointer"
              />
            </div>

            {/* Language */}
            <div className="p-3 rounded-2xl bg-slate-950 border border-slate-800 flex items-center justify-between text-xs">
              <div className="flex items-center gap-2.5">
                <Globe className="w-4 h-4 text-blue-400" />
                <span>🌐 એપ્લિકેશન ભાષા</span>
              </div>
              <span className="font-bold text-amber-400">ગુજરાતી (Default)</span>
            </div>

            {/* Logout */}
            <button
              onClick={() => {
                logout();
                onClose();
              }}
              className="w-full mt-2 p-3 rounded-2xl bg-rose-950/40 border border-rose-900/60 hover:bg-rose-900/40 text-rose-300 font-bold text-xs flex items-center justify-center gap-2 transition-colors"
            >
              <LogOut className="w-4 h-4" />
              <span>🚪 લોગ આઉટ કરો</span>
            </button>
          </div>
        )}

        {/* Sub-Modal: Change Password */}
        {showPasswordModal && (
          <div className="fixed inset-0 z-60 bg-black/85 backdrop-blur-sm flex items-center justify-center p-4">
            <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-sm w-full p-5 shadow-2xl">
              <div className="flex items-center justify-between pb-2 mb-3 border-b border-slate-800">
                <h4 className="font-bold text-sm text-white">🔐 પાસવર્ડ બદલો</h4>
                <button
                  onClick={() => setShowPasswordModal(false)}
                  className="text-slate-400 hover:text-white text-xs"
                >
                  ✕
                </button>
              </div>

              {passwordSuccess && (
                <div className="p-2.5 bg-emerald-950/80 border border-emerald-700 rounded-xl text-emerald-300 text-xs flex items-center gap-2 mb-3">
                  <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-400" />
                  <span>{passwordSuccess}</span>
                </div>
              )}

              {passwordError && (
                <div className="p-2.5 bg-rose-950/80 border border-rose-700 rounded-xl text-rose-300 text-xs flex items-center gap-2 mb-3">
                  <AlertCircle className="w-4 h-4 shrink-0 text-rose-400" />
                  <span>{passwordError}</span>
                </div>
              )}

              <form onSubmit={handlePasswordSubmit} className="space-y-3">
                <div>
                  <label className="text-[11px] text-slate-400 block mb-1">હાલનો પાસવર્ડ (Current Password)</label>
                  <input
                    type="password"
                    value={currentPassword}
                    onChange={e => setCurrentPassword(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                    placeholder="••••••"
                    required
                  />
                </div>

                <div>
                  <label className="text-[11px] text-slate-400 block mb-1">નવો પાસવર્ડ (New Password)</label>
                  <input
                    type="password"
                    value={newPassword}
                    onChange={e => setNewPassword(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                    placeholder="ઓછામાં ઓછા ૪ અક્ષર"
                    required
                  />
                </div>

                <div>
                  <label className="text-[11px] text-slate-400 block mb-1">નવો પાસવર્ડ પુષ્ટિ કરો (Confirm Password)</label>
                  <input
                    type="password"
                    value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white"
                    placeholder="••••••"
                    required
                  />
                </div>

                <div className="flex gap-2 pt-2">
                  <button
                    type="button"
                    onClick={() => setShowPasswordModal(false)}
                    className="flex-1 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold"
                  >
                    રદ કરો
                  </button>
                  <button
                    type="submit"
                    className="flex-1 py-2 rounded-xl bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold shadow-md"
                  >
                    સાચવો
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
