package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppLocalizer
import com.example.data.local.DuaReferenceLocalization
import com.example.data.local.DuaTranslationLocalization

enum class WidgetContentType(
    val id: String,
    val titleHa: String,
    val titleEn: String,
    val icon: String
) {
    ADDUA("ADDUA", "Addu'o'i (Du'as)", "Daily Du'as", "🤲"),
    AZKAR("AZKAR", "Azkar (Tasbeeh / Safe da Yamma)", "Daily Adhkar & Tasbeeh", "📿"),
    SURAH("SURAH", "Surah & Ayoyin Alkur'ani", "Surah & Quranic Ayahs", "📖"),
    PRAYER_TIMES("PRAYER_TIMES", "Lokutan Sallah (Prayer Times)", "Prayer Times Only", "🕌"),
    COMBINED("COMBINED", "Haɗaɗɗe (Lokutan Sallah + Addu'a)", "Combined (Prayer + Dua)", "🌟")
}

enum class WidgetFontSize(
    val id: String,
    val labelHa: String,
    val labelEn: String,
    val labelAr: String,
    val scaleArabic: Float,
    val scaleTranslit: Float,
    val scaleTranslation: Float
) {
    SMALL("SMALL", "Ƙarami", "Small", "صغير", 12f, 9.5f, 9.5f),
    NORMAL("NORMAL", "Daidai", "Normal", "متوسط", 14f, 11f, 10.5f),
    LARGE("LARGE", "Babba", "Large", "كبير", 17f, 13f, 12.5f),
    EXTRA_LARGE("EXTRA_LARGE", "Mafi Girma", "Extra Large", "كبير جداً", 20f, 15f, 14.5f)
}

data class WidgetItem(
    val id: String,
    val type: WidgetContentType,
    val title: String,
    val arabic: String,
    val transliteration: String = "",
    val translation: String,
    val reference: String,
    val dbId: Int? = null
)

object WidgetContentManager {

    private const val PREFS_NAME = "zakiru_widget_prefs"
    private const val KEY_CONTENT_TYPE = "key_widget_content_type"
    private const val KEY_CURRENT_INDEX_PREFIX = "key_widget_index_"
    private const val KEY_SPECIFIC_ITEM_ID = "key_widget_specific_item"
    private const val KEY_CUSTOM_ACTIVE = "key_widget_custom_active"
    private const val KEY_CUSTOM_ID = "key_widget_custom_id"
    private const val KEY_CUSTOM_TYPE = "key_widget_custom_type"
    private const val KEY_CUSTOM_TITLE = "key_widget_custom_title"
    private const val KEY_CUSTOM_ARABIC = "key_widget_custom_arabic"
    private const val KEY_CUSTOM_TRANSLITERATION = "key_widget_custom_transliteration"
    private const val KEY_CUSTOM_TRANSLATION = "key_widget_custom_translation"
    private const val KEY_CUSTOM_REFERENCE = "key_widget_custom_reference"
    private const val KEY_CUSTOM_DB_ID = "key_widget_custom_db_id"
    private const val KEY_FONT_SIZE = "key_widget_font_size"

