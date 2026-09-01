package com.example.data.local

object AsmaulHusnaData {

    fun getAsmaulHusnaDuas(): List<DuaEntity> {
        val list = mutableListOf<DuaEntity>()

        // 1. Introduction and Benefits of Knowing Asma'ul Husna
        list.add(
            DuaEntity(
                id = 244,
                category = "Asma'ul Husna",
                title = "Amfanin Sanin Asma'ul Husna (Virtue & Power of the 99 Names of Allah)",
                arabic = "وَلِلَّهِ الْأَسْمَاءُ الْحُسْنَىٰ فَادْعُوهُ بِهَا\n\nقَالَ رَسُولُ اللَّهِ ﷺ: «إِنَّ لِلَّهِ تِسْعَةً وَتِسْعِينَ اسْمًا مِائَةً إِلَّا وَاحِدًا، مَنْ أَحْصَاهَا دَخَلَ الْجَنَّةَ»",
                transliteration = "Wa lillahil-Asma'ul-Husna fad'oohu biha.\nQala Rasulullahi (SAW): Inna lillahi tis'atan wa tis'ina isman mi'atan illa wahida, man ahsaha dakhalal-Jannah.",
                translation = "And to Allah belong the most beautiful names, so invoke Him by them (Surah Al-A'raf 7:180).\n\nThe Messenger of Allah (ﷺ) said: 'Indeed, Allah has ninety-nine names, one hundred minus one; whoever memorizes, understands, and acts upon them will enter Paradise.' (Sahih Al-Bukhari 2736, Sahih Muslim 2677).\n\nImmense Benefits of Knowing Allah's 99 Names:\n1. Direct Path to Paradise: Memorizing, reflecting, and living by them guarantees entrance to Jannah.\n2. Instant Response to Du'a: Calling upon Allah by His specific Names matching your need (e.g. Ya Razzaq for wealth, Ya Shafi for health, Ya Ghaffar for forgiveness) ensures your prayers are accepted.\n3. Inner Peace & Healing: Knowing the perfection of Allah removes all despair, anxiety, and fear from the heart.\n4. True Tawhid & Knowledge of our Creator.",
                translationHausa = "Kuma Allah Yana da sunaye mafiya kyau (Asma'ul Husna), saboda haka ku roƙe Shi da su (Surah Al-A'raf: 180).\n\nManzon Allah (ﷺ) ya ce: 'Lallai Allah Yana da sunaye casa'in da tara (99), ɗari ba ɗaya ba. Duk wanda ya kiyaye su (ya haddace su, ya fahimci ma'anarsu, ya yi imani da su, kuma ya bauta wa Allah da yin addu'a da su) zai shiga Aljanna.' (Sahih Al-Bukhari 2736, Sahih Muslim 2677).\n\nAmfanin Sanin Sunayen Allah Kyawawa:\n1. Hanyar Shiga Aljanna: Wanda ya san su, ya haddace su, kuma ya yi aiki da su zai shiga Aljanna.\n2. Samun Amsar Addu'a: Idan kana neman arziki ka ce 'Ya Razzaq', neman gafara ka ce 'Ya Ghaffar', neman lafiya ka ce 'Ya Shafi' - Allah Yana amsa addu'ar bawan da ya kira Shi da sunanSa.\n3. Samun Natsuwa da Yaye Tsoro da Damuwa a Zuciya.\n4. Zurfafa Son Allah da Bautata Masa da Ikhlasi.",
                translationYoruba = "Gbogbo orúkọ rere jẹ́ ti Allāhu, nítorí náà ẹ pe E pẹ̀lú wọn (Surah Al-A'raf: 180).\nÀnábì (SAW) sọ pé: 'Dájúdájú Allāhu ní orúkọ mọ́kàndínlọ́gọ́rùn-ún (99), ẹnikẹ́ni tí ó bá kẹ́kọ̀ọ́ wọn tí ó sì pa wọ́n mọ́ yóò wọ Alujanna.'",
                translationIgbo = "Aha niile kachasị mma bụ nke Chineke, ya mere kpọkuo Ya site na ha (Surah Al-A'raf: 180).\nOnye Amụma (SAW) kwuru sị: 'N'ezie Chineke nwere aha iri itoolu na itoolu (99), onye ọ bụla mụtara ha ma na-eme ihe ha pụtara ga-aba na Paradaịs.'",
                reference = "Surah Al-A'raf 7:180, Sahih Al-Bukhari 2736, Sahih Muslim 2677"
            )
        )

        // 99 Names data helper
        val namesRaw = listOf(
            Triple("1. Ar-Rahman (الرَّحْمَنُ)", "Ar-Rahman", "The Most Gracious / Mai Rahama ga Dukkan Halittu"),
            Triple("2. Ar-Rahim (الرَّحِيمُ)", "Ar-Rahim", "The Most Merciful / Mai Jinkai na Musamman ga Muminai"),
            Triple("3. Al-Malik (الْمَلِكُ)", "Al-Malik", "The King and Absolute Sovereign / Mamallaki Kuma Sarkin Sarakuna"),
            Triple("4. Al-Quddus (الْقُدُّوسُ)", "Al-Quddus", "The Most Sacred & Pure / Mai Tsarki Daga Dukkan Aibi"),
            Triple("5. As-Salam (السَّلَامُ)", "As-Salam", "The Source of Peace & Security / Mai Aminci da Bada Zaman Lafiya"),
            Triple("6. Al-Mu'min (الْمُؤْمِنُ)", "Al-Mu'min", "The Giver of Faith & Security / Mai Bada Imani da Aminci ga Halittu"),
            Triple("7. Al-Muhaymin (الْمُهَيْمِنُ)", "Al-Muhaymin", "The Guardian & Overseer / Mai Kula da Tsare Komai"),
            Triple("8. Al-Aziz (الْعَزِيزُ)", "Al-Aziz", "The All-Mighty & Invincible / Mabuwayi Wanda Ba a Rinqayar Shi"),
            Triple("9. Al-Jabbar (الْجَبَّارُ)", "Al-Jabbar", "The Compeller & Restorer / Mai Tilastawa Kuma Mai Gyara Karyayyu"),
            Triple("10. Al-Mutakabbir (الْمُتَكَبِّرُ)", "Al-Mutakabbir", "The Supreme & Majestic / Mai Girma Wanda Girma Ya Keɓanta da Shi"),
            Triple("11. Al-Khaliq (الْخَالِقُ)", "Al-Khaliq", "The Creator / Mahaliccin Dukkan Halittu"),
            Triple("12. Al-Bari' (الْبَارِئُ)", "Al-Bari'", "The Originator / Mai Fara Halitta Daga Babu"),
            Triple("13. Al-Musawwir (الْمُصَوِّرُ)", "Al-Musawwir", "The Fashioner of Forms / Mai Siffanta Halitta Yadda Ya So"),
            Triple("14. Al-Ghaffar (الْغَفَّارُ)", "Al-Ghaffar", "The All-Forgiving / Mai Yawan Gafara ga Masu Nema"),
            Triple("15. Al-Qahhar (الْقَهَّارُ)", "Al-Qahhar", "The Subduer / Mai Rinqaye a Kan Dukkan Komai"),
            Triple("16. Al-Wahhab (الْوَهَّابُ)", "Al-Wahhab", "The Supreme Bestower / Mai Yawan Kyauta Ba Tare da Neman Lada Ba"),
            Triple("17. Ar-Razzaq (الرَّزَّاقُ)", "Ar-Razzaq", "The Total Provider / Mai Ciyarwa da Azurta Halittu"),
            Triple("18. Al-Fattah (الْفَتَّاحُ)", "Al-Fattah", "The Supreme Opener & Judge / Mai Budewa da Hukunci"),
            Triple("19. Al-Alim (الْعَلِيمُ)", "Al-Alim", "The All-Knowing / Masanin Dukkan Abin da Ya Boyu da na Fili"),
            Triple("20. Al-Qabid (الْقَابِضُ)", "Al-Qabid", "The Restrainer & Withholder / Mai Damkewa da Tsuke Arziki Don Hikima"),
            Triple("21. Al-Basit (الْبَاسِطُ)", "Al-Basit", "The Expander & Enlarger / Mai Yalwata Arziki da Rahama"),
            Triple("22. Al-Khafid (الْخَافِضُ)", "Al-Khafid", "The Abaser / Mai Kaskantar da Azzalumai"),
            Triple("23. Ar-Rafi' (الرَّافِعُ)", "Ar-Rafi'", "The Exalter / Mai Daukaka Darajar Muminai"),
            Triple("24. Al-Mu'izz (الْمُعِزُّ)", "Al-Mu'izz", "The Giver of Honor / Mai Bada Daukaka da Izza"),
            Triple("25. Al-Mudhill (الْمُذِلُّ)", "Al-Mudhill", "The Giver of Dishonor / Mai Kaskantar da Masu Sabo"),
            Triple("26. As-Sami' (السَّمِيعُ)", "As-Sami'", "The All-Hearing / Mai Jin Dukkan Sauti da Addu'o'i"),
            Triple("27. Al-Basir (الْبَصِيرُ)", "Al-Basir", "The All-Seeing / Mai Ganin Dukkan Halittu da Sirrika"),
            Triple("28. Al-Hakam (الْحَكَمُ)", "Al-Hakam", "The Impartial Judge / Al-Qali Mai Hukunci da Adalci"),
            Triple("29. Al-Adl (الْعَدْلُ)", "Al-Adl", "The Utterly Just / Mai Cikakken Adalci"),
            Triple("30. Al-Latif (اللَّطِيفُ)", "Al-Latif", "The Subtle & Kind / Mai Taushin Rahama da Sanin Boyayyun Al'amura"),
            Triple("31. Al-Khabir (الْخَبِيرُ)", "Al-Khabir", "The All-Aware / Masani a Kan Dukkan Bayanai"),
            Triple("32. Al-Halim (الْحَلِيمُ)", "Al-Halim", "The Forbearing / Mai Hakuri da Rangwame"),
            Triple("33. Al-Azim (الْعَظِيمُ)", "Al-Azim", "The Magnificent / Mai Girma Madaukaki"),
            Triple("34. Al-Ghafur (الْغَفُورُ)", "Al-Ghafur", "The Forgiver & Concealer / Mai Yafiyar Zunubai"),
            Triple("35. Ash-Shakur (الشَّكُورُ)", "Ash-Shakur", "The Most Appreciative / Mai Godiya da Bada Lada Mai Yawa"),
            Triple("36. Al-Aliyy (الْعَلِيُّ)", "Al-Aliyy", "The Most High / Madaukaki Sama da Kowa"),
            Triple("37. Al-Kabir (الْكَبِيرُ)", "Al-Kabir", "The Greatest / Babba Wanda Babu Mai Kama da Shi"),
            Triple("38. Al-Hafiz (الْحَفِيظُ)", "Al-Hafiz", "The Preserver & Protector / Mai Tsarewa da Kula da Halittu"),
            Triple("39. Al-Muqit (الْمُقِيتُ)", "Al-Muqit", "The Sustainer & Nourisher / Mai Ciyarwa da Tsara Rikon Komai"),
            Triple("40. Al-Hasib (الْحَسِيبُ)", "Al-Hasib", "The Reckoner & Sufficient / Mai Kididdiga da Isarwa ga Bayi"),
            Triple("41. Al-Jalil (الْجَلِيلُ)", "Al-Jalil", "The Majestic & Sublime / Ma'abucin Daukaka da Cikar Girma"),
            Triple("42. Al-Karim (الْكَرِيمُ)", "Al-Karim", "The Most Generous / Mai Yawan Karamci da Baiwa"),
            Triple("43. Ar-Raqib (الرَّقِيبُ)", "Ar-Raqib", "The Watchful / Mai Tsaron Komai Ba Tare da Wani Abu Ya Boye Masa Ba"),
            Triple("44. Al-Mujib (الْمُجِيبُ)", "Al-Mujib", "The Responsive & Answerer / Mai Amsa Addu'ar Masu Roƙo"),
            Triple("45. Al-Wasi' (الْوَاسِعُ)", "Al-Wasi'", "The All-Encompassing / Mai Yalwar Rahama, Ilmi, da Ni'ima"),
            Triple("46. Al-Hakim (الْحَكِيمُ)", "Al-Hakim", "The All-Wise / Mai Hikima a Cikin Dukkan AyyukanSa"),
            Triple("47. Al-Wadud (الْوَدُودُ)", "Al-Wadud", "The Loving / Mai Kaunar BayanSa Salihai"),
            Triple("48. Al-Majid (الْمَجِيدُ)", "Al-Majid", "The Glorious / Mai Girma da Cikar Kyawawan Siffofi"),
            Triple("49. Al-Ba'ith (الْبَاعِثُ)", "Al-Ba'ith", "The Resurrector / Mai Tayar da Halittu Ranar Kiyama"),
            Triple("50. Ash-Shahid (الشَّهِيدُ)", "Ash-Shahid", "The Witness / Mai Shaida a Kan Komai"),
            Triple("51. Al-Haqq (الْحَقُّ)", "Al-Haqq", "The Absolute Truth / Gaskiya Tabbatacciya"),
            Triple("52. Al-Wakil (الْوَكِيلُ)", "Al-Wakil", "The Ultimate Trustee / Abin Dogaro Wanda Ya Isa Dogaro da Shi"),
            Triple("53. Al-Qawiyy (الْقَوِيُّ)", "Al-Qawiyy", "The All-Strong / Mai Cikakken Karfi da Iko"),
            Triple("54. Al-Matin (الْمَتِينُ)", "Al-Matin", "The Firm & Steadfast / Kakkarfa Wanda KarfinSa Ba Ya Karewa"),
            Triple("55. Al-Waliyy (الْوَلِيُّ)", "Al-Waliyy", "The Protecting Friend / Majibincin Al'amuran BayanSa"),
            Triple("56. Al-Hamid (الْحَمِيدُ)", "Al-Hamid", "The Praiseworthy / Abin Godiya a Cikin Dukkan Halaye"),
            Triple("57. Al-Muhsi (الْمُحْصِي)", "Al-Muhsi", "The Accounter of All / Mai Kididdigar Komai Ba Tare da Mantawa Ba"),
            Triple("58. Al-Mubdi' (الْمُبْدِئُ)", "Al-Mubdi'", "The Originator / Mai Fara Halitta Daga Farko"),
            Triple("59. Al-Mu'id (الْمُعِيدُ)", "Al-Mu'id", "The Restorer / Mai Mayar da Halitta Bayan Mutuwa"),
            Triple("60. Al-Muhyi (الْمُحْيِي)", "Al-Muhyi", "The Giver of Life / Mai Rayar da Matattu"),
            Triple("61. Al-Mumit (الْمُمِيتُ)", "Al-Mumit", "The Bringer of Death / Mai Kashe Masu Rai"),
            Triple("62. Al-Hayy (الْحَيُّ)", "Al-Hayy", "The Ever-Living / Mai Rai Madawwami Wanda Ba Ya Mutuwa"),
            Triple("63. Al-Qayyum (الْقَيُّومُ)", "Al-Qayyum", "The Self-Sustaining / Tsayayye Mai Kula da Tsayuwar Halittu"),
            Triple("64. Al-Wajid (الْوَاجِدُ)", "Al-Wajid", "The Finder & Resourceful / Mai Samun Duk Abin da Yake So"),
            Triple("65. Al-Majid (الْمَاجِدُ)", "Al-Majid", "The Noble & Generous / Mai Karramawa da Daukaka"),
            Triple("66. Al-Wahid (الْوَاحِدُ)", "Al-Wahid", "The Unique / Daya Til Ba Tare da Abokin Tarayya Ba"),
            Triple("67. Al-Ahad (الْأَحَدُ)", "Al-Ahad", "The Indivisible One / Makaɗaici a Cikin ZatinSa da SiffofinSa"),
            Triple("68. As-Samad (الصَّمَدُ)", "As-Samad", "The Eternal Refuge / Wanda Dukkan Halittu Ke Nufa da Bukatunsu"),
            Triple("69. Al-Qadir (الْقَادِرُ)", "Al-Qadir", "The Omnipotent / Mai Ikon Aikata Duk Abin da Ya So"),
            Triple("70. Al-Muqtadir (الْمُقْتَدِرُ)", "Al-Muqtadir", "The Determiner / Mai Cikakken Ikon Zartarwa"),
            Triple("71. Al-Muqaddim (الْمُقَدِّمُ)", "Al-Muqaddim", "The Expediter / Mai Gabatar da Wanda Ya So"),
            Triple("72. Al-Mu'akhkhir (الْمُؤَخِّرُ)", "Al-Mu'akhkhir", "The Delayer / Mai Jinkirta Abin da Ya So Don Hikima"),
            Triple("73. Al-Awwal (الْأَوَّلُ)", "Al-Awwal", "The First / Na Farko Wanda Babu Wani Abu Kafin Shi"),
            Triple("74. Al-Akhir (الْآخِرُ)", "Al-Akhir", "The Last / Na Karshe Wanda Babu Wani Abu Bayan Shi"),
            Triple("75. Az-Zahir (الظَّاهِرُ)", "Az-Zahir", "The Manifest / Mabayyani Wanda AyoyinSa Suka Bayyana"),
            Triple("76. Al-Batin (الْبَاطِنُ)", "Al-Batin", "The Hidden / Boyayye Wanda Idanu Ba Sa Iya Ganin Shi a Duniya"),
            Triple("77. Al-Wali (الْوَالِي)", "Al-Wali", "The Governor / Mai Mulki da Kula da Dukkan Halittu"),
            Triple("78. Al-Muta'ali (الْمُتَعَالِي)", "Al-Muta'ali", "The Supreme / Madaukaki Sama da Dukkan Halitta"),
            Triple("79. Al-Barr (الْبَرُّ)", "Al-Barr", "The Most Benign / Mai Yawan Alheri da Tausayi"),
            Triple("80. At-Tawwab (التَّوَّابُ)", "At-Tawwab", "The Accepter of Repentance / Mai Karbar Tubar Masu Tuba"),
            Triple("81. Al-Muntaqim (الْمُنْتَقِمُ)", "Al-Muntaqim", "The Avenger / Mai Daukar Fansa a Kan Azzalumai"),
            Triple("82. Al-Afuww (الْعَفُوُّ)", "Al-Afuww", "The Pardoner / Mai Goge Zunubai da Yafiya"),
            Triple("83. Ar-Ra'uf (الرَّؤُوفُ)", "Ar-Ra'uf", "The Clement / Mai Tsananin Jinkai da Tausayi"),
            Triple("84. Malik-ul-Mulk (مَالِكُ الْمُلْكِ)", "Malik-ul-Mulk", "Owner of All Sovereignty / Mamallakin Dukkan Mulki"),
            Triple("85. Dhul-Jalali wal-Ikram (ذُو الْجَلَالِ وَالْإِكْرَامِ)", "Dhul-Jalali wal-Ikram", "Lord of Majesty and Generosity / Ma'abucin Girma da Karramawa"),
            Triple("86. Al-Muqsit (الْمُقْسِطُ)", "Al-Muqsit", "The Equitable / Mai Daidaito da Kwatar Hakki"),
            Triple("87. Al-Jami' (الْجَامِعُ)", "Al-Jami'", "The Gatherer / Mai Tara Halittu Ranar Sakamako"),
            Triple("88. Al-Ghaniyy (الْغَنِيُّ)", "Al-Ghaniyy", "The Self-Sufficient / Mawadaci Wanda Ba Ya Bukatar Kowa"),
            Triple("89. Al-Mughni (الْمُغْنِي)", "Al-Mughni", "The Enricher / Mai Azurtawa da Wadatar da BayanSa"),
            Triple("90. Al-Mani' (الْمَانِعُ)", "Al-Mani'", "The Withholder / Mai Hanawa Don Hikima da Kariya"),
            Triple("91. Ad-Darr (الضَّارُّ)", "Ad-Darr", "The Afflictor / Mai Hukunta Wanda Ya So Don Hikima"),
            Triple("92. An-Nafi' (النَّافِعُ)", "An-Nafi'", "The Benefactor / Mai Bada Amfani da Alheri"),
            Triple("93. An-Nur (النُّورُ)", "An-Nur", "The Light / Hasken Sammai da Qasa"),
            Triple("94. Al-Hadi (الْهَادِي)", "Al-Hadi", "The Guide / Mai Shiryarwa zuwa Ga Tafarkin Kwarai"),
            Triple("95. Al-Badi' (الْبَدِيعُ)", "Al-Badi'", "The Incomparable Originator / Mai Halitta Ba Tare da Koyi da Kowa Ba"),
            Triple("96. Al-Baqi (الْبَاقِي)", "Al-Baqi", "The Everlasting / Madawwami Wanda Ba Ya Karewa"),
            Triple("97. Al-Warith (الْوَارِثُ)", "Al-Warith", "The Supreme Inheritor / Magajin Dukkan Halittu"),
            Triple("98. Ar-Rashid (الرَّشِيدُ)", "Ar-Rashid", "The Guide to Right Path / Mai Shiryarwa zuwa Ga Gaskiya da Daidaito"),
            Triple("99. As-Sabur (الصَّبُورُ)", "As-Sabur", "The Patient / Mai Hakuri Wanda Ba Ya Gaggawar Hukunci")
        )

        var currentId = 245
        for (item in namesRaw) {
            val titleClean = item.first
            val translit = item.second
            val desc = item.third
            val arabicOnly = titleClean.substringAfter("(").substringBefore(")")

            list.add(
                DuaEntity(
                    id = currentId,
                    category = "Asma'ul Husna",
                    title = "Sunan Allah: $translit",
                    arabic = arabicOnly,
                    transliteration = translit,
                    translation = "Allah's Name: $translit - $desc. Supplicate by saying: 'Ya $translit, ...'",
                    translationHausa = "Sunan Allah: $translit - $desc. Ana addu'a da shi: 'Ya $translit, Ka azurta ni / Ka gafarta mini / Ka ji kai na.'",
                    translationYoruba = "Orúkọ Allāhu: $translit - $desc.",
                    translationIgbo = "Aha Chineke: $translit - $desc.",
                    reference = "Surah Al-A'raf 7:180, Sahih Al-Bukhari & Muslim"
                )
            )
            currentId++
        }

        return list
    }
}
