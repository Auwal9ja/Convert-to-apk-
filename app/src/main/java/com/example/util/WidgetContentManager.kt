package com.example.util

import android.content.Context
import android.content.SharedPreferences

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

data class WidgetItem(
    val id: String,
    val type: WidgetContentType,
    val title: String,
    val arabic: String,
    val translation: String,
    val reference: String
)

object WidgetContentManager {

    private const val PREFS_NAME = "zakiru_widget_prefs"
    private const val KEY_CONTENT_TYPE = "key_widget_content_type"
    private const val KEY_CURRENT_INDEX_PREFIX = "key_widget_index_"
    private const val KEY_SPECIFIC_ITEM_ID = "key_widget_specific_item"

    // 1. Curated Addu'o'i (Du'as)
    val duasList = listOf(
        WidgetItem(
            id = "dua_1",
            type = WidgetContentType.ADDUA,
            title = "🤲 Sayyidul Istighfar",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لاَ يَغْفِرُ الذُّنُوبَ إِلاَّ أَنْتَ",
            translation = "Ya Allah, kai ne Ubangijina, babu abin bautawa da gaskiya sai kai. Ka halicce ni kuma ni bawanka ne. Ka gafarta mini zunubaina domin babu mai gafarta zunubi sai kai.",
            reference = "★ Buhari: 6306 • Shugaban Neman Gafara"
        ),
        WidgetItem(
            id = "dua_2",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Neman Sauƙi",
            arabic = "اللَّهُمَّ لا سَهْلَ إِلاَّ مَا جَعَلْتَهُ سَهْلاً، وَأَنْتَ تَجْعَلُ الْحَزْنَ إِذَا شِئْتَ سَهْلاً",
            translation = "Ya Allah, babu abu mai sauƙi face wanda ka sauƙaƙa shi, kuma idan ka so kana sanya kowane abu mai wuya ya zama mai sauƙi.",
            reference = "★ Ibn Hibban: 974 • Neman Yaye Matsala"
        ),
        WidgetItem(
            id = "dua_3",
            type = WidgetContentType.ADDUA,
            title = "🤲 Neman Tsari daga Damuwa",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْبُخْلِ وَالْجُبْنِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ",
            translation = "Ya Allah, ina neman tsarinka daga damuwa da baƙin ciki, da kasala, da rowa da tsoro, da nauyin bashi da rinjayar mutane.",
            reference = "★ Buhari: 2893 • Kariya daga Bakin Ciki"
        ),
        WidgetItem(
            id = "dua_4",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Fita daga Gida",
            arabic = "بِسْمِ اللَّهِ، تَوَكَّلْتُ عَلَى اللَّهِ، وَلاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ",
            translation = "Da sunan Allah, na dogara ga Allah, babu dabara kuma babu ƙarfi sai da taimakon Allah.",
            reference = "★ Abu Dawud: 5095 • Kariya da Jagora"
        ),
        WidgetItem(
            id = "dua_5",
            type = WidgetContentType.ADDUA,
            title = "🤲 Neman Alherin Duniya da Lahira",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            translation = "Ya Ubangijinmu, ka ba mu alheri a duniya, da alheri a lahira, kuma ka kare mu daga azabar wuta.",
            reference = "★ Suratul Baqarah: 201 • Addu'a Mafi Cika"
        ),
        WidgetItem(
            id = "dua_6",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Tabbatar da Zuciya",
            arabic = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ",
            translation = "Ya mai juya zukata, ka tabbatar da zuciyata a kan addininka.",
            reference = "★ Tirmidhi: 3522 • Tabbatuwa a Addini"
        ),
        WidgetItem(
            id = "dua_7",
            type = WidgetContentType.ADDUA,
            title = "🤲 Addu'ar Iyaye",
            arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            translation = "Ya Ubangijina, ka yi musu rahama kamar yadda suka rene ni tun ina ƙarami.",
            reference = "★ Suratul Isra'i: 24 • Ladabi da Neman Rahama"
        ),
        WidgetItem(
            id = "dua_8",
            type = WidgetContentType.ADDUA,
            title = "🤲 Neman Karuwar Ilimi",
            arabic = "رَّبِّ زِدْنِي عِلْمًا",
            translation = "Ya Ubangijina, ka ƙara mini ilimi mai amfani.",
            reference = "★ Surah Ta-Ha: 114 • Bunkasa Ilimi"
        )
    )

