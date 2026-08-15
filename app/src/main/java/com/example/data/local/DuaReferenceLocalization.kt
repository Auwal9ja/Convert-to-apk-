package com.example.data.local

object DuaReferenceLocalization {
    fun getLocalizedReference(duaId: Int, language: String): String? {
        return when (language) {
            "Hausa" -> getHausaReference(duaId)
            "Yoruba" -> getYorubaReference(duaId)
            "Igbo" -> getIgboReference(duaId)
            "Arabic" -> getArabicReference(duaId)
            "French" -> getFrenchReference(duaId)
            "Spanish" -> getSpanishReference(duaId)
            "Urdu" -> getUrduReference(duaId)
            "Chinese" -> getChineseReference(duaId)
            else -> null
        }
    }

    private fun getHausaReference(id: Int): String? {
        return when (id) {
            1 -> "Falalar ambaton Allah (Zikiri) tana da girma kwarai a Musulunci:\n1. Allah Yana ambaton bawan da ke ambatonsa ('Ku ambace Ni Zan ambace ku' - Al-Baqarah 2:152).\n2. Zikiri yana sanya natsuwar zuciya da samun salama ('Lallai da ambaton Allah zuciyoyi ke natsuwa' - Ar-Ra'd 13:28).\n3. Annabi (SAW) ya kwatanta mai zikiri da mai rai, wanda ba ya yi kuma da matacce (Sahih Al-Bukhari).\n4. Yana goge zunubai, yana korar Shaidan, kuma yana kawo kariya da albarka a rayuwa.\nMadogara: Surah Al-Baqarah 2:152, Surah Al-Ahzab 33:41-42, Sahih Al-Bukhari 11/208, Sahih Muslim 1/539. Hisnul Muslim Babi na 1."
            41 -> "Karanta wannan idan an tashi daga barci yana nuna godiya mai girma ga rayuwar jiki da ta ruhi, tare da amincewa cewa Allah Shi kadai ke bayar da rai bayan mutuwa kuma zuwa gare Shi ne makoma ta karshe.\nMadogara: Al-Bukhari, duba Al-Asqalani, Fathul-Bari 11/113, Muslim 4/2083."
            2 -> "Wanda duk ya fadi wannan da rana yana mai yakini da shi, kuma ya mutu a ranar kafin yamma, to yana daya daga cikin 'yan Aljanna; idan kuma ya fadi haka da daddare yana mai yakini da shi, ya kuma mutu kafin gari ya waye, to yana daya daga cikin 'yan Aljanna.\nMadogara: Al-Bukhari 7/150."
            3 -> "Wanda duk ya karanta wannan sau uku da safe da kuma sau uku da yamma, babu abin da zai cutar da shi a ranar ko daren, yana zama garkuwa mai karfi daga dukkan fitinu da cutarwa na farat daya.\nMadogara: Abu Dawud 4/323, At-Tirmidhi 5/465."
            4 -> "Wanda duk ya karanta wannan sau uku da safe da kuma sau uku da yamma, ya zama wajibi a kan Allah ya yardar da shi a Ranar Kiyama, ya cika zuciyarsa da gamsuwa da aminci.\nMadogara: Abu Dawud 4/318, At-Tirmidhi 5/465."
            5 -> "Ana karantawa kafin barci domin mika rai gaba daya ga Allah lokacin barci (wanda a cikin Alkur'ani ake kiransa da karamar mutuwa) da kuma neman kariya ta Ubangiji a cikin dare.\nMadogara: Al-Bukhari (Fathul-Bari 11/113), Muslim 4/2083."
            6 -> "Wannan addu'ar tana nuna murna ga lafiyar jiki, farfadowar ruhi, da kuma babbar ni'ima ta samun izinin ambaton Allah da bauta masa bayan bude idanu a sabuwar rana.\nMadogara: At-Tirmidhi 5/473."
            7 -> "Wanda duk ya karanta wannan lokacin shiga masallaci, Shaidan zai ce: 'An kare shi daga gare ni sauran ranar,' yana tabbatar da kariyar ruhi yayin da yake cikin gidan Allah.\nMadogara: Abu Dawud 1/127, Al-Albani a Sahih Abu Dawud."
            8 -> "Ana karantawa yayin fita daga masallaci domin neman albarkar Allah, arziki, lafiya, da abinci na halal yayin komawa ga harkokin duniya da ayyuka.\nMadogara: Muslim 1/494."
            10 -> "Ambatar sunan Allah kafin cin abinci yana hana Shaidan rabawa ko dauke albarka da abinci na abincinku, yana tabbatar da cewa abincin yana kawo matsakaicin lafiya da lada.\nMadogara: Abu Dawud 3/347, At-Tirmidhi 4/288."
            11 -> "Idan mutum ya manta ambatar sunan Allah a farko, to karanta wannan lokacin da ya tuna zai katse kasancewar Shaidan nan take kuma ya mayar da albarkar abincin.\nMadogara: Abu Dawud 3/347, At-Tirmidhi 4/288."
            12 -> "Annabi (tsira da amincin Allah su tabbata a gare shi) ya ce: 'Wanda duk ya ci abinci ya fadi wannan a karshensa, za a gafarta masa dukkan zunubansa na baya,' yana jaddada darajar godiya ta kaskantar da kai.\nMadogara: At-Tirmidhi, Abu Dawud, Ibn Majah."
            13 -> "Idan mutum ya fadi wannan lokacin fita daga gida, ana ce masa: 'An shiryar da kai, an tsare ka, kuma an kiyaye ka,' kuma shaidanu suna nisanta da shi, suna kiyaye shi a tafiyarsa.\nMadogara: Abu Dawud 4/325, At-Tirmidhi 5/490."
            14 -> "Karantawa lokacin shiga gida yana tabbatar da cewa aminci, albarka, da kariya ta mala'iku suna tare da ku, kuma Shaidan ya gane ba shi da wurin zama ko abinci a daren.\nMadogara: Abu Dawud 4/325."
            15 -> "Ana karantawa yayin hawa abin hawa don tafiya. Yana kaskantar da matafiyi ta hanyar tabbatar da cewa dukkan ababen hawa sun hore mana ne kawai da falalar Allah, kuma yana tuna mana tafiyarmu ta karshe zuwa gare Shi.\nMadogara: Muslim 2/978."
            16 -> "Annabi Muhammad (tsira da amincin Allah su tabbata a gare shi) ya kasance yana yawaita karanta wannan addu'ar, domin tana duba sauki daga damuwar hankali, rashin aiki na jiki, nauyin kudi, da zaluncin mutane.\nMadogara: Al-Bukhari 7/158."
            17 -> "Da aka sani da 'Addu'ar Damuwa'. Annabi (tsira da amincin Allah su tabbata a gare shi) ya kasance yana maimaita wadannan kalmomi lokacin tsananin wahala ko gaggawa don neman rahamar Allah da kwantar da hankalinsa.\nMadogara: Al-Bukhari 8/154, Muslim 4/2092."
            18 -> "Yana daga cikin Sunnar Annabi mai tabbata neman gafarar Allah sau uku nan take bayan kammala kowace sallah ta farilla don cike duk wani rashin natsuwa ko tawaya lokacin bautar.\nMadogara: Muslim 1/414."
            19 -> "Annabi (tsira da amincin Allah su tabbata a gare shi) ya ce: 'Wanda duk ya karanta wannan bayan kowace sallah, za a gafarta masa zunubansa koda sun kai kumfan teku yawa.'\nMadogara: Muslim 1/418."
            20 -> "Atishawa ni'ima ce ta jiki daga Allah. Karanta yabo, mayar da martani ga dan uwa da addu'ar rahama, da bayar da shiriya shine kyakkyawan tsari da ke gina soyayya ta zamantakewa da 'yan uwantaka.\nMadogara: Al-Bukhari 7/125."
            21 -> "Ana karantawa don neman kariya ta Ubangiji daga kazantattun ruhohi da ke cikin bandaki.\nMadogara: Al-Bukhari 1/45, Muslim 1/283."
            22 -> "Ana karantawa yayin fita don gode wa Allah da Ya sauƙaƙa nauyin jiki da kuma neman tsarkakar ruhi.\nMadogara: Abu Dawud, At-Tirmidhi, Ibn Majah."
            23 -> "Fadin wannan yana da kyau kwarai kafin yin alwala.\nMadogara: Abu Dawud, Ibn Majah, Ahmad."
            24 -> "Ana bude kofofin Aljanna guda takwas ga duk wanda ya karanta wannan bayan ya kammala alwala.\nMadogara: Muslim 1/209, At-Tirmidhi 1/78."
            25 -> "Ana karantawa don murkushe fushi na farat daya da mayar da kwanciyar hankali.\nMadogara: Al-Bukhari 7/99, Muslim 4/2015."
            26 -> "Ana fada yayin ziyartar mara lafiya don kawo masa sanyin gwiwa da bege.\nMadogara: Al-Bukhari, duba Al-Asqalani, Fathul-Bari 10/118."
            27 -> "Addu'a mai zurfi ta neman rahama da shiga kabari cikin girma.\nMadogara: Muslim 2/663."
            28 -> "Annabi (tsira da amincin Allah su tabbata a gare shi) ya koyar da wannan don yanke shawara a kan kowane lamari mai muhimmanci.\nMadogara: Al-Bukhari 7/162."
            29 -> "Karanta wannan lokacin da masifa ta faru yana kawo kwanciyar hankali, lada, da kuma musanyawa mafi alheri.\nMadogara: Muslim 2/632."
            30 -> "Annabi (tsira da amincin Allah su tabbata a gare shi) ya kasance yana karanta wannan addu'ar fiye da sau 100 a kullum.\nMadogara: Al-Bukhari, Muslim 4/2075."
            31 -> "Kyakkyawar addu'ar neman sauki wajen biyan bashi da samun wadatar kudi.\nMadogara: At-Tirmidhi 5/560."
            32 -> "Ana fada lokacin da ake ruwan sama don rokon Allah albarka da amfani maimakon barna.\nMadogara: Al-Bukhari, duba Al-Asqalani, Fathul-Bari 2/518."
            33 -> "Yana bayyana cewa ruwan sama yana zuwa ne kawai daga falalar Allah da rahamarSa.\nMadogara: Al-Bukhari 1/205, Muslim 1/83."
            34 -> "Ana karantawa lokacin da aka ji tsawa don nuna kaskantar da kai da tsoron girman Allah.\nMadogara: Muwatta Malik 2/992."
            35 -> "Yana rokon ruwan sama mai amfani da kuma kariya daga guguwa mai barna.\nMadogara: Abu Dawud 4/326, Ibn Majah 2/1228."
            36 -> "Samun lada mai yawa (kyakkyawan lada miliyan daya, goge zunubai miliyan daya) don ambaton Allah a cikin kasuwanni masu cunkoso.\nMadogara: At-Tirmidhi 5/291."
            37 -> "Annabi ya kasance yana neman tsari ga Hassan da Husayn da wadannan kalmomi.\nMadogara: Al-Bukhari 4/119."
            38 -> "Addu'a mai muhimmanci don kiyaye tauhidi na gaskiya da tsarkakar niyya.\nMadogara: Ahmad 4/403, Al-Albani a Sahihul-Jami' 3/233."
            39 -> "Yana mika gaisuwa ga mamata da rokon kariya/lafiya ga rayayyu da mamata duka.\nMadogara: Muslim 2/671, Ibn Majah 1/494."
            40 -> "Aika salati ga Annabi Muhammad yana ninka lada sau goma kuma yana daukaka daraja.\nMadogara: Al-Bukhari, duba Al-Asqalani, Fathul-Bari 6/408."
            42 -> "Annabi (tsira da amincin Allah su tabbata a gare shi) ya ce: 'Wanda duk ya saka tufafi kuma ya fadi wannan, za a gafarta masa dukkan zunubansa na baya.'\nMadogara: Abu Dawud, At-Tirmidhi, Ibn Majah."
            43 -> "Addu'ar sanya sabuwar riga don rokon alherin abin da aka yi rigar dominsa da kariya daga sharrinsa.\nMadogara: Abu Dawud, At-Tirmidhi. Hisnul Muslim Babi na 4."
            44 -> "Addu'a ga wanda ya sanya sababbin tufafi domin ya rayu abin yabo kuma ya cika da shahada.\nMadogara: Ibn Majah 2/1178, An-Nasa'i."
            45 -> "Ambaton sunan Allah lokacin cire tufafi domin samun kariya daga idon aljanu.\nMadogara: At-Tirmidhi 2/505."
            46 -> "Addu'ar tafiya zuwa masallaci don neman haske a zuciya, harshe, ji, gani, da ko'ina a jiki.\nMadogara: Al-Bukhari 11/116, Muslim 1/526."
            47 -> "Addu'ar bayan kiran sallah don rokon matsayin Wasilah da daukaka ga Manzon Allah (SAW).\nMadogara: Al-Bukhari 1/152."
            48 -> "Falalar ziyartar mara lafiya: Mala'iku dubu saba'in suna yi masa salati daga safe har yamma.\nMadogara: At-Tirmidhi 2/124."
            49 -> "Addu'ar da ake yi wa mara lafiya don neman samun waraka da tsarki daga zunubai.\nMadogara: Al-Bukhari 10/118."
            50 -> "Addu'ar bude sallah (Istiftah) don nuna tsarki da girmamawa ga Allah Madaukaki.\nMadogara: Abu Dawud 1/206, At-Tirmidhi 1/242."
            51 -> "Zikirin ruku'u don girmama Allah Ubangiji Mai girma.\nMadogara: Abu Dawud, Ibn Majah, An-Nasa'i."
            52 -> "Yabon Allah lokacin dagowa daga ruku'u da godiya mai yawa mai albarka.\nMadogara: Al-Bukhari 2/284, Abu Dawud 1/201."
            53 -> "Zikirin sujada don tsarkake Ubangiji Mafi daukaka a mafi kusancin bawa da Mahaliccinsa.\nMadogara: Abu Dawud 1/230, At-Tirmidhi."
            54 -> "Addu'ar neman gafara a tsakanin sujadu biyu.\nMadogara: Abu Dawud 1/231."
            55 -> "Addu'ar sujadar tilawa lokacin karanta ayar sujada a Alkur'ani.\nMadogara: At-Tirmidhi 2/474, An-Nasa'i."
            56 -> "Tahiyya (Tashahhud) da shaidawa da gaisuwa ga Annabi da bayin Allah na kwarai.\nMadogara: Al-Bukhari 1/13, Muslim 1/301."
            57 -> "Zikirin juye-juye cikin barci da tuna kasantuwar Allah Shi kadai Mai rinjaye.\nMadogara: An-Nasa'i, Al-Hakim 1/540."
            58 -> "Addu'ar kariya lokacin da barci ya gagara ko aka tsorata a cikin dare.\nMadogara: Abu Dawud 4/12, At-Tirmidhi 3/171."
            59 -> "Abin da ake fada bayan mummunan mafarki da tofa tofi a hagu sau 3 don neman tsari daga shaidan.\nMadogara: Muslim 4/1772-1773."
            60 -> "Addu'ar Qunuti a sallar Wutiri don neman shiriya, lafiya da kariya daga kaddarar sharri.\nMadogara: Abu Dawud, At-Tirmidhi, An-Nasa'i, Ibn Majah."
            61 -> "Zikiri bayan sallame sallar Wutiri sau uku tare da tsarkake Sarki Mai tsarki.\nMadogara: An-Nasa'i 3/244, Al-Bukhari."
            62 -> "Kaffarar zaure (Kaffaratul Majlis) don kankare duk wata kuskuren magana da ta auku a wajen zama.\nMadogara: At-Tirmidhi 3/153, Abu Dawud 4/274."
            63 -> "Gaisuwa ta Musulunci da amsa ta da ninka addu'ar salama da rahama da albarka.\nMadogara: Abu Dawud 4/350, At-Tirmidhi 5/52."
            64 -> "Addu'ar sauka a wani wuri don neman tsari da kariya daga dukkan sharrin halittu.\nMadogara: Muslim 4/2080."
            65 -> "Ruqyah (Addu'ar waraka) don samun kariya daga idon hasada, sihiri da kowace cuta.\nMadogara: Muslim 4/1715."
            66 -> "Addu'ar fuskantar abokin gaba ko azzalumi don neman taimakon Allah da kariya.\nMadogara: Abu Dawud 2/89, At-Tirmidhi 5/560."
            67 -> "Addu'ar tsoron zaluncin sarki ko mai mulki don neman garkuwar Ubangiji.\nMadogara: Al-Bukhari a Al-Adab Al-Mufrad 707."
            68 -> "Addu'ar neman galaba a kan abokan gaba da korar fitinunsu.\nMadogara: Muslim 3/1363."
            69 -> "Addu'ar tsoron wata al'umma ko mutane don neman kariya da isarwar Allah.\nMadogara: Abu Dawud 2/89."
            70 -> "Addu'ar neman tsari lokacin da waswasin shakka ya zo a zuciya game da imani.\nMadogara: Abu Dawud 4/329, Muslim 1/120."
            71 -> "Addu'ar korar waswasin shaidan a cikin sallah ko karatu.\nMadogara: Muslim 4/1729."
            72 -> "Addu'ar neman saukaka al'amari mai wahala da tsanani.\nMadogara: Ibn Hibban 972, Ibn As-Sunni 351."
            73 -> "Addu'ar tuba da yin alwala da sallar raka'a biyu bayan aikata zunubi don samun gafara.\nMadogara: Abu Dawud 2/86, At-Tirmidhi 2/257."
            74 -> "Neman tsari daga shaidan da kiran kiran sallah don korar sa.\nMadogara: Muslim 1/291."
            75 -> "Abin da ake cewa lokacin da wani abu da ba a so ya faru: 'Kaddarar Allah ce kuma abin da Ya so Shi Ya aikata.'\nMadogara: Muslim 4/2058."
            76 -> "Taya murna ga wanda aka haifawa jariri da yi masa addu'ar albarka da zama mutumin kwarai.\nMadogara: Al-Adhkar na An-Nawawi."
            77 -> "Addu'ar mara lafiya da ya yanke kauna daga rayuwa don mika kaddara ga mafi alherin zabi na Allah.\nMadogara: Al-Bukhari 7/155, Muslim 4/2064."
            78 -> "Addu'a ce mai albarka da ke tattaro neman gyaran addini, rayuwar duniya, gobe kiyama, da kuma rokon sanya rayuwa ta kasance karuwar alheri da mutuwa ta kasance hutu daga dukkan sharri.\nMadogara: Sahih Muslim, Littafi na 17, Hadisi na 1472 (Muslim 2720)."
            79 -> "Addu'a ce ingantacciya ta yaye damuwa da bakin ciki da neman sanya Alkur'ani ya zama furen zuciya da hasken kirji.\nMadogara: Musnad Ahmad 1/391, Ibn Hibban 3/253, Sahih Al-Kalim Al-Tayyib 124."
            80 -> "Addu'a ce ta neman biyan bashi, kariya daga talauci da yabon Allah da kyawawan sunayensa.\nMadogara: Sahih Muslim 4/2084 (Hadisi 2713)."
            81 -> "Addu'a ce ta neman kyakkyawan karshe a dukkan al'amuran rayuwa da kariya daga kankantar duniya da azabar lahira.\nMadogara: Musnad Ahmad 4/181, Sahih Ibn Hibban 949, Al-Hakim 3/591."
            82 -> "Addu'a ce ta neman tabbatuwa a kan addini da gaskiya, da neman zuciya salima da harshe mai gaskiya.\nMadogara: Sunan An-Nasa'i 1304, At-Tirmidhi 3407, Musnad Ahmad 17114."
            83 -> "Addu'a ce ta neman tsari daga ilimin da ba ya amfani, zuciya maras tsoron Allah, rai maras koshi, da addu'ar da ba a amsawa.\nMadogara: Sahih Muslim 4/2088 (Hadisi 2722), An-Nasa'i 5442, Abu Dawud 1548."
            84 -> "Wanda ya karanta wannan (Ayatul Kursiyyu) da safe, an kiyaye shi daga shaidan har zuwa yamma; wanda kuma ya karanta ta da yamma, an kiyaye shi har zuwa safe.\nMadogara: Al-Hakim 1/562, Sahih At-Targhib 1/273."
            85 -> "Wanda ya karanta su (Ikhlas, Falaq, da Nas) sau uku (3) da safe da kuma yamma, sun ishe shi daga dukkan wani abu (sharrin komai).\nMadogara: Abu Dawud 4/322, At-Tirmidhi 5/567."
            86 -> "Addu'ar wayewar gari da mika dukkan mulki da yabo ga Allah, da neman alherin yini da kariya daga sharrin yini, kasala, da azabar wuta da kabari.\nMadogara: Sahih Muslim 4/2088. Ana karantawa da safe."
            87 -> "Addu'ar shigar yamma da mika dukkan mulki da yabo ga Allah, da neman alherin dare da kariya daga sharrin dare, kasala, da azabar wuta da kabari.\nMadogara: Sahih Muslim 4/2088. Ana karantawa da yamma."
            88 -> "Addu'ar yabon Allah da nuna cewa da ikonSa muke wayar gari, da ikonSa muke shigar yamma, muke rayuwa, muke mutuwa kuma zuwa gare Shi ne tashi yake.\nMadogara: At-Tirmidhi 5/466. Ana karantawa da safe."
            89 -> "Addu'ar yabon Allah da nuna cewa da ikonSa muke shigar yamma, muke wayar gari, muke rayuwa, muke mutuwa kuma zuwa gare Shi ne komawa take.\nMadogara: At-Tirmidhi 5/466. Ana karantawa da yamma."
            90 -> "Wanda ya karanta wannan sau uku (3) da yamma, babu wani abu mai dafi ko sharri da zai cutar da shi a daren.\nMadogara: At-Tirmidhi 3/187, Ahmad 2/290."
            91 -> "Wanda ya karanta wannan sau bakwai (7) da safe da yamma, Allah zai isar masa da abin da yake damunsa na duniya da lahira.\nMadogara: Abu Dawud 4/321."
            92 -> "Addu'ar neman cikakkiyar lafiyar jiki, ji da gani (sau 3), da kariya daga kafirci, talauci da azabar kabari.\nMadogara: Abu Dawud 4/324, Ahmad 5/42."
            93 -> "Wanda ya fadi wannan sau dari (100) da safe da yamma, babu wanda zai zo da wani aiki mafi falala a ranar kiyama sai wanda ya fadi misalinsa ko fiye da haka.\nMadogara: Sahih Muslim 4/2071."
            94 -> "Addu'ar neman agajin Allah da gyaran dukkan al'amura baki daya, da rokon kada Allah Ya bar mutum da kansa ko da kiftawar ido ne.\nMadogara: Al-Hakim 1/545, Sahih At-Targhib 1/273."
            95 -> "Falalar wannan zikiri sau uku (3) da safe ta fi kowane tsawon zikiri yawa da nauyi a ma'auni.\nMadogara: Sahih Muslim 4/2090. Ana karantawa sau 3 da safe."
            else -> "Madogara: Al-Bukhari, Muslim da sauran ingantattun littattafan Hadisi."
        }
    }

