package com.example.data.local

object AnsweredDuasData {

    fun getAnsweredDuas(): List<DuaEntity> {
        return listOf(
            // 1. Ismul A'zam (Hadith Buraydah)
            DuaEntity(
                id = 345,
                category = "Addu'o'i na Ijaba",
                title = "Ismul A'zam: Babban Sunan Allah da Ake Amsa Addu'a Nan Take",
                arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ بِأَنِّي أَشْهَدُ أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ، الأَحَدُ الصَّمَدُ، الَّذِي لَمْ يَلِدْ وَلَمْ يُولَدْ، وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ",
                transliteration = "Allahumma inni as'aluka bi-anni ashhadu annaka Antallahu la ilaha illa Anta, Al-Ahadus-Samad, alladhi lam yalid wa lam yulad, wa lam yakun lahu kufuwan ahad.",
                translation = "O Allah, I ask You by virtue of my bearing witness that You are Allah, there is none worthy of worship except You, the One, the Self-Sufficient Master, Who neither begets nor was begotten, and unto Whom there is none equal.",
                translationHausa = "Ya Allah, ina roƙonKa domin ina shaida cewa lallai Kai ne Allah, babu abin bautawa da gaskiya sai Kai, Makaɗaici, Wanda ake nufa da buƙatu, Wanda bai haifa ba kuma ba a haife Shi ba, kuma babu wani da ya zama kishiya a gare Shi ko guda ɗaya.",
                translationYoruba = "Allāhu, mo n bẹ Ọ nitori mo jẹri pe Iwọ ni Allāhu, ko si ọba miran ayafi Iwọ, Ọkan ṣoṣo, Olugbẹkẹle ti ko bi ọmọ ti a ko si bi, ti ko si ni orogun kankan.",
                translationIgbo = "Chineke, ana m arịọ Gị n'ihi na ana m agba akaebe na Gị bụ Chineke, ọ dịghị onye ọzọ kwesịrị ofufe ma ọ bụghị Gị, Onye Naanị Ya, Onye A Na-adabere na Ya.",
                reference = "Annabi (ﷺ) ya ji wani mutum yana wannan addu'ar, sai ya ce: 'Lallai ya roƙi Allah da Babban SunanSa (Ismul A'zam), wanda idan aka roƙe Shi da shi Yana bayarwa, idan aka yi addu'a da shi Yana amsawa.' (Abu Dawud no. 1493, At-Tirmidhi no. 3475, Sahih)."
            ),

            // 2. Ismul A'zam na Biyu (Hadith Anas)
            DuaEntity(
                id = 346,
                category = "Addu'o'i na Ijaba",
                title = "Ismul A'zam na Biyu: Neman Biyan Buƙata da Girman Allah",
                arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ بِأَنَّ لَكَ الْحَمْدَ، لَا إِلَهَ إِلَّا أَنْتَ الْمَنَّانُ، بَدِيعُ السَّمَاوَاتِ وَالأَرْضِ، يَا ذَا الْجَلَالِ وَالإِكْرَامِ، يَا حَيُّ يَا قَيُّومُ",
                transliteration = "Allahumma inni as'aluka bi-anna lakal-hamd, la ilaha illa Anta Al-Mannan, Badi'us-samawati wal-ard, Ya Dhal-Jalali wal-Ikram, Ya Hayyu Ya Qayyum.",
                translation = "O Allah, I ask You as all praise is due to You, there is none worthy of worship except You, the Bestower of all blessings, Originator of the heavens and earth, O Possessor of Majesty and Honor, O Ever-Living, O Sustainer.",
                translationHausa = "Ya Allah, lallai ina roƙonKa domin dukkan godiya ta tabbata a gare Ka, babu abin bautawa da gaskiya sai Kai, Mai yawan kyauta da alheri, Mai ƙaga halittar sammai da ƙasa, Ya Ma'abucin Girma da Karramawa, Ya Mai Rai, Ya Tsayayye.",
                translationYoruba = "Allāhu, mo n tọrọ lọwọ Rẹ pe tiRẹ ni gbogbo ọpẹ, ko si ọlọhun ayafi Iwọ, Olore pupọ, Oludasilẹ awọn ọrun ati aiye, Iwọ Oloogo ati Ọla, Iwọ Alaye, Oluduro.",
                translationIgbo = "Chineke, ana m arịọ Gị n'ihi na otuto niile bụ nke Gị, ọ dịghị onye ọzọ ma ọ bụghị Gị, Onye Na-enye amara, Onye Kere eluigwe na ụwa, Onye Nwere Ebube na Nsọpụrụ.",
                reference = "Manzon Allah (ﷺ) ya ce: 'Ya roƙi Allah da Babban SunanSa wanda idan aka kira Shi da shi Yana amsawa, idan kuma aka roƙe Shi da shi Yana bayarwa.' (Sunan Abu Dawud no. 1495, An-Nasa'i no. 1300, Sahih)."
            ),

            // 3. Addu'ar Annabi Yunus (AS) a Cikin Kifi (Dua Dhun-Nun)
            DuaEntity(
                id = 347,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Annabi Yunus (AS) a Cikin Kifi: Warware Kowace Irin Matsala",
                arabic = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
                transliteration = "La ilaha illa Anta subhanaka inni kuntu minaz-zalimin.",
                translation = "There is none worthy of worship except You; glory be to You, truly I have been among the wrongdoers.",
                translationHausa = "Babu abin bautawa da gaskiya sai Kai, tsarki ya tabbata a gare Ka, lallai na kasance daga cikin azzalumai (masu kuskure).",
                translationYoruba = "Ko si ọba miran ayafi Iwọ, Mimo ni fun Ọ, dajudaju mo wa lara awọn alabosi.",
                translationIgbo = "Ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị; otuto dịrị Gị, n'ezie anọ m n'etiti ndị mmehie.",
                reference = "Manzon Allah (ﷺ) ya ce: 'Addu'ar Zun-Nun (Yunus) lokacin da yake cikin cikin kifi: babu wani musulmi da zai roƙi Allah da ita a kan kowace irin buƙata face Allah Ya amsa masa.' (Jami' At-Tirmidhi no. 3505, Sahih)."
            ),

            // 4. Addu'ar Kunci da Tsanani (Dua'ul Karb)
            DuaEntity(
                id = 348,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Kunci da Tsanani (Dua'ul Karb) Mai Yaye Damuwa",
                arabic = "لَا إِلَهَ إِلَّا اللَّهُ الْعَظِيمُ الْحَلِيمُ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ الْعَرْشِ الْعَظِيمِ، لَا إِلَهَ إِلَّا اللَّهُ رَبُّ السَّمَاوَاتِ وَرَبُّ الْأَرْضِ وَرَبُّ الْعَرْشِ الْكَرِيمِ",
                transliteration = "La ilaha illallahul-Azimul-Halim, la ilaha illallahu Rabbul-'Arshil-'Azim, la ilaha illallahu Rabbus-samawati wa Rabbul-ardi wa Rabbul-'Arshil-Karim.",
                translation = "There is no god except Allah, the Magnificent, the Forbearing. There is no god except Allah, Lord of the Mighty Throne. There is no god except Allah, Lord of the heavens, Lord of the earth, and Lord of the Noble Throne.",
                translationHausa = "Babu abin bautawa da gaskiya sai Allah Mai Girma, Mai Haƙuri da Rangwame. Babu abin bautawa da gaskiya sai Allah Ubangijin Al'arshi Mai Girma. Babu abin bautawa da gaskiya sai Allah Ubangijin sammai da Ubangijin ƙasa da Ubangijin Al'arshi Mai Daraja.",
                translationYoruba = "Ko si ọlọhun afi Allāhu Alagbara, Alafarada. Ko si ọlọhun afi Allāhu Oluwa Itẹ Ọla Alaponle.",
                translationIgbo = "Ọ dịghị chi ọzọ ma ọ bụghị Chineke Onye Ukwu, Onye Na-enwe ndidi. Ọ dịghị chi ọzọ ma ọ bụghị Onyenwe Ocheeze Ukwu.",
                reference = "Manzon Allah (ﷺ) ya kasance yana karanta wannan addu'a yayin tsananin damuwa, kunci, ko buƙata ta musamman domin samun budi da amsar addu'a. (Sahih Al-Bukhari no. 6346, Sahih Muslim no. 2730)."
            ),

            // 5. Addu'ar Wanda Ya Farka Cikin Dare (Dua 'Ubada bin As-Samit)
            DuaEntity(
                id = 349,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Wanda Ya Farka Cikin Dare: Tabbacin Amsa Addu'a",
                arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ، اللَّهُمَّ اغْفِرْ لِي",
                transliteration = "La ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu, wa Huwa 'ala kulli shay'in Qadeer. Subhanallahi, wal-hamdu lillahi, wa la ilaha illallahu, wallahu Akbar, wa la hawla wa la quwwata illa billah. Allahummagh-fir li.",
                translation = "None has the right to be worshipped except Allah alone, without partner. To Him belongs all sovereignty and praise, and He is over all things capable. Glory is to Allah, praise is to Allah, none has the right to be worshipped except Allah, Allah is the greatest, and there is no might or power except with Allah. O Allah, forgive me.",
                translationHausa = "Babu abin bautawa da gaskiya sai Allah Shi kaɗai, ba Shi da abokin tarayya. Mulki da godiya NaSa ne, kuma Shi Mai ikon yi ne a kan komai. Tsarki ya tabbata ga Allah, godiya ta tabbata ga Allah, babu abin bautawa sai Allah, Allah ne Mafi Girma, babu dabara babu ƙarfi sai da taimakon Allah. Ya Allah Ka gafarta mini.",
                translationYoruba = "Ko si ọba miran ayafi Allāhu nikan lai si orogun; tiRẹ ni gbogbo ijọba ati ọpẹ. Allāhu dariji mi.",
                translationIgbo = "Ọ dịghị onye kwesịrị ofufe ma ọ bụghị Chineke naanị Ya. Chineke, biko gbaghara m.",
                reference = "Manzon Allah (ﷺ) ya ce: 'Wanda ya farka cikin dare ya faɗi wannan addu'ar, sannan ya ce: Allahumma-ghfir li (Ya Allah Ka gafarta mini) ko ya roƙi wata buƙata, za a amsa masa; idan ya yi alwala ya yi sallah, za a karɓi sallarsa.' (Sahih Al-Bukhari no. 1154)."
            ),

            // 6. Addu'ar Neman Agaji Cikin Gaggawa (Ya Hayyu Ya Qayyum)
            DuaEntity(
                id = 350,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Neman Agaji Cikin Gaggawa (Ya Hayyu Ya Qayyum)",
                arabic = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
                transliteration = "Ya Hayyu Ya Qayyum, bi-rahmatika astagheeth, aslih li sha'ni kullahu, wa la takilni ila nafsi tarfata 'ayn.",
                translation = "O Ever-Living One, O Sustainer, by Your mercy I seek help. Rectify for me all of my affairs, and do not leave me to myself even for the blink of an eye.",
                translationHausa = "Ya Mai Rai, Ya Tsayayye, da rahamarKa nake neman agaji, Ka gyara mini al'amurana baki ɗaya, kuma kada Ka bar ni da kaina ko da na ƙiftawar ido guda ne.",
                translationYoruba = "Iwọ Alaye, Olugbe gbogbo nkan duro, pẹlu aanu Rẹ ni mo n wa iranlọwọ. Tun gbogbo ọrọ mi ṣe fun mi.",
                translationIgbo = "Onye Dị Ndụ, Onye Na-elekọta ihe niile, site n'ebere Gị ka m na-achọ enyemaka. Mezie ihe niile gbasara m.",
                reference = "Mustadrak Al-Hakim 1/545, Sahih At-Targhib wat-Tarhib no. 661. Addu'a ce mai ƙarfi da Manzon Allah (ﷺ) ya umurci Fatimah (RA) ta lizimce ta."
            ),

            // 7. Sayyidul Istighfar
            DuaEntity(
                id = 351,
                category = "Addu'o'i na Ijaba",
                title = "Sayyidul Istighfar: Shugaban Neman Gafara Mai Bude Kofofin Alheri",
                arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika ma-stata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfirudh-dhunuba illa Anta.",
                translation = "O Allah, You are my Lord, there is none worthy of worship but You. You created me and I am Your slave. I keep Your covenant and my pledge to You as much as I am able. I seek refuge in You from the evil of what I have done. I admit to Your grace upon me and I admit to my sin. So forgive me, for none forgives sins but You.",
                translationHausa = "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halitta ni kuma ni bawanKa ne. Ina kan alkawarinKa da wa'adinKa gwargwadon ikona. Ina neman tsari da Kai daga sharrin abin da na aikata. Ina amsa muku ni'imarKa a kaina, kuma ina amsa zunubina. Don haka Ka gafarta mini, domin babu mai gafarta zunubai sai Kai.",
                translationYoruba = "Allāhu n bẹ, Iwọ ni Ọlọrun mi, ko si ọba miran ayafi Iwọ. Dariji mi nitori ko si ẹniti o le dari ẹṣẹ ji ayafi Iwọ.",
                translationIgbo = "Chineke, Gị bụ Onyenwe m, ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị. Ya mere gbaghara m mmehie m.",
                reference = "Manzon Allah (ﷺ) ya ce: 'Duk wanda ya karanta ta da yakini da safe ko yamma, ya rasu a wannan yinin ko daren, zai shiga Aljanna.' (Sahih Al-Bukhari no. 6306)."
            ),

            // 8. Addu'ar Neman Biyan Bashi da Damuwa (Abu Umamah)
            DuaEntity(
                id = 352,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Biyan Bashi da Yayewar Baƙin Ciki (Dua Abu Umamah)",
                arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
                transliteration = "Allahumma inni a'udhu bika minal-hammi wal-hazan, wa a'udhu bika minal-'ajzi wal-kasal, wa a'udhu bika minal-jubni wal-bukhl, wa a'udhu bika min ghalabatid-dayni wa qahrir-rijal.",
                translation = "O Allah, I seek refuge in You from anxiety and grief, from weakness and laziness, from cowardice and greed, and from the burden of debt and the oppression of men.",
                translationHausa = "Ya Allah, lallai ina neman tsari da Kai daga damuwa da baƙin ciki, da gazawa da kasala, da tsoro da rowa, da rinjayar bashi da danniyar mazaje (mutane).",
                translationYoruba = "Allāhu, mo wa aabo Rẹ kuro ninu aniyan ati ibanujẹ, ailera ati ọlẹ, ibẹru ati ahun, ati rirun gbese ati ifipajẹ awọn eniyan.",
                translationIgbo = "Chineke, ana m achọ mgbaba n'ime Gị site na nchegbu na mwute, adịghị ike na umengwu, na ibu arọ nke ụgwọ.",
                reference = "Abu Umamah (RA) ya ce: Na karanta wannan addu'ar da Annabi (ﷺ) ya koya mini, sai Allah Ya tafiyar da dukkan damuwata kuma Ya sauƙaƙa biyan bashina. (Sunan Abu Dawud no. 1555)."
            ),

            // 9. Addu'ar Sauƙaƙa Al'amura Masu Wuya
            DuaEntity(
                id = 353,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Sauƙaƙa Al'amura Masu Tsanani da Wuya",
                arabic = "اللَّهُمَّ لَا سَهْلَ إِلَّا مَا جَعَلْتَهُ سَهْلًا، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلًا",
                transliteration = "Allahumma la sahla illa ma ja'altahu sahla, wa Anta taj'alul-hazna idha shi'ta sahla.",
                translation = "O Allah, there is nothing made easy except what You make easy, and You can make sorrow and difficulty easy if You will.",
                translationHausa = "Ya Allah, babu wani abu mai sauƙi face abin da Ka sanya shi ya zama mai sauƙi, kuma Kai ne Kake sanya matsala ko tsanani ya zama mai sauƙi idan Ka so.",
                translationYoruba = "Allāhu, kò sí nǹkan tí ó rọrùn àfi èyí tí Ìwọ bá mú rọrùn, Ìwọ sì ń sọ ìṣòro di ìrọ̀rùn tí O bá fẹ́.",
                translationIgbo = "Chineke, ọ dịghị ihe dị mfe ma ọ bụghị ihe Ị mere ka ọ dị mfe, Ị pụkwara ime ka ihe isi ike dị mfe ma Ị chọọ.",
                reference = "Sahih Ibn Hibban no. 974, Al-Adhkar na Imam An-Nawawi."
            ),

            // 10. Addu'ar Tsarkake Zuciya da Dorewa Kan Addini
            DuaEntity(
                id = 354,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Tsarkake Zuciya da Tsayawa Kan Gaskiya (Mafi Yawan Addu'ar Annabi)",
                arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
                transliteration = "Ya Muqallibal-quloob, thabbit qalbi 'ala deenik.",
                translation = "O Turner of the hearts, make my heart firm upon Your religion.",
                translationHausa = "Ya Mai jujjuyar da zukata, Ka tabbatar da zuciyata a kan addininKa.",
                translationYoruba = "Ìwọ Olùyí ọkàn padà, fi ọkàn mi lélẹ̀ gbọn-in lórí ẹ̀sìn Rẹ.",
                translationIgbo = "Onye Na-atụgharị obi mmadụ, mee ka obi m guzosie ike n'okpukpe Gị.",
                reference = "Ummul Mu'minina Aisha (RA) ta ce wannan ita ce addu'ar da Manzon Allah (ﷺ) ya fi yawaitawa a rayuwarsa. (Jami' At-Tirmidhi no. 2140, Sahih)."
            ),

            // 11. Addu'ar Waraka daga Cuta (Annabi Ayyub AS)
            DuaEntity(
                id = 355,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Neman Cikakkiyar Waraka da Lafiya (Addu'ar Annabi Ayyub AS)",
                arabic = "رَبِّي أَنِّي مَسَّنِيَ الضُّرُّ وَأَنْتَ أَرْحَمُ الرَّاحِمِينَ",
                transliteration = "Rabbi innee massaniyad-durru wa Anta Arhamur-Rahimeen.",
                translation = "My Lord, indeed adversity has touched me, and You are the Most Merciful of the merciful.",
                translationHausa = "Ubangijina, lallai cuta da tsanani sun shafe ni, kuma Kai ne Mafi rahamar masu rahama.",
                translationYoruba = "Oluwa mi, dajudaju inira ti kan mi, Iwọ si ni Alaaanu julọ ninu awọn alaaanu.",
                translationIgbo = "Onyenwe m, ahụhụ erutela m, Gị bụkwa Onye Kacha Enwe Ebere n'etiti ndị niile na-enwe ebere.",
                reference = "Surah Al-Anbiya 21:83. Allah Ya amsa wa Annabi Ayyub (AS) Ya warkar da shi daga kowace cuta."
            ),

            // 12. Addu'ar Neman Zuriya Tagari (Annabi Zakariyya AS)
            DuaEntity(
                id = 356,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Neman Zuriya Tagari da Samun Ƴaƴa Masu Albarka (Annabi Zakariyya AS)",
                arabic = "رَبِّ هَبْ لِي مِن لَّدُنكَ ذُرِّيَّةً طَيِّبَةً ۖ إِنَّكَ سَمِيعُ الدُّعَاءِ",
                transliteration = "Rabbi hab lee min ladunka dhurriyyatan tayyibatan, innaka Samee'ud-Du'a.",
                translation = "My Lord, grant me from Yourself a good offspring. Indeed, You are the Hearer of supplication.",
                translationHausa = "Ubangijina, Ka ba ni zuriya tagari mai albarka daga gare Ka, lallai Kai ne Mai jin addu'a.",
                translationYoruba = "Oluwa mi, fi irú-ọmọ rere jinkí mi láti ọ̀dọ̀ Rẹ, dájúdájú Ìwọ ni Olùgbọ́ àdúà.",
                translationIgbo = "Onyenwe m, nye m ezi ụmụ sitere n'ebe Ị nọ, n'ezie Ị bụ Onye Na-anụ ekpere.",
                reference = "Surah Ali 'Imran 3:38. Allah Ya amsa wa Annabi Zakariyya (AS) Ya azurta shi da Annabi Yahya (AS)."
            ),

            // 13. Hasbunallahu wa Ni'mal Wakeel
            DuaEntity(
                id = 357,
                category = "Addu'o'i na Ijaba",
                title = "Hasbunallahu wa Ni'mal Wakeel: Isarwar Allah Yayin Tsoro da Makiya",
                arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
                transliteration = "Hasbunallahu wa ni'mal-Wakeel.",
                translation = "Allah is sufficient for us, and He is the best Disposer of affairs.",
                translationHausa = "Allah Ya isar mana, kuma Madalla da Shi abin dogaro.",
                translationYoruba = "Allāhu ti to fun wa, Oun si ni Olugbẹkẹle ti o dara julọ.",
                translationIgbo = "Chineke ezuru anyị, Ọ bụkwa Onye Kacha Mma A Na-adabere na Ya.",
                reference = "Surah Ali 'Imran 3:173, Sahih Al-Bukhari no. 4563. Annabi Ibrahim (AS) ya fade ta yayin da aka jefa shi a wuta, Manzon Allah (ﷺ) kuma ya fade ta lokacin da aka tsorata musulmai."
            ),

            // 14. Addu'ar Neman Ilmi, Arziki da Aiki Karbabbe
            DuaEntity(
                id = 358,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Neman Ilmi Mai Amfani, Arziki Mai Albarka da Karɓar Aiki",
                arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
                transliteration = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbalan.",
                translation = "O Allah, I ask You for knowledge that is of benefit, a good and lawful provision, and deeds that will be accepted.",
                translationHausa = "Ya Allah, lallai ina roƙonKa ilmi mai amfani, da arziki mai daɗi (halal), da kuma aiki karɓaɓɓe a wurinKa.",
                translationYoruba = "Allāhu, mo n tọrọ lọwọ Rẹ ìmọ̀ tí ó ní àǹfààní, ìpèsè tí ó mọ́, ati iṣẹ́ tí a tẹ́wọ́gbà.",
                translationIgbo = "Chineke, ana m arịọ Gị maka ihe ọmụma bara uru, nri dị mma, na ọrụ a nabatara.",
                reference = "Sunan Ibn Majah no. 925, Sahih. Annabi (ﷺ) ya kasance yana karanta ta kowace safiya bayan sallar Asuba."
            ),

            // 15. Addu'ar Neman Kariya Daga Makirci da Makiya
            DuaEntity(
                id = 359,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Neman Kariya da Samun Nasara a Kan Maƙiya",
                arabic = "اللَّهُمَّ إِنَّا نَجْعَلُكَ فِي نُحُورِهِمْ، وَنَعُوذُ بِكَ مِنْ شُرُورِهِمْ",
                transliteration = "Allahumma inna naj'aluka fee nuhoorihim, wa na'oodhu bika min shuroorihim.",
                translation = "O Allah, we place You in front of them (to defend us), and we seek refuge in You from their evil.",
                translationHausa = "Ya Allah, lallai muna sanya Ka a fuskarsu (don Ka kare mu), kuma muna neman tsari da Kai daga sharrinsu.",
                translationYoruba = "Allāhu, a fi Ọ siwaju wọn lati dabobo wa, a si wa aabo Rẹ kuro ninu aburu wọn.",
                translationIgbo = "Chineke, anyị na-etinye Gị n'ihu ha ka Ị chebe anyị, anyị na-achọkwa mgbaba n'ime Gị site n'ihe ọjọọ ha.",
                reference = "Sunan Abu Dawud no. 1537, Sahih. Manzon Allah (ﷺ) ya kasance yana karanta wannan addu'ar yayin fuskantar tsoron maƙiya."
            ),

            // 16. Addu'ar Kyautata Addini, Rayuwar Duniya da Lahira (Sahih Muslim 2720)
            DuaEntity(
                id = 380,
                category = "Addu'o'i na Ijaba",
                title = "Addu'ar Kyautata Addini, Duniyarmu da Lahira (Sahih Muslim)",
                arabic = "اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي ، وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي ، وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي ، وَاجْعَلِ الْحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ وَاجْعَلِ الْمَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ",
                transliteration = "Allahumma aslih li deenil-ladhi huwa 'ismatu amri, wa aslih li dunya yal-lati fiha ma'ashi, wa aslih li aakhiratillati fiha ma'adi, waj-'alil-hayata ziyadatan li fi kulli khayr, waj-'alil mawta rahatan li min kulli sharr.",
                translation = "O Allah, set right my religion, which is the safeguard of my affairs; and set right my world, wherein is my living; and set right my next life, to which is my return, And make life for me an increase in all good and make death a relief for me from every evil.",
                translationHausa = "Ya Allah! Ka kyautata mini addinina wanda shi ne kariya ga al'amarina, kuma Ka kyautata mini duniyata wadda a cikinta ne rayuwata take, kuma Ka kyautata mini lahirata wadda zuwa gare ta ne makomata take, kuma Ka sanya rayuwa ta zama ƙari a gare ni a cikin kowane alheri, kuma Ka sanya mutuwa ta zama hutu a gare ni daga dukkan sharri.",
                translationYoruba = "Allāhu, ṣe àtúnṣe ẹ̀sìn mi tí ó jẹ́ ààbò fún gbogbo ọ̀rọ̀ mi; ṣe àtúnṣe ayé mi tí ìgbé ayé mi wà nínú rẹ̀; ṣe àtúnṣe ọ̀run mi tí ó jẹ́ ibi àbọ̀ mi; kí O sì ṣe ẹ̀mí mi kí ó jẹ́ àlékún nínú gbogbo ohun rere, kí O sì ṣe ikú ní ìsinmi fún mi kúrò nínú gbogbo ibi.",
                translationIgbo = "Chineke, mezie okpukpe m nke bụ nchekwa nke ihe niile m; mezie ụwa m ebe ibi ndụ m dị; mezie ndụ m nke ọzọ ebe nlaghachi m dị; mezie ka ndụ baa ụba n'ime ihe ọma niile, meekwa ka ọnwụ bụrụ ahụ efe nye m site n'ihe ọjọọ niile.",
                reference = "Abu Hurairah (RA) ya ruwaito cewa Manzon Allah (ﷺ) ya kasance yana faɗin wannan addu'ar. [Sahih Muslim no. 2720]."
            )
        )
    }
}
