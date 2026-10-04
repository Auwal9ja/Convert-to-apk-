package com.example.data.local

object SleepingAndWakingData {

    data class QuranAyah(
        val number: Int,
        val arabic: String,
        val transliteration: String,
        val translationHausa: String,
        val translationEnglish: String
    )

    val surahAlMulkAyahs = listOf(
        QuranAyah(
            number = 1,
            arabic = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Tabarakalladhi biyadihil-mulku wa Huwa 'ala kulli shay'in Qadir.",
            translationHausa = "Mai albarka ne Wanda dukkan mulki yake a hannunSa, kuma Shi a kan kowane abu Mai ikon yi ne.",
            translationEnglish = "Blessed is He in whose hand is dominion, and He is over all things competent."
        ),
        QuranAyah(
            number = 2,
            arabic = "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ",
            transliteration = "Alladhi khalaqal-mawta wal-hayata liyabluwakum ayyukum ahsanu 'amala, wa Huwal-'Azizul-Ghafur.",
            translationHausa = "Wanda Ya halicci mutuwa da rayuwa domin Ya jarraba ku wannenku ne mafi kyawun aiki, kuma Shi ne Mabuwayi, Mai yawan gafara.",
            translationEnglish = "He who created death and life to test you as to which of you is best in deed - and He is the Exalted in Might, the Forgiving."
        ),
        QuranAyah(
            number = 3,
            arabic = "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ",
            transliteration = "Alladhi khalaqa sab'a samawatin tibaqan ma tara fi khalqir-Rahmani min tafawut, farji'il-basara hal tara min futur.",
            translationHausa = "Wanda Ya halicci sammai bakwai hawa-hawa, ba za ka ga wani sabani ko aibi ba a cikin halittar Mai Rahama. Ka sake mayar da gani, ko za ka ga wata fashewa?",
            translationEnglish = "He who created seven heavens in layers. You see no inconsistency in the creation of the Most Merciful. So return your vision to the sky, do you see any breaks?"
        ),
        QuranAyah(
            number = 4,
            arabic = "ثُمَّ ارْجِعِ الْبَصَرَ كَرَّتَيْنِ يَنقَلِبْ إِلَيْكَ الْبَصَرُ خَاسِئًا وَهُوَ حَسِيرٌ",
            transliteration = "Thummar-ji'il-basara karratayni yanqalib ilaykal-basaru khasi'an wa huwa hasir.",
            translationHausa = "Sa'an nan ka sake mayar da gani sau biyu, ganin zai dawo gare ka yana mai wulakanta kuma yana mai matuƙar gajiya ba tare da ganin wani aibi ba.",
            translationEnglish = "Then return your vision twice again. Your vision will return to you humbled while it is fatigued."
        ),
        QuranAyah(
            number = 5,
            arabic = "وَلَقَدْ زَيَّنَّا السَّمَاءَ الدُّنْيَا بِمَصَابِيحَ وَجَعَلْنَاهَا رُجُومًا لِّلشَّيَاطِينِ ۖ وَأَعْتَدْنَا لَهُمْ عَذَابَ السَّعِيرِ",
            transliteration = "Wa laqad zayyannas-sama'ad-dunya bimasabiha wa ja'alnaha rujuman lish-shayatini wa a'tadna lahum 'adhabas-sa'ir.",
            translationHausa = "Kuma lallai Mun ƙawata samaniyar duniya da fitilu (taurari), kuma Muka sanya su abin jifa ga shaidanu, kuma Muka tanadar musu azabar wuta mai ƙuna.",
            translationEnglish = "And We have certainly beautified the nearest heaven with stars and have made from them what is thrown at the devils and have prepared for them the punishment of the Blaze."
        ),
        QuranAyah(
            number = 6,
            arabic = "وَلِلَّذِينَ كَفَرُوا بِرَبِّهِمْ عَذَابُ جَهَنَّمَ ۖ وَبِئْسَ الْمَصِيرُ",
            transliteration = "Wa lilladhina kafaru bi-Rabbihim 'adhabu jahannama wa bi'sal-masir.",
            translationHausa = "Kuma waɗanda suka kafirce wa Ubangijinsu suna da azabar Jahannama, kuma makoma ta munana.",
            translationEnglish = "And for those who disbelieved in their Lord is the punishment of Hell, and wretched is the destination."
        ),
        QuranAyah(
            number = 7,
            arabic = "إِذَا أُلْقُوا فِيهَا سَمِعُوا لَهَا شَهِيقًا وَهِيَ تَفُورُ",
            transliteration = "Idha ulqu fiha sami'u laha shahiqan wa hiya tafur.",
            translationHausa = "Idan aka jefa su a cikinta, za su ji ruri da gurnani a gare ta alhali tana tafasa.",
            translationEnglish = "When they are thrown into it, they hear from it a dreadful inhaling while it boils up."
        ),
        QuranAyah(
            number = 8,
            arabic = "تَكَادُ تَمَيَّزُ مِنَ الْغَيْظِ ۖ كُلَّمَا أُلْقِيَ فِيهَا فَوْجٌ سَأَلَهُمْ خَزَنَتُهَا أَلَمْ يَأْتِكُمْ نَذِيرٌ",
            transliteration = "Takadu tamayyazu minal-ghayz, kullama ulqiya fiha fawjun sa'alahum khazanatuha alam ya'tikum nadhir.",
            translationHausa = "Tana kusan tsattsagewa saboda tsananin fushi; duk lokacin da aka jefa wani taro na kafirai a cikinta, masu tsaron wutar za su tambaye su: 'Shin wani mai gargaɗi bai zo muku ba?'",
            translationEnglish = "It almost bursts with rage. Every time a company is thrown into it, its keepers ask them, 'Did there not come to you a warner?'"
        ),
        QuranAyah(
            number = 9,
            arabic = "قَالُوا بَلَىٰ قَدْ جَاءَنَا نَذِيرٌ فَكَذَّبْنَا وَقُلْنَا مَا نَزَّلَ اللَّهُ مِن شَيْءٍ إِنْ أَنتُمْ إِلَّا فِي ضَلَالٍ كَبِيرٍ",
            transliteration = "Qalu bala qad ja'ana nadhirun fakadh-dhabna wa qulna ma nazzalallahu min shay'in in antum illa fi dalalin kabir.",
            translationHausa = "Za su ce: 'I lallai mai gargaɗi ya zo mana, sai muka ƙaryata kuma muka ce: Allah bai saukar da komai ba, ba ku kasance ba face a cikin ɓata mai girma.'",
            translationEnglish = "They will say, 'Yes, a warner had come to us, but we denied and said, Allah has not sent down anything. You are not except in great error.'"
        ),
        QuranAyah(
            number = 10,
            arabic = "وَقَالُوا لَوْ كُنَّا نَسْمَعُ أَوْ نَعْقِلُ مَا كُنَّا فِي أَصْحَابِ السَّعِيرِ",
            transliteration = "Wa qalu law kunna nasma'u aw na'qilu ma kunna fi as-habis-sa'ir.",
            translationHausa = "Kuma za su ce: 'Da mun kasance muna saurare da hankali ko muna yin tunani, da ba mu kasance a cikin 'yan wuta mai ƙuna ba.'",
            translationEnglish = "And they will say, 'If only we had been listening or reasoning, we would not be among the companions of the Blaze.'"
        ),
        QuranAyah(
            number = 11,
            arabic = "فَاعْتَرَفُوا بِذَنبِهِمْ فَسُحْقًا لِّأَصْحَابِ السَّعِيرِ",
            transliteration = "Fa'tarafu bidhanbihim fasuhqan li-as-habis-sa'ir.",
            translationHausa = "Sai suka amsa zunubansu da laifukansu; to halaka da nesanta daga rahama ta tabbata ga 'yan wuta mai ƙuna!",
            translationEnglish = "And they will admit their sin, so alienation for the companions of the Blaze."
        ),
        QuranAyah(
            number = 12,
            arabic = "إِنَّ الَّذِينَ يَخْشَوْنَ رَبَّهُم بِالْغَيْبِ لَهُم مَّغْفِرَةٌ وَأَجْرٌ كَبِيرٌ",
            transliteration = "Innalladhina yakhshawna Rabbahum bil-ghaybi lahum maghfiratun wa ajrun kabir.",
            translationHausa = "Lallai waɗanda ke tsoron Ubangijinsu a ɓoye suna da gafara da lada mai girma.",
            translationEnglish = "Indeed, those who fear their Lord unseen will have forgiveness and great reward."
        ),
        QuranAyah(
            number = 13,
            arabic = "وَأَسِرُّوا قَوْلَكُمْ أَوِ اجْهَرُوا بِهِ ۖ إِنَّهُ عَلِيمٌ بِذَاتِ الصُّدُورِ",
            transliteration = "Wa asirru qawlakum awij-haru bih, innahu 'Alimun bidhatis-sudur.",
            translationHausa = "Kuma ku asirce maganarku ko ku bayyana ta, lallai Shi Masani ne ga dukkan abin da ke cikin ƙiraza.",
            translationEnglish = "And conceal your speech or publicize it; indeed, He is Knowing of that within the breasts."
        ),
        QuranAyah(
            number = 14,
            arabic = "أَلَا يَعْلَمُ مَنْ خَلَقَ وَهُوَ اللَّطِيفُ الْخَبِيرُ",
            transliteration = "Ala ya'lamu man khalaqa wa Huwal-Latiful-Khabir.",
            translationHausa = "Shin Wanda Ya halitta ba Zai sani ba, alhali Shi ne Mai tausasawa, Mai cikakken labari?",
            translationEnglish = "Does He who created not know, while He is the Subtle, the Acquainted?"
        ),
        QuranAyah(
            number = 15,
            arabic = "هُوَ الَّذِي جَعَلَ لَكُمُ الْأَرْضَ ذَلُولًا فَامْشُوا فِي مَنَاكِبِهَا وَكُلُوا مِن رِّزْقِهِ ۖ وَإِلَيْهِ النُّشُورُ",
            transliteration = "Huwalladhi ja'ala lakumul-arda dhalulan famshu fi manakibiha wa kulu min rizqih, wa ilayhin-nushur.",
            translationHausa = "Shi ne Wanda Ya sanya muku ƙasa mai sauƙin zama, to ku yi tafiya a cikin sasanninta, kuma ku ci daga arzikinSa; kuma zuwa gare Shi ne tashi bayan mutuwa.",
            translationEnglish = "It is He who made the earth tame for you - so walk among its slopes and eat of His provision - and to Him is the resurrection."
        ),
        QuranAyah(
            number = 16,
            arabic = "أَأَمِنتُم مَّن فِي السَّمَاءِ أَن يَخْسِفَ بِكُمُ الْأَرْضَ فَإِذَا هِيَ تَمُورُ",
            transliteration = "A-amintum man fis-sama'i an yakhsifa bikumul-arda fa-idha hiya tamur.",
            translationHausa = "Shin kun amince da Wanda ke sama cewa ba Zai shafe ƙasa da ku ba, alhali ga ta nan tana girgiza?",
            translationEnglish = "Do you feel secure that He who is in the heaven would not cause the earth to swallow you and suddenly it would sway?"
        ),
        QuranAyah(
            number = 17,
            arabic = "أَمْ أَمِنتُم مَّن فِي السَّمَاءِ أَن يُرْسِلَ عَلَيْكُمْ حَاصِبًا ۖ فَسَتَعْلَمُونَ كَيْفَ نَذِيرِ",
            transliteration = "Am amintum man fis-sama'i an yursila 'alaykum hasiba, fasata'lamuna kayfa nadhir.",
            translationHausa = "Ko kun amince da Wanda ke sama cewa ba Zai aika muku da guguwa mai duwatsu ba? To nan gaba za ku san yadda gargaɗina yake!",
            translationEnglish = "Or do you feel secure that He who is in the heaven would not send against you a storm of stones? Then you would know how severe was My warning."
        ),
        QuranAyah(
            number = 18,
            arabic = "وَلَقَدْ كَذَّبَ الَّذِينَ مِن قَبْلِهِمْ فَكَيْفَ كَانَ نَكِيرِ",
            transliteration = "Wa laqad kadhdhaballadhina min qablihim fakayfa kana nakir.",
            translationHausa = "Kuma lallai waɗanda ke gabaninsu sun ƙaryata, to yaya musu da azabaTa ta kasance?",
            translationEnglish = "And already had those before them denied, and how terrible was My reproach."
        ),
        QuranAyah(
            number = 19,
            arabic = "أَوَلَمْ يَرَوْا إِلَى الطَّيْرِ فَوْقَهُمْ صَافَّاتٍ وَيَقْبِضْنَ ۚ مَا يُمْسِكُهُنَّ إِلَّا الرَّحْمَٰنُ ۚ إِنَّهُ بِكُلِّ شَيْءٍ بَصِيرٌ",
            transliteration = "Awa lam yaraw ilat-tayri fawqahum saffatin wa yaqbidn, ma yumsikuhunna illar-Rahman, innahu bikulli shay'in Basir.",
            translationHausa = "Shin ba su ga tsuntsaye a samansu suna shimfiɗa fukafukansu kuma suna naɗewa ba? Babu mai riƙe su a sararin samaniya sai Mai Rahama; lallai Shi Mai gani ne ga dukkan komai.",
            translationEnglish = "Do they not see the birds above them with wings outspread and folded in? None holds them up except the Most Merciful. Indeed, He is of all things Seeing."
        ),
        QuranAyah(
            number = 20,
            arabic = "أَمَّنْ هَٰذَا الَّذِي هُوَ جُندٌ لَّكُمْ يَنصُرُكُم مِّن دُونِ الرَّحْمَٰنِ ۚ إِنِ الْكَافِرُونَ إِلَّا فِي غُرُورٍ",
            transliteration = "Amman hadhalladhi huwa jundul-lakum yansurukum min dunir-Rahman, inil-kafiruna illa fi ghurur.",
            translationHausa = "Ko wane ne wannan da zai zama runduna a gare ku mai taimakonku ba tare da Mai Rahama ba? Kafirai ba su zama ba face a cikin yaudara da ruɗi.",
            translationEnglish = "Or who is it that could be an army for you to aid you other than the Most Merciful? The disbelievers are not but in delusion."
        ),
        QuranAyah(
            number = 21,
            arabic = "أَمَّنْ هَٰذَا الَّذِي يَرْزُقُكُمْ إِنْ أَمْسَكَ رِزْقَهُ ۚ بَل لَّجُّوا فِي عُتُوٍّ وَنُفُورٍ",
            transliteration = "Amman hadhalladhi yarzuqukum in amsaka rizqah, bal lajju fi 'utuwwin wa nufur.",
            translationHausa = "Ko wane ne wannan da zai azurta ku idan Ya riƙe arzikinSa? A'a, sun kange a cikin ƙiyayya, taurin kai da gudu daga gaskiya.",
            translationEnglish = "Or who is it that could provide for you if He withheld His provision? But they have persisted in insolence and aversion."
        ),
        QuranAyah(
            number = 22,
            arabic = "أَفَمَن يَمْشِي مُكِبًّا عَلَىٰ وَجْهِهِ أَهْدَىٰ أَمَّن يَمْشِي سَوِيًّا عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ",
            transliteration = "Afaman yamshi mukibban 'ala wajhihi ahda amman yamshi sawiyyan 'ala siratim-mustaqim.",
            translationHausa = "Shin wanda yake tafiya a kife a kan fuskarsa (cikin duhu) ya fi shiriya ko kuwa wanda yake tafiya a miƙe a kan hanya madaidaiciya?",
            translationEnglish = "Then is one who walks fallen on his face better guided or one who walks erect on a straight path?"
        ),
        QuranAyah(
            number = 23,
            arabic = "قُلْ هُوَ الَّذِي أَنشَأَكُمْ وَجَعَلَ لَكُمُ السَّمْعَ وَالْأَبْصَارَ وَالْأَفْئِدَةَ ۖ قَلِيلًا مَّا تَشْكُرُونَ",
            transliteration = "Qul Huwalladhi ansha'akum wa ja'ala lakumus-sam'a wal-absara wal-af'idah, qalilan ma tashkurun.",
            translationHausa = "Ka ce: 'Shi ne Wanda Ya ƙaga halittarku kuma Ya sanya muku ji, da gani, da zukata; kaɗan ne ƙwarai kuke godewa.'",
            translationEnglish = "Say, 'It is He who has produced you and made for you hearing and vision and hearts; little are you grateful.'"
        ),
        QuranAyah(
            number = 24,
            arabic = "قُلْ هُوَ الَّذِي ذَرَأَكُمْ فِي الْأَرْضِ وَإِلَيْهِ تُحْشَرُونَ",
            transliteration = "Qul Huwalladhi dhara'akum fil-ardi wa ilayhi tuhsharun.",
            translationHausa = "Ka ce: 'Shi ne Wanda Ya watsa ku a cikin ƙasa, kuma zuwa gare Shi ne za a tara ku baki ɗaya a Ranar Ƙiyama.'",
            translationEnglish = "Say, 'It is He who has multiplied you upon the earth, and to Him you will be gathered.'"
        ),
        QuranAyah(
            number = 25,
            arabic = "وَيَقُولُونَ مَتَىٰ هَٰذَا الْوَعْدُ إِن كُنتُمْ صَادِقِينَ",
            transliteration = "Wa yaquluna mata hadhal-wa'du in kuntum sadiqin.",
            translationHausa = "Kuma suna cewa: 'Yaushe ne wannan alƙawari zai cika idan kun kasance masu gaskiya?'",
            translationEnglish = "And they say, 'When is this promise, if you should be truthful?'"
        ),
        QuranAyah(
            number = 26,
            arabic = "قُلْ إِنَّمَا الْعِلْمُ عِندَ اللَّهِ وَإِنَّمَا أَنَا نَذِيرٌ مُّبِينٌ",
            transliteration = "Qul innamal-'ilmu 'indallahi wa innama ana nadhirum-mubin.",
            translationHausa = "Ka ce: 'Sanin lokacin a wurin Allah kawai yake, kuma ni mai gargaɗi ne kawai mai bayyanawa a fili.'",
            translationEnglish = "Say, 'The knowledge is only with Allah, and I am only a clear warner.'"
        ),
        QuranAyah(
            number = 27,
            arabic = "فَلَمَّا رَأَوْهُ زُلْفَةً سِيئَتْ وُجُوهُ الَّذِينَ كَفَرُوا وَقِيلَ هَٰذَا الَّذِي كُنتُم بِهِ تَدَّعُونَ",
            transliteration = "Falamma ra'awhu zulfatan si'at wujuhulladhina kafaru wa qila hadhalladhi kuntum bihi tadda'un.",
            translationHausa = "Sa'ad da suka ga azabar tana kusa, sai fuskokin waɗanda suka kafirta suka gurɓace da baƙin ciki, aka ce musu: 'Wannan shi ne abin da kuka kasance kuna gaggauta nema.'",
            translationEnglish = "But when they see it approaching, the faces of those who disbelieve will be distressed and it will be said, 'This is that which you used to call for.'"
        ),
        QuranAyah(
            number = 28,
            arabic = "قُلْ أَرَأَيْتُمْ إِنْ أَهْلَكَنِيَ اللَّهُ وَمَن مَّعِيَ أَوْ رَحِمَنَا فَمَن يُجِيرُ الْكَافِرِينَ مِنْ عَذَابٍ أَلِيمٍ",
            transliteration = "Qul ara'aytum in ahlakaniyallahu wa mam-ma'iya aw rahimana faman yujirul-kafirina min 'adhabin alim.",
            translationHausa = "Ka ce: 'Shin kun gani, idan Allah Ya halakar da ni da waɗanda ke tare da ni ko kuwa Ya yi mana rahama, to wane ne zai tseratar da kafirai daga azaba mai raɗaɗi?'",
            translationEnglish = "Say, 'Have you considered: whether Allah should cause my death and those with me or have mercy upon us, who can protect the disbelievers from a painful punishment?'"
        ),
        QuranAyah(
            number = 29,
            arabic = "قُلْ هُوَ الرَّحْمَٰنُ آمَنَّا بِهِ وَعَلَيْهِ تَوَكَّلْنَا ۖ فَسَتَعْلَمُونَ مَنْ هُوَ فِي ضَلَالٍ مُّبِينٍ",
            transliteration = "Qul Huwar-Rahmanu amanna bihi wa 'alayhi tawakkalna, fasata'lamuna man huwa fi dalalim-mubin.",
            translationHausa = "Ka ce: 'Shi ne Mai Rahama, mun yi imani da Shi, kuma a kanSa muka dogara; to nan gaba za ku san wanda yake a cikin ɓata bayyananna.'",
            translationEnglish = "Say, 'He is the Most Merciful; we have believed in Him, and upon Him we have relied. And you will come to know who it is that is in clear error.'"
        ),
        QuranAyah(
            number = 30,
            arabic = "قُلْ أَرَأَيْتُمْ إِنْ أَصْبَحَ مَاؤُكُمْ غَوْرًا فَمَن يَأْتِيكُم بِمَاءٍ مَّعِينٍ",
            transliteration = "Qul ara'aytum in asbaha ma'ukum ghawran faman ya'tikum bima'im-ma'in.",
            translationHausa = "Ka ce: 'Shin kun gani, idan ruwanku ya wayi gari ya kafe a ƙarƙashin ƙasa, to wane ne zai kawo muku ruwa mai ɓubɓuga mai daɗi?'",
            translationEnglish = "Say, 'Have you considered: if your water was to become sunken into the earth, then who could bring you flowing water?'"
        )
    )