    // 2. Curated Azkar & Tasbeeh
    val azkarList = listOf(
        WidgetItem(
            id = "zkr_1",
            type = WidgetContentType.AZKAR,
            title = "📿 Sayyidul Azkar • Sau 100",
            arabic = "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            translation = "Babu abin bautawa da gaskiya sai Allah shi kaɗai, ba shi da abokin tarayya, mulki nasa ne, yabo nasa ne, kuma shi mai ikon yi ne a kan komai.",
            reference = "★ Buhari: 3293 • Lada kamar 'yanta bayi 10"
        ),
        WidgetItem(
            id = "zkr_2",
            type = WidgetContentType.AZKAR,
            title = "📿 Kalmomi Masu Nauyi a Ma'auni",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            translation = "Tsarki ya tabbata ga Allah tare da yabon sa, Tsarki ya tabbata ga Allah mai girma.",
            reference = "★ Buhari: 6406 • Masu nauyi a ma'auni"
        ),
        WidgetItem(
            id = "zkr_3",
            type = WidgetContentType.AZKAR,
            title = "📿 Zikiri na Safiya da Maraice",
            arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ",
            translation = "Mun wayi gari kuma mulki ya wayi gari na Allah ne, yabo duka na Allah ne, babu abin bautawa da gaskiya sai Allah.",
            reference = "★ Muslim: 2723 • Wanzar da Tsaro"
        ),
        WidgetItem(
            id = "zkr_4",
            type = WidgetContentType.AZKAR,
            title = "📿 Salatin Annabi (S.A.W)",
            arabic = "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ إِنَّكَ حَمِيدٌ مَجِيدٌ",
            translation = "Ya Allah, ka yi salati ga Annabi Muhammad da iyalan Muhammad kamar yadda ka yi wa Ibrahim da iyalansa.",
            reference = "★ Buhari: 3370 • Sau 10 na salatin Allah"
        ),
        WidgetItem(
            id = "zkr_5",
            type = WidgetContentType.AZKAR,
            title = "📿 Hauqala (Taska daga Aljanna)",
            arabic = "لاَ حَوْلَ وَلاَ قُوَّةَ إِلاَّ بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
            translation = "Babu dabara kuma babu ƙarfi sai da taimakon Allah maɗaukaki mai girma.",
            reference = "★ Buhari: 6384 • Taska daga karkashin Al'arshi"
        ),
        WidgetItem(
            id = "zkr_6",
            type = WidgetContentType.AZKAR,
            title = "📿 Neman Kariya daga Sharri • Sau 3",
            arabic = "بِسْمِ اللَّهِ الَّذِي لاَ يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلاَ فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            translation = "Da sunan Allah wanda babu abin da ke cutarwa tare da sunansa a cikin ƙasa ko sama, kuma shi mai ji ne masani.",
            reference = "★ Abu Dawud: 5088 • Kariya daga kowane cutarwa"
        ),
        WidgetItem(
            id = "zkr_7",
            type = WidgetContentType.AZKAR,
            title = "📿 Isasshen Mai Kariya (Hasbiyallahu)",
            arabic = "حَسْبِيَ اللَّهُ لاَ إِلَهَ إِلاَّ هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
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
            translation = "Allah babu abin bautawa da gaskiya sai shi, Rayayye ne wanda komai ke tsayuwa da shi, gyangyaɗi ko barci ba ya kama shi.",
            reference = "★ Ayar da ta fi kowace girma a Alkur'ani"
        ),
        WidgetItem(
            id = "surah_2",
            type = WidgetContentType.SURAH,
            title = "📖 Suratul Ikhlas (Tauhid)",
            arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
            translation = "Ka ce: Shi ne Allah, shi kaɗai ne. Allah abin nufi ne da buƙata. Bai haifa ba kuma ba a haife shi ba. Kuma babu wani da ya zama tamkar sa.",
            reference = "★ Surah mai daidai da kashi ɗaya bisa uku na Alkur'ani"
        ),
        WidgetItem(
            id = "surah_3",
            type = WidgetContentType.SURAH,
            title = "📖 Suratul Mulk (Ayah: 1)",
            arabic = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            translation = "Albarka ta tabbata ga wanda mulki ke hannunsa, kuma shi mai ikon yi ne a kan komai.",
            reference = "★ Mai ceton makarancinta daga azabar kabari"
        ),
        WidgetItem(
            id = "surah_4",
            type = WidgetContentType.SURAH,
            title = "📖 Samun Sauƙi (Al-Inshirah: 5-6)",
            arabic = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            translation = "Lalle ne tare da tsanani akwai sauƙi. Lalle ne tare da tsanani akwai sauƙi.",
            reference = "★ Suratul Inshirah • Tabbatar da Yaye Matsala"
        ),
        WidgetItem(
            id = "surah_5",
            type = WidgetContentType.SURAH,
            title = "📖 Dogaro ga Allah (At-Talaq: 3)",
            arabic = "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ ۚ إِنَّ اللَّهَ بَالِغُ أَمْرِهِ",
            translation = "Kuma wanda ya dogara ga Allah, to Allah ya ishe shi. Lalle Allah mai isar da umarninsa ne.",
            reference = "★ Surah At-Talaq: 3 • Samun Isarwar Ubangiji"
        ),
        WidgetItem(
            id = "surah_6",
            type = WidgetContentType.SURAH,
            title = "📖 Salati ga Annabi (Al-Ahzab: 56)",
            arabic = "إِنَّ اللَّهَ وَمَلَائِكَتَهُ يُصَلُّونَ عَلَى النَّبِيِّ ۚ يَا أَيُّهَا الَّذِينَ آمَنُوا صَلُّوا عَلَيْهِ وَسَلِّمُوا تَسْلِيمًا",
            translation = "Lalle Allah da Mala'ikunsa suna salati ga Annabi, ya ku waɗanda suka yi imani, ku yi salati a gare shi da sallama.",
            reference = "★ Surah Al-Ahzab: 56 • Umarnin Ubangiji"
        )
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
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
     * Retrieves all items for a given content type.
     */
    fun getItemsForCategory(type: WidgetContentType): List<WidgetItem> {
        return when (type) {
            WidgetContentType.ADDUA -> duasList
            WidgetContentType.AZKAR -> azkarList
            WidgetContentType.SURAH -> surahsList
            WidgetContentType.COMBINED -> duasList + azkarList + surahsList
            WidgetContentType.PRAYER_TIMES -> emptyList()
        }
    }

    /**
     * Returns the currently active WidgetItem to display based on current selection/index.
     */
    fun getCurrentItem(context: Context): WidgetItem {
        val type = getSelectedContentType(context)
        val items = getItemsForCategory(type)
        if (items.isEmpty()) {
            return duasList[0]
        }

        // Check if user locked onto a specific item
        val specificId = getPrefs(context).getString(KEY_SPECIFIC_ITEM_ID, null)
        if (!specificId.isNullOrBlank()) {
            val found = items.firstOrNull { it.id == specificId }
            if (found != null) return found
        }

        val index = getPrefs(context).getInt(KEY_CURRENT_INDEX_PREFIX + type.id, 0)
        return items[index.coerceIn(0, items.size - 1)]
    }

    /**
     * Cycles to the next item within the active category (e.g. Next Dua / Next Zikr / Next Surah).
     */
    fun nextItem(context: Context): WidgetItem {
        val type = getSelectedContentType(context)
        val items = getItemsForCategory(type)
        if (items.isEmpty()) {
            return duasList[0]
        }

        val currentIndex = getPrefs(context).getInt(KEY_CURRENT_INDEX_PREFIX + type.id, 0)
        val nextIndex = (currentIndex + 1) % items.size
        getPrefs(context).edit()
            .putInt(KEY_CURRENT_INDEX_PREFIX + type.id, nextIndex)
            .remove(KEY_SPECIFIC_ITEM_ID)
            .apply()

        return items[nextIndex]
    }

    /**
     * Toggles to the next content category (Addua -> Azkar -> Surah -> Prayer Times -> Combined).
     */
    fun toggleNextCategory(context: Context): WidgetContentType {
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
        getPrefs(context).edit().putString(KEY_SPECIFIC_ITEM_ID, item.id).apply()
    }
}