    private fun getYorubaReference(id: Int): String? {
        return when (id) {
            1 -> "Àwọn oore ati virtues ti ranti Allāhu (Zikiri) ga pupọ ninu Ẹ̀sìn Islam:\n1. Allāhu n ranti ẹru ti o ba ranti Rẹ ('Ẹ ranti Mi, Emi yoo ranti yin' - 2:152).\n2. Zikiri n mu alaafia ati itẹlọrun ba ọkan ('Daju ninu iranti Allāhu ni awọn ọkan ti n ni ifọkanbalẹ' - 13:28).\n3. Ojisẹ Allāhu (SAW) fi ẹni ti n ranti Allāhu we alaaye, ẹni ti ko si ranti Rẹ we oku.\n4. Ó n rọ ẹṣẹ ji, o n le Ṣaitan jinna, o si n so ẹru pọ mọ Ọlọrun Rẹ.\nÌtọ́kasí: Surah Al-Baqarah 2:152, Surah Al-Ahzab 33:41-42, Sahih Al-Bukhari, Sahih Muslim. Hisnul Muslim Ori 1."
            41 -> "Sísọ eyi lẹyin jidide n ṣe afihan ọpẹ nla fun imularada jiki ati ti ẹmi, ni jẹwọ pe Allāhu nikan lo n funni ni iye lẹyin iku ati pe sọdọ Rẹ ni atun-kójọ kẹhin wà.\nÌtọ́kasí: Al-Bukhari, duba Al-Asqalani, Fathul-Bari 11/113, Muslim 4/2083."
            2 -> "Ẹnikẹni ti o ba sọ eyi lakoko ọsan pẹlu igbagbọ to daju, ti o si ku ni ọjọ yẹn ṣaaju irọlẹ, yoo wa ninu awọn eniyan Ọgba Rere; ti o ba si sọ ọ ni alẹ pẹlu igbagbọ to daju, ti o si ku ṣaaju owurọ, yoo wa ninu awọn eniyan Ọgba Rere.\nÌtọ́kasí: Al-Bukhari 7/150."
            3 -> "Ẹnikẹni ti o ba ka eyi ni igba mẹta ni owurọ ati igba mẹta ni irọlẹ, ko si nkan ti yoo pa a lara ni ọjọ tabi alẹ yẹn, o jẹ gẹgẹ bi apata ti o lagbara lọwọ gbogbo aburu ojiji ati ipalara.\nÌtọ́kasí: Abu Dawud 4/323, At-Tirmidhi 5/465."
            4 -> "Ẹnikẹni ti o ba ka eyi ni igba mẹta ni owurọ ati igba mẹta ni irọlẹ, o di dandan fun Allāhu lati mu inu rẹ dun ni Ọjinde, ti yoo fi itẹlọrun ati alaafia kun ọkan rẹ.\nÌtọ́kasí: Abu Dawud 4/318, At-Tirmidhi 5/465."
            5 -> "A n sọ eyi ṣaaju sisun lati fi ẹmi rẹ le Allāhu lọwọ patapata lakoko oorun (ti a tọka si ninu Alkur'ani gẹgẹ bi iku kekere) ati lati wa aabo Rẹ jakejado alẹ.\nÌtọ́kasí: Al-Bukhari (Fathul-Bari 11/113), Muslim 4/2083."
            6 -> "Adura yi n ṣe afihan ilera jiki, isoji ẹmi, ati oore nla ti lile ranti Allāhu ati jọsin fun Un lẹyin ṣiṣi oju si ọjọ tuntun.\nÌtọ́kasí: At-Tirmidhi 5/473."
            7 -> "Ẹnikẹni ti o ba ka eyi nigba ti o ba n wọ masallaci, satani yoo sọ pe: 'A ti daabobo rẹ lọwọ mi fun iyoku ọjọ naa,' ni idaniloju aabo ẹmi lakoko ti o wa ninu ile Allāhu.\nÌtọ́kasí: Abu Dawud 1/127, Al-Albani ni Sahih Abu Dawud."
            8 -> "A n sọ eyi nigba ti a ba n jade kuro ni masallaci lati wa oore, ounjẹ, ilera, ati ipese halal lọdọ Allāhu bi o ṣe n pada si awọn ọrọ aye ati awọn iṣẹ rẹ.\nÌtọ́kasí: Muslim 1/494."
            84 -> "Ẹnikẹni ti o ba ka Ayat Al-Kursi ni owurọ, a o daabobo rẹ lọwọ satani titi di alẹ; ẹnikẹni ti o ba ka a ni alẹ, a o daabobo rẹ titi di owurọ.\nÌtọ́kasí: Al-Hakim 1/562, Sahih At-Targhib 1/273."
            85 -> "Ẹnikẹni ti o ba ka Suratul Ikhlas, Falaq ati Nas ni igba mẹta ni owurọ ati irọlẹ, wọn yoo to fun u lọwọ gbogbo aburu.\nÌtọ́kasí: Abu Dawud 4/322, At-Tirmidhi 5/567."
            86 -> "Adura owurọ fun ifi gbogbo ọla ati ọpẹ le Allāhu lọwọ ati tọrọ aabo lọwọ aburu ọjọ, ọlẹ, ati iya ọrun.\nÌtọ́kasí: Sahih Muslim 4/2088. Ka ni owurọ."
            87 -> "Adura irọlẹ fun ifi gbogbo ọla ati ọpẹ le Allāhu lọwọ ati tọrọ aabo lọwọ aburu alẹ, ọlẹ, ati iya ọrun.\nÌtọ́kasí: Sahih Muslim 4/2088. Ka ni irọlẹ."
            88 -> "Adura owurọ fun jẹri pe pẹlu agbara Allāhu la fi ri owurọ ati pe sọdọ Rẹ ni atun-kójọ kẹhin wà.\nÌtọ́kasí: At-Tirmidhi 5/466."
            89 -> "Adura irọlẹ fun jẹri pe pẹlu agbara Allāhu la fi ri irọlẹ ati pe sọdọ Rẹ ni ibi adabọ wà.\nÌtọ́kasí: At-Tirmidhi 5/466."
            90 -> "Ẹnikẹni ti o ba ka eyi ni igba mẹta ni irọlẹ, ko si majele tabi ipalara ti yoo ba a ni alẹ yẹn.\nÌtọ́kasí: At-Tirmidhi 3/187, Ahmad 2/290."
            91 -> "Ẹnikẹni ti o ba ka eyi ni igba meje ni owurọ ati irọlẹ, Allāhu yoo to fun gbogbo aibalẹ aye ati ti ọrun rẹ.\nÌtọ́kasí: Abu Dawud 4/321."
            92 -> "Adura fun ilera ara, igbọran ati iriran, ati àbò lọwọ aigbagbọ, osi ati iya isà-òkú.\nÌtọ́kasí: Abu Dawud 4/324, Ahmad 5/42."
            93 -> "Ẹnikẹni ti o ba sọ 'Subhanallahi wa bihamdihi' ni igba ọgọrun ni owurọ ati irọlẹ, ko si ẹni ti yoo mu iṣẹ t'o dara ju ti rẹ lọ ni Ọjọ Ajinde.\nÌtọ́kasí: Sahih Muslim 4/2071."
            94 -> "Adura fun iranlọwọ Allāhu ati atunṣe gbogbo ọrọ aye laisi fifi wa silẹ fun ara wa fun iṣẹju kan.\nÌtọ́kasí: Al-Hakim 1/545, Sahih At-Targhib 1/273."
            95 -> "Oore zikiri yi ni igba mẹta ni owurọ tobi ju gbogbo zikiri gigun lọ lori iwon.\nÌtọ́kasí: Sahih Muslim 4/2090. Ka ni igba mẹta ni owurọ."
            78 -> "Adura ajẹmọ-gbogbo ni eyi ti o n ro atunṣe ẹsin, aye, ọrun, ati bibere ki igbesi aye jẹ alekun oore ati pe ki iku jẹ isimi kuro ninu gbogbo aburu.\nÌtọ́kasí: Sahih Muslim, Iwe 17, Hadisi 1472 (Muslim 2720)."
            79 -> "Adura fun imukuro aibalẹ ati ibanujẹ ati kikọ Al-Kur’ani ni idunnu ọkan.\nÌtọ́kasí: Musnad Ahmad 1/391, Ibn Hibban 3/253, Sahih Al-Kalim Al-Tayyib 124."
            80 -> "Adura fun isanwo gbese, àbò kuro ninu osi ati iyin pẹlu orukọ Allāhu gigun.\nÌtọ́kasí: Sahih Muslim 4/2084 (Hadisi 2713)."
            81 -> "Adura fun igbẹhin rere ninu gbogbo ọrọ aye ati àbò kuro ninu itiju aye ati iya ọrun.\nÌtọ́kasí: Musnad Ahmad 4/181, Sahih Ibn Hibban 949, Al-Hakim 3/591."
            82 -> "Adura fun iduroṣinṣin lori ẹsin ati ọna mọọmọ, ati bibere ọkan mimọ ati ahọn olootọ.\nÌtọ́kasí: Sunan An-Nasa'i 1304, At-Tirmidhi 3407, Musnad Ahmad 17114."
            83 -> "Adura fun aabo lọwọ mọ ti ko ni alafia, ọkan ti ko ni tẹriba, ẹmi ti ko ni tẹ lọrun, ati adura ti ko ni gba.\nÌtọ́kasí: Sahih Muslim 4/2088 (Hadisi 2722), An-Nasa'i 5442, Abu Dawud 1548."
            else -> "Ìtọ́kasí: Al-Bukhari, Muslim ati awọn iwe Hadisi mimọ miran."
        }
    }