    // Complete continuous Arabic text with Bismillah and Ayah separators
    val fullSurahAlMulkArabic: String = buildString {
        append("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\n\n")
        surahAlMulkAyahs.forEachIndexed { index, ayah ->
            append(ayah.arabic)
            append(" ﴿${index + 1}﴾ ")
            if ((index + 1) % 3 == 0) append("\n")
        }
    }

    val fullSurahAlMulkTransliteration: String = buildString {
        append("Bismillahir-Rahmanir-Rahim\n\n")
        surahAlMulkAyahs.forEach { ayah ->
            append("(${ayah.number}) ")
            append(ayah.transliteration)
            append("\n")
        }
    }

    val fullSurahAlMulkTranslationHausa: String = buildString {
        append("Da sunan Allah Mai Rahama Mai Jinkai\n\n")
        surahAlMulkAyahs.forEach { ayah ->
            append("(${ayah.number}) ")
            append(ayah.translationHausa)
            append("\n")
        }
    }

    val fullSurahAlMulkTranslationEnglish: String = buildString {
        append("In the Name of Allah, the Most Gracious, the Most Merciful\n\n")
        surahAlMulkAyahs.forEach { ayah ->
            append("(${ayah.number}) ")
            append(ayah.translationEnglish)
            append("\n")
        }
    }

