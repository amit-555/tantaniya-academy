import React, { useState } from 'react';
import { StudyMaterialItem, GkItem } from '../types';
import { BookOpen, Award, FileText, Zap } from 'lucide-react';

interface StudyMaterialScreenProps {
  materials: StudyMaterialItem[];
  gkItems: GkItem[];
}

export const StudyMaterialScreen: React.FC<StudyMaterialScreenProps> = ({ materials, gkItems }) => {
  const [tab, setTab] = useState<'materials' | 'gk'>('materials');

  return (
    <div className="max-w-4xl mx-auto px-4 py-4 pb-24 space-y-4">
      {/* Header and Filter */}
      <div className="flex items-center justify-between pb-3 border-b border-slate-200">
        <div>
          <h2 className="text-base sm:text-lg font-bold text-slate-900 flex items-center gap-2">
            <BookOpen className="w-5 h-5 text-amber-600" />
            <span>સ્ટડી મટીરીયલ & જી.કે.</span>
          </h2>
          <p className="text-xs text-slate-500">ક્વિક રિવિઝન નોટ્સ, શોર્ટ ટ્રીક્સ અને સામાન્ય જ્ઞાન</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-2">
        <button
          onClick={() => setTab('materials')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-colors ${
            tab === 'materials' ? 'bg-amber-500 text-slate-950 shadow-sm' : 'bg-white text-slate-600 border border-slate-200'
          }`}
        >
          રિવિઝન નોટ્સ & ટ્રીક્સ ({materials.length})
        </button>
        <button
          onClick={() => setTab('gk')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-colors ${
            tab === 'gk' ? 'bg-amber-500 text-slate-950 shadow-sm' : 'bg-white text-slate-600 border border-slate-200'
          }`}
        >
          ગુજરાત GK વનલાઇનર ({gkItems.length})
        </button>
      </div>

      {/* Material List */}
      {tab === 'materials' && (
        <div className="space-y-4">
          {materials.map(m => (
            <div key={m.id} className="bg-white border border-slate-200/90 rounded-2xl p-4 sm:p-5 shadow-sm">
              <div className="flex items-center gap-2 mb-2">
                <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-amber-100 text-amber-800">
                  {m.type}
                </span>
                <span className="text-[11px] text-slate-500">{m.subject} • {m.topic}</span>
              </div>

              <h3 className="text-sm sm:text-base font-bold text-slate-900 mb-1">{m.title}</h3>
              <p className="text-xs text-slate-600 mb-3">{m.description}</p>

              <div className="bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs text-slate-800 whitespace-pre-line leading-relaxed font-mono">
                {m.contentText}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* GK List */}
      {tab === 'gk' && (
        <div className="space-y-4">
          {gkItems.map(g => (
            <div key={g.id} className="bg-white border border-slate-200/90 rounded-2xl p-4 sm:p-5 shadow-sm">
              <div className="flex items-center gap-2 mb-2">
                <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-blue-100 text-blue-800">
                  {g.category}
                </span>
                <span className="text-[11px] text-slate-500">{g.subject}</span>
              </div>

              <h3 className="text-sm sm:text-base font-bold text-slate-900 mb-2">{g.title}</h3>
              <div className="text-xs text-slate-700 whitespace-pre-line leading-relaxed mb-3">
                {g.content}
              </div>

              <div className="bg-amber-50 border border-amber-200/60 rounded-xl p-3 text-xs text-amber-900">
                <div className="font-bold flex items-center gap-1.5 mb-1 text-amber-950">
                  <Zap className="w-3.5 h-3.5 text-amber-600 fill-amber-600" />
                  <span>ગોલ્ડન ફેક્ટ:</span>
                </div>
                <div>{g.oneLinerFact}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