    // 1. Curated Addu'o'i (Du'as) - Authentic & Comprehensive
    val duasList = listOf(
        // Allahumma Rabba Jibrilu (Opening Supplication in Night Prayer - Sahih Muslim 770)
        WidgetItem(
            id = "dua_rabba_jibril",
            type = WidgetContentType.ADDUA,
            title = "🤲 Buɗe Sallar Dare (Allahumma Rabba Jibrilu)",
            arabic = "اللَّهُمَّ رَبَّ جِبْرَائِيلَ وَمِيكَائِيلَ وَإِسْرَافِيلَ ، فَاطِرَ السَّمَاوَاتِ وَالأَرْضِ ، عَالِمَ الْغَيْبِ وَالشَّهَادَةِ ، أَنْتَ تَحْكُمُ بَيْنَ عِبَادِكَ فِيمَا كَانُوا فِيهِ يَخْتَلِفُونَ ، اهْدِنِي لِمَا اخْتُلِفَ فِيهِ مِنَ الْحَقِّ بِإِذْنِكَ ، إِنَّكَ تَهْدِي مَنْ تَشَاءُ إِلَى صِرَاطٍ مُسْتَقِيمٍ",
            transliteration = "Allahumma Rabba Jibra'eela wa Meeka'eela wa Israfeela, Fatiras-samawati wal-ardi, 'Alimal-ghaybi wash-shahadati, Anta tahkumu bayna 'ibadika feema kanoo feehi yakhtalifoon, ihdinee lima-khtulifa feehi minal-haqqi bi-idhnik, innaka tahdee man tasha'u ila siratin mustaqeem.",
            translation = "Ya Allah, Ubangijin Jibrilu da Mika'ilu da Israfilu, Mai ƙaga halittar sammai da ƙasa, Masanin gaibi da bayyane, Kai ne Kake yin hukunci a tsakanin bayinKa a kan abin da suka kasance suna saɓawa a kansa, Ka shiryar da ni zuwa ga abin da aka saɓa a kansa na gaskiya da izininKa, lallai Kai kana shiryar da wanda Kake so zuwa ga tafarki madaidaici.",
            reference = "★ Sahih Muslim: 770 • Addu'ar Buɗe Sallar Dare (Hadisin Aisha RA)",
            dbId = 381
        ),
        WidgetItem(
            id = "dua_1",
            type = WidgetContentType.ADDUA,
            title = "🤲 Sayyidul Istighfar",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
            transliteration = "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'oodhu bika min sharri ma sana'tu, aboo'u laka bini'matika 'alayya, wa aboo'u bidhanbee faghfir lee fa'innahu la yaghfirudh-dhunooba illa Anta.",
            translation = "Ya Allah, kai ne Ubangijina, babu abin bautawa da gaskiya sai kai. Ka halicce ni kuma ni bawanka ne. Ka gafarta mini zunubaina domin babu mai gafarta zunubi sai kai.",
            reference = "★ Buhari: 6306 • Shugaban Neman Gafara",
            dbId = 1
        ),
        WidgetItem(
            id = "dua_muslim_2720",
            type = WidgetContentType.ADDUA,
            title = "🤲 Kyautata Addini, Duniya & Lahira",
            arabic = "اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي ، وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي ، وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي ، وَاجْعَلِ الْحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ وَاجْعَلِ الْمَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ",
            transliteration = "Allahumma aslih li deenil-ladhi huwa 'ismatu amri, wa aslih li dunyaya al-lati fiha ma'ashi, wa aslih li akhirati allati fiha ma'adi, waj'alil-hayata ziyadatan li fi kulli khayr, waj'alil-mawta rahatan li min kulli sharr.",
            translation = "Ya Allah! Ka kyautata mini addinina wanda shi ne kariya ga al'amarina, da duniyata wadda rayuwata take a ciki, da lahirata makomata, Ka sanya rayuwa ƙarin alheri, mutuwa kuma hutu daga kowane sharri.",
            reference = "★ Sahih Muslim: 2720 • Hadisin Abu Huraira (RA)",
            dbId = 380
        ),
        WidgetItem(
            id = "dua_2",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Neman Sauƙi",
            arabic = "اللَّهُمَّ لا سَهْلَ إِلاَّ مَا جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلاً",
            transliteration = "Allahumma la sahla illa ma ja'altahu sahla, wa Anta taj'alul-hazna idha shi'ta sahla.",
            translation = "Ya Allah, babu abu mai sauƙi face wanda ka sauƙaƙa shi, kuma idan ka so kana sanya kowane abu mai wuya ya zama mai sauƙi.",
            reference = "★ Ibn Hibban: 974 • Neman Yaye Matsala",
            dbId = 181
        ),
        WidgetItem(
            id = "dua_3",
            type = WidgetContentType.ADDUA,
            title = "🤲 Neman Tsari daga Damuwa",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            transliteration = "Allahumma inni a'oodhu bika minal-hammi wal-hazani, wal-'ajzi wal-kasali, wal-bukhli wal-jubni, wa dala'id-dayni wa ghalabatir-rijal.",
            translation = "Ya Allah, ina neman tsarinka daga damuwa da baƙin ciki, da kasala, da rowa da tsoro, da nauyin bashi da rinjayar mutane.",
            reference = "★ Buhari: 2893 • Kariya daga Bakin Ciki",
            dbId = 182
        ),
        WidgetItem(
            id = "dua_4",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Fita daga Gida",
            arabic = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ",
            transliteration = "Bismillahi, tawakkaltu 'alallahi, wa la hawla wa la quwwata illa billah.",
            translation = "Da sunan Allah, na dogara ga Allah, babu dabara kuma babu ƙarfi sai da taimakon Allah.",
            reference = "★ Abu Dawud: 5095 • Kariya da Jagora",
            dbId = 16
        ),
        WidgetItem(
            id = "dua_5",
            type = WidgetContentType.ADDUA,
            title = "🤲 Neman Alherin Duniya da Lahira",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
            translation = "Ya Ubangijinmu, ka ba mu alheri a duniya, da alheri a lahira, kuma ka kare mu daga azabar wuta.",
            reference = "★ Suratul Baqarah: 201 • Addu'a Mafi Cika",
            dbId = 180
        ),
        WidgetItem(
            id = "dua_6",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Tabbatar da Zuciya",
            arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
            transliteration = "Ya Muqallibal-quloobi thabbit qalbi 'ala deenik.",
            translation = "Ya mai juya zukata, ka tabbatar da zuciyata a kan addininka.",
            reference = "★ Tirmidhi: 3522 • Tabbatuwa a Addini",
            dbId = 183
        ),
        WidgetItem(
            id = "dua_7",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Iyaye",
            arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            transliteration = "Rabbir-hamhuma kama rabbayani sagheera.",
            translation = "Ya Ubangijina, ka yi musu rahama kamar yadda suka rene ni tun ina ƙarami.",
            reference = "★ Suratul Isra'i: 24 • Ladabi da Neman Rahama",
            dbId = 350
        ),
        WidgetItem(
            id = "dua_8",
            type = WidgetContentType.ADDUA,
            title = "🤲 Neman Karuwar Ilimi",
            arabic = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "Rabbi zidnee 'ilma.",
            translation = "Ya Ubangijina, ka ƙara mini ilimi mai amfani.",
            reference = "★ Surah Ta-Ha: 114 • Bunkasa Ilimi",
            dbId = 351
        )
    )