    private fun getIgboReference(id: Int): String? {
        return when (id) {
            1 -> "Uru dị n'ịtụgharị uche na ichere Chineke (Dhikr) dị ukwuu n'Islam:\n1. Chineke na-echeta ohu nke na-echeta Ya ('Cheta m ka m cheta gị' - 2:152).\n2. Dhikr na-eweta udo na afọ ojuju n'obi ('N'ezie, n'icheta Chineke ka obi na-enwe udo' - 13:28).\n3. Onye Ozi Chineke (SAW) jiri onye na-echeta Chineke tụnyere onye dị ndụ, ma jiri onye na-adịghị echeta Ya tụnyere onye nwụrụ anwụ.\n4. Ọ na-agbaghara mmehie, na-achụ Ekwensu, ma na-eweta nchebe na ngọzi.\nEbe nsinyere: Surah Al-Baqarah 2:152, Surah Al-Ahzab 33:41-42, Sahih Al-Bukhari, Sahih Muslim. Hisnul Muslim Isi nke 1."
            41 -> "Ịgụ nke a mgbe a mụrụ anya na-egosi ekele miri emi maka mbilite n'ọnwụ nke anụ ahụ na nke ime mmụọ, na-ekwupụta na ọ bụ naanị Chineke na-enye ndụ mgbe ọnwụ gasịrị na n'aka Ya ka mbilite n'ọnwụ ikpeazụ dị.\nEbe nsinyere: Al-Bukhari, duba Al-Asqalani, Fathul-Bari 11/113, Muslim 4/2083."
            2 -> "Onye ọ bụla nke kwuru nke a n'ehihie na nkwenye siri ike, ma nwụọ n'ụbọchị ahụ tupu anyasị, ọ ga-eso ná ndị Paradaịs; ọ bụrụkwa na mmadụ ekwuo ya n'abalị na nkwenye siri ike, ma nwụọ tupu ụtụtụ, ọ ga-eso ná ndị Paradaịs.\nEbe nsinyere: Al-Bukhari 7/150."
            3 -> "Onye ọ bụla nke gụrụ nke a ugboro atọ n'ụtụtụ na ugboro atọ n'anyasị, ọ dịghị ihe ga-emerụ ha ahụ n'ụbọchị ma ọ bụ abalị ahụ, ọ na-eje ozi dị ka ọta siri ike megide ihe mberede niile.\nEbe nsinyere: Abu Dawud 4/323, At-Tirmidhi 5/465."
            4 -> "Onye ọ bụla nke gụrụ nke a ugboro atọ n'ụtụtụ na ugboro atọ n'anyasị, ọ na-aghọ ọrụ dị n'aka Chineke ime ka obi tọọ ya ụtọ n'Ụbọchị Mbilite n'Ọnwụ, na-eme ka obi ya jupụta na afọ ojuju na udo.\nEbe nsinyere: Abu Dawud 4/318, At-Tirmidhi 5/465."
            84 -> "Onye gụrụ Ayat Al-Kursi n'ụtụtụ, a na-echebe ya pụọ n'aka ekwensu ruo anyasị; onye gụrụ ya n'anyasị, a na-echebe ya ruo ụtụtụ.\nEbe nsinyere: Al-Hakim 1/562, Sahih At-Targhib 1/273."
            85 -> "Onye gụrụ Surah Ikhlas, Falaq na Nas ugboro atọ n'ụtụtụ na anyasị, ha ga-ezuru ya megide ihe ọjọọ niile.\nEbe nsinyere: Abu Dawud 4/322, At-Tirmidhi 5/567."
            86 -> "Ekpere ụtụtụ maka inye Chineke otuto na ike niile, na ịchọ nchebe pụọ n'ihe ọjọọ nke ụbọchị na ahụhụ ili.\nEbe nsinyere: Sahih Muslim 4/2088."
            87 -> "Ekpere anyasị maka inye Chineke otuto na ike niile, na ịchọ nchebe pụọ n'ihe ọjọọ nke abalị na ahụhụ ili.\nEbe nsinyere: Sahih Muslim 4/2088."
            90 -> "Onye gụrụ nke a ugboro atọ n'anyasị, ọ dịghị ihe ọjọọ ma ọ bụ nsí ga-emerụ ya ahụ n'abalị ahụ.\nEbe nsinyere: At-Tirmidhi 3/187, Ahmad 2/290."
            91 -> "Onye gụrụ nke a ugboro asaa n'ụtụtụ na anyasị, Chineke ga-ezuru ya n'ihe niile na-enye ya nchegbu.\nEbe nsinyere: Abu Dawud 4/321."
            93 -> "Onye kwuru nke a ugboro 100 n'ụtụtụ na anyasị, ọ dịghị onye ga-eweta ọrụ dị mma karịa ya n'Ụbọchị Mbilite n'Ọnwụ.\nEbe nsinyere: Sahih Muslim 4/2071."
            else -> "Ebe nsinyere: Sahih Al-Bukhari, Muslim na akwụkwọ Hadith ndị ọzọ."
        }
    }