    fun getSleepingAndWakingDuas(): List<DuaEntity> {
        return listOf(
            // 1. SURAH AL-MULK FULL (Item #1, ID 5)
            DuaEntity(
                id = 5,
                category = "Sleeping & Waking Up",
                title = "Suratul Mulk (Karatun Kafin Barci - Ayoyi 1-30)",
                arabic = fullSurahAlMulkArabic,
                transliteration = fullSurahAlMulkTransliteration,
                translation = fullSurahAlMulkTranslationEnglish,
                translationHausa = fullSurahAlMulkTranslationHausa,
                translationYoruba = "Kika gbogbo Suratul Mulk (ẹsẹ 1 si 30) ṣaaju ki o to sun. Allāhu yoo daabobo oluka rẹ lọwọ ijiya iboji.",
                translationIgbo = "Ịgụ Suratul Mulk zuru oke (amaokwu 1 ruo 30) tupu ị lakpuo ụra. Chineke ga-echebe onye na-agụ ya pụọ n'ahụhụ nke ili.",
                reference = "Surah Al-Mulk (Surah ta 67, Makkiyyah, Ayoyi 30). Sunnah ce ta tabbata daga Manzon Allah (ﷺ) cewa ba ya yin barci har sai ya karanta Suratus Sajdah da Suratul Mulk. (Jami' At-Tirmidhi no. 2892, Sunan An-Nasa'i). Ta kasance mai ceton mai karanta ta da kare shi daga azabar kabari (Al-Mani'ah / Al-Munjiyah)."
            ),

            // 2. FALALAR SURATUL MULK KAFIN BARCI (Item #2, ID 6)
            DuaEntity(
                id = 6,
                category = "Sleeping & Waking Up",
                title = "Falalar Suratul Mulk Kafin Barci (Kariya daga Azabar Kabari)",
                arabic = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ، عَنِ النَّبِيِّ ﷺ قَالَ: «إِنَّ سُورَةً مِنَ الْقُرْآنِ ثَلَاثُونَ آيَةً شَفَعَتْ لِرَجُلٍ حَتَّى غُفِرَ لَهُ: تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ».\n\nوَعَنْ جَابِرٍ رَضِيَ اللَّهُ عَنْهُ: «أَنَّ النَّبِيَّ ﷺ كَانَ لَا يَنَامُ حَتَّى يَقْرَأَ: الم تَنْزِيلُ، وَتَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ».\n\nوَقَالَ ابْنُ مَسْعُودٍ رَضِيَ اللَّهُ عَنْهُ: «مَنْ قَرَأَ تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ كُلَّ لَيْلَةٍ مَنَعَهُ اللَّهُ بِهَا مِنْ عَذَابِ الْقَبْرِ، وَكُنَّا فِي عَهْدِ رَسُولِ اللَّهِ ﷺ نُسَمِّيهَا الْمَانِعَةَ».",
                transliteration = "'An Abi Hurayrah (RA), 'anin-Nabiyyi (SAW) qala: Inna suratan minal-Qur'ani thalathuna ayatan shafa'at lirajulin hatta ghufira lahu: Tabarakalladhi biyadihil-Mulk. Wa 'an Jabirin (RA): Annan-Nabiyya (SAW) kana la yanamu hatta yaqra'a Alif-Lam-Mim Tanzil wa Tabarakalladhi biyadihil-Mulk. Wa qala Ibnu Mas'ud (RA): Man qara'a Tabarakalladhi biyadihil-Mulku kulla laylatin mana'ahullahu biha min 'adhabil-qabr, wa kunna fi 'ahdi Rasulillahi (SAW) nusammihal-Mani'ah.",
                translation = "The Prophet (ﷺ) said: 'Indeed, there is a surah in the Quran of thirty verses that interceded for a man until he was forgiven: Tabarakalladhi biyadihil-Mulk.' And Jabir (RA) narrated: 'The Prophet (ﷺ) would not sleep until he had recited Surah As-Sajdah and Surah Al-Mulk.' And Ibn Mas'ud (RA) said: 'Whoever recites Surah Al-Mulk every night, Allah will protect him from the torment of the grave. During the time of the Messenger of Allah (ﷺ), we used to call it Al-Mani'ah (The Shield/Protector).'",
                translationHausa = "Falalar Suratul Mulk a Sunnah:\n1. Manzon Allah (ﷺ) ya ce: 'Lallai a cikin Alƙur'ani akwai wata sura mai ayoyi talatin (30) da ta ci gaba da yin ceto ga wani mutum har sai da aka gafarta masa zunubansa; ita ce Tabarakalladhi biyadihil-Mulk.' (At-Tirmidhi 2891, Abu Dawud 1400, Hasan).\n2. Hadisin Jabir (RA): 'Manzon Allah (ﷺ) ya kasance ba ya yin barci har sai ya karanta Suratus Sajdah da Suratul Mulk.' (At-Tirmidhi 2892, Ahmad).\n3. Sayyidina Abdullahi bn Mas'ud (RA) ya ce: 'Wanda duk ya karanta Tabarakalladhi biyadihil-mulk a kowace dare, Allah zai kare shi da ita daga azabar kabari. A zamanin Manzon Allah (ﷺ) muna kiranta Al-Mani'ah (Mai bayar da kariya da garkuwa).' (An-Nasa'i a Al-Kubra 10547, Al-Hakim 2/498).",
                translationYoruba = "Àwọn Àǹfààní Suratul Mulk: Ojisẹ Allāhu (SAW) sọ pe Suratul Mulk ni ẹsẹ ọgbọn ti o n bẹbẹ fun eniyan titi a fi dariji ẹṣẹ rẹ, ko si ki n sun titi yoo fi ka a. O tun daabobo eniyan lọwọ ijiya iboji.",
                translationIgbo = "Uru dị na Suratul Mulk: Onye Ozi Chineke (SAW) sịrị na Suratul Mulk na-arịọrọ mmadụ arịrịọ ruo mgbe agbaghaara ya mmehie ya, ọ dịghịkwa ehi ụra ruo mgbe ọ gụchara ya. Ọ na-echebekwa pụọ n'ahụhụ nke ili.",
                reference = "Sunan At-Tirmidhi no. 2891 & 2892, Sunan Abu Dawud no. 1400, Musnad Ahmad no. 14249, Sunan An-Nasa'i As-Sunan Al-Kubra no. 10547. Sahih/Hasan."
            ),

            // 3. LADUBBAN KWANCIYA BARCI (Item #3, ID 57)
            DuaEntity(
                id = 57,
                category = "Sleeping & Waking Up",
                title = "Ladubban Kwanciya Barci a Sunnah",
                arabic = "آدَابُ النَّوْمِ فِي السُّنَّةِ النَّبَوِيَّةِ:\n١. الْوُضُوءُ قَبْلَ النَّوْمِ كَوُضُوءِ الصَّلَاةِ.\n٢. نَفْضُ الْفِرَاشِ ثَلَاثَ مَرَّاتٍ بِدَاخِلَةِ الْإِزَارِ قَبْلَ الِاضْطِجَاعِ.\n٣. النَّوْمُ عَلَى الشِّقِّ الْأَيْمَنِ وَوَضْعُ الْكَفِّ الْأَيْمَنِ تَحْتَ الْخَدِّ الْأَيْمَنِ.\n٤. إِطْفَاءُ السُّرُجِ وَإِغْلَاقُ الْأَبْوَابِ وَتَغْطِيَةُ الْآنِيَةِ مَعَ ذِكْرِ اسْمِ اللَّهِ.\n٥. جَمْعُ الْكَفَّيْنِ وَالنَّفْثُ فِيهِمَا بَعْدَ قِرَاءَةِ الْمُعَوِّذَاتِ (الإخلاص، الفلق، الناس) ثَلَاثَ مَرَّاتٍ وَمَسْحُ مَا اسْتَطَاعَ مِنَ الْجَسَدِ.\n٦. قِرَاءَةُ آيَةِ الْكُرْسِيِّ وَآخِرِ آيَتَيْنِ مِنْ سُورَةِ الْبَقَرَةِ وَسُورَةِ الْمُلْكِ.\n٧. التَّسْبِيحُ (سبحان الله 33، الحمد لله 33، الله أكبر 34).\n٨. قِرَاءَةُ أَدْعِيَةِ النَّوْمِ الْمَأْثُورَةِ حَتَّى يَغْلِبَكَ النَّوْمُ عَلَى ذِكْرِ اللَّهِ.",
                transliteration = "Adabun-Nawm fis-Sunnatin-Nabawiyyah: 1. Al-Wudu'u qablan-nawm ka-wudu'is-salah. 2. Nafdul-firashi thalatha marratin bidakhilatil-izar. 3. An-Nawmu 'alash-shiqqil-aymani wa wad'ul-kaffil-aymani tahtal-khaddil-ayman. 4. Itfa'us-suruji wa ighlaqul-abwabi ma'a dhikris-millah. 5. Jam'ul-kaffayni wan-nafthu fihima ba'da qira'atil-mu'awwidhat thalatha marrat. 6. Qira'atu Ayatil-Kursiyyi wa akhiru ayatayni min Suratil-Baqarah wa Suratul-Mulk. 7. At-Tasbih (Subhanallah 33, Alhamdu lillah 33, Allahu Akbar 34). 8. Ad'iyatun-nawmi hatta yaghlibakan-nawmu 'ala dhikrillah.",
                translation = "Sunnah Etiquettes of Sleeping: 1. Perform ablution (wudu) before bed just like the ablution for prayer. 2. Dust off the mattress three times with the border of your garment before lying down. 3. Sleep on your right side and place your right hand under your right cheek. 4. Extinguish lights, lock doors, and cover containers while mentioning Allah's name. 5. Cup your palms, recite the three Quls (Ikhlas, Falaq, Nas), blow into hands, and wipe over your body three times. 6. Recite Ayat al-Kursi, the last two verses of Surah Al-Baqarah, and Surah Al-Mulk. 7. Glorify Allah with bedtime Tasbih (Subhanallah 33, Alhamdulillah 33, Allahu Akbar 34). 8. Recite the prophetic sleeping supplications so sleep overtakes you while remembering Allah.",
                translationHausa = "Ladubban Kwanciya Barci a Sunnar Manzon Allah (ﷺ):\n1. Yin alwala kafin kwanciya kamar yadda ake alwalar sallah (Bukhari 247).\n2. Kaɗe shimfiɗa sau uku da gefen tufa kafin kwanciya domin korar duk wata cuta (Bukhari 6320).\n3. Kwanciya a kan gefen dama tare da sanya tafin hannun dama a ƙarƙashin kumatun dama (Abu Dawud 5046).\n4. Kashe fitilu da wuta, rufe kofofi, da rufe kwantena na ruwa da abinci tare da ambaton sunan Allah (Bismillahi) (Bukhari 3280).\n5. Haɗa tafukan hannaye, tofa tofi a ciki bayan karanta Ikhlas, Falaq, da Nas, sannan a shafi jiki sau uku daga kai zuwa ƙasa (Bukhari 5017).\n6. Karanta Ayatul Kursiyyi, ayoyi biyu na ƙarshen Suratul Baqarah, da Suratul Mulk baki ɗaya.\n7. Yin Tasbihin Nana Fadima: Subhanallah (sau 33), Alhamdulillah (sau 33), Allahu Akbar (sau 34).\n8. Karanta addu'o'in kwanciya barci har sai barci ya ɗauke ka kana cikin zikirin Allah.",
                translationYoruba = "Àwọn Ẹ̀kọ́ Isún ni Sunnah: Ṣe àlwálà, gbọn ibùsùn rẹ ni igba mẹta, sùn si apa ọtun pẹlu ọwọ ọtun labẹ ẹrẹkẹ, pa ina, ka Ayatul Kursi ati Suratul Mulk, ki o si ṣe Tasbihi.",
                translationIgbo = "Usoro Ihi Ụra na Sunnah: Mee alwala, fegharịa ihe ndina ugboro atọ, hie ụra n'akụkụ aka nri, gụọ Ayatul Kursi na Suratul Mulk, ma mee Tasbih.",
                reference = "Sahih Al-Bukhari no. 247, 3280, 5017, 6320; Sahih Muslim no. 2710, 2714; Sunan Abu Dawud no. 5046."
            ),

            // 4. TASBIHIN KWANCIYA BARCI (Item #4, ID 58)
            DuaEntity(
                id = 58,
                category = "Sleeping & Waking Up",
                title = "Tasbihin Kwanciya Barci (Subhanallah 33, Alhamdulillah 33, Allahu Akbar 34)",
                arabic = "«سُبْحَانَ اللَّهِ» (ثَلَاثًا وَثَلَاثِينَ)،\n«وَالْحَمْدُ لِلَّهِ» (ثَلَاثًا وَثَلَاثِينَ)،\n«وَاللَّهُ أَكْبَرُ» (أَرْبَعًا وَثَلَاثِينَ).\n\nقَالَ رَسُولُ اللَّهِ ﷺ لِعَلِيٍّ وَفَاطِمَةَ رَضِيَ اللَّهُ عَنْهُمَا: «أَلَا أَدُلُّكُمَا عَلَى مَا هُوَ خَيْرٌ لَكُمَا مِنْ خَادِمٍ؟ إِذَا أَوَيْتُمَا إِلَى فِرَاشِكُمَا أَوْ أَخَذْتُمَا مَضَاجِعَكُمَا فَكَبِّرَا أَرْبَعًا وَثَلَاثِينَ، وَسَبِّحَا ثَلَاثًا وَثَلَاثِينَ، وَاحْمَدَا ثَلَاثًا وَثَلَاثِينَ، فَهَذَا خَيْرٌ لَكُمَا مِنْ خَادِمٍ».",
                transliteration = "Subhanallah (33 times), Alhamdu lillah (33 times), Allahu Akbar (34 times). Qala Rasulullahi (SAW) li 'Aliyyin wa Fatimata (RA): Ala adullukuma 'ala ma huwa khayrul-lakuma min khadim? Idha awaytuma ila firashikuma aw akhadhtuma madaji'akuma fakabbira arba'an wa thalathin, wa sabbiha thalathan wa thalathin, wahmada thalathan wa thalathin, fahadha khayrul-lakuma min khadim.",
                translation = "Subhanallah (Glory be to Allah) - 33 times,\nAlhamdulillah (Praise be to Allah) - 33 times,\nAllahu Akbar (Allah is the Greatest) - 34 times.\n\nThe Messenger of Allah (ﷺ) said to Ali and Fatima (RA): 'Shall I not direct you to something that is better for you than a servant? When you go to your bed, declare Allah's greatness (Allahu Akbar) 34 times, and glorify Him (Subhanallah) 33 times, and praise Him (Alhamdulillah) 33 times. That is better for you than a servant.'",
                translationHausa = "Tasbihin Nana Fadima da Sayyidina Ali kafin Barci:\n• Subhanallah (Tsarki ya tabbata ga Allah) - Sau 33\n• Alhamdulillah (Dukkan godiya ta tabbata ga Allah) - Sau 33\n• Allahu Akbar (Allah ne Mafi Girma) - Sau 34\n\nManzon Allah (ﷺ) ya ce wa Nana Fadima da Sayyidina Ali lokacin da suka nemi a ba su mai aiki: 'Shin ba na nuna muku abin da ya fi muku alheri fiye da bawa mai yi muku aiki ba? Idan za ku kwanta a shimfiɗarku, ku ce Allahu Akbar sau 34, Subhanallah sau 33, da Alhamdulillah sau 33; wannan ya fi muku alheri fiye da bawa mai aiki.'\nSayyidina Ali ya ce: 'Ban taɓa barin wannan tasbihin ba tun lokacin da na ji shi daga Manzon Allah (ﷺ).'",
                translationYoruba = "Tasbihi Ṣaaju Isun: Subhanallah (ẹgbẹ 33), Alhamdulillah (ẹgbẹ 33), Allahu Akbar (ẹgbẹ 34). Ojisẹ Allāhu sọ pe eyi dara ju iranṣẹ lọ fun Ali ati Fatima.",
                translationIgbo = "Tasbih Tupu Ụra: Subhanallah (ugboro 33), Alhamdulillah (ugboro 33), Allahu Akbar (ugboro 34). Ọ ka ohu mma dịka Onye Ozi Chineke sịrị kụziere Fatima na Ali.",
                reference = "Sahih Al-Bukhari no. 3705, 5361, 6318; Sahih Muslim no. 2727."
            ),

            // 5. ADDU'AR KWANCIYA BARCI (Bismika Rabbi Wada'tu Janbi) (Item #5, ID 59)
            DuaEntity(
                id = 59,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Kwanciya Barci (Bismika Rabbi Wada'tu Janbi)",
                arabic = "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا، بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
                transliteration = "Bismika Rabbi wada'tu janbi, wa bika arfa'uh, fa'in amsakta nafsi farhamha, wa in arsaltaha fahfazha bima tahfazu bihi 'ibadakas-salihin.",
                translation = "In Your Name my Lord, I put down my side and by Your Name I raise it up. If You hold back my soul (in death), have mercy upon it; and if You release it, protect it with that which You protect Your righteous slaves.",
                translationHausa = "Da sunanKa ya Ubangijina na shimfiɗa gefen jikina (na kwanta), kuma da ikonKa nake ɗaga shi (nake tashi). Idan Ka riƙe raina (Ka ɗauki raina a cikin barcin) to Ka yi masa rahama, idan kuma Ka sako shi (Ka bar ni da rai) to Ka kare shi da abin da Kake kare bayinKa salihai da shi.",
                translationYoruba = "Pẹlu orukọ Rẹ Oluwa mi ni mo fi ẹgbẹ mi lélẹ̀, ati pẹlu Rẹ ni mo fi n gbe e dide. Bi O ba gba ẹmi mi, ṣe aanu fun un; bi O ba si da a pada, daabobo o pẹlu ohun ti O fi n daabobo awọn ẹru Rẹ olododo.",
                translationIgbo = "N'aha Gị Onyenwe m ka m gbadara n'akụkụ m, ma site n'aka Gị ka m na-ebili. Ọ bụrụ na Ị jide mkpụrụ obi m, meere ya ebere; ọ bụrụkwa na Ị hapụ ya, chebe ya site n'ihe Ị na-eji echebe ndị ohu Gị eziomume.",
                reference = "Sahih Al-Bukhari no. 6320, Sahih Muslim no. 2714."
            ),

            // 6. ADDU'AR KWANCIYA BARCI (Bismika Allahumma Amutu wa Ahya) (ID 360)
            DuaEntity(
                id = 360,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Kwanciya Barci (Bismika Allahumma Amutu wa Ahya)",
                arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                transliteration = "Bismika Allahumma amutu wa ahya.",
                translation = "In Your Name, O Allah, I die and I live.",
                translationHausa = "Da sunanKa ya Allah nake mutuwa (nake barci) kuma nake rayuwa (nake tashi).",
                translationYoruba = "Pẹlu orukọ Rẹ Allāhu ni mo n ku ti mo si n di alaaye.",
                translationIgbo = "N'aha Gị Chineke ka m na-anwụ ma na-adị ndụ.",
                reference = "Sahih Al-Bukhari no. 6312, 6324; Sahih Muslim no. 2711."
            ),

            // 7. ADDU'AR MIKA WUYA (Allahumma Aslamtu Nafsi Ilayk) (ID 361)
            DuaEntity(
                id = 361,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Mika Wuya Kafin Barci (Mutuwa a kan Musulunci)",
                arabic = "اللَّهُمَّ أَسْلَمْتُ نَفْسِي إِلَيْكَ، وَفَوَّضْتُ أَمْرِي إِلَيْكَ، وَوَجَّهْتُ وَجْهِي إِلَيْكَ، وَأَلْجَأْتُ ظَهْرِي إِلَيْكَ، رَغْبَةً وَرَهْبَةً إِلَيْكَ، لَا مَلْجَأَ وَلَا مَنْجَا مِنْكَ إِلَّا إِلَيْكَ، آمَنْتُ بِكِتَابِكَ الَّذِي أَنْزَلْتَ، وَبِنَبِيِّكَ الَّذِي أَرْسَلْتَ",
                transliteration = "Allahumma aslamtu nafsi ilayk, wa fawwadtu amri ilayk, wa wajjahtu wajhi ilayk, wa alja'tu zahri ilayk, raghbatan wa rahbatan ilayk, la malja'a wa la manja minka illa ilayk, amantu bikitabikal-ladhi anzalt, wa bi-nabiyyikal-ladhi arsalt.",
                translation = "O Allah, I have submitted myself to You, and entrusted my affairs to You, and turned my face to You, and leaned my back against You, out of hope and fear of You. There is no refuge or sanctuary from You except in You. I believe in Your Book which You sent down, and in Your Prophet whom You sent. (The Prophet said: If you die that night, you die upon the natural true faith).",
                translationHausa = "Ya Allah, na miƙa wuya ga raina zuwa gare Ka, na miƙa al'amarina zuwa gare Ka, na fuskantar da fuskata zuwa gare Ka, kuma na jingina bayana zuwa gare Ka, domin kwadayi da tsoronKa. Babu mafaka babu wurin tsira daga gare Ka sai zuwa gare Ka. Na yi imani da LittafinKa da Ka saukar, kuma na yi imani da AnnabinKa da Ka aiko.\n\nManzon Allah (ﷺ) ya ce wa Al-Bara' bn Azib: 'Idan ka karanta wannan kafin ka kwanta sannan ka mutu a wannan daren, ka mutu a kan dabi'ar addinin gaskiya (Fitrah/Musulunci), kuma ka sanya su su zama kalmominka na karshe.'",
                translationYoruba = "Allāhu, mo fi ẹmi mi lé Ọ lọwọ, mo gbe ọrọ mi lé Ọ lọwọ. Bi o ba ku ni alẹ naa, o ku lori ẹsin Islam.",
                translationIgbo = "Chineke, enyela m onwe m n'aka Gị, tụkwasịkwa Gị obi. Ọ bụrụ na ị nwụọ n'abalị ahụ, ị nwụrụ na okpukpe Islam.",
                reference = "Sahih Al-Bukhari no. 247, 6313, 6315; Sahih Muslim no. 2710."
            ),

            // 8. NEMAN TSARI DAGA AZABA (Sau 3 da hannun dama a kumatu) (ID 362)
            DuaEntity(
                id = 362,
                category = "Sleeping & Waking Up",
                title = "Neman Tsari daga Azabar Ranar Tashin Alkiyama (Sau 3)",
                arabic = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                transliteration = "Allahumma qini 'adhabaka yawma tab'athu 'ibadak.",
                translation = "O Allah, protect me from Your punishment on the Day when You resurrect Your slaves. (Recited 3 times while placing the right hand under the right cheek).",
                translationHausa = "Ya Allah, Ka kare ni daga azabarKa a ranar da Zaka tayar da bayinKa. (Ana karantawa sau 3 tare da sanya tafin hannun dama a ƙarƙashin kumatun dama).",
                translationYoruba = "Allāhu, gba mi lọwọ ijiya Rẹ ni ọjọ ti O ba ji awọn ẹru Rẹ dide. (Igba mẹta).",
                translationIgbo = "Chineke, chebe m pụọ n'ahụhụ Gị n'ụbọchị Ị ga-akpọlite ndị ohu Gị. (Ugboro 3).",
                reference = "Sunan Abu Dawud no. 5045, Jami' At-Tirmidhi no. 3398. Sahih."
            ),

            // 9. AYATUL KURSIYYI KAFIN BARCI (ID 363)
            DuaEntity(
                id = 363,
                category = "Sleeping & Waking Up",
                title = "Ayatul Kursiyyi Kafin Barci (Kariya da Garkuwa daga Shaidan)",
                arabic = "اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm, lahu ma fis-samawati wa ma fil-ard, man dhal-ladhi yashfa'u 'indahu illa bi'idhnih, ya'lamu ma bayna aydihim wa ma khalfahum, wa la yuhituna bishay'im-min 'ilmihi illa bima sha', wasi'a kursiyyuhus-samawati wal-ard, wa la ya'uduhu hifzuhuma, wa Huwal-'Aliyyul-'Azim.",
                translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
                translationHausa = "Allah, babu abin bautawa da gaskiya sai Shi, Mai Rayuwa ne, Mai Kula da komai. Gyangyaɗi ba ya kama Shi kuma barci ba ya ɗaukarSa. NaSa ne abin da ke cikin sammai da abin da ke cikin ƙasa. Wane ne zai yi ceto a wurinSa face da izininSa? Yana sanin abin da ke gabaninsu da abin da ke bayansu, kuma ba su kewayewa da komai daga iliminSa face da abin da Ya so. KursiyyinSa ya yalwaci sammai da ƙasa, kuma kiyaye su ba ya gajiyar da Shi; kuma Shi ne Mafi ɗaukaka, Mafi girma.\n\nFalala: Manzon Allah (ﷺ) ya tabbatar wa Abu Hurairah (RA) cewa: 'Idan ka kwanta a shimfiɗarka ka karanta Ayatul Kursiyyi, wani mai kiyaye ka daga Allah zai ci gaba da tsare ka, kuma shaidan ba zai kusance ka ba har gari ya waye.'",
                translationYoruba = "Ayatul Kursi ṣaaju isun: Allāhu yoo ran olutọju kan lati daabobo ọ, satani ko si ni sunmọ ọ titi di owurọ.",
                translationIgbo = "Ayatul Kursi tupu ị lakpuo ụra: Chineke ga-eziga onye nchebe ga-echebe gị, Setan agaghịkwa abịakwute gị ruo ụtụtụ.",
                reference = "Surah Al-Baqarah 2:255; Sahih Al-Bukhari no. 2311 (Hadisin Abu Hurairah)."
            ),

            // 10. AYOYI BIYU NA KARSHEN SURATUL BAQARAH (ID 364)
            DuaEntity(
                id = 364,
                category = "Sleeping & Waking Up",
                title = "Ayoyi Biyu na Ƙarshen Suratul Baqarah (Amanar-Rasul)",
                arabic = "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ ۝ لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
                transliteration = "Amanar-Rasulu bima unzila ilayhi mir-Rabbihi wal-mu'minun, kullun amana billahi wa mala'ikatihi wa kutubihi wa rusulih, la nufarriqu bayna ahadim-mir-rusulih, wa qalu sami'na wa ata'na ghufranaka Rabbana wa ilaykal-masir. La yukallifullahu nafsan illa wus'aha, laha ma kasabat wa 'alayha maktasabat, Rabbana la tu'akhidhna in nasina aw akhta'na, Rabbana wa la tahmil 'alayna isran kama hamaltahu 'alalladhina min qablina, Rabbana wa la tuhammilna ma la taqata lana bih, wa'fu 'anna waghfir lana warhamna, Anta Mawlana fansurna 'alal-qawmil-kafirin.",
                translation = "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers. All of them have believed in Allah and His angels and His books and His messengers... (The Prophet ﷺ said: 'Whoever recites the last two verses of Surah Al-Baqarah at night, they will suffice him').",
                translationHausa = "Manzon Allah (ﷺ) ya ce: 'Wanda duk ya karanta ayoyi biyu na ƙarshen Suratul Baqarah (Amanar-Rasul) a cikin dare, sun isar masa (daga dukkan sharri, ko sun isar masa a matsayin tsaro da ibada).' (Sahih Al-Bukhari da Muslim).\n\nMa'ana: Manzo ya yi imani da abin da aka saukar masa daga Ubangijinsa, da muminai... Ya Ubangijinmu kada Ka kama mu idan mun manta ko mun yi kuskure... Ka yafe mana, Ka gafarta mana, Ka ji ƙanmu, Kai ne Majiɓincinmu, Ka taimake mu a kan mutane kafirai.",
                translationYoruba = "Ẹsẹ meji ti o kẹhin ti Suratul Baqarah: Ẹnikẹni ti o ba ka wọn ni alẹ, wọn yoo to fun un gẹgẹ bi aabo.",
                translationIgbo = "Amaokwu abụọ ikpeazụ nke Suratul Baqarah: Onye ọ bụla na-agụ ha n'abalị, ha ga-ezuru ya dịka nchebe.",
                reference = "Surah Al-Baqarah 2:285-286; Sahih Al-Bukhari no. 5009, 5051; Sahih Muslim no. 807, 808."
            ),

            // 11. AL-MU'AWWIDHAT KAFIN BARCI (Ikhlas, Falaq, Nas tare da tofa a hannu) (ID 365)
            DuaEntity(
                id = 365,
                category = "Sleeping & Waking Up",
                title = "Al-Mu'awwidhat (Ikhlas, Falaq, Nas) Kafin Barci da Tofa a Hannu",
                arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ: ﴿قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ﴾\n\nبِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ: ﴿قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ﴾\n\nبِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ: ﴿قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ﴾",
                transliteration = "Qul Huwallahu Ahad... Qul A'udhu bi-Rabbil-Falaq... Qul A'udhu bi-Rabbin-Nas. (Recited 3 times, blowing into cupped hands and wiping the body).",
                translation = "Reciting Surah Al-Ikhlas, Surah Al-Falaq, and Surah An-Nas three times before sleep. Aisha (RA) narrated that whenever the Prophet (ﷺ) went to bed each night, he would cup his palms together, blow into them, recite these three Surahs, and then wipe as much of his body as he could with his hands, starting with his head, face, and the front of his body, doing that three times.",
                translationHausa = "Karanta Suratul Ikhlas, Suratul Falaq, da Suratun Nas kafin barci:\nUwar Muminai Nana Aisha (RA) ta ruwaito cewa: 'Manzon Allah (ﷺ) ya kasance a kowace dare idan zai kwanta, yakan haɗa tafukan hannayensa biyu ya tofa tofi a cikinsu, sannan ya karanta Qul Huwallahu Ahad, da Qul A'udhu bi-Rabbil-Falaq, da Qul A'udhu bi-Rabbin-Nas, sannan ya shafi dukkan abin da zai iya shafa na jikinsa da hannayen, yana farawa daga kansa da fuskarsa da gaban jikinsa; yana yin haka sau uku.'",
                translationYoruba = "Kika Suratul Ikhlas, Falaq ati Nas ni igba mẹta ṣaaju isun, ati fifi pa gbogbo ara.",
                translationIgbo = "Ịgụ Suratul Ikhlas, Falaq na Nas ugboro atọ tupu ụra, na iji aka hichaa ahụ niile.",
                reference = "Sahih Al-Bukhari no. 5017, 5748; Sahih Muslim no. 2192."
            ),

            // 12. GODIYA GA CIYARWA DA MASAKI (Alhamdu lillahilladhi at'amana wa saqana) (ID 366)
            DuaEntity(
                id = 366,
                category = "Sleeping & Waking Up",
                title = "Godiya ga Allah da Ya Ciyar da Mu kuma Ya Ba Mu Masauki",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا، وَكَفَانَا، وَآوَانَا، فَكَمْ مِمَّنْ لَا كَافِيَ لَهُ وَلَا مُؤْوِيَ",
                transliteration = "Alhamdu lillahil-ladhi at'amana wa saqana, wa kafana, wa awana, fakam mimman la kafiya lahu wa la mu'wiya.",
                translation = "Praise is to Allah Who has fed us and given us drink, and who has sufficed us and sheltered us; for how many are there who have no one to suffice them or shelter them!",
                translationHausa = "Dukkan godiya ta tabbata ga Allah Wanda Ya ciyar da mu, Ya shayar da mu, Ya isar mana (wajen buƙatunmu), kuma Ya ba mu wurin zama da masauki; domin kuwa da yawa akwai waɗanda ba su da mai isar musu kuma ba su da wurin kwana ko masauki!",
                translationYoruba = "Ọpẹ ni fun Allāhu ti o bọ wa, ti o fun wa ni omi mu, ti o to fun wa ti o si fun wa ni ibugbe; melo melo ninu awọn ti ko ni ẹnikan ti yoo to fun wọn tabi fun wọn ni ibugbe!",
                translationIgbo = "Otuto dịrị Chineke Onye nyere anyị nri na mmiri, Onye zuru anyị ma nye anyị ebe obibi; lee ka ọtụtụ ndị na-enweghị onye ga-ezuru ha ma ọ bụ nye ha ebe obibi!",
                reference = "Sahih Muslim no. 2715; Sunan Abu Dawud no. 5053; Jami' At-Tirmidhi no. 3396."
            ),

            // 13. BABBAR ADDU'AR BARCI (Allahumma Rabbas-Samawat) (ID 367)
            DuaEntity(
                id = 367,
                category = "Sleeping & Waking Up",
                title = "Babbar Addu'ar Barci ta Ubangijin Sammai da Ƙasa (Kariya da Biyan Bashi)",
                arabic = "اللَّهُمَّ رَبَّ السَّمَاوَاتِ وَرَبَّ الْأَرْضِ وَرَبَّ الْعَرْشِ الْعَظِيمِ، رَبَّنَا وَرَبَّ كُلِّ شَيْءٍ، فَالِقَ الْحَبِّ وَالنَّوَى، وَمُنْزِلَ التَّوْرَاةِ وَالْإِنْجِيلِ وَالْفُرْقَانِ، أَعُوذُ بِكَ مِنْ شَرِّ كُلِّ شَيْءٍ أَنْتَ آخِذٌ بِنَاصِيَتِهِ. اللَّهُمَّ أَنْتَ الْأَوَّلُ فَلَيْسَ قَبْلَكَ شَيْءٌ، وَأَنْتَ الْآخِرُ فَلَيْسَ بَعْدَكَ شَيْءٌ، وَأَنْتَ الظَّاهِرُ فَلَيْسَ فَوْقَكَ شَيْءٌ، وَأَنْتَ الْبَاطِنُ فَلَيْسَ دُونَكَ شَيْءٌ، اقْضِ عَنَّا الدَّيْنَ، وَأَغْنِنَا مِنَ الْفَقْرِ",
                transliteration = "Allahumma Rabbas-samawati wa Rabbal-ardi wa Rabbal-'Arshil-'Azim, Rabbana wa Rabba kulli shay', faliqal-habbi wan-nawa, wa munzilat-Tawrati wal-Injili wal-Furqan, a'udhu bika min sharri kulli shay'in Anta akhidhum binasiyatih. Allahumma Antal-Awwalu falaysa qablaka shay', wa Antal-Akhiru falaysa ba'daka shay', wa Antaz-Zahiru falaysa fawqaka shay', wa Antal-Batinu falaysa dunaka shay', iqdi 'annad-dayna wa aghnina minal-faqr.",
                translation = "O Allah, Lord of the heavens and Lord of the earth and Lord of the Mighty Throne, our Lord and Lord of everything, Cleaver of the grain and date stone, Revealer of the Torah, the Gospel, and the Criterion (Quran), I seek refuge in You from the evil of everything whose forelock You hold. O Allah, You are the First, there is nothing before You; and You are the Last, there is nothing after You; and You are the Manifest, there is nothing above You; and You are the Hidden, there is nothing closer than You. Settle our debts and relieve us of poverty.",
                translationHausa = "Ya Allah, Ubangijin sammai da Ubangijin ƙasa da Ubangijin Al'arshi mai girma, Ubangijinmu kuma Ubangijin kowane abu, Mai tsaga ƙwaya da ƙwallo (don fitar da tsiro), Mai saukar da Attaura da Linjila da Alƙur'ani Mai rarrabewa. Ina neman tsari da Kai daga sharrin kowane abu wanda Kai ne Mai riƙe da makwancin gashin goshinsa (Mai cikakken iko a kansa). Ya Allah, Kai ne Na Farko ba wani abu a gabaninKa, kuma Kai ne Na Ƙarshe ba wani abu a bayanKa, Kai ne Mafi Ɗaukaka ba wani abu a samanKa, kuma Kai ne Mafi Kusa ba wani abu da Ya fi Ka kusanci. Ka biya mana basussukanmu kuma Ka wadata mu daga talauci.",
                translationYoruba = "Allāhu, Oluwa awọn sanma ati ilẹ ati Itẹ Ọla t'o tobi, san gbese wa ki O si sọ wa di ọlọrọ kuro ninu osi.",
                translationIgbo = "Chineke, Onyenwe eluigwe na ala na Ocheeze Ukwu, kwụọ ụgwọ anyị ma mee ka anyị baa ọgaranya pụọ na ịda ogbenye.",
                reference = "Sahih Muslim no. 2713; Sunan Abu Dawud no. 5051; Jami' At-Tirmidhi no. 3400."
            ),

            // 14. ADDU'AR FIRGITA A BARCI KO KASA BARCI (ID 368)
            DuaEntity(
                id = 368,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Lokacin Firgita Cikin Barci ko Jin Tsoro ko Rashin Barci",
                arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ غَضَبِهِ وَعِقَابِهِ، وَشَرِّ عِبَادِهِ، وَمِنْ هَمَزَاتِ الشَّيَاطِينِ وَأَنْ يَحْضُرُونِ",
                transliteration = "A'udhu bikalimatil-lahit-tammati min ghadabihi wa 'iqabihi, wa sharri 'ibadihi, wa min hamazatish-shayatini wa an yahdurun.",
                translation = "I seek refuge in the perfect words of Allah from His anger and His punishment, from the evil of His slaves, and from the whisperings and incitements of the devils, and from their presence near me.",
                translationHausa = "Ina neman tsari da cikakkun kalmomin Allah daga fushinSa da uƙubarSa, da sharrin bayinSa, da daga fizgar shaidanu da waswasinsu da kuma halartarsu kusa da ni.",
                translationYoruba = "Mo wa isadi pẹlu awọn ọrọ Allāhu ti o peye lọwọ ibinu Rẹ ati ijiya Rẹ, ati lọwọ aburu awọn ẹru Rẹ, ati lọwọ ifura satani.",
                translationIgbo = "Ana m achọ ebe mgbaba n'okwu zuru oke nke Chineke pụọ n'iwe Ya na ahụhụ Ya, na n'ihe ọjọọ nke ndị ohu Ya.",
                reference = "Sunan Abu Dawud no. 3893; Jami' At-Tirmidhi no. 3528; Musnad Ahmad no. 6696. Hasan."
            ),

            // 15. ADDU'AR MUMMUNAN MAFARKI (ID 369)
            DuaEntity(
                id = 369,
                category = "Sleeping & Waking Up",
                title = "Abin da Ake Fada Bayan Mummunan Mafarki (Mafarkin Firgita)",
                arabic = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ، وَمِنْ شَرِّ هَذِهِ الرُّؤْيَا (ثَلَاثَ مَرَّاتٍ مَعَ النَّفْثِ عَنْ يَسَارِهِ، وَيَتَحَوَّلُ عَنْ جَنْبِهِ، وَلَا يُحَدِّثْ بِهَا أَحَدًا)",
                transliteration = "A'udhu billahi minash-shaytanir-rajim, wa min sharri hadhihir-ru'ya (thalatha marratin ma'an-nafthi 'an yasarih).",
                translation = "I seek refuge in Allah from Satan the outcast and from the evil of this dream. (Spit lightly to the left three times, turn over to the other side, and do not tell anyone about it; then it will not harm you).",
                translationHausa = "Ina neman tsari da Allah daga Shaidan jifaffe, da kuma daga sharrin wannan mafarki.\n\nSunnah idan an yi mummunan mafarki:\n1. Tofa tofi (busa mai ɗigon yawu) a gefen hagu sau uku.\n2. Neman tsari da Allah daga Shaidan da sharrin mafarkin sau uku.\n3. Juya zuwa wani gefen na daban da aka kwanta a kansa.\n4. Kada a faɗa wa kowa wannan mafarkin; domin ba zai cutar da mutum ba.\n5. Idan mutum yana so, ya tashi ya yi alwala ya yi sallah.",
                translationYoruba = "Mo wa isadi pẹlu Allāhu lọwọ satani ati lọwọ aburu ala yi. Tọ ẹyọ itọ si apa osi ni igba mẹta, ma si sọ fun ẹnikẹni.",
                translationIgbo = "Ana m achọ ebe mgbaba n'aka Chineke pụọ n'aka Setan na n'ihe ọjọọ nke nrọ a. Gbụọ asụ n'akụkụ aka ekpe ugboro atọ.",
                reference = "Sahih Al-Bukhari no. 6984, 6986, 7044; Sahih Muslim no. 2261, 2262."
            ),

            // 16. ADDU'AR JUYAWA A GADO CIKIN DARE (ID 370)
            DuaEntity(
                id = 370,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Mai Juyawa a kan Gado Cikin Dare",
                arabic = "لَا إِلَهَ إِلَّا اللَّهُ الْوَاحِدُ الْقَهَّارُ، رَبُّ السَّمَاوَاتِ وَالْأَرْضِ وَمَا بَيْنَهُمَا الْعَزِيزُ الْغَفَّارُ",
                transliteration = "La ilaha illallahul-Wahidul-Qahhar, Rabbus-samawati wal-ardi wa ma baynahumal-'Azizul-Ghaffar.",
                translation = "There is no deity except Allah, the One, the Irresistible, Lord of the heavens and the earth and all between them, the Exalted in Might, the Perpetual Forgiver.",
                translationHausa = "Babu abin bautawa da gaskiya sai Allah, Guda Ɗaya, Mai rinjaye a kan komai, Ubangijin sammai da ƙasa da abin da ke tsakaninsu, Mabuwayi, Mai yawan gafara.",
                translationYoruba = "Ko si ọba miran afi Allāhu ti o wa Nikan, Onijakadi, Oluwa awọn sanma ati ilẹ ati ohun ti n bẹ laarin wọn.",
                translationIgbo = "Ọ dịghị onye kwesịrị ofufe ma ọ bụghị Chineke, Onye Naanị Ya, Onye Pụrụ Ime Ihe Niile, Onyenwe eluigwe na ụwa.",
                reference = "Sunan An-Nasa'i As-Sunan Al-Kubra no. 10565; Al-Mustadrak na Al-Hakim 1/540. Sahih."
            ),

            // 17. ADDU'AR WANDA YA TASHI CIKIN DARE (Ubawata / Tahajjud) (ID 371)
            DuaEntity(
                id = 371,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Wanda Ya Farka Cikin Dare (Biyan Addu'a da Karɓar Sallah)",
                arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ، رَبِّ اغْفِرْ لِي",
                transliteration = "La ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamdu, wa Huwa 'ala kulli shay'in Qadir, Subhanallahi, wal-hamdu lillahi, wa la ilaha illallahu, wallahu Akbar, wa la hawla wa la quwwata illa billahil-'Aliyyil-'Azim, Rabbigh-fir li.",
                translation = "None has the right to be worshipped except Allah alone, without partner. Unto Him belongs dominion and praise, and He has power over all things. Glory be to Allah, all praise is due to Allah, there is no god but Allah, Allah is the Greatest, and there is no might or power except with Allah the Most High, the Supreme. O Lord, forgive me! (The Prophet ﷺ said: If he asks, his prayer is answered; and if he prays, his prayer is accepted).",
                translationHausa = "Babu abin bautawa da gaskiya sai Allah Shi kaɗai, ba Shi da abokin tarayya. Mulki da dukkan godiya NaSa ne, kuma Shi a kan kowane abu Mai cikakken iko ne. Tsarki ya tabbata ga Allah, godiya ta tabbata ga Allah, babu abin bautawa sai Allah, Allah ne Mafi Girma, kuma babu dabara babu ƙarfi sai da ikon Allah Mafi ɗaukaka, Mafi girma. Ya Ubangijina Ka gafarta mini!\n\nFalala: Manzon Allah (ﷺ) ya ce: 'Wanda duk ya farka cikin dare ya faɗi wannan, sannan ya ce: Ya Allah Ka gafarta mini ko ya yi wata addu'a, za a amsa masa; idan kuma ya tashi ya yi alwala ya yi sallah, za a karɓi sallarsa.'",
                translationYoruba = "Ko si ọba miran ayafi Allāhu Ọkan ṣoṣo... Bi o ba tọrọ idariji, a o dariji i; bi o ba gbadura, a o gba adura rẹ.",
                translationIgbo = "Ọ dịghị onye ọzọ kwesịrị ofufe ma ọ bụghị Chineke... Ọ bụrụ na ọ rịọ mgbaghara, a ga-agbaghara ya; ọ bụrụkwa na o kpee ekpere, a ga-aza ya.",
                reference = "Sahih Al-Bukhari no. 1154; Sunan Abu Dawud no. 5060; Jami' At-Tirmidhi no. 3414."
            ),

            // 18. ADDU'AR TASHI DAGA BARCI TA FARKO (ID 372)
            DuaEntity(
                id = 372,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Tashi daga Barci (Alhamdu Lillahilladhi Ahyana)",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
                transliteration = "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur.",
                translation = "Praise is to Allah Who gave us life after having taken it from us and unto Him is the resurrection.",
                translationHausa = "Dukkan godiya ta tabbata ga Allah Wanda Ya raya mu bayan Ya kashe mu (Ya ɗauki ranmu a lokacin barci), kuma zuwa gare Shi ne tashi bayan mutuwa.",
                translationYoruba = "Ọpẹ ni fun Allāhu ti o sọ wa di alaaye lẹhin ti o ti mu wa ku, ati sọdọ Rẹ ni ajinde yoo jẹ.",
                translationIgbo = "Otuto dịrị Chineke Onye mere ka anyị dị ndụ mgbe O mechara ka anyị nwụọ, ma n'ebe Ọ nọ ka mbilite n'ọnwụ dị.",
                reference = "Sahih Al-Bukhari no. 6312, 6324; Sahih Muslim no. 2711."
            ),

            // 19. ADDU'AR TASHI DAGA BARCI TA BIYU (ID 373)
            DuaEntity(
                id = 373,
                category = "Sleeping & Waking Up",
                title = "Addu'ar Tashi daga Barci (Lafiyar Jiki da Mayar da Rai)",
                arabic = "الْحَمْدُ لِلَّهِ الَّذِي عَافَانِي فِي جَسَدِي، وَرَدَّ عَلَيَّ رُوحِي، وَأَذِنَ لِي بِذِكْرِهِ",
                transliteration = "Alhamdu lillahil-ladhi 'afani fi jasadi, wa radda 'alayya ruhi, wa adhina li bidhikrih.",
                translation = "Praise is to Allah Who gave health and vitality to my body, and returned my soul to me, and permitted me to remember Him.",
                translationHausa = "Dukkan godiya ta tabbata ga Allah Wanda Ya ba wa jikina lafiya da ƙarfi, Ya mayar mini da raina, kuma Ya ba ni izinin ambatonSa.",
                translationYoruba = "Ọpẹ ni fun Allāhu ti o fun ara mi ni ilera, ti o si da ẹmi mi pada fun mi, ti o si gba mi laaye lati ranti Rẹ.",
                translationIgbo = "Otuto dịrị Chineke Onye nyere ahụ m ahụike na ike, weghachiri mkpụrụ obi m n'ime m, ma nye m ikike icheta aha Ya.",
                reference = "Jami' At-Tirmidhi no. 3401; Ibn As-Sunni no. 9. Hasan."
            )
        )
    }
}