    // 2. Curated Azkar & Tasbeeh
    val azkarList = listOf(
        WidgetItem(
            id = "zkr_1",
            type = WidgetContentType.AZKAR,
            title = "📿 Sayyidul Azkar • Sau 100",
            arabic = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu, wa Huwa 'ala kulli shay'in Qadeer.",
            translation = "Babu abin bautawa da gaskiya sai Allah shi kaɗai, ba shi da abokin tarayya, mulki nasa ne, yabo nasa ne, kuma shi mai ikon yi ne a kan komai.",
            reference = "★ Buhari: 3293 • Lada kamar 'yanta bayi 10"
        ),
        WidgetItem(
            id = "zkr_2",
            type = WidgetContentType.AZKAR,
            title = "📿 Kalmomi Masu Nauyi a Ma'auni",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "Subhanallahi wa bihamdihi, Subhanallahil-'Azeem.",
            translation = "Tsarki ya tabbata ga Allah tare da yabon sa, Tsarki ya tabbata ga Allah mai girma.",
            reference = "★ Buhari: 6406 • Masu nauyi a ma'auni"
        ),
        WidgetItem(
            id = "zkr_3",
            type = WidgetContentType.AZKAR,
            title = "📿 Zikiri na Safiya da Maraice",
            arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ",
            transliteration = "Asbahna wa asbahal-mulku lillah, wal-hamdu lillah, la ilaha illallahu wahdahu la shareeka lah.",
            translation = "Mun wayi gari kuma mulki ya wayi gari na Allah ne, yabo duka na Allah ne, babu abin bautawa da gaskiya sai Allah.",
            reference = "★ Muslim: 2723 • Wanzar da Tsaro"
        ),
        WidgetItem(
            id = "zkr_4",
            type = WidgetContentType.AZKAR,
            title = "📿 Salatin Annabi (S.A.W)",
            arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            transliteration = "Allahumma salli 'ala Muhammadin wa 'ala ali Muhammad, kama sallayta 'ala Ibrahima wa 'ala ali Ibrahim, innaka Hameedum-Majeed.",
            translation = "Ya Allah, ka yi salati ga Annabi Muhammad da iyalan Muhammad kamar yadda ka yi wa Ibrahim da iyalansa.",
            reference = "★ Buhari: 3370 • Sau 10 na salatin Allah"
        ),
        WidgetItem(
            id = "zkr_5",
            type = WidgetContentType.AZKAR,
            title = "📿 Hauqala (Taska daga Aljanna)",
            arabic = "لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            transliteration = "La hawla wa la quwwata illa billahil-'Aliyyil-'Azeem.",
            translation = "Babu dabara kuma babu ƙarfi sai da taimakon Allah maɗaukaki mai girma.",
            reference = "★ Buhari: 6384 • Taska daga karkashin Al'arshi"
        ),
        WidgetItem(
            id = "zkr_6",
            type = WidgetContentType.AZKAR,
            title = "📿 Neman Kariya daga Sharri • Sau 3",
            arabic = "بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            transliteration = "Bismillahilladhee la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Samee'ul-'Aleem.",
            translation = "Da sunan Allah wanda babu abin da ke cutarwa tare da sunansa a cikin ƙasa ko sama, kuma shi mai ji ne masani.",
            reference = "★ Abu Dawud: 5088 • Kariya daga kowane cutarwa"
        ),
        WidgetItem(
            id = "zkr_7",
            type = WidgetContentType.AZKAR,
            title = "📿 Isasshen Mai Kariya (Hasbiyallahu)",
            arabic = "حَسْبِيَ اللَّهُ لاَ إِلَهَ إِلاَّ هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
            transliteration = "Hasbiyallahu la ilaha illa Huwa, 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azeem.",
            translation = "Allah ya ishe ni, babu abin bautawa sai shi, a kansa na dogara kuma shi ne Ubangijin Al'arshi mai girma.",
            reference = "★ Abu Dawud: 5081 • Isarwa a al'amuran duniya"
        )
    )

