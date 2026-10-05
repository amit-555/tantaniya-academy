package com.example.data.database

import com.example.data.dao.StudyProDao
import com.example.data.entity.*

object DatabaseInitialData {
    suspend fun prepopulate(dao: StudyProDao) {
        // 1. Initial Users
        val primaryAdmin = UserEntity(
            id = 1,
            name = "Amit Gangal (Primary Admin)",
            email = "gangalamit005@gmail.com",
            password = "admin",
            role = "ADMIN",
            targetExam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)"
        )
        val adminUser = UserEntity(
            id = 2,
            name = "એડમિન કમિશનર (Admin)",
            email = "admin@studypro.in",
            password = "admin",
            role = "ADMIN",
            targetExam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)"
        )
        val studentUser = UserEntity(
            id = 3,
            name = "વિદ્યાર્થી મિત્ર (Pravin Solanki)",
            email = "student@studypro.in",
            password = "user",
            role = "USER",
            targetExam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)"
        )
        try {
            dao.insertUser(primaryAdmin)
            dao.insertUser(adminUser)
            dao.insertUser(studentUser)
        } catch (_: Exception) {}

        // 2. Dynamic Default Exams (Admin can add unlimited more)
        val exam1 = ExamEntity(
            id = 1,
            title = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
            code = "GPC-LRD",
            description = "ભાગ-અ (80 ગુણ) + ભાગ-બ (120 ગુણ) = કુલ 200 પ્રશ્નો, 200 ગુણ, 3 કલાક, 0.25 નેગેટિવ માર્કિંગ",
            totalMarks = 200,
            durationMinutes = 180,
            negativeMarking = 0.25,
            isPublished = true
        )
        val exam2 = ExamEntity(
            id = 2,
            title = "ફોરેસ્ટ બીટ ગાર્ડ (વનરક્ષક)",
            code = "FBG",
            description = "સામાન્ય જ્ઞાન, ગુજરાત ભૂગોળ, વન અને પર્યાવરણ, વિજ્ઞાન = કુલ 150 પ્રશ્નો, 150 ગુણ, 2.5 કલાક",
            totalMarks = 150,
            durationMinutes = 150,
            negativeMarking = 0.25,
            isPublished = true
        )
        val exam3 = ExamEntity(
            id = 3,
            title = "MPHW (મલ્ટી પર્પઝ હેલ્થ વર્કર)",
            code = "MPHW",
            description = "આરોગ્ય વિજ્ઞાન, પ્રાથમિક સારવાર, રોગ નિયંત્રણ અને સામાન્ય અભ્યાસ = કુલ 100 પ્રશ્નો, 100 ગુણ, 2 કલાક",
            totalMarks = 100,
            durationMinutes = 120,
            negativeMarking = 0.25,
            isPublished = true
        )
        dao.insertExam(exam1)
        dao.insertExam(exam2)
        dao.insertExam(exam3)

        // 3. Dynamic Syllabus Structure
        val syllabusList = listOf(
            // Police Constable (Exam 1)
            SyllabusEntity(examId = 1, partName = "ભાગ-અ (Part A)", subjectName = "રીઝનિંગ (Reasoning)", topicName = "લોજિકલ ક્ષમતા, કોડિંગ-ડિકોડિંગ, દિશા-અંતર, બ્લડ રિલેશન", weightageMarks = 30),
            SyllabusEntity(examId = 1, partName = "ભાગ-અ (Part A)", subjectName = "ગણિત (Mathematics)", topicName = "સંખ્યા પદ્ધતિ, ટકાવારી, નફો-ખોટ, ગુણોત્તર, સરેરાશ, સમય-કાર્ય", weightageMarks = 30),
            SyllabusEntity(examId = 1, partName = "ભાગ-અ (Part A)", subjectName = "ગુજરાતી ભાષા (Gujarati)", topicName = "રૂઢિપ્રયોગો, કહેવતો, સમાનાર્થી-વિરોધી, ગદ્ય સમીક્ષા", weightageMarks = 20),
            SyllabusEntity(examId = 1, partName = "ભાગ-બ (Part B)", subjectName = "ભારતીય બંધારણ (Constitution)", topicName = "આમુખ, મૂળભૂત અધિકારો અને ફરજો, રાષ્ટ્રપતિ, સંસદ, ન્યાયતંત્ર", weightageMarks = 30),
            SyllabusEntity(examId = 1, partName = "ભાગ-બ (Part B)", subjectName = "ગુજરાતનો ઇતિહાસ અને વારસો", topicName = "સિંધુ સંસ્કૃતિ, સોલંકી યુગ, મહાગુજરાત આંદોલન, કળા-સાહિત્ય", weightageMarks = 25),
            SyllabusEntity(examId = 1, partName = "ભાગ-બ (Part B)", subjectName = "ગુજરાત અને ભારતની ભૂગોળ", topicName = "નદીઓ, ડુંગરો, અભયારણ્યો, જમીન અને આબોહવા, ખનીજો", weightageMarks = 25),
            SyllabusEntity(examId = 1, partName = "ભાગ-બ (Part B)", subjectName = "સામાન્ય વિજ્ઞાન અને ટેકનોલોજી", topicName = "માનવ શરીર, રોગો અને વિટામિન્સ, ભૌતિક-રસાયણ વિજ્ઞાન, ISRO", weightageMarks = 20),
            SyllabusEntity(examId = 1, partName = "ભાગ-બ (Part B)", subjectName = "કરંટ અફેર્સ અને જનરલ નોલેજ", topicName = "ગુજરાત અને રાષ્ટ્રીય પ્રવાહો, રમતગમત, પુરસ્કારો અને યોજનાઓ", weightageMarks = 20),

            // Forest Beat Guard (Exam 2)
            SyllabusEntity(examId = 2, partName = "ભાગ-અ (સામાન્ય જ્ઞાન & ગણિત)", subjectName = "જનરલ નોલેજ & કરંટ અફેર્સ", topicName = "ગુજરાત ઇતિહાસ, વર્તમાન પ્રવાહો, પંચાયતી રાજ", weightageMarks = 35),
            SyllabusEntity(examId = 2, partName = "ભાગ-અ (સામાન્ય જ્ઞાન & ગણિત)", subjectName = "સામાન્ય ગણિત & રીઝનિંગ", topicName = "સાદુરૂપ, ટકાવારી, શ્રેણી, દિશા-અંતર", weightageMarks = 25),
            SyllabusEntity(examId = 2, partName = "ભાગ-બ (વન & પર્યાવરણ વિશેષ)", subjectName = "વન & પર્યાવરણ", topicName = "જંગલોના પ્રકાર, વન્યજીવ અભયારણ્યો, વૃક્ષો, રાષ્ટ્રીય ઉદ્યાનો", weightageMarks = 50),
            SyllabusEntity(examId = 2, partName = "ભાગ-બ (વન & પર્યાવરણ વિશેષ)", subjectName = "ગુજરાત ભૂગોળ & જમીન", topicName = "નદી પ્રણાલી, આબોહવા, વનસ્પતિ, ખનીજ સંપત્તિ", weightageMarks = 40),

            // MPHW (Exam 3)
            SyllabusEntity(examId = 3, partName = "ભાગ-અ (હેલ્થ વિજ્ઞાન વિશેષ)", subjectName = "પ્રાથમિક આરોગ્ય & રોગ નિયંત્રણ", topicName = "સંચારી-બિનસંચારી રોગો, રસીકરણ, મેલેરિયા, ડેન્ગ્યુ, TB", weightageMarks = 50),
            SyllabusEntity(examId = 3, partName = "ભાગ-અ (હેલ્થ વિજ્ઞાન વિશેષ)", subjectName = "સ્વચ્છતા & પ્રાથમિક સારવાર", topicName = "પાણી શુદ્ધિકરણ, પોષણ, પ્રાથમિક સારવાર પદ્ધતિઓ", weightageMarks = 20),
            SyllabusEntity(examId = 3, partName = "ભાગ-બ (સામાન્ય અભ્યાસ)", subjectName = "સામાન્ય જ્ઞાન & ગુજરાતી", topicName = "ગુજરાત પરિચય, ગુજરાતી વ્યાકરણ, પાયાનું ગણિત", weightageMarks = 30)
        )
        dao.insertSyllabusList(syllabusList)

        // 4. Complete Dynamic Tests
        val test1 = TestEntity(
            id = 1,
            examId = 1,
            title = "ગુજરાત પોલીસ કોન્સ્ટેબલ ફુલ મોક ટેસ્ટ - ૦૧",
            description = "નવા પરીક્ષા નિયમો મુજબ ભાગ-અ (ગણિત/રીઝનિંગ) + ભાગ-બ (બંધારણ/GK/ઇતિહાસ/વિજ્ઞાન) સંપૂર્ણ ૨૦૦ પ્રશ્નોની પૂર્ણ મોક ટેસ્ટ",
            durationMinutes = 180,
            totalQuestions = 200,
            totalMarks = 200,
            negativeMarking = 0.25,
            passingMarks = 80.0,
            instructions = "૧. કુલ ૨૦૦ પ્રશ્નો છે.\n૨. પ્રત્યેક સાચા જવાબનો +૧ ગુણ રહેશે.\n૩. પ્રત્યેક ખોટા જવાબ દીઠ ૦.૨૫ ગુણ કપાશે.\n૪. સમય સમાપ્ત થતાં ટેસ્ટ ઓટો-સબમિટ થશે.",
            isPublished = true
        )

        val test2 = TestEntity(
            id = 2,
            examId = 2,
            title = "ફોરેસ્ટ બીટ ગાર્ડ (વનરક્ષક) ફુલ મોક ટેસ્ટ - ૦૧",
            description = "વન અને પર્યાવરણ, વન્યજીવ સંરક્ષણ, ગુજરાત ભૂગોળ અને સામાન્ય ગણિતનું પરિપૂર્ણ મોક પેપર",
            durationMinutes = 150,
            totalQuestions = 150,
            totalMarks = 150,
            negativeMarking = 0.25,
            passingMarks = 60.0,
            instructions = "૧. કુલ ૧૫૦ પ્રશ્નો છે.\n૨. નેગેટિવ માર્કિંગ ૦.૨૫ રહેશે.\n૩. પર્યાવરણ અને ભૂગોળના વિશેષ ગુણભાર પર ધ્યાન આપો.",
            isPublished = true
        )

        val test3 = TestEntity(
            id = 3,
            examId = 3,
            title = "MPHW હેલ્થ વર્કર સ્પેશિયલ મોક ટેસ્ટ - ૦૧",
            description = "રોગચાળો નિયંત્રણ, રસીકરણ શિડ્યુલ, પ્રાથમિક આરોગ્ય સંભાળ અને સામાન્ય અભ્યાસ પેપર",
            durationMinutes = 120,
            totalQuestions = 100,
            totalMarks = 100,
            negativeMarking = 0.25,
            passingMarks = 40.0,
            instructions = "૧. કુલ ૧૦૦ પ્રશ્નો છે.\n૨. દરેક સાચા જવાબ પર ૧ ગુણ અને ખોટા જવાબ પર ૦.૨૫ નેગેટિવ રહેશે.",
            isPublished = true
        )

        dao.insertTest(test1)
        dao.insertTest(test2)
        dao.insertTest(test3)

        // 5. Authentic 200 Questions for Police Constable + Forest Guard & MPHW
        val constable200 = Constable200QuestionsGenerator.generateAll200Questions()

        val additionalQuestions = listOf(
            // Forest Beat Guard (Exam 2)
            QuestionEntity(
                id = 201,
                examId = 2,
                part = "ભાગ-બ (વન & પર્યાવરણ વિશેષ)",
                subject = "વન & પર્યાવરણ",
                topic = "વન્યજીવ સંરક્ષણ",
                difficulty = "સરળ",
                questionText = "ભારતમાં 'પ્રોજેક્ટ ટાઈગર' (વાઘ સંરક્ષણ પરિયોજના) કયા વર્ષે શરૂ કરવામાં આવ્યો હતો?",
                optionA = "૧૯૭૨",
                optionB = "૧૯૭૩",
                optionC = "૧૯૮૦",
                optionD = "૧૯૯૨",
                correctOption = "B",
                explanation = "૧ એપ્રિલ ૧૯૭૩ ના રોજ જિમ કોર્બેટ નેશનલ પાર્ક (ઉત્તરાખંડ) થી પ્રોજેક્ટ ટાઈગર શરૂ કરાયો હતો.",
                marks = 1.0,
                negativeMarks = 0.25
            ),
            QuestionEntity(
                id = 202,
                examId = 2,
                part = "ભાગ-બ (વન & પર્યાવરણ વિશેષ)",
                subject = "વન & પર્યાવરણ",
                topic = "જંગલોના પ્રકાર",
                difficulty = "મધ્યમ",
                questionText = "ગુજરાતમાં સૌથી વધુ જંગલ વિસ્તાર (વન આચ્છાદન) કયા જિલ્લામાં આવેલો છે?",
                optionA = "ડાંગ",
                optionB = "કચ્છ",
                optionC = "નર્મદા",
                optionD = "જુનાગઢ",
                correctOption = "A",
                explanation = "ટકાવારીની દ્રષ્ટિએ ડાંગ જિલ્લો ગુજરાતમાં સૌથી વધુ (આશરે ૭૭%) જંગલ વિસ્તાર ધરાવે છે.",
                marks = 1.0,
                negativeMarks = 0.25
            ),
            QuestionEntity(
                id = 203,
                examId = 2,
                part = "ભાગ-બ (વન & પર્યાવરણ વિશેષ)",
                subject = "વન & પર્યાવરણ",
                topic = "પર્યાવરણ દિવસ",
                difficulty = "સરળ",
                questionText = "વિશ્વ વન દિવસ (World Forestry Day) દર વર્ષે કઈ તારીખે ઉજવવામાં આવે છે?",
                optionA = "૨૧ માર્ચ",
                optionB = "૨૨ એપ્રિલ",
                optionC = "૫ જૂન",
                optionD = "૧૬ સપ્ટેમ્બર",
                correctOption = "A",
                explanation = "૨૧ માર્ચ વિશ્વ વન દિવસ, ૨૨ માર્ચ વિશ્વ જળ દિવસ અને ૨૨ એપ્રિલ પૃથ્વી દિવસ તરીકે ઉજવાય છે.",
                marks = 1.0,
                negativeMarks = 0.25
            ),

            // MPHW (Exam 3)
            QuestionEntity(
                id = 204,
                examId = 3,
                part = "ભાગ-અ (હેલ્થ વિજ્ઞાન વિશેષ)",
                subject = "પ્રાથમિક આરોગ્ય & રોગ નિયંત્રણ",
                topic = "વાહકજન્ય રોગો (Vector Borne Diseases)",
                difficulty = "સરળ",
                questionText = "મેલેરિયા રોગ કયા મચ્છરના કરડવાથી ફેલાય છે?",
                optionA = "માદા એનોફિલીસ (Female Anopheles)",
                optionB = "એડીસ ઇજિપ્તી (Aedes Aegypti)",
                optionC = "ક્યુલેક્સ (Culex)",
                optionD = "સેન્ડ ફ્લાય",
                correctOption = "A",
                explanation = "મેલેરિયા પ્લાઝમોડિયમ નામના પરોપજીવીથી થાય છે અને તેનો ફેલાવો માદા એનોફિલીસ મચ્છર દ્વારા થાય છે. એડીસ મચ્છર ડેન્ગ્યુ ફેલાવે છે.",
                marks = 1.0,
                negativeMarks = 0.25
            ),
            QuestionEntity(
                id = 205,
                examId = 3,
                part = "ભાગ-અ (હેલ્થ વિજ્ઞાન વિશેષ)",
                subject = "સ્વચ્છતા & પ્રાથમિક સારવાર",
                topic = "પાણી શુદ્ધિકરણ",
                difficulty = "મધ્યમ",
                questionText = "પીવાના પાણીના શુદ્ધિકરણ માટે અને કુવાના પાણીને જંતુમુક્ત કરવા માટે શાનો ઉપયોગ થાય છે?",
                optionA = "બ્લીચિંગ પાવડર (ક્લોરિન)",
                optionB = "ડીડીટી (DDT)",
                optionC = "ફિનાઇલ",
                optionD = "મીઠું",
                correctOption = "A",
                explanation = "બ્લીચિંગ પાવડરમાં રહેલો મુક્ત ક્લોરિન પાણીમાં રહેલા રોગકારક બેક્ટેરિયાનો નાશ કરીને પાણીને પીવાલાયક શુદ્ધ બનાવે છે.",
                marks = 1.0,
                negativeMarks = 0.25
            )
        )

        dao.insertQuestions(constable200 + additionalQuestions)

        // 6. Map Questions to Tests
        // Test 1: Full 200 Questions Police Constable Mock Test
        for (i in 1..200) {
            dao.insertTestQuestion(TestQuestionEntity(testId = 1, questionId = i, orderIndex = i))
        }

        // Test 2: Forest Beat Guard Mock Test
        dao.insertTestQuestion(TestQuestionEntity(testId = 2, questionId = 201, orderIndex = 1))
        dao.insertTestQuestion(TestQuestionEntity(testId = 2, questionId = 202, orderIndex = 2))
        dao.insertTestQuestion(TestQuestionEntity(testId = 2, questionId = 203, orderIndex = 3))
        for (i in 91..130 step 4) { // add Gujarat Geography questions
            dao.insertTestQuestion(TestQuestionEntity(testId = 2, questionId = i, orderIndex = 4 + (i - 91) / 4))
        }

        // Test 3: MPHW Health Worker Mock Test
        dao.insertTestQuestion(TestQuestionEntity(testId = 3, questionId = 204, orderIndex = 1))
        dao.insertTestQuestion(TestQuestionEntity(testId = 3, questionId = 205, orderIndex = 2))
        for (i in 131..150 step 3) { // add General Science questions
            dao.insertTestQuestion(TestQuestionEntity(testId = 3, questionId = i, orderIndex = 3 + (i - 131) / 3))
        }

        // 7. Initial Current Affairs
        val caList = listOf(
            CurrentAffairsEntity(
                dateText = "૦૫ ઓક્ટોબર ૨૦૨૬",
                headline = "ગુજરાત સરકારે ગીર અને બરડા અભયારણ્યમાં વન્યજીવ કોરિડોર યોજના મંજૂર કરી",
                description = "એશિયાટિક સિંહોના સુરક્ષિત સ્થળાંતર અને સંરક્ષણ માટે વિશેષ ટાસ્ક ફોર્સ અને રેડિયો-કોલરિંગ મોનિટરિંગ પ્રોજેક્ટ શરૂ કરવામાં આવ્યો.",
                category = "ગુજરાત",
                importantFacts = "ગીરથી બરડા વચ્ચે કુદરતી સિંહ કોરિડોર નિર્માણ કરાશે.",
                relatedExam = "ફોરેસ્ટ બીટ ગાર્ડ (વનરક્ષક)",
                relatedSubject = "વન & પર્યાવરણ",
                mcqQuestion = "બરડા વન્યજીવ અભયારણ્ય ગુજરાતના કયા જિલ્લામાં આવેલું છે?",
                mcqOptionA = "પોરબંદર",
                mcqOptionB = "કચ્છ",
                mcqOptionC = "સુરેન્દ્રનગર",
                mcqOptionD = "મહેસાણા",
                mcqCorrect = "A",
                isPublished = true
            ),
            CurrentAffairsEntity(
                dateText = "૦૪ ઓક્ટોબર ૨૦૨૬",
                headline = "આરોગ્ય મંત્રાલય દ્વારા સાર્વત્રિક રસીકરણ મિશન ઇન્દ્રધનુષ ૬.૦ ની જાહેરાત",
                description = "રાજ્યના અંતરિયાળ ગામડાઓમાં ગર્ભવતી મહિલાઓ અને શિશુઓને તમામ જીવલેણ રોગો સામે વિનામૂલ્યે ૧૦૦% રસીકરણ પૂરું પડાશે.",
                category = "યોજનાઓ",
                importantFacts = "મિશન ઇન્દ્રધનુષ રસીકરણ કવરેજ વધારવા માટેની ભારત સરકારની ફ્લેગશિપ યોજના છે.",
                relatedExam = "MPHW (મલ્ટી પર્પઝ હેલ્થ વર્કર)",
                relatedSubject = "પ્રાથમિક આરોગ્ય & રોગ નિયંત્રણ",
                mcqQuestion = "મિશન ઇન્દ્રધનુષ શાની સાથે સંબંધિત છે?",
                mcqOptionA = "સંપૂર્ણ બાળ રસીકરણ",
                mcqOptionB = "સૌર ઊર્જા",
                mcqOptionC = "રેલવે સુરક્ષા",
                mcqOptionD = "બેંકિંગ સુધારો",
                mcqCorrect = "A",
                isPublished = true
            ),
            CurrentAffairsEntity(
                dateText = "૦૩ ઓક્ટોબર ૨૦૨૬",
                headline = "ગુજરાતમાં સ્માર્ટ પોલીસિંગ માટે નવો AI આધારિત સુરક્ષા કંટ્રોલ રૂમ શરૂ",
                description = "રાજ્યના મુખ્ય શહેરોમાં સીસીટીવી નેટવર્ક અને ટ્રાફિક મોનિટરિંગ માટે ત્રિનેત્ર ૨.૦ પ્રોજેક્ટનું લોકાર્પણ થયું.",
                category = "ગુજરાત",
                importantFacts = "ઈ-ગુજકોપ (e-GujCop) પ્રોજેક્ટ હેઠળ તમામ પોલીસ સ્ટેશનો ડિજિટલાઈઝ્ડ છે.",
                relatedExam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
                relatedSubject = "કરંટ અફેર્સ અને જનરલ નોલેજ",
                mcqQuestion = "ગુજરાત પોલીસનું સત્તાવાર સૂત્ર કયું છે?",
                mcqOptionA = "સેવા, સુરક્ષા, શાંતિ",
                mcqOptionB = "સત્યમેવ જયતે",
                mcqOptionC = "યોગક્ષેમં વહામ્યહમ્",
                mcqOptionD = "શૌર્ય અને દ્રઢતા",
                mcqCorrect = "A",
                isPublished = true
            )
        )
        dao.insertCurrentAffairsList(caList)

        // 8. Initial GK Items
        val gkList = listOf(
            GkItemEntity(
                category = "બંધારણ",
                title = "ભારતીય બંધારણના મહત્વપૂર્ણ અનુચ્છેદો",
                content = "અનુચ્છેદ ૧૪: કાયદા સમક્ષ સમાનતા\nઅનુચ્છેદ ૧૭: અસ્પૃશ્યતા નાબૂદી\nઅનુચ્છેદ ૨૧: જીવન અને વ્યક્તિગત સ્વતંત્રતાનો અધિકાર\nઅનુચ્છેદ ૪૦: ગ્રામ પંચાયતોની રચના\nઅનુચ્છેદ ૩૨૪: ચૂંટણી પંચની જોગવાઈ",
                oneLinerFact = "ભારતનું બંધારણ વિશ્વનું સૌથી મોટું લિખિત બંધારણ છે.",
                question = "ભારતમાં કાયદા સમક્ષ સમાનતા કયા અનુચ્છેદમાં છે?",
                answer = "અનુચ્છેદ ૧૪",
                explanation = "અનુચ્છેદ ૧૪ રાજ્યમાં તમામ વ્યક્તિઓને કાયદા સમક્ષ સમાનતા અને કાયદાનું સમાન રક્ષણ આપે છે.",
                exam = "ગુજરાત પોલીસ કોન્સ્ટેબલ (LRD)",
                isPublished = true
            ),
            GkItemEntity(
                category = "ગુજરાત ભૂગોળ",
                title = "ગુજરાતના જંગલો અને અભયારણ્યો",
                content = "ગુજરાતમાં કુલ ૪ રાષ્ટ્રીય ઉદ્યાનો અને ૨૩ અભયારણ્યો આવેલા છે.\n૧. ગીર રાષ્ટ્રીય ઉદ્યાન (એશિયાટિક સિંહ)\n૨. વેળાવદર બ્લેકબક નેશનલ પાર્ક (કાળિયાર)\n૩. વાંસદા નેશનલ પાર્ક (ચિત્તો, દીપડો)\n૪. મરીન નેશનલ પાર્ક (જામનગર - પરવાળા)",
                oneLinerFact = "ગુજરાતમાં દેશનો પ્રથમ મરીન નેશનલ પાર્ક જામનગર ખાતે આવેલો છે.",
                question = "વેળાવદર રાષ્ટ્રીય ઉદ્યાન કયા પ્રાણી માટે જાણીતો છે?",
                answer = "કાળિયાર (Blackbuck)",
                explanation = "ભાવનગર જિલ્લાના વલ્લભીપુર પાસે આવેલો વેળાવદર નેશનલ પાર્ક કાળિયાર અને ખડમોર પક્ષી માટે વિશ્વવિખ્યાત છે.",
                exam = "ફોરેસ્ટ બીટ ગાર્ડ (વનરક્ષક)",
                isPublished = true
            ),
            GkItemEntity(
                category = "વિજ્ઞાન",
                title = "માનવ રોગો અને રસીકરણ માળખું",
                content = "વાયરસથી થતા રોગો: પોલિયો, હડકવા, ડેન્ગ્યુ, એઇડ્સ, શીતળા\nબેક્ટેરિયાથી થતા રોગો: ટીબી, કોલેરા, ટાઇફોઇડ, ડિપ્થેરિયા\nપ્રોટોઝોઆથી થતો રોગ: મેલેરિયા\nપેન્ટાવેલેન્ટ રસી: ડીપ્થેરિયા, પર્ટ્યુસીસ, ટિટનેસ, હિપેટાઇટિસ-બી અને Hib",
                oneLinerFact = "પેન્ટાવેલેન્ટ રસી એક સાથે ૫ જીવલેણ રોગો સામે રક્ષણ પૂરું પાડે છે.",
                question = "પેન્ટાવેલેન્ટ રસી કેટલા રોગો સામે રક્ષણ આપે છે?",
                answer = "૫ રોગો",
                explanation = "તે ગળાનો સોજો (ડિપ્થેરિયા), કાળી ખાંસી, ધનુર્વા, કમળો અને હિમોફિલસ ઇન્ફ્લુએન્ઝા ટાઇપ-બી સામે રક્ષણ આપે છે.",
                exam = "MPHW (મલ્ટી પર્પઝ હેલ્થ વર્કર)",
                isPublished = true
            )
        )
        dao.insertGkItemsList(gkList)

        // 9. Initial Study Materials
        val studyList = listOf(
            StudyMaterialEntity(
                examId = 1,
                subject = "ભારતીય બંધારણ",
                topic = "આમુખ અને મહત્વની જોગવાઈઓ",
                title = "બંધારણ સારાંશ નોટ્સ (Quick Revision)",
                description = "પોલીસ કોન્સ્ટેબલ પરીક્ષા લક્ષી રિવિઝન માટે તૈયાર કરાયેલી વનલાઇનર નોટ્સ",
                type = "નોટ્સ",
                contentText = "૧. બંધારણ સભાની પ્રથમ બેઠક ૯ ડિસેમ્બર ૧૯૪૬ ના રોજ મળી હતી.\n૨. પ્રારૂપ (ખરડા) સમિતિના અધ્યક્ષ ડૉ. બી. આર. આંબેડકર હતા.\n૩. બંધારણ બનતા ૨ વર્ષ, ૧૧ મહિના અને ૧૮ દિવસનો સમય લાગ્યો હતો.\n૪. ૨૬ નવેમ્બર ૧૯૪૯ ના રોજ બંધારણ સ્વીકારાયું અને ૨૬ જાન્યુઆરી ૧૯૫૦ થી અમલમાં આવ્યું.\n૫. ભારતીય બંધારણમાં મૂળ ૨૨ ભાગો, ૩૯૫ અનુચ્છેદો અને ૮ પરિશિષ્ટો હતા.",
                isPublished = true
            ),
            StudyMaterialEntity(
                examId = 2,
                subject = "વન & પર્યાવરણ",
                topic = "વન્યજીવ સંરક્ષણ કાયદો",
                title = "વન્યજીવ સંરક્ષણ અધિનિયમ, ૧૯૭૨ (Key Points)",
                description = "ફોરેસ્ટ ગાર્ડ પરીક્ષા માટે અત્યંત ઉપયોગી કાયદાકીય વન-લાઇનર્સ",
                type = "નોટ્સ",
                contentText = "૧. વન્યજીવ સંરક્ષણ અધિનિયમ ૧૯૭૨ માં સંસદ દ્વારા પસાર કરાયો.\n૨. તેમાં અનુસૂચિ-૧ માં દર્શાવેલ વન્યજીવોના શિકાર પર કડક સજાની જોગવાઈ છે.\n૩. પ્રોજેક્ટ ટાઈગર ૧૯૭૩ માં અને પ્રોજેક્ટ એલિફન્ટ ૧૯૯૨ માં શરૂ કરાયો.\n૪. જંગલો બંધારણની સમવર્તી યાદી (Concurrent List) નો વિષય છે.",
                isPublished = true
            )
        )
        dao.insertStudyMaterialsList(studyList)

        // 10. Default Settings
        dao.setSetting(AppSettingEntity("leaderboard_enabled", "true"))
        dao.setSetting(AppSettingEntity("default_exam_id", "1"))
    }
}
