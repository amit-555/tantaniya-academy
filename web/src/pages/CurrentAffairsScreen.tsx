import React from 'react';
import { CurrentAffairsItem } from '../types';
import { Compass, Calendar, BookOpen, Share2 } from 'lucide-react';

interface CurrentAffairsScreenProps {
  items: CurrentAffairsItem[];
}

export const CurrentAffairsScreen: React.FC<CurrentAffairsScreenProps> = ({ items }) => {
  return (
    <div className="max-w-4xl mx-auto px-4 py-4 pb-24 space-y-4">
      <div className="flex items-center justify-between pb-3 border-b border-slate-200">
        <div>
          <h2 className="text-base sm:text-lg font-bold text-slate-900 flex items-center gap-2">
            <Compass className="w-5 h-5 text-blue-600" />
            <span>દૈનિક કરંટ અફેર્સ (વર્તમાન પ્રવાહો)</span>
          </h2>
          <p className="text-xs text-slate-500">
            ગુજરાત પોલીસ કોન્સ્ટેબલ, ફોરેસ્ટ ગાર્ડ અને સ્પર્ધાત્મક પરીક્ષાઓ માટે ઉપયોગી
          </p>
        </div>
      </div>

      <div className="space-y-4">
        {items.map(item => (
          <div
            key={item.id}
            className="bg-white border border-slate-200/90 rounded-2xl p-4 sm:p-5 shadow-sm hover:shadow-md transition-shadow"
          >
            <div className="flex items-center gap-2 mb-2">
              <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-blue-100 text-blue-800">
                {item.category}
              </span>
              <span className="text-[11px] text-slate-400 flex items-center gap-1">
                <Calendar className="w-3 h-3" />
                {item.dateText}
              </span>
            </div>

            <h3 className="text-sm sm:text-base font-bold text-slate-900 mb-2 leading-snug">
              {item.headline}
            </h3>

            <p className="text-xs sm:text-sm text-slate-700 leading-relaxed mb-3">
              {item.description}
            </p>

            {item.importantFacts && (
              <div className="bg-slate-50 border border-slate-100 rounded-xl p-3 text-xs text-slate-800 space-y-1">
                <div className="font-bold text-slate-900 text-[11px]">મહત્વપૂર્ણ મુદ્દા:</div>
                <div className="whitespace-pre-line text-slate-600">{item.importantFacts}</div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};