    // 3. Curated Surahs & Quranic Ayahs
    val surahsList = listOf(
        WidgetItem(
            id = "surah_1",
            type = WidgetContentType.SURAH,
            title = "📖 Ayatul Kursiyyu (Baqarah: 255)",
            arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ",
            transliteration = "Allahu la ilaha illa Huwal-Hayyul-Qayyum, la ta'khudhuhu sinatun wa la nawm, lahu ma fis-samawati wa ma fil-ard...",
            translation = "Allah babu abin bautawa da gaskiya sai shi, Rayayye ne wanda komai ke tsayuwa da shi, gyangyaɗi ko barci ba ya kama shi.",
            reference = "★ Ayar da ta fi kowace girma a Alkur'ani"
        ),
        WidgetItem(
            id = "surah_2",
            type = WidgetContentType.SURAH,
            title = "📖 Suratul Ikhlas (Tauhid)",
            arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
            transliteration = "Qul Huwallahu Ahad, Allahus-Samad, lam yalid wa lam yoolad, wa lam yakun lahu kufuwan ahad.",
            translation = "Ka ce: Shi ne Allah, shi kaɗai ne. Allah abin nufi ne da buƙata. Bai haifa ba kuma ba a haife shi ba. Kuma babu wani da ya zama tamkar sa.",
            reference = "★ Surah mai daidai da kashi ɗaya bisa uku na Alkur'ani"
        ),
        WidgetItem(
            id = "surah_3",
            type = WidgetContentType.SURAH,
            title = "📖 Suratul Mulk (Ayah: 1)",
            arabic = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            transliteration = "Tabarakalladhee biyadihil-mulku wa Huwa 'ala kulli shay'in Qadeer.",
            translation = "Albarka ta tabbata ga wanda mulki ke hannunsa, kuma shi mai ikon yi ne a kan komai.",
            reference = "★ Mai ceton makarancinta daga azabar kabari"
        ),
        WidgetItem(
            id = "surah_4",
            type = WidgetContentType.SURAH,
            title = "📖 Samun Sauƙi (Al-Inshirah: 5-6)",
            arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            transliteration = "Fa'inna ma'al-'usri yusra, inna ma'al-'usri yusra.",
            translation = "Lalle ne tare da tsanani akwai sauƙi. Lalle ne tare da tsanani akwai sauƙi.",
            reference = "★ Suratul Inshirah • Tabbatar da Yaye Matsala"
        ),
        WidgetItem(
            id = "surah_5",
            type = WidgetContentType.SURAH,
            title = "📖 Dogaro ga Allah (At-Talaq: 3)",
            arabic = "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ ۚ إِنَّ اللَّهَ بَالِغُ أَمْرِهِ",
            transliteration = "Wa man yatawakkal 'alallahi fahuwa hasbuh, innallaha balighu amrih.",
            translation = "Kuma wanda ya dogara ga Allah, to Allah ya ishe shi. Lalle Allah mai isar da umarninsa ne.",
            reference = "★ Surah At-Talaq: 3 • Samun Isarwar Ubangiji"
        ),
        WidgetItem(
            id = "surah_6",
            type = WidgetContentType.SURAH,
            title = "📖 Salati ga Annabi (Al-Ahzab: 56)",
            arabic = "إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ ۚ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا",
            transliteration = "Innallaha wa mala'ikatahu yusalloona 'alan-Nabi, ya ayyuhalladheena amanoo salloo 'alayhi wa sallimoo tasleema.",
            translation = "Lalle Allah da Mala'ikunsa suna salati ga Annabi, ya ku waɗanda suka yi imani, ku yi salati a gare shi da sallama.",
            reference = "★ Surah Al-Ahzab: 56 • Umarnin Ubangiji"
        )
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Gets active font size for widget.
     */
    fun getWidgetFontSize(context: Context): WidgetFontSize {
        val id = getPrefs(context).getString(KEY_FONT_SIZE, WidgetFontSize.NORMAL.id)
        return WidgetFontSize.values().firstOrNull { it.id == id } ?: WidgetFontSize.NORMAL
    }

    /**
     * Sets active font size for widget.
     */
    fun setWidgetFontSize(context: Context, size: WidgetFontSize) {
        getPrefs(context).edit().putString(KEY_FONT_SIZE, size.id).apply()
    }

    /**
     * Cycles to the next font size (SMALL -> NORMAL -> LARGE -> EXTRA_LARGE -> SMALL).
     */
    fun cycleNextFontSize(context: Context): WidgetFontSize {
        val current = getWidgetFontSize(context)
        val all = WidgetFontSize.values()
        val next = all[(current.ordinal + 1) % all.size]
        setWidgetFontSize(context, next)
        return next
    }

    /**
     * Gets current user-selected widget content type (default is ADDUA or COMBINED).
     */
    fun getSelectedContentType(context: Context): WidgetContentType {
        val id = getPrefs(context).getString(KEY_CONTENT_TYPE, WidgetContentType.ADDUA.id)
        return WidgetContentType.values().firstOrNull { it.id == id } ?: WidgetContentType.ADDUA
    }

    /**
     * Updates user's preferred content type to display on widget.
     */
    fun setSelectedContentType(context: Context, type: WidgetContentType) {
        getPrefs(context).edit().putString(KEY_CONTENT_TYPE, type.id).apply()
    }

    /**
     * Lazily creates all authentic Duas & Azkar items from the full app database.
     */
    val allDatabaseWidgetItems: List<WidgetItem> by lazy {
        com.example.data.local.DuaDatabaseSeeder.getSeedDuas().map { dua ->
            val isZikr = dua.category.contains("Adhkar", ignoreCase = true) ||
                         dua.category.contains("Tasbih", ignoreCase = true) ||
                         dua.category.contains("Asma", ignoreCase = true)
            val isSurah = dua.category.contains("Surah", ignoreCase = true) ||
                          dua.category.contains("Rabbana", ignoreCase = true) ||
                          dua.id == 5 || dua.id == 6 || dua.id == 361 || dua.id == 363 || dua.id == 364
            val type = when {
                isSurah -> WidgetContentType.SURAH
                isZikr -> WidgetContentType.AZKAR
                else -> WidgetContentType.ADDUA
            }
            val icon = when (type) {
                WidgetContentType.AZKAR -> "📿"
                WidgetContentType.SURAH -> "📖"
                else -> "🤲"
            }
            WidgetItem(
                id = "db_${dua.id}",
                type = type,
                title = "$icon ${dua.title}",
                arabic = dua.arabic,
                transliteration = dua.transliteration,
                translation = dua.translationHausa.ifBlank { dua.translation },
                reference = if (dua.reference.startsWith("★")) dua.reference else "★ ${dua.reference}",
                dbId = dua.id
            )
        }
    }

    /**
     * Retrieves all items for a given content type from the full app database.
     */
    fun getItemsForCategory(type: WidgetContentType): List<WidgetItem> {
        val all = allDatabaseWidgetItems
        return when (type) {
            WidgetContentType.ADDUA -> {
                val list = all.filter { it.type == WidgetContentType.ADDUA }
                if (list.isNotEmpty()) list else all
            }
            WidgetContentType.AZKAR -> {
                val list = all.filter { it.type == WidgetContentType.AZKAR }
                if (list.isNotEmpty()) list else all
            }
            WidgetContentType.SURAH -> {
                val list = all.filter { it.type == WidgetContentType.SURAH }
                if (list.isNotEmpty()) list else all
            }
            WidgetContentType.COMBINED,
            WidgetContentType.PRAYER_TIMES -> all
        }
    }

    /**
     * Localizes a WidgetItem according to the active app language across all supported languages.
     */
    fun localizeItem(item: WidgetItem, language: String): WidgetItem {
        val dbId = item.dbId ?: item.id.removePrefix("db_").toIntOrNull()
        val seedDua = if (dbId != null) {
            com.example.data.local.DuaDatabaseSeeder.getSeedDuas().find { it.id == dbId }
        } else {
            com.example.data.local.DuaDatabaseSeeder.getSeedDuas().find { 
                it.arabic == item.arabic || it.title == item.title.removePrefix("🤲 ").removePrefix("📿 ").removePrefix("📖 ")
            }
        }

        if (seedDua != null) {
            val localizedTitle = AppLocalizer.getDuaTitle(seedDua.id, seedDua.title, language)
            val localizedMeaning = DuaTranslationLocalization.getLocalizedTranslation(
                duaId = seedDua.id,
                language = language,
                defaultTranslation = seedDua.translation,
                hausa = seedDua.translationHausa,
                yoruba = seedDua.translationYoruba,
                igbo = seedDua.translationIgbo
            )
            val localizedRef = DuaReferenceLocalization.getLocalizedReference(seedDua.id, language) ?: seedDua.reference
            val icon = when (item.type) {
                WidgetContentType.AZKAR -> "📿"
                WidgetContentType.SURAH -> "📖"
                else -> "🤲"
            }
            return item.copy(
                title = "$icon $localizedTitle",
                translation = localizedMeaning,
                reference = if (localizedRef.startsWith("★")) localizedRef else "★ $localizedRef",
                arabic = seedDua.arabic,
                transliteration = seedDua.transliteration,
                dbId = seedDua.id
            )
        }

        return item
    }

    /**
     * Returns the currently active WidgetItem to display based on current selection/index
     * with full dynamic localization support across all selected languages.
     */
    fun getCurrentItem(context: Context): WidgetItem {
        val selectedLanguage = AppLocalizer.getAppSelectedLanguage(context)

        // 1. Check if user selected a custom Dua from the full database list
        if (getPrefs(context).getBoolean(KEY_CUSTOM_ACTIVE, false)) {
            val dbId = getPrefs(context).getInt(KEY_CUSTOM_DB_ID, -1).takeIf { it > 0 }
            if (dbId != null) {
                val foundSeed = com.example.data.local.DuaDatabaseSeeder.getSeedDuas().find { it.id == dbId }
                if (foundSeed != null) {
                    val isZikr = foundSeed.category.contains("Adhkar", ignoreCase = true) ||
                                 foundSeed.category.contains("Tasbih", ignoreCase = true) ||
                                 foundSeed.category.contains("Asma", ignoreCase = true)
                    val isSurah = foundSeed.category.contains("Surah", ignoreCase = true) ||
                                  foundSeed.category.contains("Rabbana", ignoreCase = true)
                    val type = when {
                        isSurah -> WidgetContentType.SURAH
                        isZikr -> WidgetContentType.AZKAR
                        else -> WidgetContentType.ADDUA
                    }
                    val rawItem = WidgetItem(
                        id = "db_${foundSeed.id}",
                        type = type,
                        title = foundSeed.title,
                        arabic = foundSeed.arabic,
                        transliteration = foundSeed.transliteration,
                        translation = foundSeed.translationHausa.ifBlank { foundSeed.translation },
                        reference = foundSeed.reference,
                        dbId = foundSeed.id
                    )
                    return localizeItem(rawItem, selectedLanguage)
                }
            }
            val title = getPrefs(context).getString(KEY_CUSTOM_TITLE, "") ?: ""
            val arabic = getPrefs(context).getString(KEY_CUSTOM_ARABIC, "") ?: ""
            val transliteration = getPrefs(context).getString(KEY_CUSTOM_TRANSLITERATION, "") ?: ""
            val translation = getPrefs(context).getString(KEY_CUSTOM_TRANSLATION, "") ?: ""
            val reference = getPrefs(context).getString(KEY_CUSTOM_REFERENCE, "") ?: ""
            val typeStr = getPrefs(context).getString(KEY_CUSTOM_TYPE, WidgetContentType.ADDUA.id)
            val type = WidgetContentType.values().firstOrNull { it.id == typeStr } ?: WidgetContentType.ADDUA
            val id = getPrefs(context).getString(KEY_CUSTOM_ID, "custom") ?: "custom"

            if (title.isNotBlank() && arabic.isNotBlank()) {
                val rawItem = WidgetItem(id, type, title, arabic, transliteration, translation, reference, dbId)
                return localizeItem(rawItem, selectedLanguage)
            }
        }

        val type = getSelectedContentType(context)
        val items = getItemsForCategory(type)
        if (items.isEmpty()) {
            val fallback = allDatabaseWidgetItems.firstOrNull() ?: duasList[0]
            return localizeItem(fallback, selectedLanguage)
        }

        // Check if user locked onto a specific item
        val specificId = getPrefs(context).getString(KEY_SPECIFIC_ITEM_ID, null)
        if (!specificId.isNullOrBlank()) {
            val found = items.firstOrNull { it.id == specificId }
            if (found != null) return localizeItem(found, selectedLanguage)
        }

        val currentIndex = getPrefs(context).getInt(KEY_CURRENT_INDEX_PREFIX + type.id, 0)
        val safeIndex = if (items.isNotEmpty()) Math.floorMod(currentIndex, items.size) else 0
        val selectedItem = items[safeIndex]
        return localizeItem(selectedItem, selectedLanguage)
    }

    /**
     * Cycles to the next item within the active category across the entire database list.
     */
    fun nextItem(context: Context): WidgetItem {
        // Clear custom locking when user explicitly clicks Next / Shuffle
        getPrefs(context).edit().putBoolean(KEY_CUSTOM_ACTIVE, false).apply()

        val type = getSelectedContentType(context)
        val items = getItemsForCategory(type)
        if (items.isEmpty()) {
            return getCurrentItem(context)
        }

        val currentIndex = getPrefs(context).getInt(KEY_CURRENT_INDEX_PREFIX + type.id, 0)
        val nextIndex = (currentIndex + 1) % items.size
        getPrefs(context).edit()
            .putInt(KEY_CURRENT_INDEX_PREFIX + type.id, nextIndex)
            .remove(KEY_SPECIFIC_ITEM_ID)
            .apply()

        val selectedLanguage = AppLocalizer.getAppSelectedLanguage(context)
        return localizeItem(items[nextIndex], selectedLanguage)
    }

    /**
     * Toggles to the next content category (Addua -> Azkar -> Surah -> Prayer Times -> Combined).
     */
    fun toggleNextCategory(context: Context): WidgetContentType {
        getPrefs(context).edit().putBoolean(KEY_CUSTOM_ACTIVE, false).apply()
        val current = getSelectedContentType(context)
        val all = WidgetContentType.values()
        val next = all[(current.ordinal + 1) % all.size]
        setSelectedContentType(context, next)
        return next
    }

    /**
     * Locks onto a specific item chosen by the user.
     */
    fun setSelectedItem(context: Context, item: WidgetItem) {
        setSelectedContentType(context, item.type)
        getPrefs(context).edit()
            .putBoolean(KEY_CUSTOM_ACTIVE, false)
            .putString(KEY_SPECIFIC_ITEM_ID, item.id)
            .apply()
    }

    /**
     * Sets a custom DuaEntity chosen from the full database list as the active widget item.
     */
    fun setCustomDuaEntity(context: Context, dua: com.example.data.local.DuaEntity, language: String): WidgetItem {
        val translated = DuaTranslationLocalization.getLocalizedTranslation(
            dua.id,
            language,
            dua.translation,
            dua.translationHausa,
            dua.translationYoruba,
            dua.translationIgbo
        )

        val localizedTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, language)
        val localizedRef = DuaReferenceLocalization.getLocalizedReference(dua.id, language) ?: dua.reference

        val type = when {
            dua.category.contains("Morning", ignoreCase = true) ||
            dua.category.contains("Evening", ignoreCase = true) ||
            dua.category.contains("Post-Salah", ignoreCase = true) ||
            dua.category.contains("Tasbih", ignoreCase = true) -> WidgetContentType.AZKAR

            dua.category.contains("Quran", ignoreCase = true) ||
            dua.category.contains("Rabbana", ignoreCase = true) ||
            dua.category.contains("Surah", ignoreCase = true) -> WidgetContentType.SURAH

            else -> WidgetContentType.ADDUA
        }

        val icon = when (type) {
            WidgetContentType.AZKAR -> "📿"
            WidgetContentType.SURAH -> "📖"
            else -> "🤲"
        }

        val item = WidgetItem(
            id = "db_${dua.id}",
            type = type,
            title = "$icon $localizedTitle",
            arabic = dua.arabic,
            transliteration = dua.transliteration,
            translation = translated,
            reference = if (localizedRef.isNotBlank()) "★ $localizedRef" else "★ ${dua.category}",
            dbId = dua.id
        )

        getPrefs(context).edit()
            .putBoolean(KEY_CUSTOM_ACTIVE, true)
            .putString(KEY_CUSTOM_ID, item.id)
            .putString(KEY_CUSTOM_TYPE, item.type.id)
            .putString(KEY_CUSTOM_TITLE, item.title)
            .putString(KEY_CUSTOM_ARABIC, item.arabic)
            .putString(KEY_CUSTOM_TRANSLITERATION, item.transliteration)
            .putString(KEY_CUSTOM_TRANSLATION, item.translation)
            .putString(KEY_CUSTOM_REFERENCE, item.reference)
            .putInt(KEY_CUSTOM_DB_ID, dua.id)
            .putString(KEY_CONTENT_TYPE, item.type.id)
            .apply()

        return item
    }

    fun isCustomSelected(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_CUSTOM_ACTIVE, false)
    }

    fun getSelectedCustomId(context: Context): String? {
        return if (isCustomSelected(context)) getPrefs(context).getString(KEY_CUSTOM_ID, null) else null
    }
}