    private fun getArabicReference(id: Int): String? {
        return when (id) {
            1 -> "فضل الذكر عظيم في الإسلام:\n١. أن الله تعالى يذكر من ذكره (فاذكروني أذكركم - البقرة 152).\n٢. الذكر يورث طمأنينة القلب (ألا بذكر الله تطمئن القلوب - الرعد 28).\n٣. شبه النبي ﷺ الذي يذكر ربه بالحي والذي لا يذكره بالميت (صحيح البخاري).\n٤. يطرد الشيطان، ويحط الخطايا، ويجلب الحفظ والبركة.\nالمرجع: سورة البقرة 2:152، سورة الأحزاب 33:41-42، صحيح البخاري 11/208، صحيح مسلم 1/539. حصن المسلم الباب الأول."
            41 -> "قراءة هذا الذكر عند الاستيقاظ تعبر عن الشكر العميق لإحياء البدن والروح، والاعتراف بأن الله وحده يحيي الموتى وإليه النشور.\nالمرجع: البخاري، وانظر العسقلاني فتح الباري 11/113، ومسلم 4/2083."
            2 -> "من قالها موقناً بها حين يمسي فمات من ليلته دخل الجنة، وكذلك إذا أصبح.\nالمرجع: البخاري 7/150."
            3 -> "من قالها ثلاثاً إذا أصبح وثلاثاً إذا أمسى لم يضره شيء في ذلك اليوم أو تلك الليلة، وهي درع قوي ضد فجاءة النقم والمكاره.\nالمرجع: أبو داود 4/323، والترمذي 5/465."
            4 -> "من قالها ثلاثاً حين يصبح وثلاثاً حين يمسي كان حقاً على الله أن يرضيه يوم القيامة ويملأ قلبه طمأنينة وسلاماً.\nالمرجع: أبو داود 4/318، والترمذي 5/465."
            84 -> "من قرأ آية الكرسي حين يصبح أُجير من الجن حتى يمسي، ومن قرأها حين يمسي أُجير منهم حتى يصبح.\nالمرجع: أخرجه الحاكم 1/562، وصححه الألباني في صحيح الترغيب والترهيب 1/273."
            85 -> "من قرأ المعوذات (الإخلاص والفلق والناس) ثلاث مرات حين يصبح وحين يمسي كفته من كل شيء.\nالمرجع: أبو داود 4/322، والترمذي 5/567."
            86 -> "دعاء الصباح لإعلان التوحيد وتفويض الأمر والملك لله تعالى وسؤال خير اليوم والاستعاذة من شره وعذاب القبر.\nالمرجع: صحيح مسلم 4/2088. يقال في الصباح."
            87 -> "دعاء المساء لإعلان التوحيد وتفويض الأمر والملك لله تعالى وسؤال خير الليلة والاستعاذة من شرها وعذاب القبر.\nالمرجع: صحيح مسلم 4/2088. يقال في المساء."
            88 -> "دعاء الصباح واستشعار فضل الله وتدبيره للأحياء والأموات وإليه النشور.\nالمرجع: الترمذي 5/466. يقال صباحاً."
            89 -> "دعاء المساء واستشعار فضل الله وتدبيره للأحياء والأموات وإليه المصير.\nالمرجع: الترمذي 5/466. يقال مساءً."
            90 -> "من قالها ثلاث مرات حين يمسي لم تضره حمة أو لدغة تلك الليلة.\nالمرجع: الترمذي 3/187، وأحمد 2/290."
            91 -> "من قالها حين يصبح وحين يمسي سبع مرات كفاه الله ما أهمه من أمر الدنيا والآخرة.\nالمرجع: أبو داود 4/321."
            92 -> "سؤال الله تعالى العافية التامة في البدن والسمع والبصر ثلاثاً والاستعاذة من الكفر والفقر وعذاب القبر.\nالمرجع: أبو داود 4/324، وأحمد 5/42."
            93 -> "من قال سبحان الله وبحمده مائة مرة حين يصبح وحين يمسي لم يأتِ أحد يوم القيامة بأفضل مما جاء به إلا أحد قال مثل ما قال أو زاد عليه.\nالمرجع: صحيح مسلم 4/2071."
            94 -> "استغاثة برحمة الله وتوكيل الأمر كله إليه وسؤال الصلاح وعدم الوكول إلى النفس طرفة عين.\nالمرجع: الحاكم 1/545، وصحيح الترغيب 1/273."
            95 -> "فضل هذا الذكر يعدل ساعات طويلة من التسبيح في الميزان.\nالمرجع: صحيح مسلم 4/2090. يقال ثلاث مرات صباحاً."
            else -> "المرجع: صحيح البخاري وصحيح مسلم وكتب السنة المعتمدة."
        }
    }

