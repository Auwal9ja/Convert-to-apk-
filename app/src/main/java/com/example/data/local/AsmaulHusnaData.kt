package com.example.data.local

object AsmaulHusnaData {

    data class AsmaDetail(
        val number: Int,
        val arabic: String,
        val transliteration: String,
        val meaningEn: String,
        val meaningHa: String,
        val bestDuaHa: String,
        val bestDuaEn: String,
        val bestDuaYo: String,
        val bestDuaIg: String
    )

    fun getAsmaulHusnaDuas(): List<DuaEntity> {
        val list = mutableListOf<DuaEntity>()

        // 1. Introduction and Comprehensive Benefits of Knowing Asma'ul Husna
        list.add(
            DuaEntity(
                id = 244,
                category = "Asma'ul Husna",
                title = "Amfanin Sanin Asma'ul Husna (Virtue & Power of the 99 Names of Allah)",
                arabic = "وَلِلَّهِ الْأَسْمَاءُ الْحُسْنَىٰ فَادْعُوهُ بِهَا\n\nقَالَ رَسُولُ اللَّهِ ﷺ: «إِنَّ لِلَّهِ تِسْعَةً وَتِسْعِينَ اسْمًا مِائَةً إِلَّا وَاحِدًا، مَنْ أَحْصَاهَا دَخَلَ الْجَنَّةَ»",
                transliteration = "Wa lillahil-Asma'ul-Husna fad'oohu biha.\nQala Rasulullahi (SAW): Inna lillahi tis'atan wa tis'ina isman mi'atan illa wahida, man ahsaha dakhalal-Jannah.",
                translation = "And to Allah belong the most beautiful names, so invoke Him by them (Surah Al-A'raf 7:180).\n\nThe Messenger of Allah (ﷺ) said: 'Indeed, Allah has ninety-nine names, one hundred minus one; whoever memorizes, understands, and acts upon them will enter Paradise.' (Sahih Al-Bukhari 2736, Sahih Muslim 2677).\n\nImmense Benefits of Knowing Allah's 99 Names:\n1. Direct Path to Paradise: Memorizing, reflecting, and living by them guarantees entrance to Jannah.\n2. Instant Response to Du'a: Calling upon Allah by His specific Names matching your exact need (e.g. Ya Razzaq for wealth, Ya Shafi for health, Ya Ghaffar for forgiveness, Ya Jabbar for broken hearts) ensures your prayers are accepted.\n3. Inner Peace & Healing: Knowing the perfection of Allah removes all despair, anxiety, and fear from the heart.\n4. True Tawhid & Knowledge of our Creator.",
                translationHausa = "Kuma Allah Yana da sunaye mafiya kyau (Asma'ul Husna), saboda haka ku roƙe Shi da su (Surah Al-A'raf: 180).\n\nManzon Allah (ﷺ) ya ce: 'Lallai Allah Yana da sunaye casa'in da tara (99), ɗari ba ɗaya ba. Duk wanda ya kiyaye su (ya haddace su, ya fahimci ma'anarsu, ya yi imani da su, kuma ya bauta wa Allah da yin addu'a da su) zai shiga Aljanna.' (Sahih Al-Bukhari 2736, Sahih Muslim 2677).\n\nAmfanin Sanin Sunayen Allah Kyawawa:\n1. Hanyar Shiga Aljanna: Wanda ya san su, ya haddace su, kuma ya yi aiki da su zai shiga Aljanna.\n2. Samun Amsar Addu'a: Idan kana neman arziki ka ce 'Ya Razzaq', neman gafara ka ce 'Ya Ghaffar', neman lafiya ka ce 'Ya Shafi', gyaran karyayyen al'amari ka ce 'Ya Jabbar' - Allah Yana amsa addu'ar bawan da ya kira Shi da sunanSa da ya dace da buƙatar.\n3. Samun Natsuwa da Yaye Tsoro da Damuwa a Zuciya.\n4. Zurfafa Son Allah da Bautata Masa da Ikhlasi.",
                translationYoruba = "Gbogbo orúkọ rere jẹ́ ti Allāhu, nítorí náà ẹ pe E pẹ̀lú wọn (Surah Al-A'raf: 180).\nÀnábì (SAW) sọ pé: 'Dájúdájú Allāhu ní orúkọ mọ́kàndínlọ́gọ́rùn-ún (99), ẹnikẹ́ni tí ó bá kẹ́kọ̀ọ́ wọn tí ó sì pa wọ́n mọ́ yóò wọ Alujanna.'",
                translationIgbo = "Aha niile kachasị mma bụ nke Chineke, ya mere kpọkuo Ya site na ha (Surah Al-A'raf: 180).\nOnye Amụma (SAW) kwuru sị: 'N'ezie Chineke nwere aha iri itoolu na itoolu (99), onye ọ bụla mụtara ha ma na-eme ihe ha pụtara ga-aba na Paradaịs.'",
                reference = "Surah Al-A'raf 7:180, Sahih Al-Bukhari 2736, Sahih Muslim 2677"
            )
        )

        val names = listOf(
            AsmaDetail(
                1, "الرَّحْمَنُ", "Ar-Rahman",
                "The Most Gracious", "Mai Rahama ga Dukkan Halittu",
                "Ya Rahman, Ka yi mini cikakkiyar rahama a duniya da lahira, Ka lulluɓe ni da tausayinKa a dukkan al'amurana.",
                "O The Most Gracious, envelop me, my family, and all my affairs with Your boundless mercy in this world and the Hereafter.",
                "Iwọ Àkọ́kọ́ ninu Aanu, fi aanu Rẹ ti o gbooro bo emi ati ẹbi mi ni ayé ati lẹ́yìn ikú.",
                "Onye Kacha Enwe Ebere, kpuchie mụ na ezinụlọ m ebere Gị na-enweghị ngwụcha n'ụwa a na n'ọdịnihu."
            ),
            AsmaDetail(
                2, "الرَّحِيمُ", "Ar-Rahim",
                "The Most Merciful", "Mai Jinkai na Musamman ga Muminai",
                "Ya Rahim, Ka ji ƙaina da rahamarKa ta musamman a ranar hisabi, Ka sanya ni cikin bayinKa muminai masu shiga Aljanna.",
                "O The Most Merciful, bestow Your special mercy upon me on the Day of Judgment and admit me into Paradise among the righteous believers.",
                "Iwọ Alaanu fun awọn onigbagbọ, fi aanu Rẹ pẹlu mi ni Ọjọ Idajọ ki O si fi mi sinu Alujanna.",
                "Onye Na-eme Ebere, mee m ebere pụrụ iche n'Ụbọchị Ikpe ma mee ka m banye Paradaịs."
            ),
            AsmaDetail(
                3, "الْمَلِكُ", "Al-Malik",
                "The King & Sovereign", "Mamallaki Kuma Sarkin Sarakuna",
                "Ya Malik, Ka mallaka mini alherin duniya da lahira, Ka kare ni daga mulkin azzalumai, kuma kada Ka bar ni in ƙasƙanta ga kowa sai Kai.",
                "O Absolute Sovereign, grant me dignity, protect me from tyrants, and never leave me in need of anyone besides You.",
                "Iwọ Ọba Awọn Ọba, fun mi ni ọlá ati ipese rere, ma ṣe fi mi silẹ labẹ ailera fun ẹnikẹni miran.",
                "Eze Ndị Eze, nye m nsọpụrụ na ihe ọma niile, emela ka m dabere n'onye ọ bụla ma ọ bụghị Gị."
            ),
            AsmaDetail(
                4, "الْقُدُّوسُ", "Al-Quddus",
                "The Most Holy & Pure", "Mai Tsarki Daga Dukkan Aibi",
                "Ya Quddus, Ka tsarkake zuciyata daga munafunci da hassada, jikina daga cututtuka, da dukiyata daga haram.",
                "O Most Holy and Pure, purify my heart from hypocrisy and envy, my body from diseases, and my wealth from all impurities.",
                "Iwọ Mímọ́ Jùlọ, wẹ ọkàn mi mọ́ kuro ninu agabagebe, ara mi kuro ninu aisan, ati owo mi kuro ninu eewọ.",
                "Onye Dị Nsọ Kachasị, sachapụ obi m pụọ n'anyaụfụ, ahụ m pụọ n'ọrịa, na akụ na ụba m pụọ n'ihe ọjọọ."
            ),
            AsmaDetail(
                5, "السَّلَامُ", "As-Salam",
                "The Source of Peace", "Mai Aminci da Bada Zaman Lafiya",
                "Ya Salam, Ka ba ni cikakkiyar lafiya da aminci a zuciyata, da zaman lafiya a gidana da ƙasata, Ka kubutar da ni daga azabar kabari da wuta.",
                "O Source of Peace, bless me with peace of mind, physical safety, and harmony in my home, and grant me salvation from the grave and fire.",
                "Iwọ Orisun Alaafia, fun mi ni alaafia pipe ninu ọkàn, ile mi ati orilẹ-ede mi, gba mi kuro ninu ijiya.",
                "Isi Iyi nke Udo, nye m udo n'obi m, ezinụlọ m na obodo m, ma zọpụta m n'ahụhụ niile."
            ),
            AsmaDetail(
                6, "الْمُؤْمِنُ", "Al-Mu'min",
                "The Giver of Faith & Security", "Mai Bada Imani da Aminci",
                "Ya Mu'min, Ka ƙara mini ƙarfin imani da tsoronKa, Ka amintar da ni daga dukkan tsoro, firgici da damuwar rayuwa.",
                "O Giver of Faith and Security, strengthen my faith, safeguard me from all fear and terror, and grant me ultimate tranquility.",
                "Iwọ Olufunni ni Igbagbọ ati Aabo, mu igbagbọ mi lagbara ki O si daabobo mi kuro ninu gbogbo iberu.",
                "Onye Na-enye Okwukwe na Nchebe, mee ka okwukwe m sie ike ma chebe m pụọ n'egwu niile."
            ),
            AsmaDetail(
                7, "الْمُهَيْمِنُ", "Al-Muhaymin",
                "The Guardian & Overseer", "Mai Kula da Tsare Komai",
                "Ya Muhaymin, Ka tsare ni da iyalina a ƙarƙashin kulawarKa da kariyarka ta musamman a kowane lokaci.",
                "O Preserver and Watchful Guardian, protect me, my loved ones, and my faith under Your watchful and infallible care.",
                "Iwọ Oluso Agba, daabobo mi ati awọn ayanfe mi labẹ abojuto Rẹ ti o peye.",
                "Onye Nche na Onye Nlekọta, chebe mụ na ndị m hụrụ n'anya n'okpuru nlekọta Gị pụrụ iche."
            ),
            AsmaDetail(
                8, "الْعَزِيزُ", "Al-Aziz",
                "The All-Mighty", "Mabuwayi Mai Izza da Buwaya",
                "Ya Aziz, Ka ba ni daraja da izzar musulunci, Ka kiyaye ni daga ƙasƙanci da wulaƙanci a gaban mutane da maƙiya.",
                "O The All-Mighty, grant me the honor and strength of faith, and never let me be humiliated before my enemies or creation.",
                "Iwọ Alagbara Titobi, fun mi ni ọlá ti ẹsin Islam, ma jẹ ki n di ẹlẹya niwaju awọn ọta.",
                "Onye Pụrụ Ime Ihe Niile, nye m ugwu nke okwukwe, emela ka m nwee ihere n'ihu ndị iro m."
            ),
            AsmaDetail(
                9, "الْجَبَّارُ", "Al-Jabbar",
                "The Compeller & Restorer", "Mai Gyara Karyayyu da Tilastawa",
                "Ya Jabbar, Ka gyara karyayyar zuciyata da karyayyen al'amarina, Ka mayar mini da asarata da mafi alheri da sauƙi.",
                "O Compeller and Restorer of the Broken, heal my broken heart, restore what I have lost with something far better, and rectify my affairs.",
                "Iwọ Olutun Awọn Ti O Fọ Ṣe, wo ọkàn mi ti o gbọgbẹ sàn, ki O si fi ohun rere ropo gbogbo adanu mi.",
                "Onye Na-edozi Ndị Gbajiri Agbaji, gwọọ obi m gbajiri agbaji, ma weghachi ihe m tụfuru na nke ka mma."
            ),
            AsmaDetail(
                10, "الْمُتَكَبِّرُ", "Al-Mutakabbir",
                "The Supreme in Greatness", "Mai Girma Wanda Girma Ya Keɓanta da Shi",
                "Ya Mutakabbir, Ka tsare ni daga girman kai da alfahari, Ka kare ni daga duk wani azzalumi mai nuna isa da danniya.",
                "O Supreme in Greatness, protect me from arrogance and vanity, and shield me against oppressive tyrants.",
                "Iwọ Ẹni Ti Titobi Wà Fun Nikan, pa mi mọ kuro ninu igberaga, ki O si gba mi lọwọ awọn aninilara.",
                "Onye Kasị Elu n'Ebube, chebe m pụọ na mpako, ma chebe m pụọ n'aka ndị ọchịchị aka ike."
            ),
            AsmaDetail(
                11, "الْخَالِقُ", "Al-Khaliq",
                "The Creator", "Mahaliccin Dukkan Halittu",
                "Ya Khaliq, Ka kyautata halittata da ɗabi'ata, Ka ƙirƙira mini hanyoyin alheri da mafita daga kowace irin matsala.",
                "O Creator of all things, perfect my character just as You perfected my creation, and create for me ways out of every difficulty.",
                "Iwọ Ẹlẹ́dàá ohun gbogbo, ṣe iwa mi ni rere gẹgẹ bi O ṣe da mi ni ẹlẹwa, ki O si da ọna abayọ fun mi.",
                "Onye Okike nke ihe niile, mee ka agwa m zuo oke ma meere m ụzọ n'ụdị nsogbu ọ bụla."
            ),
            AsmaDetail(
                12, "الْبَارِئُ", "Al-Bari'",
                "The Originator & Maker", "Mai Fara Halitta Daga Babu",
                "Ya Bari', Ka warkar da dukkan cutukan jikina da zuciyata, Ka tseratar da ni daga dukkan aibi da musiba.",
                "O Originator and Maker, heal all physical and spiritual illnesses within me and keep me free from flaws and tribulations.",
                "Iwọ Oludasilẹ, wo gbogbo aisan ara ati ti ẹmi mi sàn, ki O si gba mi kuro ninu gbogbo abuku.",
                "Onye Mmalite Ihe Niile, gwọọ ọrịa niile nke anụ ahụ na nke mmụọ, ma chebe m pụọ na nsogbu."
            ),
            AsmaDetail(
                13, "الْمُصَوِّرُ", "Al-Musawwir",
                "The Fashioner of Forms", "Mai Siffanta Halitta Yadda Ya So",
                "Ya Musawwir, Ka azurta ni da zuriya tagari masu kyawun sura da dabi'a, Ka siffanta makomata da kyakkyawan ƙarshe.",
                "O Fashioner of Forms, bless me with righteous, beautiful offspring, and shape my destiny with a blessed conclusion.",
                "Iwọ Oluyaworan ẹda, fi awọn ọmọ rere ti o lẹwa ati oníwà-rere jinki mi, ki O si fi ipari rere pari ayé mi.",
                "Onye Na-akpụ Ụdị Ihe, nye m ụmụ ezi omume ma mee ka ọgwụgwụ ndụ m bụrụ nke a gọziri agọzi."
            ),
            AsmaDetail(
                14, "الْغَفَّارُ", "Al-Ghaffar",
                "The All-Forgiving", "Mai Yawan Gafara da Rufe Asiri",
                "Ya Ghaffar, Ka shafe zunubaina na baya da na gaba, Ka rufa mini asirina a duniya da ranar hisabi.",
                "O Constant Forgiver, erase my past and future sins, conceal my shortcomings in this world and on the Day of Reckoning.",
                "Iwọ Alaforiji Titi Lailai, pa gbogbo ẹṣẹ mi rẹ, ki O si bo asiri mi ni ayé ati ni Ọjọ Idajọ.",
                "Onye Na-agbaghara Mmehie Mgbe Niile, hichapụ mmehie m niile ma kpuchie adịghị ike m n'ụbọchị ikpe."
            ),
            AsmaDetail(
                15, "الْقَهَّارُ", "Al-Qahhar",
                "The Subduer", "Mai Rinƙaye a Kan Dukkan Komai",
                "Ya Qahhar, Ka karya maƙiyan addini, mayaudara, da mugayen shaidanu da sihirin da ke neman cutar da ni.",
                "O The All-Subduer, break the power of evil enemies, deceitful oppressors, and the harms of witchcraft and shayateen.",
                "Iwọ Aṣẹgun Gbogbo Nkan, fọ agbara awọn ọta ibi, awọn eṣu, ati idan ti o n wa lati pa mi lara.",
                "Onye Na-emeri Ihe Niile, bibie ike nke ndị iro ọjọọ, ndị amoosu na ndị mmụọ ọjọọ."
            ),
            AsmaDetail(
                16, "الْوَهَّابُ", "Al-Wahhab",
                "The Supreme Bestower", "Mai Yawan Kyauta Ba Tare da Neman Lada Ba",
                "Ya Wahhab, Ka yi mini baiwa da kyauta ta musamman daga taskarKa; Ka ba ni hikima, zuriya tagari, da arziki mai albarka.",
                "O Supreme Bestower, bestow upon me unconditional blessings, wisdom, righteous lineage, and unending bounties.",
                "Iwọ Oluranlọwọ nla, fi ọgbọn, ọmọ rere, ati ọrọ ti o kun fun ibukun jinki mi lati inu ile-iṣura Rẹ.",
                "Onye Na-enye Onyinye Kachasị, nye m amamihe, ezi ụmụ, na akụ na ụba jupụtara na ngọzi."
            ),
            AsmaDetail(
                17, "الرَّزَّاقُ", "Ar-Razzaq",
                "The Total Provider", "Mai Azurtawa da Ciyar da Halittu",
                "Ya Razzaq, Ka buɗe mini ƙofofin arziƙi na halal mai yalwa, Ka albarkaci abin da Ka ba ni ta inda ban taba zato ba.",
                "O Ultimate Provider, open the doors of abundant halal sustenance for me from sources I never could imagine.",
                "Iwọ Olupese Gbogbo Ẹda, ṣi awọn ilẹkun ipese halal ti o pọ̀ fun mi lati ibi ti Emi ko lero.",
                "Onye Na-enye Ihe Niile, mepere m ụzọ nke nri halal bara ụba site n'ebe m na-atụghị anya ya."
            ),
            AsmaDetail(
                18, "الْفَتَّاحُ", "Al-Fattah",
                "The Supreme Opener", "Mai Buɗewa da Hukunci",
                "Ya Fattah, Ka buɗe mini dukkan ƙofofin nasara, ilmi, alheri da arziƙi da suka rufe a rayuwata.",
                "O Supreme Opener, open wide every closed door of success, knowledge, goodness, and victory in my life.",
                "Iwọ Olusi Ilẹkun Rere, ṣi gbogbo ilẹkun aṣeyọri, imọ ati ibukun ti o ti tì mọ mi ninu ayé mi.",
                "Onye Na-emeghe Ụzọ Kachasị, mepee ụzọ niile nke ihe ịga nke ọma, mmụta na ngọzi mechiri emechi."
            ),
            AsmaDetail(
                19, "الْعَلِيمُ", "Al-Alim",
                "The All-Knowing", "Masanin Dukkan Abin da Ya Ɓoyu da na Fili",
                "Ya Alim, Ka sanar da ni ilmi mai amfani a addinina da rayuwata, Ka kiyaye ni daga jahilci da bin son rai.",
                "O The All-Knowing, endow me with beneficial knowledge, wisdom, and safeguard me against ignorance and misguided desires.",
                "Iwọ Masanin Gbogbo Nkan, fun mi ni imọ ti o wulo ninu ẹsin ati ayé mi, ki O si gba mi kuro ninu aimọkan.",
                "Onye Maara Ihe Niile, nye m ihe ọmụma bara uru ma chebe m pụọ n'amaghị ihe na agụụ ọjọọ."
            ),
            AsmaDetail(
                20, "الْقَابِضُ", "Al-Qabid",
                "The Restrainer", "Mai Damƙewa da Riƙe Abu Don Hikima",
                "Ya Qabid, Ka riƙe mini raina a kan imani yayin mutuwa, Ka danne son zuciyata da sha'awar aikata sabo.",
                "O The Restrainer, withhold my soul only upon pure faith at death, and restrain my base desires from sin.",
                "Iwọ Oludaduro, di ẹmi mi mu lori igbagbọ ododo nigbati ikú ba de, ki O si ká ifẹkufẹ ẹṣẹ ninu mi.",
                "Onye Na-ejide Ihe, jide mkpụrụ obi m n'okwukwe mgbe m na-anwụ, ma gbochie agụụ mmehie n'ime m."
            ),
            AsmaDetail(
                21, "الْبَاسِطُ", "Al-Basit",
                "The Expander & Enlarger", "Mai Yalwata Arziƙi da Rahama",
                "Ya Basit, Ka yalwata mini ƙirjina da natsuwa, Ka yalwata arziƙina da rayuwata cikin sauƙi da farin ciki.",
                "O The Expander, expand my heart with peace and certainty, and expand my provisions with ease and abundant barakah.",
                "Iwọ Olugbooro Ipese, gbooro àyà mi pẹlu alaafia, ki O si gbooro ounjẹ ati ọrọ mi pẹlu irọrun.",
                "Onye Na-agbasa Ihe, gbasaa obi m n'udo ma gbasaa akụ na ụba m n'ụzọ dị mfe ma gọzie ya."
            ),
            AsmaDetail(
                22, "الْخَافِضُ", "Al-Khafid",
                "The Abaser", "Mai Ƙasƙantar da Azzalumai",
                "Ya Khafid, Ka ƙasƙantar da maƙiya da azzalumai masu nufina da sharri, Ka tsare ni daga faɗawa cikin kaskanci.",
                "O The Abaser, abase every oppressor and schemer against me, and protect me from moral and spiritual downfall.",
                "Iwọ Olurẹlẹ Awọn Alagidi, rẹ gbogbo awọn ọta ati aninilara lẹlẹ, ki O si gba mi lọwọ itiju.",
                "Onye Na-eweda Ndị Mpako n'Ala, wedata ndị na-emegbu m n'ala ma chebe m pụọ n'ihe ihere."
            ),
            AsmaDetail(
                23, "الرَّافِعُ", "Ar-Rafi'",
                "The Exalter", "Mai Ɗaukaka Darajar Bayi",
                "Ya Rafi', Ka ɗaukaka darajata, matsayina, da sunana a duniya da lahira ta hanyar ilmi da taƙawa.",
                "O The Exalter, raise my rank, honour, and standing in this world and the Hereafter through knowledge and righteous piety.",
                "Iwọ Olugbega, gbe ipo mi ati orukọ mi ga ni ayé ati lẹ́yìn ikú pẹlu imọ ati ibẹru Ọlọhun.",
                "Onye Na-ebuli Elu, bulie ọnọdụ m na aha m elu n'ụwa a na n'ọdịnihu site n'amamihe na nsọpụrụ Chineke."
            ),
            AsmaDetail(
                24, "الْمُعِزُّ", "Al-Mu'izz",
                "The Giver of Honor", "Mai Bada Ɗaukaka da Izza",
                "Ya Mu'izz, Ka tufatar da ni da izzar musulunci, Ka sa in zama mai kwarjini da daraja a idon mutane.",
                "O Giver of Honor, clothe me with the dignity of Islam and make me respected and honored among creation.",
                "Iwọ Olufunni ni Ọlá, wọ mi ni aṣọ ọlá Islam, ki O si jẹ ki n ni ojurere ati ọla ni oju awọn eniyan.",
                "Onye Na-enye Ugwu, yikwasị m ugwu nke Islam ma mee ka a na-asọpụrụ m n'etiti mmadụ."
            ),
            AsmaDetail(
                25, "الْمُذِلُّ", "Al-Mudhill",
                "The Giver of Dishonor", "Mai Ƙasƙantar da Masu Sabo",
                "Ya Mudhill, Ka ƙasƙantar da duk wani mugu mai neman cutar da ni, Ka tseratar da ni daga zama abin zagi ko wulakanci.",
                "O Giver of Dishonor, humiliate the corrupt schemers who seek my destruction, and save me from disgrace.",
                "Iwọ Olutiju Awọn Ẹlẹṣẹ, ba awọn ẹlẹtan ti o n wa iparun mi jẹ, ki O si gba mi lọwọ itiju ayé.",
                "Onye Na-eweda Ndị Mmehie, mee ka ndị na-achọ mbibi m nwee ihere ma zọpụta m n'ihere."
            ),
            AsmaDetail(
                26, "السَّمِيعُ", "As-Sami'",
                "The All-Hearing", "Mai Jin Dukkan Sauti da Addu'o'i",
                "Ya Sami', Ka ji ƙunshin zuciyata da addu'ata, Ka amsa kukan da nake yi a asirce da fili.",
                "O The All-Hearing, hear the silent whispers of my heart and answer my sincere prayers and cries for help.",
                "Iwọ Olugbọran Gbogbo Ohun, gbọ adura ikọkọ ti ọkàn mi, ki O si dahun igbe ẹbẹ mi.",
                "Onye Na-anụ Ihe Niile, nụrụ ekpere dị n'ime obi m ma zaa arịrịọ m niile."
            ),
            AsmaDetail(
                27, "الْبَصِيرُ", "Al-Basir",
                "The All-Seeing", "Mai Ganin Dukkan Halittu da Sirrika",
                "Ya Basir, Ka duba halin da nake ciki da idon rahamarKa, Ka sa in riƙa jin tsoronKa a ɓoye kamar yadda nake ji a fili.",
                "O The All-Seeing, gaze upon my struggles with Your merciful eyes, and grant me conscious awareness of Your presence.",
                "Iwọ Oluriran Gbogbo Nkan, fi oju aanu Rẹ wo ipo mi, ki O si fun mi ni ibẹru Rẹ ni ikọkọ ati ni gbangba.",
                "Onye Na-ahụ Ihe Niile, lelee ọnọdụ m anya ebere ma mee ka m na-atụ egwu Gị na nzuzo na n'ihu ọha."
            ),
            AsmaDetail(
                28, "الْحَكَمُ", "Al-Hakam",
                "The Impartial Judge", "Alƙali Mai Hukunci da Adalci",
                "Ya Hakam, Ka yi mini hukunci na adalci a kan duk wanda ya zalunce ni, Ka sa in zama mai gaskiya da adalci.",
                "O The Supreme Judge, judge between me and those who have wronged me with perfect justice, and make me truthful.",
                "Iwọ Onidajọ Ododo, ṣe idajọ ododo laarin emi ati awọn ti o se mi ni aiṣododo.",
                "Onye Ikpe Ziri Ezi, kpeere mụ na ndị na-emegbu m ikpe ziri ezi ma mee ka m na-ekwu eziokwu mgbe niile."
            ),
            AsmaDetail(
                29, "الْعَدْلُ", "Al-Adl",
                "The Utterly Just", "Mai Cikakken Adalci",
                "Ya Adl, Ka ba ni ikon yin adalci ga kaina da iyalina da mutane, Ka tseratar da ni daga zalunci ko zaluntar wani.",
                "O The Utterly Just, guide me to uphold justice for myself and others, and preserve me from committing oppression.",
                "Iwọ Ẹni Ti O Peye Ninu Idajọ, fun mi ni agbara lati ṣe deede fun gbogbo eniyan, ki O si gba mi lọwọ aninilara.",
                "Onye Ikpe Zuru Oke, nyere m aka ka m na-eme ihe ziri ezi n'ebe mmadụ niile nọ ma zọpụta m n'imejọ mmadụ."
            ),
            AsmaDetail(
                30, "اللَّطِيفُ", "Al-Latif",
                "The Subtle & Gentle", "Mai Taushin Rahama da Sanin Ɓoyayyun Al'amura",
                "Ya Latif, Ka yi mini taushi da sauƙi a dukkan lamurana, Ka fitar da ni daga kunci ta hanyoyin da ban sani ba.",
                "O The Subtle and Gentle, treat my affairs with gentle kindness and deliver me from distress through subtle, unseen ways.",
                "Iwọ Ẹni Ti Ọwọ́ Rẹ Rọ Ni Aanu, ba mi ṣe awọn nkan mi ni pẹlẹbẹ ki O si mu mi jade ninu inira.",
                "Onye Dị Nwayọ n'Ebere, meere m ihe n'ụzọ dị mfe ma wepụta m n'ahụhụ n'ụzọ m na-amaghị."
            ),
            AsmaDetail(
                31, "الْخَبِيرُ", "Al-Khabir",
                "The All-Aware", "Masani a Kan Dukkan Bayanai da Sirrika",
                "Ya Khabir, Ka san dukkan buƙatuna da damuwata fiye da yadda na sani, Ka zaba mini mafi alheri a dukkan lamurana.",
                "O The All-Aware, You know my deepest needs better than I do; decree what is best for me in all matters.",
                "Iwọ Olumọ-ikọkọ, Iwọ mọ aini mi ju emi lọ, yan ohun ti o dara julọ fun mi ninu gbogbo nkan.",
                "Onye Maara Ihe Nzuzo Niile, Gị maara mkpa m karịa ka m maara, họrọ ihe kacha mma maka m n'ihe niile."
            ),
            AsmaDetail(
                32, "الْحَلِيمُ", "Al-Halim",
                "The Most Forbearing", "Mai Haƙuri da Rangwame",
                "Ya Halim, Ka yi mini haƙuri da rangwame a kan kuskurena da zunubaina, kada Ka yi mini gaggawar uƙuba.",
                "O Most Forbearing, bear with my shortcomings, overlook my transgressions, and do not hasten punishment upon me.",
                "Iwọ Alafarada, dariji awọn aṣiṣe mi, ki O ma ṣe yara fi ijiya ba mi lori awọn ẹṣẹ mi.",
                "Onye Na-enwe Ndidi Kachasị, gbaghara adịghị ike m ma emela ngwa ngwa n'inye m ahụhụ."
            ),
            AsmaDetail(
                33, "الْعَظِيمُ", "Al-Azim",
                "The Magnificent", "Mai Girma Madaukaki",
                "Ya Azim, Ka sanya girmanka da kwarjininKa a zuciyata, Ka tseratar da ni daga girman azabar wutar Jahannama.",
                "O The Magnificent, fill my heart with awe and reverence for You, and deliver me from the terrifying torment of Hellfire.",
                "Iwọ Ọba Titobi, fi ọlá ati iberu Rẹ kun ọkàn mi, ki O si gba mi kuro ninu ina Jahannama.",
                "Onye Ukwu n'Ebube, mee ka obi m jupụta n'egwu Gị ma zọpụta m n'ọkụ ala mmụọ."
            ),
            AsmaDetail(
                34, "الْغَفُورُ", "Al-Ghafur",
                "The Great Forgiver", "Mai Yafiyar Zunubai",
                "Ya Ghafur, Ka gafarta mini zunubaina baki ɗaya, Ka shafe laifukana kuma kada Ka tona asirina.",
                "O Most Forgiving, grant me complete pardon for all my sins, wipe away my faults, and never expose my shortcomings.",
                "Iwọ Alaforiji, dari gbogbo ẹṣẹ mi jin mi patapata, ki O si bo awọn aṣiṣe mi mọlẹ.",
                "Onye Na-agbaghara Mmehie, gbaghara m mmehie m niile ma kpuchie adịghị ike m niile."
            ),
            AsmaDetail(
                35, "الشَّكُورُ", "Ash-Shakur",
                "The Most Appreciative", "Mai Godiya da Bada Lada Mai Yawa",
                "Ya Shakur, Ka karɓi ɗan ƙaramin aiki na da ibadata, Ka ninka mini ladana da falalarKa marar iyaka.",
                "O Most Appreciative, accept my meager deeds, multiply their rewards exponentially, and grant me endless blessings.",
                "Iwọ Olumọriri Iṣẹ, gba iṣẹ ijosin kekere mi, ki O si ṣe afikun ẹsan ati ibukun fun mi.",
                "Onye Na-enye Ụgwọ Ọrụ Bara Ụba, nabata obere ofufe m ma mụbaa ụgwọ ọrụ m na ngọzi Gị."
            ),
            AsmaDetail(
                36, "الْعَلِيُّ", "Al-Aliyy",
                "The Most High", "Madaukaki Sama da Kowa",
                "Ya Aliyy, Ka ɗaga darajata da imanina, Ka sanya ni cikin sahun bayinKa maɗaukaka a Aljannatul Firdaus.",
                "O The Most High, elevate my spiritual standing and place me among the highest ranks in Jannatul Firdaus.",
                "Iwọ Ọga-ogo, gbe ipo ẹmi mi ga, ki O si fi mi sinu awọn ipo giga julọ ninu Alujanna Firdaus.",
                "Onye Kasị Elu, bulie ọnọdụ mmụọ m elu ma tinye m n'ọkwa kachasị elu na Paradaịs Firdaus."
            ),
            AsmaDetail(
                37, "الْكَبِيرُ", "Al-Kabir",
                "The Incomparably Great", "Babba Wanda Babu Mai Kama da Shi",
                "Ya Kabir, Ka sa dukkan matsala da damuwa su zama ƙanana a idona idan na tuna girmanka, Ka isar mini a kan kowane al'amari.",
                "O The Incomparably Great, make all worldly troubles small in my eyes compared to Your greatness, and suffice me in everything.",
                "Iwọ Ẹni Titobi, jẹ ki gbogbo aniyan ayé di kekere ni oju mi niwaju titobi Rẹ.",
                "Onye Ukwu Na-enweghị Ntụnyere, mee ka nsogbu ụwa niile dị obere n'anya m n'ihu ịdị ukwuu Gị."
            ),
            AsmaDetail(
                38, "الْحَفِيظُ", "Al-Hafiz",
                "The Preserver & Protector", "Mai Tsarewa da Kula da Halittu",
                "Ya Hafiz, Ka tsare jikina, imanina, iyalina, da dukiyata daga shaidanu, sihiri, mugun ido, hatsari da cututtuka.",
                "O Ultimate Preserver and Protector, guard my faith, body, family, and wealth from devils, evil eye, accidents, and diseases.",
                "Iwọ Oludabobo Gbogbo Nkan, pa igbagbọ, ara, ẹbi ati ọrọ mi mọ́ kuro lọwọ eṣu, oju ibi ati aisan.",
                "Onye Nchebe Kasị Elu, chebe okwukwe m, ahụ m, ezinụlọ m na akụ m pụọ n'aka ekwensu na anya ọjọọ."
            ),
            AsmaDetail(
                39, "الْمُقِيتُ", "Al-Muqit",
                "The Nourisher & Sustainer", "Mai Ciyarwa da Ƙarfafa Rayuwa",
                "Ya Muqit, Ka ciyar da zuciyata da hasken ambatonKa, Ka azurta jikina da lafiya da abinci mai albarka.",
                "O All-Nourisher and Sustainer, nourish my heart with Your remembrance and bless my body with strength and wholesome sustenance.",
                "Iwọ Olubọ-ẹda, fi imọlẹ iranti Rẹ bọ́ ọkàn mi, ki O si fun ara mi ni ilera ati ounjẹ alala.",
                "Onye Na-azụ Ihe Niile, zụọ obi m site na ncheta Gị ma nye ahụ m ike na nri dị mma."
            ),
            AsmaDetail(
                40, "الْحَسِيبُ", "Al-Hasib",
                "The Reckoner & Sufficient", "Mai Ƙididdiga da Isarwa ga Bayi",
                "Ya Hasib, Ka isar mini a kan dukkan buƙatu da damuwata, Ka sauƙaƙa mini hisabi a ranar sakamako.",
                "O The Reckoner and Sufficient One, be sufficient for me in all my needs and grant me an easy accounting on the Day of Judgment.",
                "Iwọ Oluka Iṣẹ ati Oluto fun gbogbo aini, to fun mi ninu ohun gbogbo ki O si jẹ ki iṣiro mi rọrun ni Ọjọ Idajọ.",
                "Onye Na-agụta Ihe Niile ma Zuru Ezu, zuru m n'ihe niile m chọrọ ma mee ka ngụkọ m dị mfe n'ụbọchị ikpe."
            ),
            AsmaDetail(
                41, "الْجَلِيلُ", "Al-Jalil",
                "The Majestic & Sublime", "Ma'abucin Ɗaukaka da Cikar Girma",
                "Ya Jalil, Ka tufatar da ni da kwarjini da mutunci, Ka kiyaye ni daga dukkan abubuwan da ke rage mutunci da daraja.",
                "O The Majestic and Sublime, clothe me with moral dignity and honor, and preserve me from anything that diminishes my character.",
                "Iwọ Ọba Ọlọla, fi iwa rere ati ọlá wọ̀ mi, ki O si pa mi mọ kuro ninu ohun ti o n ba orukọ rere jẹ.",
                "Onye Ebube, yikwasị m ugwu na ezi agwa, ma chebe m pụọ n'ihe na-emebi aha ọma."
            ),
            AsmaDetail(
                42, "الْكَرِيمُ", "Al-Karim",
                "The Most Generous", "Mai Yawan Karamci da Baiwa",
                "Ya Karim, Ka kyautata mini da falalarKa ba tare da hisabi ba, Ka sa in zama mai karamci da kyautatawa ga mutane.",
                "O The Most Generous, bestow upon me Your boundless generosity without measure, and make me generous to others.",
                "Iwọ Ọlawọ Jùlọ, fi oore-ọfẹ Rẹ ti ko ni odiwọn jinki mi, ki O si jẹ ki n jẹ ọlawọ fun awọn ẹlomiran.",
                "Onye Na-emesapụ Aka Kachasị, nye m amara Gị na-enweghị oke ma mee ka m na-emesapụ aka nye ndị ọzọ."
            ),
            AsmaDetail(
                43, "الرَّقِيبُ", "Ar-Raqib",
                "The All-Watchful", "Mai Tsaron Komai Ba Tare da Mantawa Ba",
                "Ya Raqib, Ka sanya ni in ji tsoronKa a ɓoye da bayyane, Ka tsare iyalina da mutuncina a ƙarƙashin tsaronKa.",
                "O The All-Watchful, bless me with constant mindfulness of You, and keep my family and honor under Your vigilant care.",
                "Iwọ Olutọju Agba, jẹ ki n ni ibẹru Rẹ nigba gbogbo, ki O si pa ẹbi ati ọlá mi mọ́.",
                "Onye Na-eche Nche Mgbe Niile, mee ka m na-echeta Gị mgbe niile ma chebe ezinụlọ m na ugwu m."
            ),
            AsmaDetail(
                44, "الْمُجِيبُ", "Al-Mujib",
                "The Responsive Answerer", "Mai Amsa Addu'ar Masu Roƙo",
                "Ya Mujib, Ka amsa dukkan addu'o'ina da buƙatuna na alheri a duniya da lahira, kada Ka mayar da hannayena wayam.",
                "O The Responsive Answerer of Prayers, accept all my righteous prayers and never turn my outstretched hands away empty.",
                "Iwọ Oludahun Adura, gba gbogbo adura rere mi, ma ṣe jẹ ki ọwọ́ mi pada ni ofo.",
                "Onye Na-aza Ekpere, nara ekpere m niile dị mma ma emela ka aka m laghachi n'efu."
            ),
            AsmaDetail(
                45, "الْوَاسِعُ", "Al-Wasi'",
                "The All-Encompassing", "Mai Yalwar Rahama, Ilmi, da Ni'ima",
                "Ya Wasi', Ka yalwata mini rahamarKa, arziƙinKa, da ilminKa wanda ya game kowace halitta, Ka yaye mini kowane irin ƙunci.",
                "O The All-Encompassing, expand for me Your boundless mercy, provisions, and knowledge, and relieve me of every hardship.",
                "Iwọ Oníwá-àyè Gbooro, gbooro aanu, imọ ati ipese Rẹ fun mi, ki O si yọ gbogbo iponju kuro lori mi.",
                "Onye Karịrị Ihe Niile, gbasaara m ebere Gị, ihe ọmụma na nri, ma wepụ ihe isi ike niile n'ebe m nọ."
            ),
            AsmaDetail(
                46, "الْحَكِيمُ", "Al-Hakim",
                "The All-Wise", "Mai Hikima a Cikin Dukkan AyyukanSa",
                "Ya Hakim, Ka ba ni hikima da basirar fahimtar addini da yanke shawara madaidaiciya a rayuwata.",
                "O The All-Wise, bestow upon me wisdom, clarity, and sound discernment in every decision of my life.",
                "Iwọ Ọlọgbọn Pipe, fun mi ni ọgbọn, oye ẹsin ati ipinnu ti o tọ ninu gbogbo igbesi ayé mi.",
                "Onye Amamihe Zuru Oke, nye m amamihe, nghọta nke okwukwe na mkpebi ziri ezi na ndụ m."
            ),
            AsmaDetail(
                47, "الْوَدُودُ", "Al-Wadud",
                "The Loving One", "Mai Ƙaunar BayanSa Salihai",
                "Ya Wadud, Ka so ni, Ka sanya Mala'ikunKa da bayinKa salihai su so ni, Ka cika gidana da soyayya da zaman lafiya.",
                "O The Loving One, love me, make Your righteous angels and servants love me, and fill my home with affection and tranquility.",
                "Iwọ Ọba Olufẹẹ, fẹ́ mi, jẹ ki awọn mọlaika ati awọn olododo fẹ́ mi, ki O si fi ifẹ kun ile mi.",
                "Onye Ịhụnanya Zuru Oke, hụ m n'anya, mee ka ndị mmụọ ozi na ndị ezi omume hụ m n'anya, ma jupụta ụlọ m n'ịhụnanya."
            ),
            AsmaDetail(
                48, "الْمَجِيدُ", "Al-Majid",
                "The Most Glorious", "Mai Girma da Cikar Kyawawan Siffofi",
                "Ya Majid, Ka cika rayuwata da ɗaukaka, albarka da kyakkyawan ƙarshe, Ka ɗaukaka addinin musulunci a zuciyata.",
                "O The Most Glorious, crown my life with divine honor, barakah, and a noble ending upon true faith.",
                "Iwọ Ologo Jùlọ, fi ọla nla, ibukun ati ipari rere dé ayé mi lori igbagbọ ododo.",
                "Onye Ebube Kachasị, chọọ ndụ m mma site n'ugwu, ngọzi na ezi ọgwụgwụ n'okwukwe."
            ),
            AsmaDetail(
                49, "الْبَاعِثُ", "Al-Ba'ith",
                "The Resurrector", "Mai Tayar da Halittu Ranar Ƙiyama",
                "Ya Ba'ith, Ka raya zuciyata da hasken imani da shiriya, Ka tada ni ranar ƙiyama tare da Annabi Muhammad (ﷺ).",
                "O The Resurrector, revive my dead heart with faith and guidance, and resurrect me on the Day of Reckoning with the Prophet (ﷺ).",
                "Iwọ Olujinde Awọn Oku, ji ọkàn mi dide pẹlu imọlẹ igbagbọ, ki O si ji mi dide pẹlu Ànábì Muhammad (SAW).",
                "Onye Na-akpọlite Ndị Nwụrụ Anwụ, mee ka obi m dị ndụ site n'okwukwe ma kpọlite m n'ụbọchị ikpe ya na Onye Amụma (SAW)."
            ),
            AsmaDetail(
                50, "الشَّهِيدُ", "Ash-Shahid",
                "The All-Witnessing", "Mai Shaida a Kan Komai",
                "Ya Shahid, Ka shaida cewa na yarda da Kai a matsayin Ubangiji na gaskiya, Ka tsare ni daga saɓa maKa a ɓoye.",
                "O The All-Witnessing, bear witness that I testify You are my only Lord, and save me from committing sins in secret.",
                "Iwọ Ẹlẹ́rìí Gbogbo Ohun, jẹri pe Mo jẹri pe Iwọ ni Ọlọhun mi, ki O si pa mi mọ kuro ninu ẹṣẹ ikọkọ.",
                "Onye Akaebe nke Ihe Niile, gbaa akaebe na m kwenyere na Gị bụ Chineke m, ma zọpụta m pụọ na mmehie nzuzo."
            ),
            AsmaDetail(
                51, "الْحَقُّ", "Al-Haqq",
                "The Absolute Truth", "Gaskiya Tabbatacciya",
                "Ya Haqq, Ka nuna mini gaskiya a matsayin gaskiya Ka ba ni ikon binta, Ka nuna mini ƙarya a matsayin ƙarya Ka ba ni ikon guje mata.",
                "O The Absolute Truth, show me truth as truth and enable me to follow it, and show me falsehood as falsehood and enable me to avoid it.",
                "Iwọ Otitọ Titi Lailai, fi otitọ han mi ki O si fun mi ni agbara lati tẹle e, ki O si pa mi mọ kuro ninu irọ.",
                "Eziokwu Zuru Oke, gosi m eziokwu ma nye m ike ịgbaso ya, ma gosi m ụgha ka m zere ya."
            ),
            AsmaDetail(
                52, "الْوَكِيلُ", "Al-Wakil",
                "The Ultimate Trustee", "Abin Dogaro Wanda Ya Isa Dogaro da Shi",
                "Ya Wakil, Na miƙa dukkan al'amurana gare Ka, Ka isar mini, Ka zame mini mai tsarewa da warware mini kowace damuwa.",
                "O The Ultimate Trustee, I entrust all my affairs into Your hands; be my advocate, protector, and sufficient helper.",
                "Iwọ Olugbẹkẹle Titobi, mo fi gbogbo ọ̀ràn mi le Ọ lọwọ, to fun mi ki O si yanju gbogbo iṣoro mi.",
                "Onye Nlekọta Kasị Elu, ana m atụkwasị ihe niile n'aka Gị; buru onye nchebe m ma dozie nsogbu m niile."
            ),
            AsmaDetail(
                53, "الْقَوِيُّ", "Al-Qawiyy",
                "The All-Strong", "Mai Cikakken Ƙarfi da Iko",
                "Ya Qawiyy, Ka ƙarfafa raunin jikina da imanina, Ka ba ni kariya daga zaluncin masu ƙarfi da maƙiya.",
                "O The All-Strong, strengthen my weak body and fragile faith, and shield me against the oppression of the strong.",
                "Iwọ Alagbara Jùlọ, mu ailera ara ati igbagbọ mi le, ki O si gba mi lọwọ ipa awọn aninilara.",
                "Onye Ike Niile Dịịrị, mee ka ahụ m na okwukwe m dị ike ma chebe m pụọ n'ike nke ndị na-emegbu mmadụ."
            ),
            AsmaDetail(
                54, "الْمَتِينُ", "Al-Matin",
                "The Firm & Steadfast", "Ƙaƙƙarfa Wanda ƘarfinSa Ba Ya Ƙarewa",
                "Ya Matin, Ka tabbatar da ƙafafuna a kan tafarkin gaskiya da ibada, kada Ka bar ni in karkata ko in gajiya.",
                "O The Firm and Steadfast, make my feet steadfast upon the straight path and righteous worship, never letting me waver.",
                "Iwọ Ẹni Ti Agbara Rẹ Ki I Yẹ, fi ẹsẹ mi mulẹ lori ọna ododo, ma jẹ ki n yapa kuro ninu ijosin Rẹ.",
                "Onye Kwụgidere Ike, mee ka ụkwụ m guzosie ike n'ụzọ ziri ezi, emela ka m si n'ezi ofufe pụọ."
            ),
            AsmaDetail(
                55, "الْوَلِيُّ", "Al-Waliyy",
                "The Protecting Ally", "Majibincin Al'amuran BayanSa",
                "Ya Waliyy, Ka jibinci dukkan al'amurana na duniya da lahira, Ka zame mini masoyi, mai taimako da kariya a kowane hali.",
                "O The Protecting Friend and Ally, take full charge of my affairs in this life and the next, and be my constant helper.",
                "Iwọ Oluranlọwọ ati Ọrẹ Olododo, ṣe itọju gbogbo nkan mi ni ayé ati lẹ́yìn ikú, ki O si jẹ olugbeja mi.",
                "Onye Enyemaka na Enyi nke Ezi Omume, lekọta ihe niile gbasara m n'ụwa a na n'ọdịnihu ma bụrụ onye na-enyere m aka."
            ),
            AsmaDetail(
                56, "الْحَمِيدُ", "Al-Hamid",
                "The Praiseworthy", "Abin Godiya a Cikin Dukkan Halaye",
                "Ya Hamid, Ka sa in zama mai yawan godiya da yabonKa a cikin sauƙi da tsanani, Ka sanya yabonKa ya zama abincin raina.",
                "O The Praiseworthy, make my tongue moist with Your praise in ease and hardship, and make me truly grateful.",
                "Iwọ Ẹni Ti O Yẹ Fun Gbogbo Ọpẹ́, jẹ ki ahọn mi ma yin Ọ ni igba irọrun ati igba iṣoro.",
                "Onye Kwesịrị Otuto Niile, mee ka ire m na-eto Gị n'oge dị mfe na n'oge ihe isi ike."
            ),
            AsmaDetail(
                57, "الْمُحْصِي", "Al-Muhsi",
                "The All-Accounter", "Mai Ƙididdigar Komai Ba Tare da Mantawa Ba",
                "Ya Muhsi, Ka san dukkan ayyukana, Ka gafarta kuskuren da na manta da shi, Ka rubuta mini lada a kan kowace daƙiƙa ta rayuwata.",
                "O The All-Accounter, pardon the sins I have forgotten, and count every moment of my life as a source of good deeds.",
                "Iwọ Oluka Ohun Gbogbo, dariji awọn ẹṣẹ ti mo ti gbagbe, ki O si ka gbogbo akoko ayé mi si iṣẹ rere.",
                "Onye Na-agụta Ihe Niile, gbaghara mmehie m chefuru echefu ma gụọ oge ndụ m niile maka ezi ọrụ."
            ),
            AsmaDetail(
                58, "الْمُبْدِئُ", "Al-Mubdi'",
                "The Originator", "Mai Fara Halitta Daga Farko",
                "Ya Mubdi', Ka fara mini sabuwar rayuwa mai cike da albarka, nasara, farin ciki da shiriya ta gaskiya.",
                "O The Originator, initiate for me a new chapter of life filled with blessings, success, happiness, and authentic guidance.",
                "Iwọ Olupilẹṣẹ Ohun Gbogbo, bẹrẹ igbesi ayé titun ti o kun fun ibukun ati aṣeyọri fun mi.",
                "Onye Mmalite Ihe Niile, bidooro m ndụ ọhụrụ jupụtara na ngọzi, ihe ịga nke ọma na obi ụtọ."
            ),
            AsmaDetail(
                59, "الْمُعِيدُ", "Al-Mu'id",
                "The Restorer", "Mai Mayar da Halitta Bayan Mutuwa",
                "Ya Mu'id, Ka mayar mini da duk wani alheri da na rasa, Ka dawo mini da lafiyata, arziƙina da farin cikina da suka salwanta.",
                "O The Restorer, restore unto me all the goodness and blessings I have lost, and revive my health and happiness.",
                "Iwọ Oluda Ohun Pada, da gbogbo oore, ilera ati ayọ ti mo ti padanu pada fun mi pẹlu oore-ọfẹ Rẹ.",
                "Onye Na-eweghachi Ihe, weghachiri m ihe ọma niile, ahụ ike na obi ụtọ m tụfuru."
            ),
            AsmaDetail(
                60, "الْمُحْيِي", "Al-Muhyi",
                "The Giver of Life", "Mai Rayar da Matattu",
                "Ya Muhyi, Ka raya zuciyata da hasken ambatonKa da Alkur'ani, Ka sa in mutu a kan kalmar Shahada.",
                "O The Giver of Life, revive my heart with the light of the Quran and Your remembrance, and cause me to die upon pure Tawhid.",
                "Iwọ Olufunni ni Ẹmi, sọ ọkàn mi di aye pẹlu imọlẹ Al-Qur'an, ki O si jẹ ki n kú lori kalimatu Shahada.",
                "Onye Na-enye Ndụ, mee ka obi m dị ndụ site na Quran ma mee ka m nwụọ n'ezi nkwupụta okwukwe."
            ),
            AsmaDetail(
                61, "الْمُمِيتُ", "Al-Mumit",
                "The Bringer of Death", "Mai Kashe Masu Rai",
                "Ya Mumit, Ka kashe son zuciyata da sha'awar zunubi a cikina, Ka sauƙaƙa mini fitar rai yayin mutuwa.",
                "O Bringer of Death, extinguish corrupt desires and sins within me, and grant me a peaceful, painless transition at death.",
                "Iwọ Olupani, pa awọn ifẹkufẹ ẹṣẹ ninu mi, ki O si jẹ ki ikú mi jẹ irọrun pẹlu alaafia.",
                "Onye Na-ewepụ Ndụ, gbuo agụụ mmehie dị n'ime m ma mee ka ọnwụ m dị mfe ma dị nwayọ."
            ),
            AsmaDetail(
                62, "الْحَيُّ", "Al-Hayy",
                "The Ever-Living", "Mai Rai Madawwami Wanda Ba Ya Mutuwa",
                "Ya Hayy, Ya Mai Rai, Ka ba ni cikakkiyar rayuwa mai albarka da kariya, Ka tsare ni daga mutuwa cikin saɓo.",
                "O The Ever-Living, grant me a blessed and wholesome life, and protect me from facing death in a state of disobedience.",
                "Iwọ Alaye Titi Lailai, fun mi ni igbesi ayé ti o kun fun ibukun, ki O si pa mi mọ kuro ninu ikú buburu.",
                "Onye Dị Ndụ Mgbe Niile, nye m ndụ a gọziri agọzi ma chebe m pụọ n'ọnwụ n'ọnọdụ mmehie."
            ),
            AsmaDetail(
                63, "الْقَيُّومُ", "Al-Qayyum",
                "The Self-Sustaining", "Tsayayye Mai Kula da Tsayuwar Halittu",
                "Ya Qayyum, da rahamarKa nake neman agaji, Ka gyara mini dukkan al'amurana, kada Ka bar ni da kaina ko da na ƙiftawar ido guda ne.",
                "O The Self-Sustaining, by Your mercy I seek assistance; rectify all my affairs and never leave me to myself even for a blink.",
                "Iwọ Olugbẹkẹle Gbogbo Ẹda, pẹlu aanu Rẹ ni mo n wa iranlọwọ; tun gbogbo ọ̀ràn mi ṣe, ma fi mi silẹ fun ara mi.",
                "Onye Na-elekọta Ihe Niile n'Onwe Ya, site n'ebere Gị ka m na-arịọ enyemaka; mezie ihe niile gbasara m."
            ),
            AsmaDetail(
                64, "الْوَاجِدُ", "Al-Wajid",
                "The All-Finding", "Mai Samun Duk Abin da Yake So",
                "Ya Wajid, Ka wadata ni daga taskarKa da arziƙin da ba ya ƙarewa, Ka sanya ni in wadatu da abin da Ka ba ni.",
                "O The All-Finding and Resourceful, enrich me from Your endless treasures and make me content with what You decree.",
                "Iwọ Ẹni Ti Ko Ṣe Alaini Nkan Kan, sọ mi di ọlọ́rọ̀ lati inu ile-iṣura Rẹ ki O si jẹ ki n ni itẹlọrun.",
                "Onye Na-enwe Ihe Niile, mee ka m baa ọgaranya site n'akụ Gị ma mee ka m nwee afọ ojuju n'ihe I nyere m."
            ),
            AsmaDetail(
                65, "الْمَاجِدُ", "Al-Majid",
                "The Noble & Generous", "Mai Karramawa da Ɗaukaka",
                "Ya Majid, Ka karrama ni da shiga Aljanna, Ka ɗaukaka iyalina da zuriata cikin mutunci da daraja.",
                "O The Noble and Magnificent, honor me with entry into Paradise, and elevate my family and offspring with dignity.",
                "Iwọ Ọba Ọlọlá Titobi, bu ọlá fun mi lati wọ Alujanna, ki O si gbe ẹbi ati awọn ọmọ mi ga.",
                "Onye Ugwu Kachasị, kwanyere m ugwu ịbanye Paradaịs ma bulie ezinụlọ m elu n'ugwu."
            ),
            AsmaDetail(
                66, "الْوَاحِدُ", "Al-Wahid",
                "The Unique One", "Ɗaya Til Ba Tare da Abokin Tarayya Ba",
                "Ya Wahid, Ka tsarkake zuciyata daga dukkan shirka da riya, Ka sa in bauta maKa Kai kaɗai da ikhlasi.",
                "O The One and Only, purify my heart from all forms of polytheism and ostentation, and grant me sincere devotion to You alone.",
                "Iwọ Ọkan Ṣoṣo, wẹ ọkàn mi mọ́ kuro ninu ẹbọ ati agabagebe, ki O jẹ ki n jọsin fun Ọ nikan pẹlu otitọ.",
                "Onye Ọ dịghị Onye Dị Ka Ya, sachapụ obi m pụọ n'ikpere arụsị ma mee ka m fee naanị Gị n'eziokwu."
            ),
            AsmaDetail(
                67, "الْأَحَدُ", "Al-Ahad",
                "The Indivisible One", "Makaɗaici a Cikin ZatinSa da SiffofinSa",
                "Ya Ahad, Ka haɗa kan al'ummar musulmi a kan gaskiya, Ka tsare imanina daga dukkan rarrabuwa da ɓata.",
                "O The Indivisible Unique One, unite our hearts upon truth and preserve my faith from deviation and division.",
                "Iwọ Ọkan Ṣoṣo Pátápátá, darapọ awọn onigbagbọ lori otitọ, ki O si daabobo igbagbọ mi kuro ninu ipaya.",
                "Onye Pụrụ Iche, mee ka obi anyị dị n'otu n'eziokwu ma chebe okwukwe m pụọ na nghọtahie."
            ),
            AsmaDetail(
                68, "الصَّمَدُ", "As-Samad",
                "The Eternal Refuge", "Wanda Dukkan Halittu Ke Nufa da Buƙatunsu",
                "Ya Samad, Kai kaɗai nake nufa da buƙatuna; Ka biya mini dukkan buƙatun duniya da lahira, kada Ka sa in roƙi wani.",
                "O The Eternal Refuge, You alone I turn to for all my needs; fulfill every need of mine and never make me dependent on creation.",
                "Iwọ Ẹni Ti Gbogbo Ẹda N Kọju Si Fun Aini Wọn, yanju gbogbo aini mi, ma ṣe jẹ ki n bẹ ẹnikẹni miran.",
                "Ebe Mgbaba Ebighị Ebi, naanị Gị ka m na-arịọ mkpa m niile; mezuo ha ma emela ka m dabere n'onye ọ bụla."
            ),
            AsmaDetail(
                69, "الْقَادِرُ", "Al-Qadir",
                "The All-Capable", "Mai Ikon Aikata Duk Abin da Ya So",
                "Ya Qadir, Ka ƙaddara mini alheri a kowane fanni na rayuwata, Ka sauƙaƙa mini abin da ya gagara a wajen mutane.",
                "O The All-Capable, decree goodness for me in every sphere of life, and make possible for me that which seems impossible.",
                "Iwọ Olukapa Ohun Gbogbo, kọ oore fun mi ninu gbogbo nkan, ki O si ṣe ohun ti o ṣoro di irọrun fun mi.",
                "Onye Ike Niile Dịịrị, nye m ihe ọma n'akụkụ niile nke ndụ m ma mee ka ihe siri ike dịrị m mfe."
            ),
            AsmaDetail(
                70, "الْمُقْتَدِرُ", "Al-Muqtadir",
                "The Supreme Determiner", "Mai Cikakken Ikon Zartarwa",
                "Ya Muqtadir, Ka zartar da nasara, waraka, da buɗi a cikin rayuwata ta hanyar cikakken ikonKa.",
                "O The Supreme Determiner, execute victory, complete healing, and boundless expansion in my life through Your power.",
                "Iwọ Oludari Aṣẹ, fi agbara Rẹ mu aṣeyọri, iwosan ati igbega wá sinu igbesi ayé mi.",
                "Onye Na-ekpebi Ihe Niile, mee ka m nwee mmeri, ọgwụgwọ na mmepe site n'ike Gị kachasị elu."
            ),
            AsmaDetail(
                71, "الْمُقَدِّمُ", "Al-Muqaddim",
                "The Expediter", "Mai Gabatar da Wanda Ya So",
                "Ya Muqaddim, Ka gabatar da ni wajen dukkan aikin alheri, shiga Aljanna da sonka, Ka sa in zama na gaba a kowane alheri.",
                "O The Expediter, advance me in every good deed, righteous leadership, and foremost entry into Paradise.",
                "Iwọ Olufi Ohun Siwaju, fi mi siwaju ninu iṣẹ rere, ifẹ Rẹ ati iwọle sinu Alujanna.",
                "Onye Na-ebute Ihe n'Ihu, mee ka m na-ebu ụzọ n'ezi ọrụ niile ma buru ụzọ banye na Paradaịs."
            ),
            AsmaDetail(
                72, "الْمُؤَخِّرُ", "Al-Mu'akhkhir",
                "The Delayer", "Mai Jinkirta Abin da Ya So Don Hikima",
                "Ya Mu'akhkhir, Ka jinkirta mini bala'i, cuta, mutuwa mai muni da dukkan sharri, Ka tsare ni daga faɗawa tarkon shaiɗan.",
                "O The Delayer, delay and repel every calamity, illness, evil death, and misfortune from my path.",
                "Iwọ Olufi Ohun Sẹhin, fi gbogbo ajalu, aisan ati ikú buburu sẹhin kuro lọdọ mi.",
                "Onye Na-egbu Oge Ihe, wepụ ọdachi, ọrịa na ọnwụ ọjọọ pụọ n'ụzọ m n'ihi amamihe Gị."
            ),
            AsmaDetail(
                73, "الْأَوَّلُ", "Al-Awwal",
                "The Very First", "Na Farko Wanda Babu Wani Abu Kafin Shi",
                "Ya Awwal, Ka sa sonKa da bin dokokinKa su zama na farko a dukkan rayuwata da manufofina.",
                "O The Very First, make Your love and obedience the absolute first priority in every aspect of my life.",
                "Iwọ Àkọ́kọ́ Ti Ko Ni Ibẹrẹ, jẹ ki ifẹ Rẹ ati igbọran si Ọ jẹ ohun akọkọ ninu gbogbo ayé mi.",
                "Onye Mbụ Na-enweghị Mmalite, mee ka ịhụnanya Gị na irubere Gị isi bụrụ ihe mbụ na ndụ m."
            ),
            AsmaDetail(
                74, "الْآخِرُ", "Al-Akhir",
                "The Very Last", "Na Ƙarshe Wanda Babu Wani Abu Bayan Shi",
                "Ya Akhir, Ka kyautata ƙarshen rayuwata, Ka sanya mafi alherin kwanakina su zama ranar da zan haɗu da Kai.",
                "O The Very Last, bless the end of my life with righteousness, and make the best of my days the day I meet You.",
                "Iwọ Ipari Ti Ko Ni Opin, ṣe ipari ayé mi ni rere, ki O si jẹ ki ọjọ ti o dara julọ jẹ ọjọ ti n ó pade Rẹ.",
                "Onye Ikpeazụ Na-enweghị Ọgwụgwụ, gọzie ọgwụgwụ ndụ m ma mee ka ụbọchị kacha mma bụrụ ụbọchị m ga-ezute Gị."
            ),
            AsmaDetail(
                75, "الظَّاهِرُ", "Az-Zahir",
                "The Manifest", "Mabayyani Wanda AyoyinSa Suka Bayyana",
                "Ya Zahir, Ka bayyana gaskiya da nasara a cikin rayuwata, Ka biya mini bashin da ke kaina, Ka kare ni daga maƙiya.",
                "O The Manifest, manifest truth and victory in my life, help me settle all my debts, and shield me against adversaries.",
                "Iwọ Ẹni Ti O Han Gbangba, fi otitọ ati iṣẹgun han ninu ayé mi, san awọn gbese mi ki O si gba mi lọwọ awọn ọta.",
                "Onye Pụtara Ìhè, gosipụta eziokwu na mmeri na ndụ m, nyere m aka ịkwụ ụgwọ m ma chebe m pụọ n'aka ndị iro."
            ),
            AsmaDetail(
                76, "الْبَاطِنُ", "Al-Batin",
                "The Hidden", "Ɓoyayye Wanda Idanu Ba Sa Iya Ganin Shi a Duniya",
                "Ya Batin, Ka tsarkake sirrina da zuciyata, Ka wadata ni daga fatara da talauci na ɓoye da na bayyane.",
                "O The Hidden, purify my innermost secrets and heart, and enrich me against internal and external poverty.",
                "Iwọ Ẹni Ti O Wà Ni Ikọkọ, wẹ ọkàn ati asiri mi mọ́, ki O si sọ mi di ọlọ́rọ̀ kuro ninu aini ati osi.",
                "Onye Nzuzo, sachapụ obi m na ihe nzuzo m, ma mee ka m baa ọgaranya pụọ n'ogbenye nke ime na nke elu."
            ),
            AsmaDetail(
                77, "الْوَالِي", "Al-Wali",
                "The Sole Governor", "Mai Mulki da Kula da Dukkan Halittu",
                "Ya Wali, Ka tafiyar da al'amurana cikin aminci da sauƙi, Ka bamu shugabanni nagari masu tausayi da adalci.",
                "O The Sole Governor, govern my life with peace and ease, and grant us compassionate and just leaders.",
                "Iwọ Oludari Ayé, dari ayé mi pẹlu alaafia, ki O si fun wa ni awọn aṣaaju ti o ni aanu ati ododo.",
                "Onye Na-achị Ihe Niile, chịa ndụ m n'udo ma nye anyị ndị isi nwere ebere na ikpe ziri ezi."
            ),
            AsmaDetail(
                78, "الْمُتَعَالِي", "Al-Muta'ali",
                "The Supreme Exalted", "Maɗaukaki Sama da Dukkan Halitta",
                "Ya Muta'ali, Ka ɗaga martabar addinin musulunci, Ka kare mu daga dukkan ƙasƙanci da sharrin masu ƙiyayya.",
                "O The Supreme Exalted One, elevate the status of Islam, and protect us from humiliation and malicious harm.",
                "Iwọ Ẹni Giga Jùlọ, gbe ọla ẹsin Islam ga, ki O si daabobo wa kuro ninu itiju ati aburu.",
                "Onye Kasị Elu n'Ebube, bulie ugwu nke Islam elu ma chebe anyị pụọ n'ihere na ihe ọjọọ."
            ),
            AsmaDetail(
                79, "الْبَرُّ", "Al-Barr",
                "The Source of Goodness", "Mai Yawan Alheri da Tausayi",
                "Ya Barr, Ka sadar da ni da falalarKa da alherinKa, Ka sa in zama mai biyayya da kyautatawa ga iyayena da mutane.",
                "O The Source of All Goodness, bestow upon me Your benign favors, and make me dutiful and kind to my parents and others.",
                "Iwọ Olupese Oore Gbogbo, fi oore Rẹ jinki mi, ki O si jẹ ki n jẹ olugbọran ati oníwà-rere si awọn obi mi.",
                "Isi Iyi nke Ihe Ọma Niile, nye m amara Gị ma mee ka m na-erubere ndị mụrụ m isi ma na-eme ha ihe ọma."
            ),
            AsmaDetail(
                80, "التَّوَّابُ", "At-Tawwab",
                "The Accepter of Repentance", "Mai Karɓar Tubar Masu Tuba",
                "Ya Tawwab, Ka karɓi tubata, Ka wanke zunubaina, Ka ba ni ikon daina saɓo da komawa gare Ka a kowane lokaci.",
                "O Accepter of Repentance, accept my sincere repentance, cleanse my sins, and keep me constantly returning to You.",
                "Iwọ Olugba Ironupiwada, gba ironupiwada mi, wẹ ẹṣẹ mi mọ́, ki O jẹ ki n ma pada si ọdọ Rẹ nigbagbogbo.",
                "Onye Na-anabata Nchegharị, nabata nchegharị m, sachapụ mmehie m ma mee ka m na-alaghachikwute Gị mgbe niile."
            ),
            AsmaDetail(
                81, "الْمُنْتَقِمُ", "Al-Muntaqim",
                "The Avenger of the Oppressed", "Mai Ɗaukar Fansa a Kan Azzalumai",
                "Ya Muntaqim, Ka kwatar wa raunana haƙƙinsu daga hannun azzalumai, Ka kare ni daga zalunci da maciya amana.",
                "O The Avenger of the Oppressed, champion the rights of the weak against tyrants, and protect me from treacherous enemies.",
                "Iwọ Olugbesan Lori Awọn Aninilara, gba ẹtọ awọn alailera lọwọ awọn aninilara, ki O si daabobo mi lọwọ awọn ọdàlẹ̀.",
                "Onye Na-abọ Ọbọ Maka Ndị Na-enweghị Ike, gbapụta ndị na-enweghị ike n'aka ndị ọchịchị aka ike ma chebe m."
            ),
            AsmaDetail(
                82, "الْعَفُوُّ", "Al-Afuww",
                "The Supreme Pardoner", "Mai Goge Zunubai da Yafiya",
                "Ya Afuww, Lallai Ka kasance Mai yafiya Kuma Kana son yafiya, don haka Ka shafe laifukana baki ɗaya.",
                "O The Supreme Pardoner, You are indeed Pardoning and You love to pardon, so completely erase all my sins.",
                "Iwọ Alaforiji Nla, Dájúdájú Iwọ n fẹ́ aforiji, nitorina dari gbogbo ẹṣẹ mi jin mi patapata.",
                "Onye Na-agbaghara Mmehie Kachasị, n'ezie Ị hụrụ mgbaghara n'anya, ya mere hichapụ mmehie m niile kpamkpam."
            ),
            AsmaDetail(
                83, "الرَّؤُوفُ", "Ar-Ra'uf",
                "The Clement & Compassionate", "Mai Tsananin Jinkai da Tausayi",
                "Ya Ra'uf, Ka yi mini tsananin tausayi da rangwame a ranar hisabi da kuma yayin fuskantar tsananin rayuwa.",
                "O Most Clement and Compassionate, shower me with tender compassion on the Day of Reckoning and in all hardships.",
                "Iwọ Alaaanu Titobi, fi aanu ati iyonu Rẹ bọ mi ni Ọjọ Idajọ ati lakoko awọn inira ayé.",
                "Onye Obi Ebere Kachasị, meere m ebere pụrụ iche n'Ụbọchị Ikpe na n'oge nsogbu niile nke ndụ."
            ),
            AsmaDetail(
                84, "مَالِكُ الْمُلْكِ", "Malik-ul-Mulk",
                "Owner of All Sovereignty", "Mamallakin Dukkan Mulki",
                "Ya Malik-al-Mulk, Ka ba ni iko da kariya a kan zuciyata wajen bin gaskiya, Ka azurta ni da dukiya da ilmi mai amfanar addininka.",
                "O Master of all Sovereignty, grant me self-mastery in following the truth, and grant me resources to serve Islam.",
                "Iwọ Ọba Gbogbo Ijọba, fun mi ni agbara lori ọkàn mi lati tẹle otitọ, ki O si fun mi ni ọrọ lati sin ẹsin Rẹ.",
                "Onye Nwe Ọchịchị Niile, nye m ike n'obi m ịgbaso eziokwu ma nye m akụ na ụba iji jee ozi nke Islam."
            ),
            AsmaDetail(
                85, "ذُو الْجَلَالِ وَالْإِكْرَامِ", "Dhul-Jalali wal-Ikram",
                "Lord of Majesty & Generosity", "Ma'abucin Girma da Karramawa",
                "Ya Dhal-Jalali wal-Ikram, Ka karrama ni da ganin kyakkyawar Fuskarka a Aljanna, Ka cika rayuwata da albarka.",
                "O Lord of Majesty and Generosity, honor me with the supreme joy of beholding Your Noble Face in Paradise.",
                "Iwọ Ọba Ọlọla Titobi ati Olurannilọwọ, bu ọla fun mi lati ri Oju Rẹ ninu Alujanna, ki O si fi ibukun kun ayé mi.",
                "Onyenwe Ebube na Mmesapụ Aka, kwanyere m ugwu ịhụ Ihu Gị dị nsọ na Paradaịs ma gọzie ndụ m."
            ),
            AsmaDetail(
                86, "الْمُقْسِطُ", "Al-Muqsit",
                "The Impartial & Equitable", "Mai Daidaito da Kwatar Haƙƙi",
                "Ya Muqsit, Ka kiyaye ni daga tauye haƙƙin kowa, Ka kwatar mini haƙƙina daga duk wanda ya tauye mini a duniya da lahira.",
                "O The Impartial and Equitable, preserve me from injustice, and grant me justice against anyone who deprived me.",
                "Iwọ Onidajọ Titọ, pa mi mọ kuro ninu gbigba ẹtọ ẹlomiran, ki O si gba ẹtọ mi pada fun mi.",
                "Onye Na-eme Ihe Ziri Ezi, chebe m pụọ n'imejọ mmadụ ma weghachite ikike m n'aka ndị megburu m."
            ),
            AsmaDetail(
                87, "الْجَامِعُ", "Al-Jami'",
                "The Gatherer", "Mai Tara Halittu Ranar Sakamako",
                "Ya Jami', Ka tara mu tare da iyayenmu, iyalanmu, da masoyanmu a Aljannatul Firdaus ba tare da hisabi ba.",
                "O The Gatherer, gather me, my parents, family, and loved ones in the highest gardens of Jannatul Firdaus without reckoning.",
                "Iwọ Olukojọpọ Gbogbo Ẹda, ko emi, awọn obi mi ati awọn ayanfe mi jọ sinu Alujanna Firdaus laisi ijiya.",
                "Onye Na-achịkọta Ihe Niile, chịkọta mụ na ndị mụrụ m, ezinụlọ m na ndị m hụrụ n'anya na Paradaịs Firdaus."
            ),
            AsmaDetail(
                88, "الْغَنِيُّ", "Al-Ghaniyy",
                "The Self-Sufficient", "Mawadaci Wanda Ba Ya Buƙatar Kowa",
                "Ya Ghaniyy, Ka wadata ni daga taskarKa mai yalwa, Ka tsare ni daga ƙasƙancin roƙon wani halitta.",
                "O The Self-Sufficient, enrich me from Your limitless bounty so that I never have to beg or rely on any creation.",
                "Iwọ Ọlọ́rọ̀ Ti Ko Ṣe Alaini Nkan, sọ mi di ọlọ́rọ̀ lati inu oore Rẹ, ma jẹ ki n bẹ ẹnikẹni ninu ẹda Rẹ.",
                "Onye Ọgaranya zuru oke, mee ka m baa ọgaranya site n'amara Gị ka m ghara ịrịọ onye ọ bụla ihe n'ụwa."
            ),
            AsmaDetail(
                89, "الْمُغْنِي", "Al-Mughni",
                "The Ultimate Enricher", "Mai Azurtawa da Wadatar da BayanSa",
                "Ya Mughni, Ka wadata zuciyata da natsuwa, Ka azurta ni da arziƙi mai yalwa da albarka don in taimaki addininka.",
                "O The Ultimate Enricher, enrich my heart with contentment, and bless me with wealth to spend generously in Your cause.",
                "Iwọ Olusọni di Ọlọ́rọ̀, fi itẹlọrun kun ọkàn mi, ki O si fun mi ni ọrọ pẹpẹ lati lo fun oju-ọna Rẹ.",
                "Onye Na-eme Ka Mmadụ Baa Ọgaranya, mee ka obi m nwee afọ ojuju ma nye m akụ m ga-eji jeere Gị ozi."
            ),
            AsmaDetail(
                90, "الْمَانِعُ", "Al-Mani'",
                "The Withholder & Protector", "Mai Hanawa Don Hikima da Kariya",
                "Ya Mani', Ka hana dukkan sharri, musiba, sihiri, da cuta daga samuna, Ka tsare ni daga abubuwan da za su cutar da ni.",
                "O The Withholder and Preventer of Harm, shield and prevent every evil, tragedy, illness, and witchcraft from reaching me.",
                "Iwọ Oludena Ibi, dènà gbogbo aburu, ajalu, idan ati aisan kuro lọdọ mi pẹlu aabo Rẹ.",
                "Onye Na-egbochi Ihe Ọjọọ, gbochie ihe ọjọọ niile, ọrịa na amoosu ka ha ghara irute m."
            ),
            AsmaDetail(
                91, "الضَّارُّ", "Ad-Darr",
                "The Lord of Affliction & Trial", "Mai Hukunta Wanda Ya So Don Hikima",
                "Ya Darr, Ka kare ni daga cutarwar shaidanu da mutane, Ka sanya dukkan cutarwa ta zama kaffarar zunubaina.",
                "O Lord of Affliction and Trial, protect me from the harms of creatures, and make every adversity an expiation for my sins.",
                "Iwọ Olufunni ni Idanwo, daabobo mi lọwọ ipalara awọn eṣu ati eniyan, ki O si jẹ ki inira mi jẹ aforiji ẹṣẹ.",
                "Onye Na-achịkwa Ahụhụ na Ule, chebe m pụọ n'ihe mmerụ nke mmadụ ma mee ka ahụhụ m bụrụ mgbaghara mmehie."
            ),
            AsmaDetail(
                92, "النَّافِعُ", "An-Nafi'",
                "The Source of All Benefit", "Mai Bada Amfani da Alheri",
                "Ya Nafi', Ka amfanar da ni da ilmina, dukiyata, da rayuwata, Ka sa in zama mai amfani ga dukkan al'umma.",
                "O The Source of All Benefit, make my knowledge, wealth, and life beneficial, and make me a beacon of benefit for humanity.",
                "Iwọ Olufunni ni Anfaani, jẹ ki imọ, ọrọ ati ayé mi jẹ anfaani, ki O si jẹ ki n wulo fun gbogbo eniyan.",
                "Isi Iyi nke Uru Niile, mee ka ihe ọmụma, akụ na ndụ m baa uru ma mee ka m baara mmadụ niile uru."
            ),
            AsmaDetail(
                93, "النُّورُ", "An-Nur",
                "The Divine Light", "Hasken Sammai da Ƙasa",
                "Ya Nur, Ka sanya haske a cikin zuciyata, a idanuna, a jikina, a kabarina, da kuma a kan gadar Siradi ranar sakamako.",
                "O The Divine Light, place light in my heart, my sight, my body, my grave, and on the bridge of As-Sirat on Judgment Day.",
                "Iwọ Imọlẹ Ọrun ati Ayé, fi imọlẹ sinu ọkàn mi, oju mi, ara mi, iboji mi, ati lori afara Sirat ni Ọjọ Idajọ.",
                "Ìhè Nsọ nke Eluigwe na Ụwa, tinye ìhè n'obi m, anya m, ahụ m, ili m na n'elu àkwà mmiri Sirat n'Ụbọchị Ikpe."
            ),
            AsmaDetail(
                94, "الْهَادِي", "Al-Hadi",
                "The Ultimate Guide", "Mai Shiryarwa zuwa Ga Tafarkin Ƙwarai",
                "Ya Hadi, Ka shiryar da ni, da iyalina, da zuriata zuwa ga tafarki madaidaici, Ka kare mu daga ɓata har ƙarshen rayuwarmu.",
                "O The Ultimate Guide, guide me, my family, and future generations upon the straight path, preserving us from misguidance.",
                "Iwọ Olutọsọna Titobi, tọ́ emi, ẹbi mi ati awọn ọmọ mi sọna titọ, ki O si pa wa mọ kuro ninu iparun.",
                "Onye Nduzi Kasị Elu, duru mụ na ezinụlọ m n'ụzọ ziri ezi ma chebe anyị pụọ n'ịkpa kpafuo."
            ),
            AsmaDetail(
                95, "الْبَدِيعُ", "Al-Badi'",
                "The Incomparable Originator", "Mai Halitta Ba Tare da Koyi da Kowa Ba",
                "Ya Badi', Ka ƙirƙira mini mafita mai ban mamaki a duk lokacin da na shiga tsanani, Ka sanya rayuwata ta zama kyakkyawa.",
                "O Incomparable Originator, originate wonderful solutions for me in every distress, and beautify my life with grace.",
                "Iwọ Oludasilẹ Ẹlẹwa, ṣe ọna abayọ iyanu fun mi ninu gbogbo inira, ki O si ṣe ayé mi ni ẹlẹwa.",
                "Onye Mmalite Ịhe Pụrụ Iche, meere m ụzọ mgbapụ magburu onwe ya n'oge ihe isi ike ma mee ka ndụ m maa mma."
            ),
            AsmaDetail(
                96, "الْبَاقِي", "Al-Baqi",
                "The Everlasting", "Madawwami Wanda Ba Ya Ƙarewa",
                "Ya Baqi, Ka ba ni sakamako madawwami mai daɗi a gidan Aljanna, Ka sanya ayyukana na alheri su dawwama bayan mutuwata.",
                "O The Everlasting, grant me everlasting bliss in Paradise, and let my continuous righteous deeds endure after my death.",
                "Iwọ Ẹni Ti Ki I Ku Titi Lailai, fun mi ni igbadun ayeraye ninu Alujanna, ki O si jẹ ki iṣẹ rere mi tẹsiwaju lẹhin ikú mi.",
                "Onye Na-adịru Ebighị Ebi, nye m anụrị ebighị ebi na Paradaịs ma mee ka ezi ọrụ m dịgide mgbe m nwụsịrị."
            ),
            AsmaDetail(
                97, "الْوَارِثُ", "Al-Warith",
                "The Supreme Inheritor", "Magajin Dukkan Halittu",
                "Ya Warith, Ka azurta ni da magada nagari masu addini, Ka sa in zama magajin Aljannar Ni'ima (Jannatun Na'im).",
                "O The Supreme Inheritor, bless me with righteous heirs who uphold faith, and make me an inheritor of the Gardens of Delight.",
                "Iwọ Ojogun Ohun Gbogbo, fi awọn ọmọ rere ti o ni ẹsin jinki mi, ki O si jẹ ki n jẹ ojogun Alujanna Na'eem.",
                "Onye Nketa Kasị Elu, nye m ezi ndị nketa na-atụ egwu Chineke ma mee ka m bụrụ onye nketa nke Paradaịs Na'im."
            ),
            AsmaDetail(
                98, "الرَّشِيدُ", "Ar-Rashid",
                "The Infallible Guide to Rectitude", "Mai Shiryarwa zuwa Ga Gaskiya da Daidaito",
                "Ya Rashid, Ka shiryar da ni zuwa ga hikima, basira, da yanke shawara mafi dacewa a dukkan al'amurana.",
                "O The Infallible Guide to Rectitude, guide me towards supreme wisdom, righteousness, and the most sound decisions in all things.",
                "Iwọ Olutọsọna Ọlọgbọn, tọ́ mi si ọgbọn ati ipinnu ti o tọ julọ ninu gbogbo ọ̀ràn mi.",
                "Onye Nduzi nke Ezi Omume, duzie m n'amamihe na mkpebi kacha mma n'ihe niile m na-eme."
            ),
            AsmaDetail(
                99, "الصَّبُورُ", "As-Sabur",
                "The Most Patient", "Mai Haƙuri Wanda Ba Ya Gaggawar Hukunci",
                "Ya Sabur, Ka saukar da haƙuri, juriya, da natsuwa a cikin zuciyata yayin fuskantar jarrabawa da tsananin rayuwa.",
                "O The Most Patient, bestow upon my heart immense patience, perseverance, and peace during times of trial and adversity.",
                "Iwọ Onisuuru Titobi, fi suuru ati iduroṣinṣin kun ọkàn mi lakoko awọn idanwo ati inira ayé.",
                "Onye Na-enwe Ndidi Kachasị, nye obi m ndidi dị ukwuu na udo n'oge ọnwụnwa na ihe isi ike."
            )
        )

        var currentId = 245
        for (item in names) {
            val titleClean = "${item.number}. ${item.transliteration} (${item.arabic})"

            list.add(
                DuaEntity(
                    id = currentId,
                    category = "Asma'ul Husna",
                    title = "Sunan Allah: ${item.transliteration}",
                    arabic = item.arabic,
                    transliteration = item.transliteration,
                    translation = "Allah's Name: ${item.transliteration} (${item.meaningEn}).\n\n✨ Most Suitable Supplication to seek from Allah:\n\"${item.bestDuaEn}\"",
                    translationHausa = "Sunan Allah: ${item.transliteration} - ${item.meaningHa}.\n\n✨ Abin da yafi cancanta mutum ya nema wajen Allah da wannan Sunan:\n\"${item.bestDuaHa}\"",
                    translationYoruba = "Orúkọ Allāhu: ${item.transliteration} - ${item.meaningEn}.\n\n✨ Àdúà tí ó yẹ jùlọ láti bẹ Allāhu pẹ̀lú orúkọ yìí:\n\"${item.bestDuaYo}\"",
                    translationIgbo = "Aha Chineke: ${item.transliteration} - ${item.meaningEn}.\n\n✨ Ekpere kachasị mma ịrịọ Chineke site na aha a:\n\"${item.bestDuaIg}\"",
                    reference = "Surah Al-A'raf 7:180, Sahih Al-Bukhari 2736, Sahih Muslim 2677"
                )
            )
            currentId++
        }

        return list
    }
}
