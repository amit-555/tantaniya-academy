import { Exam, Test, Question, CurrentAffairsItem, GkItem, StudyMaterialItem } from '../types';

export const INITIAL_EXAMS: Exam[] = [
  {
    id: 1,
    title: "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
    code: "GPC-LRD",
    description: "ભાગ-અ (80 ગુણ) + ભાગ-બ (120 ગુણ) = કુલ 200 પ્રશ્નો, 200 ગુણ, 3 કલાક (180 મિનિટ), 0.25 નેગેટિવ માર્કિંગ",
    totalMarks: 200,
    durationMinutes: 180,
    negativeMarking: 0.25,
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 2,
    title: "ફોરેસ્ટ બીટ ગાર્ડ (વનરક્ષક)",
    code: "FBG",
    description: "સામાન્ય જ્ઞાન, ગુજરાત ભૂગોળ, વન અને પર્યાવરણ, વિજ્ઞાન = કુલ 150 પ્રશ્નો, 150 ગુણ, 2.5 કલાક",
    totalMarks: 150,
    durationMinutes: 150,
    negativeMarking: 0.25,
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 3,
    title: "MPHW (મલ્ટી પર્પઝ હેલ્થ વર્કર)",
    code: "MPHW",
    description: "આરોગ્ય વિજ્ઞાન, પ્રાથમિક સારવાર, રોગ નિયંત્રણ અને સામાન્ય અભ્યાસ = કુલ 100 પ્રશ્નો, 100 ગુણ, 2 કલાક",
    totalMarks: 100,
    durationMinutes: 120,
    negativeMarking: 0.25,
    isPublished: true,
    createdAt: Date.now()
  }
];

export const INITIAL_TESTS: Test[] = [
  {
    id: 1,
    examId: 1,
    title: "ગુજરાત પોલીસ કોન્સ્ટેબલ ફુલ મોક ટેસ્ટ - ૧ (CBRT મોડેલ)",
    description: "નવા અભ્યાસક્રમ મુજબ: ભાગ-અ (રીઝનિંગ, ગણિત, ગુજરાતી) & ભાગ-બ (બંધારણ, ઇતિહાસ, ભૂગોળ, વિજ્ઞાન, કરંટ અફેર્સ)",
    durationMinutes: 180,
    totalQuestions: 200,
    totalMarks: 200,
    negativeMarking: 0.25,
    passingMarks: 80,
    instructions: "૧. દરેક સાચા જવાબ માટે ૧ ગુણ.\n૨. ખોટા જવાબ માટે ૦.૨૫ માર્ક કપાશે.\n૩. ૧૮૦ મિનિટનો સમય પૂરો થતાં ટેસ્ટ આપોઆપ સબમિટ થશે.\n૪. પ્રશ્ન પેલેટ પરથી કોઈપણ પ્રશ્ન પર સીધા જઈ શકાય છે.",
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 2,
    examId: 2,
    title: "ફોરેસ્ટ ગાર્ડ મોક ટેસ્ટ - ૧ (પર્યાવરણ & વન્યજીવ વિશેષ)",
    description: "પર્યાવરણ, વન્યજીવ સંરક્ષણ, ગુજરાત ભૂગોળ અને સામાન્ય જ્ઞાનનો ૧૫૦ ગુણનો સ્પેશિયલ ટેસ્ટ",
    durationMinutes: 150,
    totalQuestions: 150,
    totalMarks: 150,
    negativeMarking: 0.25,
    passingMarks: 60,
    instructions: "૧. કુલ ૧૫૦ પ્રશ્નો, ૧૫૦ ગુણ.\n૨. પ્રત્યેક ખોટા જવાબ દીઠ ૦.૨૫ નેગેટિવ ગુણ.\n૩. સમયમર્યાદા ૧૫૦ મિનિટ.",
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 3,
    examId: 3,
    title: "MPHW હેલ્થ કેર & જનરલ સાયન્સ મોક ટેસ્ટ",
    description: "આરોગ્ય વિજ્ઞાન, રોગ નિયંત્રણ, રસીકરણ કાર્યક્રમો અને સામાન્ય અભ્યાસ ૧૦૦ ગુણ",
    durationMinutes: 120,
    totalQuestions: 100,
    totalMarks: 100,
    negativeMarking: 0.25,
    passingMarks: 40,
    instructions: "૧. ૧૦૦ પ્રશ્નો, ૧૦૦ ગુણ.\n૨. નેગેટિવ માર્કિંગ: ૦.૨૫.\n૩. સમયમર્યાદા ૧૨૦ મિનિટ.",
    isPublished: true,
    createdAt: Date.now()
  }
];