    private fun getFrenchReference(id: Int): String? {
        return when (id) {
            84 -> "Quiconque récite le verset du Trône (Ayat Al-Kursi) le matin est protégé jusqu'au soir, et quiconque le récite le soir est protégé jusqu'au matin.\nRéférence: Al-Hakim 1/562, Sahih At-Targhib 1/273."
            85 -> "Quiconque récite les sourates Al-Ikhlas, Al-Falaq et An-Nas 3 fois matin et soir, elles lui suffiront contre toute chose.\nRéférence: Abu Dawud 4/322, At-Tirmidhi 5/567."
            86 -> "Invocation du matin confiant toute royauté et louange à Allah, demandant le bien de la journée et cherchant refuge contre le mal et le châtiment de la tombe.\nRéférence: Sahih Muslim 4/2088."
            87 -> "Invocation du soir confiant toute royauté et louange à Allah, demandant le bien de la nuit et cherchant refuge contre le mal et le châtiment de la tombe.\nRéférence: Sahih Muslim 4/2088."
            90 -> "Quiconque récite ceci 3 fois le soir ne sera touché par aucun mal ni venin cette nuit-là.\nRéférence: At-Tirmidhi 3/187, Ahmad 2/290."
            91 -> "Quiconque récite ceci 7 fois matin et soir, Allah lui suffira pour ce qui le préoccupe ici-bas et dans l'au-delà.\nRéférence: Abu Dawud 4/321."
            93 -> "Quiconque dit ceci 100 fois matin et soir, nul ne viendra au Jour de la Résurrection avec une meilleure œuvre.\nRéférence: Sahih Muslim 4/2071."
            else -> "Référence: Sahih Al-Bukhari, Sahih Muslim et recueils de hadiths authentiques."
        }
    }

    private fun getSpanishReference(id: Int): String? {
        return when (id) {
            84 -> "Quien recite Ayat Al-Kursi por la mañana estará protegido hasta la tarde, y quien lo recite por la tarde estará protegido hasta la mañana.\nReferencia: Al-Hakim 1/562, Sahih At-Targhib 1/273."
            85 -> "Quien recite las suras Al-Ikhlas, Al-Falaq y An-Nas 3 veces por la mañana y por la tarde, le bastarán contra todo mal.\nReferencia: Abu Dawud 4/322, At-Tirmidhi 5/567."
            86 -> "Súplica matutina alabando a Allah y pidiendo el bien del día y la protección contra el castigo de la tumba.\nReferencia: Sahih Muslim 4/2088."
            87 -> "Súplica vespertina alabando a Allah y pidiendo el bien de la noche y la protección contra el castigo de la tumba.\nReferencia: Sahih Muslim 4/2088."
            90 -> "Quien recite esto 3 veces por la tarde no sufrirá ningún daño esa noche.\nReferencia: At-Tirmidhi 3/187, Ahmad 2/290."
            91 -> "Quien recite esto 7 veces por la mañana y por la tarde, Allah le bastará ante cualquier aflicción.\nReferencia: Abu Dawud 4/321."
            93 -> "Quien recite esto 100 veces por la mañana y por la tarde, nadie presentará una mejor obra el Día de la Resurrección.\nReferencia: Sahih Muslim 4/2071."
            else -> "Referencia: Sahih Al-Bukhari, Sahih Muslim y libros auténticos de Hadiz."
        }
    }