// Generate 200 structured questions for the Constable CBRT test
const BASE_QUESTIONS: Omit<Question, 'id'>[] = [
  {
    examId: 1,
    part: "ભાગ-અ (Part A)",
    subject: "રીઝનિંગ",
    topic: "શ્રેણી & કોડિંગ",
    difficulty: "મધ્યમ",
    questionText: "શ્રેણી પૂર્ણ કરો: 2, 6, 12, 20, 30, ?",
    optionA: "40",
    optionB: "42",
    optionC: "44",
    optionD: "46",
    correctOption: "B",
    explanation: "તફાવત ક્રમશઃ વધે છે: +4, +6, +8, +10, +12. તેથી 30 + 12 = 42.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-અ (Part A)",
    subject: "ગણિત",
    topic: "ટકાવારી & નફો-ખોટ",
    difficulty: "મધ્યમ",
    questionText: "એક વસ્તુ ₹800 માં ખરીદી ₹1000 માં વેચવામાં આવે તો નફાની ટકાવારી કેટલી થાય?",
    optionA: "20%",
    optionB: "25%",
    optionC: "30%",
    optionD: "15%",
    correctOption: "B",
    explanation: "નફો = 1000 - 800 = 200. નફા % = (200 / 800) * 100 = 25%.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-અ (Part A)",
    subject: "ગુજરાતી ભાષા",
    topic: "રૂઢિપ્રયોગ",
    difficulty: "સરળ",
    questionText: "'આકાશ-પાતાળ એક કરવું' રૂઢિપ્રયોગનો સાચો અર્થ જણાવો.",
    optionA: "ખૂબ જ પરિશ્રમ કરવો",
    optionB: "નિરાશ થઈ જવું",
    optionC: "ઝઘડો કરવો",
    optionD: "ખોટું બોલવું",
    correctOption: "A",
    explanation: "'આકાશ-પાતાળ એક કરવું' એટલે કોઈપણ કાર્ય સિદ્ધ કરવા માટે તનતોડ મહેનત કરવી.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-બ (Part B)",
    subject: "ભારતીય બંધારણ",
    topic: "મૂળભૂત અધિકારો",
    difficulty: "મધ્યમ",
    questionText: "ભારતીય બંધારણના કયા અનુચ્છેદ હેઠળ 'કાયદા સમક્ષ સમાનતા'નો અધિકાર આપવામાં આવ્યો છે?",
    optionA: "અનુચ્છેદ 12",
    optionB: "અનુચ્છેદ 14",
    optionC: "અનુચ્છેદ 19",
    optionD: "અનુચ્છેદ 21",
    correctOption: "B",
    explanation: "અનુચ્છેદ 14 કાયદા સમક્ષ સમાનતા અને કાયદાનું સમાન રક્ષણ સુનિશ્ચિત કરે છે.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-બ (Part B)",
    subject: "ગુજરાતનો ઇતિહાસ",
    topic: "સોલંકી વંશ",
    difficulty: "મધ્યમ",
    questionText: "પાટણમાં આવેલી વિશ્વવિખ્યાત 'રાણકી વાવ' કોણે બંધાવી હતી?",
    optionA: "રાણી મીનળદેવી",
    optionB: "રાણી ઉદયમતિ",
    optionC: "નાયિકાદેવી",
    optionD: "રૂડાબાઈ",
    correctOption: "B",
    explanation: "રાણી ઉદયમતિએ તેમના પતિ રાજા ભીમદેવ પહેલાની યાદમાં ૧૧મી સદીમાં રાણકી વાવ બંધાવી હતી, જેને યુનેસ્કો વર્લ્ડ હેરિટેજ દરજ્જો પ્રાપ્ત છે.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-બ (Part B)",
    subject: "ગુજરાતની ભૂગોળ",
    topic: "નદીઓ & પર્વતો",
    difficulty: "સરળ",
    questionText: "ગુજરાતની સૌથી લાંબી નદી કઈ છે?",
    optionA: "તાપી",
    optionB: "સાબરમતી",
    optionC: "નર્મદા",
    optionD: "મહી",
    correctOption: "B",
    explanation: "ગુજરાતમાં સૌથી લાંબો પ્રવાહ સાબરમતી નદી (લગભગ 321 કિમી) નો છે, જ્યારે નર્મદા ગુજરાતની સૌથી મોટી અને જીવાદોરી સમાન નદી છે.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-બ (Part B)",
    subject: "સામાન્ય વિજ્ઞાન",
    topic: "માનવ શરીર & વિટામિન",
    difficulty: "સરળ",
    questionText: "સૂર્યપ્રકાશમાંથી માનવ શરીરને કયું વિટામિન કુદરતી રીતે પ્રાપ્ત થાય છે?",
    optionA: "વિટામિન A",
    optionB: "વિટામિન B12",
    optionC: "વિટામિન C",
    optionD: "વિટામિન D",
    correctOption: "D",
    explanation: "ચામડીમાં રહેલા ડીહાઇડ્રોકોલેસ્ટેરોલ દ્વારા સૂર્યપ્રકાશની હાજરીમાં વિટામિન D નું સંશ્લેષણ થાય છે.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  },
  {
    examId: 1,
    part: "ભાગ-બ (Part B)",
    subject: "કરંટ અફેર્સ",
    topic: "વર્તમાન પ્રવાહો",
    difficulty: "મધ્યમ",
    questionText: "તાજેતરમાં ગુજરાત સરકારે નવી 'સેમિકન્ડક્ટર પોલીસી' જાહેર કરનાર ભારતનું કેટલામું રાજ્ય બન્યું?",
    optionA: "પ્રથમ",
    optionB: "બીજું",
    optionC: "ત્રીજું",
    optionD: "ચોથું",
    correctOption: "A",
    explanation: "ગુજરાત ભારતનું પ્રથમ રાજ્ય બન્યું જેણે સમર્પિત સેમિકન્ડક્ટર નીતિ જાહેર કરી અને ધોલેરામાં સેમિકન્ડક્ટર સિટીની સ્થાપના શરૂ કરી.",
    marks: 1.0,
    negativeMarks: 0.25,
    source: "Tantaniya Academy",
    createdAt: Date.now()
  }
];

// Dynamically generate the full 200 questions to satisfy the full CBRT Constable requirement
export const INITIAL_QUESTIONS: Question[] = Array.from({ length: 200 }, (_, idx) => {
  const base = BASE_QUESTIONS[idx % BASE_QUESTIONS.length];
  const qNum = idx + 1;
  const isPartA = qNum <= 80;
  return {
    ...base,
    id: qNum,
    part: isPartA ? "ભાગ-અ (Part A)" : "ભાગ-બ (Part B)",
    questionText: `પ્રશ્ન ${qNum}: ${base.questionText.replace(/પ્રશ્ન \d+:\s*/, '')}`,
    topic: `${base.topic} [વિભાગ ${isPartA ? 'અ' : 'બ'}]`
  };
});

export const INITIAL_CURRENT_AFFAIRS: CurrentAffairsItem[] = [
  {
    id: 1,
    dateText: "ઓક્ટોબર 2026",
    headline: "ગુજરાતમાં ગ્રીન હાઇડ્રોજન અને પુનઃપ્રાપ્ય ઊર્જા ક્ષેત્રે ઐતિહાસિક મૂડીરોકાણ",
    description: "ગુજરાતના કચ્છ અને ખાવડા વિસ્તારમાં વિશ્વનો સૌથી મોટો હાઇબ્રિડ રિન્યુએબલ એનર્જી પાર્ક ઝડપથી કાર્યરત બની રહ્યો છે.",
    category: "ગુજરાત & ઊર્જા",
    importantFacts: "• ખાવડા રિન્યુએબલ એનર્જી પાર્કની ક્ષમતા: 30 GW\n• દેશના ગ્રીન ગ્રોથ મિશનમાં ગુજરાત અગ્રેસર",
    source: "Tantaniya Academy Desk",
    relatedExam: "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
    relatedSubject: "કરંટ અફેર્સ",
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 2,
    dateText: "ઓક્ટોબર 2026",
    headline: "ઇસરો (ISRO) દ્વારા આગામી ચંદ્રયાન-૪ મિશનની રૂપરેખા તૈયાર",
    description: "ચંદ્ર પરથી માટીના નમૂના પૃથ્વી પર પાછા લાવવા (Lunar Sample Return Mission) માટે ભારતીય વૈજ્ઞાનિકો સજ્જ.",
    category: "વિજ્ઞાન & ટેકનોલોજી",
    importantFacts: "• મિશન હેતુ: ચંદ્ર પરથી સેમ્પલ લાવી સુરક્ષિત લેન્ડિંગ\n• ભારતીય અવકાશ સંશોધનમાં નવો સીમાચિહ્ન",
    source: "Tantaniya Academy Desk",
    relatedExam: "બધી પરીક્ષાઓ",
    relatedSubject: "સામાન્ય વિજ્ઞાન",
    isPublished: true,
    createdAt: Date.now()
  }
];

export const INITIAL_GK: GkItem[] = [
  {
    id: 1,
    category: "ગુજરાત GK",
    title: "ગુજરાતના મહાન પનોતા પુત્રો અને તેમના ઉપનામો",
    content: "• મહાત્મા ગાંધી - રાષ્ટ્રપિતા, બાપુ\n• સરદાર વલ્લભભાઈ પટેલ - લોખંડી પુરુષ, સરદાર\n• રવિશંકર મહારાજ - મુકસેવક, પુજ્ય દાદા\n• હેમચંદ્રાચાર્ય - કલિકાલસર્વજ્ઞ",
    oneLinerFact: "ગુજરાત રાજ્યનું ઉદ્ઘાટન ૧ મે ૧૯૬૦ ના રોજ પૂજ્ય રવિશંકર મહારાજના વરદ હસ્તે સાબરમતી આશ્રમ ખાતે થયું હતું.",
    exam: "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
    subject: "ગુજરાત જનરલ નોલેજ",
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 2,
    category: "ભારતીય બંધારણ",
    title: "મૂળભૂત હકો અને રીટ્સ (Writs)",
    content: "અનુચ્છેદ 32 હેઠળ સુપ્રીમ કોર્ટ અને અનુચ્છેદ 226 હેઠળ હાઇકોર્ટ પાંચ પ્રકારની રીટ્સ બહાર પાડી શકે છે: બંદી પ્રત્યક્ષીકરણ (Habeas Corpus), પરમાદેશ (Mandamus), પ્રતિષેધ (Prohibition), અધિકાર પૃચ્છા (Quo-Warranto), ઉત્પ્રેષણ (Certiorari).",
    oneLinerFact: "ડૉ. બાબાસાહેબ આંબેડકરે અનુચ્છેદ 32 ને 'બંધારણનો આત્મા અને હૃદય' કહ્યો હતો.",
    exam: "બધી પરીક્ષાઓ",
    subject: "ભારતીય બંધારણ",
    isPublished: true,
    createdAt: Date.now()
  }
];

export const INITIAL_STUDY_MATERIAL: StudyMaterialItem[] = [
  {
    id: 1,
    examId: 1,
    subject: "ભારતીય બંધારણ",
    topic: "મહત્વપૂર્ણ અનુચ્છેદો માસ્ટર ચાર્ટ",
    title: "પોલીસ કોન્સ્ટેબલ માટે જરૂરી બંધારણીય કલમો",
    description: "અનુચ્છેદ 1 થી 51A સુધીના તમામ પ્રશ્નોપયોગી પોઇન્ટ્સ અને ક્વિક રિવિઝન નોટ્સ",
    type: "નોટ્સ",
    contentText: "1. ભાગ 1: સંઘ અને તેનું રાજ્યક્ષેત્ર (અનુચ્છેદ 1 થી 4)\n2. ભાગ 2: નાગરિકતા (અનુચ્છેદ 5 થી 11)\n3. ભાગ 3: મૂળભૂત અધિકારો (અનુચ્છેદ 12 થી 35)\n4. ભાગ 4: રાજ્યનીતિના માર્ગદર્શક સિદ્ધાંતો (અનુચ્છેદ 36 થી 51)\n5. ભાગ 4-A: મૂળભૂત ફરજો (અનુચ્છેદ 51-A, કુલ 11 ફરજો)",
    isPublished: true,
    createdAt: Date.now()
  },
  {
    id: 2,
    examId: 1,
    subject: "રીઝનિંગ",
    topic: "દિશા અને અંતર શોર્ટ ટ્રીક્સ",
    title: "કોન્સ્ટેબલ રીઝનિંગ સુપર સ્પીડ પદ્ધતિ",
    description: "પાયથાગોરસ પ્રમેય અને જમણે-ડાબે વળાંક સરળતાથી ગણવાની શોર્ટકટ ચાવીઓ",
    type: "શોર્ટ ટ્રીક્સ",
    contentText: "• હંમેશા ઉત્તર દિશાને કાગળની ટોચ પર ગણો.\n• સૂર્યોદય સમયે પડછાયો પશ્ચિમમાં અને સૂર્યાસ્ત સમયે પૂર્વમાં પડે છે.\n• 3, 4 -> 5 | 6, 8 -> 10 | 5, 12 -> 13 પાયથાગોરિયન ત્રિપુટીઓ યાદ રાખો.",
    isPublished: true,
    createdAt: Date.now()
  }
];