    private fun getUrduReference(id: Int): String? {
        return when (id) {
            84 -> "جو شخص صبح کے وقت آیت الکرسی پڑھے گا وہ شام تک شیطان سے محفوظ رہے گا، اور جو شام کو پڑھے گا وہ صبح تک محفوظ رہے گا۔\nحوالہ: الحاکم 1/562، صحیح الترغیب 1/273۔"
            85 -> "جو شخص صبح اور شام تین تین مرتبہ سورۃ الاخلاص اور معوذتین پڑھے گا وہ ہر چیز کے شر سے کافی ہوں گی۔\nحوالہ: ابو داؤد 4/322، الترمذی 5/567۔"
            86 -> "صبح کے وقت اللہ کی حمد و ثنا اور دن کی خیر و بھلائی اور عذاب قبر سے پناہ کی مسنون دعا۔\nحوالہ: صحیح مسلم 4/2088۔"
            87 -> "شام کے وقت اللہ کی حمد و ثنا اور رات کی خیر و بھلائی اور عذاب قبر سے پناہ کی مسنون دعا۔\nحوالہ: صحیح مسلم 4/2088۔"
            90 -> "جو شخص شام کے وقت تین مرتبہ یہ پڑھے گا اس رات کوئی زہریلی یا نقصان دہ چیز اسے نقصان نہ پہنچائے گی۔\nحوالہ: الترمذی 3/187، احمد 2/290۔"
            91 -> "جو شخص صبح اور شام سات مرتبہ یہ پڑھے گا اللہ تعالیٰ اس کے دنیا اور آخرت کے تمام غموں کے لیے کافی ہوگا۔\nحوالہ: ابو داؤد 4/321۔"
            93 -> "جو شخص صبح اور شام سو مرتبہ 'سبحان اللہ وبحمدہ' کہے گا قیامت کے دن اس سے افضل عمل کسی کا نہ ہوگا۔\nحوالہ: صحیح مسلم 4/2071۔"
            else -> "حوالہ: صحیح بخاری، صحیح مسلم اور دیگر کتب احادیث صحیحہ۔"
        }
    }

    private fun getChineseReference(id: Int): String? {
        return when (id) {
            84 -> "早晨诵读此经文（库尔西经文）者，将受保护免受恶魔侵扰直至傍晚；傍晚诵读者将受保护直至清晨。\n出处：哈基姆经训集 1/562。"
            85 -> "早晚各念三次忠诚章、曙光章与人类章者，足以抵御一切祸害。\n出处：艾布·达伍德经训集 4/322，提尔密济经训集 5/567。"
            86 -> "早晨赞念安拉并祈求白昼之吉祥与免受坟墓刑罚。\n出处：穆斯林圣训实录 4/2088。"
            87 -> "傍晚赞念安拉并祈求夜晚之安宁与免受坟墓刑罚。\n出处：穆斯林圣训实录 4/2088。"
            90 -> "傍晚诵读三次者，当夜不受任何伤害。\n出处：提尔密济经训集 3/187。"
            91 -> "早晚各念七次者，安拉必为其解除现后两世的一切忧虑。\n出处：艾布·达伍德经训集 4/321。"
            93 -> "早晚各念一百遍者，复生日无人带来比其更优越的善功。\n出处：穆斯林圣训实录 4/2071。"
            else -> "出处：布哈里圣训实录、穆斯林圣训实录等可靠圣训集。"
        }
    }
}
