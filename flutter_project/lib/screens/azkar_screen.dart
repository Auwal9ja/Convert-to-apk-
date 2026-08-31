import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AzkarScreen extends StatefulWidget {
  const AzkarScreen({super.key});

  @override
  State<AzkarScreen> createState() => _AzkarScreenState();
}

class _AzkarScreenState extends State<AzkarScreen> {
  String _selectedCategory = "All";
  String _selectedLanguage = "Hausa";
  String _searchQuery = "";
  final Map<int, int> _counters = {};

  @override
  void initState() {
    super.initState();
    _loadLanguagePreference();
  }

  Future<void> _loadLanguagePreference() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _selectedLanguage = prefs.getString('selected_language') ?? "Hausa";
    });
  }

  final List<Map<String, dynamic>> _azkarList = const [
    // --- MORNING ADHKAR (ZIKIRIN SAFE) ---
    {
      "id": 1,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "title": "Supplication for the Morning (Asbahna wa Asbahal-Mulk)",
      "titleHa": "Addu'ar Safiya (Asbahna wa Asbahal Mulk)",
      "arabic": "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
      "transliteration": "Asbahna wa asbahal-mulku lillahi, wal-hamdu lillahi, la ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadir.",
      "translationEn": "We have reached the morning and all sovereignty belongs to Allah, and all praise is for Allah. None has the right to be worshipped except Allah alone, without partner; to Him belongs all sovereignty and praise, and He is over all things omnipotent.",
      "translationHa": "Mun wayi gari kuma mulki ya wayi gari yana mai tabbata ga Allah, godiya ta tabbata ga Allah. Babu abin bautawa da gaskiya sai Allah shi kaɗai, bashi da abokin tarayya. Mulki da godiya nasa ne, kuma shi mai ikon yi ne a kan komai.",
      "translationYo": "A ti bọ si owurọ ati pe ijọba bọ si ti Allāhu, gbogbo ọpẹ si jẹ ti Allāhu. Ko si ọlọhun miiran bikoṣe Allāhu nikan lai si orogun...",
      "translationIg": "Anyị erutela ụtụtụ ma ọchịchị niile bụ nke Chineke, otuto niile bụkwa nke Chineke...",
      "targetCount": 1,
      "reference": "Sahih Muslim 4/2088 (Hisnul Muslim)"
    },
    {
      "id": 2,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "title": "Sayyidul Istighfar in the Morning (Master Supplication for Forgiveness)",
      "titleHa": "Sayyidul Istighfar da Safe (Shugaban Neman Gafara)",
      "arabic": "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
      "transliteration": "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika ma-stata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfiru-dhunuba illa Anta.",
      "translationEn": "O Allah, You are my Lord, there is none worthy of worship but You. You created me and I am Your slave. I keep Your covenant and my pledge to You so far as I am able. I seek refuge in You from the evil of what I have done. I admit to Your grace upon me and I admit to my sin. So forgive me, for none forgives sins but You.",
      "translationHa": "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halitta ni kuma ni bawanKa ne. Ina kan alkawarinKa da wa'adinKa gwargwadon ikona. Ina neman tsari da Kai daga sharrin abin da na aikata. Ina amsa muku ni'imarKa a kaina, kuma ina amsa zunubina. Don haka Ka gafarta mini, domin babu mai gafarta zunubai sai Kai.",
      "translationYo": "Allāhu n bẹ, Iwọ ni Ọlọrun mi, ko si ọba miran ti a gbọdọ jọsin fun afi Iwọ. Iwọ lo da mi, emi si ni ẹru Rẹ...",
      "translationIg": "Chineke, Gị bụ Onyenwe m, ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị...",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 7/150"
    },
    {
      "id": 3,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "title": "Morning Protection from All Harm (Recite 3 times)",
      "titleHa": "Neman Kariyar Safe Daga Dukkan Cutarwa (Sau 3)",
      "arabic": "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
      "transliteration": "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
      "translationEn": "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing (Recited 3 times in morning).",
      "translationHa": "Da sunan Allah wanda babu abin da ke cutarwa tare da sunanSa a kasa ko a sama, kuma Shi ne Mai ji, Masani. (Sau 3 da safe).",
      "translationYo": "Pẹlu orukọ Allāhu ti nkan kankan ko le pa lara pẹlu orukọ Rẹ ni ilẹ ati ni oju sanma...",
      "translationIg": "N'aha Chineke, Onye na-enweghị ihe ọ bụla nwere ike imerụ ahụ n'ụwa ma ọ bụ n'eluigwe...",
      "targetCount": 3,
      "reference": "Sunan Abi Dawud 4/323, Jami' At-Tirmidhi 5/465"
    },
    {
      "id": 4,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "title": "Beneficial Knowledge, Good Sustenance & Accepted Deeds",
      "titleHa": "Neman Ilimi Mai Amfani, Arziki Halal & Aiki Karɓaɓɓe",
      "arabic": "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
      "transliteration": "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbalan.",
      "translationEn": "O Allah, I ask You for beneficial knowledge, good and lawful sustenance, and accepted deeds.",
      "translationHa": "Ya Allah! Ina roƙonKa ilimi mai amfani, da arziki na halal mai daɗi, da aiki karɓaɓɓe. (Bayan sallan Asuba).",
      "translationYo": "Allāhu, mo n tọrọ lọwọ Rẹ imọ ti o wulo, ati ohun ti o dara ti o tọ, ati iṣẹ ti a tẹwọgba.",
      "translationIg": "Chineke, ana m arịọ Gị maka ihe ọmụma bara uru, na ihe e ji ebi ndụ dị mma ma dị ọcha...",
      "targetCount": 1,
      "reference": "Sunan Ibn Majah 1/298, Sahih Ibn Majah 1/152"
    },
    {
      "id": 5,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "title": "Glorification of Allah (Subhanallahi wa Bihamdihi 100 times)",
      "titleHa": "Tasbihi ga Allah da Safiya (Sau 100)",
      "arabic": "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
      "transliteration": "Subhanallahi wa bihamdihi.",
      "translationEn": "Glory is to Allah and praise is to Him (Recite 100 times in the morning).",
      "translationHa": "Tsarki ya tabbata ga Allah tare da godiyarSa (sau 100 da safe).",
      "translationYo": "Mimo ni fun Allāhu ati gbogbo ọpẹ jẹ Tirẹ (igba 100).",
      "translationIg": "Otuto dịrị Chineke na ekele dịrị Ya (ugboro 100).",
      "targetCount": 100,
      "reference": "Sahih Muslim 4/2071"
    },
    {
      "id": 6,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "title": "Seeking Refuge & Well-Being in All Directions",
      "titleHa": "Neman Lafiya da Kariya ta Kowace Fuska",
      "arabic": "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي وَآمِنْ رَوْعَاتِي",
      "transliteration": "Allahumma inni as'alukal-'afwa wal-'afiyah fid-dunya wal-akhirah. Allahumma inni as'alukal-'afwa wal-'afiyah fi dini wa dunyaya wa ahli wa mali...",
      "translationEn": "O Allah, I ask You for forgiveness and well-being in this world and the Hereafter. O Allah, I ask You for well-being in my religion, worldly affairs, family and wealth...",
      "translationHa": "Ya Allah! Ina roƙonKa afuwa da lafiya a duniya da lahira. Ya Allah! Ina roƙonKa afuwa da lafiya a addinina, duniyata, iyalina, da dukiyata...",
      "translationYo": "Allāhu, mo n tọrọ aforijin ati alaafia lọwọ Rẹ ni aiye ati ọrun...",
      "translationIg": "Chineke, ana m arịọ Gị mgbaghara na ezi ahụike n'ụwa a na n'eluigwe...",
      "targetCount": 1,
      "reference": "Sunan Abi Dawud 5074"
    },

    // --- EVENING ADHKAR (ZIKIRIN YAMMA) ---
    {
      "id": 7,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "title": "Virtue of Evening Dhikr",
      "titleHa": "Falalar Zikirin Yamma (Ku Ambace Ni Zan Ambace Ku)",
      "arabic": "فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ، يَا أَيُّهَا الَّذِينَ آمَنُوا اذْكُرُوا اللَّهَ ذِكْرًا كَثِيرًا وَسَبِّحُوهُ بُكْرَةً وَأَصِيلًا",
      "transliteration": "Fadhkuruni adhkurkum washkuru li wa la takfurun, ya ayyuhalladhina amanu-dhkurullaha dhikran kathiran wa sabbihuhu bukratan wa asila.",
      "translationEn": "Therefore remember Me, I will remember you; and be grateful to Me, and do not disbelieve in Me. O you who believe! Remember Allah with much remembrance, and glorify Him morning and evening.",
      "translationHa": "Saboda haka ku ambace Ni, Zan ambace ku; kuma ku gode Mini, kada ku kafirce Mini. Ya ku waɗanda kuka yi imani! Ku ambaci Allah ambato mai yawa, kuma ku yi tasbihi gare Shi da safe da yamma.",
      "translationYo": "Tẹsiwaju lati ranti Mi, Emi yoo ranti yin. Ẹ si dupe fun Mi, ẹ ma ṣe ṣaipẹ fun Mi. Ẹyin ti ẹ gbagbọ! Ẹ ranti Allāhu pẹlu iranti ti o pọ̀, ki ẹ si ṣe afọmọ fun Un ni owurọ ati irọlẹ.",
      "translationIg": "Cheta m ka m cheta gị. Nwee ekele n'ebe m nọ ma ghara ịgọnarị m. Ndị kweere! Chetanụ Chineke site n'ọtụtụ ncheta, ma na-eto Ya n'ụtụtụ na n'anyasị.",
      "targetCount": 1,
      "reference": "Surah Al-Baqarah 2:152, Surah Al-Ahzab 33:41-42. Hisnul Muslim."
    },
    {
      "id": 8,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "title": "Supplication for the Evening (Amsayna wa Amsal-Mulk)",
      "titleHa": "Addu'ar Maraice (Amsayna wa Amsal Mulk)",
      "arabic": "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
      "transliteration": "Amsayna wa amsal-mulku lillahi, wal-hamdu lillahi, la ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadir.",
      "translationEn": "We have reached the evening and all sovereignty belongs to Allah, and all praise is for Allah. None has the right to be worshipped except Allah alone, without partner...",
      "translationHa": "Mun shiga maraice kuma mulki ya shiga yana mai tabbata ga Allah, godiya ta tabbata ga Allah. Babu abin bautawa da gaskiya sai Allah shi kaɗai...",
      "translationYo": "A ti bọ si irọlẹ ati pe ijọba bọ si ti Allāhu Oluwa gbogbo agbaye...",
      "translationIg": "Anyị erutela anyasị ma ọchịchị niile bụ nke Chineke, Onyenwe ụwa niile...",
      "targetCount": 1,
      "reference": "Sahih Muslim 4/2088 (Hisnul Muslim)"
    },
    {
      "id": 9,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "title": "Evening Protection Against Harm (Recite 3 times)",
      "titleHa": "Kariyar Yamma Daga Dukkan Sharri (Sau 3)",
      "arabic": "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
      "transliteration": "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
      "translationEn": "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing (3 times in evening).",
      "translationHa": "Da sunan Allah wanda babu abin da ke cutarwa tare da sunanSa a kasa ko a sama, kuma Shi ne Mai ji, Masani. (Sau 3 da yamma).",
      "translationYo": "Pẹlu orukọ Allāhu ti nkan kankan ko le pa lara pẹlu orukọ Rẹ ni ilẹ ati ni oju sanma...",
      "translationIg": "N'aha Chineke, Onye na-enweghị ihe ọ bụla nwere ike imerụ ahụ...",
      "targetCount": 3,
      "reference": "Sunan Abi Dawud 4/323, Jami' At-Tirmidhi 5/465"
    },
    {
      "id": 10,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "title": "Sayyidul Istighfar in the Evening (Master Forgiveness)",
      "titleHa": "Sayyidul Istighfar da Yamma (Shugaban Neman Gafara)",
      "arabic": "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
      "transliteration": "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfiru-dhunuba illa Anta.",
      "translationEn": "O Allah, You are my Lord, there is none worthy of worship but You. You created me and I am Your slave...",
      "translationHa": "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halitta ni kuma ni bawanKa ne... (Wanda ya faɗe ta da yamma ya rasu a daren zai shiga Aljanna).",
      "translationYo": "Allāhu n bẹ, Iwọ ni Ọlọrun mi, ko si ọba miran ti a gbọdọ jọsin fun afi Iwọ...",
      "translationIg": "Chineke, Gị bụ Onyenwe m, ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị...",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 7/150"
    },
    {
      "id": 11,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "title": "Sufficient is Allah for Me (Recite 7 times in evening)",
      "titleHa": "Hasbiyallahu da Maraice (Sau 7)",
      "arabic": "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
      "transliteration": "Hasbiyallahu la ilaha illa Huwa 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azim.",
      "translationEn": "Allah is sufficient for me. There is no deity except Him. On Him I rely, and He is the Lord of the Mighty Throne (Recite 7 times).",
      "translationHa": "Allah Ya wadatar mini, babu abin bautawa da gaskiya sai Shi, gare Shi na dogara kuma Shi ne Ubangijin Al'arshi mai girma. (Sau 7 da yamma).",
      "translationYo": "Allāhu ti to fun mi. Ko si ọlọhun miiran bikoṣe Oun. Lọdọ Rẹ ni mo gbẹkẹle...",
      "translationIg": "Chineke ezutewo m. Ọ dịghị chi ọzọ ma e wezụga Ya...",
      "targetCount": 7,
      "reference": "Sunan Abi Dawud 4/321"
    },
    {
      "id": 12,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "title": "Evening Invocation for Relief (Ya Hayyu Ya Qayyum)",
      "titleHa": "Neman Agaji da Yamma (Ya Hayyu Ya Qayyum)",
      "arabic": "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
      "transliteration": "Ya Hayyu Ya Qayyum bi-rahmatika astagheeth, aslih li sha'ni kullahu wa la takilni ila nafsi tarfata 'ayn.",
      "translationEn": "O Ever-Living One, O Sustainer, by Your mercy I seek help. Rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
      "translationHa": "Ya Mai Rai Ya Tsayayye, da rahamarKa nake neman agaji, Ka gyara mini al'amurana baki ɗaya kuma kada Ka ragamar da ni zuwa ga kaina ko da ƙiftawar ido guda ne.",
      "translationYo": "Iwọ Alaye, Olugbe gbogbo nkan duro, pẹlu aanu Rẹ ni mo n wa iranlọwọ...",
      "translationIg": "Onye Dị Ndụ, Onye Na-elekọta ihe niile, site n'ebere Gị ka m na-achọ enyemaka...",
      "targetCount": 1,
      "reference": "Mustadrak Al-Hakim 1/545, Sahih At-Targhib 1/273"
    },

    // --- SLEEPING & WAKING UP ---
    {
      "id": 13,
      "category": "Sleeping & Waking Up",
      "categoryHa": "Barci & Tashi",
      "title": "Supplication Before Sleeping",
      "titleHa": "Addu'ar Kafin Kwanciya Barci",
      "arabic": "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
      "transliteration": "Bismika Rabbi wada'tu janbi, wa bika arfa'uhu, fa'in amsakta nafsi farhamha, wa in arsaltaha fahfazha bima tahfazu bihi 'ibadakas-salihin.",
      "translationEn": "In Your Name my Lord, I lie down, and in Your Name I rise. If You take my soul, have mercy upon it; and if You return it, protect it as You protect Your righteous slaves.",
      "translationHa": "Da sunanKa Ya Ubangijina na kwantar da gefena, kuma da ikonKa nake ɗaga shi. Idan Ka riƙe raina to Ka yi masa rahama, idan kuma Ka sako shi to Ka kiyaye shi da abin da kake kiyaye bayinKa salihan bayi.",
      "translationYo": "Pẹlu orukọ Rẹ Oluwa mi ni mo fi dubulẹ, pẹlu orukọ Rẹ si ni mo fi dide...",
      "translationIg": "N'aha Gị Onyenwe m ka m ji dina ala, n'aha Gị ka m ji ebili...",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 11/126, Sahih Muslim 4/2084"
    },
    {
      "id": 14,
      "category": "Sleeping & Waking Up",
      "categoryHa": "Barci & Tashi",
      "title": "Supplication Upon Waking Up",
      "titleHa": "Addu'ar Tashi Daga Barci",
      "arabic": "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
      "transliteration": "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur.",
      "translationEn": "All praise is for Allah Who gave us life after having taken it from us and unto Him is the resurrection.",
      "translationHa": "Godiya ta tabbata ga Allah wanda Ya raya mu bayan Ya kashe mu kuma zuwa gare Shi tashi yake.",
      "translationYo": "Gbogbo ọpẹ jẹ ti Allāhu ti O sọ wa di alaye lẹhin ti O ti mu wa ku, ati ọdọ Rẹ ni ijinde n bẹ.",
      "translationIg": "Otuto niile diri Chineke Onye nyere anyi ndu mgbe O mechara ka anyi nwuo...",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 11/113, Sahih Muslim 4/2083"
    },

    // --- FOOD & DRINKING ---
    {
      "id": 15,
      "category": "Food & Eating",
      "categoryHa": "Cin Abinci",
      "title": "Supplication Before Eating",
      "titleHa": "Addu'ar Kafin Fara Cin Abinci",
      "arabic": "بِسْمِ اللَّهِ",
      "transliteration": "Bismillah.",
      "translationEn": "In the Name of Allah.",
      "translationHa": "Da sunan Allah.",
      "translationYo": "Pẹlu orukọ Allāhu.",
      "translationIg": "N'aha Chineke.",
      "targetCount": 1,
      "reference": "Sunan Abi Dawud 3/347"
    },
    {
      "id": 16,
      "category": "Food & Eating",
      "categoryHa": "Cin Abinci",
      "title": "Supplication After Finishing Meal",
      "titleHa": "Addu'ar Bayan Gama Cin Abinci",
      "arabic": "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
      "transliteration": "Alhamdu lillahil-ladhi at'amani hadha wa razaqanihi min ghayri hawlin minni wa la quwwatin.",
      "translationEn": "All praise is for Allah Who has fed me this and provided it for me without any might or power from myself.",
      "translationHa": "Godiya ta tabbata ga Allah wanda Ya ciyar da ni wannan kuma Ya azurta ni da shi ba tare da wata dabara ko ƙarfi daga gare ni ba.",
      "translationYo": "Gbogbo ọpẹ jẹ ti Allāhu ti O bọ mi pẹlu eyi ti O si pese rẹ fun mi...",
      "translationIg": "Otuto diri Chineke Onye nyere m nri a ma wetara m ya na enweghi ike n'onwe m.",
      "targetCount": 1,
      "reference": "Jami' At-Tirmidhi, Sunan Ibn Majah"
    }
  ];

  @override
  Widget build(BuildContext context) {
    final isHausa = _selectedLanguage == "Hausa";

    final categories = isHausa
        ? ["Duka", "Zikirin Safe", "Zikirin Yamma", "Barci & Tashi", "Cin Abinci"]
        : ["All", "Morning Adhkar", "Evening Adhkar", "Sleeping & Waking Up", "Food & Eating"];

    final filteredAzkar = _azkarList.filter((item) {
      // Category filter
      if (_selectedCategory != "All" && _selectedCategory != "Duka") {
        final catEn = item["category"];
        final catHa = item["categoryHa"];
        if (_selectedCategory != catEn && _selectedCategory != catHa) {
          return false;
        }
      }

      // Search filter
      if (_searchQuery.isNotEmpty) {
        final query = _searchQuery.toLowerCase();
        final title = (item["title"] ?? "").toString().toLowerCase();
        final titleHa = (item["titleHa"] ?? "").toString().toLowerCase();
        final trans = (item["transliteration"] ?? "").toString().toLowerCase();
        final arabic = (item["arabic"] ?? "").toString();
        final transEn = (item["translationEn"] ?? "").toString().toLowerCase();
        final transHa = (item["translationHa"] ?? "").toString().toLowerCase();

        return title.contains(query) ||
            titleHa.contains(query) ||
            trans.contains(query) ||
            arabic.contains(query) ||
            transEn.contains(query) ||
            transHa.contains(query);
      }

      return true;
    }).toList();

    return Scaffold(
      appBar: AppBar(
        title: Text(isHausa ? 'Hisnul Muslim (Addu\'o\'i & Azkar)' : 'Hisnul Muslim (Duas & Adhkar)'),
        elevation: 0,
        actions: [
          PopupMenuButton<String>(
            icon: const Icon(Icons.language),
            tooltip: isHausa ? "Zaɓi Harshe" : "Select Language",
            onSelected: (lang) async {
              setState(() {
                _selectedLanguage = lang;
                _selectedCategory = lang == "Hausa" ? "Duka" : "All";
              });
              final prefs = await SharedPreferences.getInstance();
              await prefs.setString('selected_language', lang);
            },
            itemBuilder: (context) => [
              const PopupMenuItem(value: "Hausa", child: Text("Hausa")),
              const PopupMenuItem(value: "English", child: Text("English")),
              const PopupMenuItem(value: "Yoruba", child: Text("Yorùbá")),
              const PopupMenuItem(value: "Igbo", child: Text("Igbo")),
            ],
          ),
        ],
      ),
      body: Column(
        children: [
          // Search Box & Language indicator
          Container(
            color: const Color(0xFF1B5E20),
            padding: const EdgeInsets.fromLTRB(14, 0, 14, 12),
            child: TextField(
              onChanged: (val) => setState(() => _searchQuery = val),
              decoration: InputDecoration(
                hintText: isHausa ? 'Nemi addu\'a ko zikiri...' : 'Search supplication or dhikr...',
                hintStyle: const TextStyle(color: Colors.white70, fontSize: 14),
                prefixIcon: const Icon(Icons.search, color: Colors.white70),
                filled: true,
                fillColor: Colors.white.withOpacity(0.18),
                contentPadding: const EdgeInsets.symmetric(vertical: 0, horizontal: 16),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                  borderSide: BorderSide.none,
                ),
              ),
              style: const TextStyle(color: Colors.white),
            ),
          ),

          // Categories Chips
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
            child: Row(
              children: categories.map((cat) {
                final isSelected = _selectedCategory == cat;
                String emoji = "📖";
                if (cat.contains("Safe") || cat.contains("Morning")) emoji = "🌅";
                if (cat.contains("Yamma") || cat.contains("Evening")) emoji = "🌆";
                if (cat.contains("Barci") || cat.contains("Sleeping")) emoji = "🌙";
                if (cat.contains("Cin") || cat.contains("Food")) emoji = "🍽️";

                return Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 4),
                  child: FilterChip(
                    label: Text("$emoji $cat"),
                    selected: isSelected,
                    onSelected: (selected) {
                      setState(() {
                        _selectedCategory = cat;
                      });
                    },
                    selectedColor: const Color(0xFF1B5E20),
                    labelStyle: TextStyle(
                      color: isSelected ? Colors.white : const Color(0xFF1B5E20),
                      fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                      fontSize: 13,
                    ),
                    backgroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20),
                      side: BorderSide(
                        color: isSelected ? const Color(0xFF1B5E20) : Colors.grey.shade300,
                      ),
                    ),
                  ),
                );
              }).toList(),
            ),
          ),

          // Main Dua List
          Expanded(
            child: filteredAzkar.isEmpty
                ? Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.search_off, size: 54, color: Colors.grey.shade400),
                        const SizedBox(height: 12),
                        Text(
                          isHausa ? "Ba a sami addu'ar da kake nema ba" : "No supplication found",
                          style: TextStyle(color: Colors.grey.shade600, fontSize: 15),
                        ),
                      ],
                    ),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    itemCount: filteredAzkar.length,
                    itemBuilder: (context, index) {
                      final item = filteredAzkar[index];
                      final id = item["id"] as int;
                      final targetCount = (item["targetCount"] as int?) ?? 1;
                      final currentCount = _counters[id] ?? 0;
                      final isCompleted = currentCount >= targetCount;

                      String titleText = item["titleEn"] ?? item["title"] ?? "";
                      if (isHausa && item["titleHa"] != null) {
                        titleText = item["titleHa"]!;
                      }

                      String translationText = item["translationEn"] ?? "";
                      if (_selectedLanguage == "Hausa" && item["translationHa"] != null) {
                        translationText = item["translationHa"]!;
                      } else if (_selectedLanguage == "Yoruba" && item["translationYo"] != null) {
                        translationText = item["translationYo"]!;
                      } else if (_selectedLanguage == "Igbo" && item["translationIg"] != null) {
                        translationText = item["translationIg"]!;
                      }

                      final isMorning = item["category"] == "Morning Adhkar";
                      final isEvening = item["category"] == "Evening Adhkar";

                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        elevation: 1.5,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16),
                          side: BorderSide(
                            color: isCompleted
                                ? const Color(0xFF2E7D32).withOpacity(0.5)
                                : Colors.grey.shade200,
                            width: isCompleted ? 1.5 : 1.0,
                          ),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              // Header with Category Badge & Repetition Pill
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                                    decoration: BoxDecoration(
                                      color: isMorning
                                          ? const Color(0xFFFFF3E0)
                                          : isEvening
                                              ? const Color(0xFFEDE7F6)
                                              : const Color(0xFFE8F5E9),
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                    child: Text(
                                      isHausa
                                          ? (item["categoryHa"] ?? item["category"])
                                          : item["category"],
                                      style: TextStyle(
                                        fontSize: 11,
                                        fontWeight: FontWeight.bold,
                                        color: isMorning
                                            ? const Color(0xFFE65100)
                                            : isEvening
                                                ? const Color(0xFF512DA8)
                                                : const Color(0xFF1B5E20),
                                      ),
                                    ),
                                  ),
                                  if (targetCount > 1)
                                    Container(
                                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                      decoration: BoxDecoration(
                                        color: const Color(0xFF004D40).withOpacity(0.08),
                                        borderRadius: BorderRadius.circular(10),
                                      ),
                                      child: Text(
                                        isHausa ? "Maimaita Sau $targetCount" : "Recite $targetCount times",
                                        style: const TextStyle(
                                          fontSize: 11,
                                          fontWeight: FontWeight.w600,
                                          color: Color(0xFF004D40),
                                        ),
                                      ),
                                    ),
                                ],
                              ),
                              const SizedBox(height: 10),

                              // Title
                              Text(
                                titleText,
                                style: const TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: 15,
                                  color: Color(0xFF1B5E20),
                                ),
                              ),
                              const SizedBox(height: 12),

                              // Arabic Script
                              Container(
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  color: const Color(0xFFF9FBF9),
                                  borderRadius: BorderRadius.circular(12),
                                ),
                                child: Text(
                                  item["arabic"]!,
                                  textAlign: TextAlign.right,
                                  style: const TextStyle(
                                    fontSize: 21,
                                    fontWeight: FontWeight.bold,
                                    height: 1.7,
                                    color: Color(0xFF132D27),
                                  ),
                                ),
                              ),
                              const SizedBox(height: 10),

                              // Transliteration
                              Text(
                                item["transliteration"]!,
                                style: TextStyle(
                                  fontStyle: FontStyle.italic,
                                  fontSize: 13,
                                  color: Colors.blueGrey.shade800,
                                  height: 1.4,
                                ),
                              ),
                              const SizedBox(height: 8),

                              // Meaning / Translation
                              Text(
                                translationText,
                                style: TextStyle(
                                  color: Colors.grey.shade800,
                                  fontSize: 13.5,
                                  height: 1.45,
                                ),
                              ),
                              const SizedBox(height: 12),

                              // Reference & Counter action button
                              const Divider(height: 1),
                              const SizedBox(height: 8),
                              Row(
                                children: [
                                  Expanded(
                                    child: Text(
                                      item["reference"] ?? "",
                                      style: TextStyle(
                                        fontSize: 11,
                                        color: Colors.grey.shade600,
                                        fontStyle: FontStyle.italic,
                                      ),
                                    ),
                                  ),
                                  // Counter Button
                                  InkWell(
                                    onTap: () {
                                      setState(() {
                                        if (currentCount < targetCount) {
                                          _counters[id] = currentCount + 1;
                                        } else {
                                          _counters[id] = 0; // reset
                                        }
                                      });
                                    },
                                    borderRadius: BorderRadius.circular(20),
                                    child: Container(
                                      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                                      decoration: BoxDecoration(
                                        color: isCompleted
                                            ? const Color(0xFF2E7D32)
                                            : const Color(0xFF1B5E20).withOpacity(0.1),
                                        borderRadius: BorderRadius.circular(20),
                                      ),
                                      child: Row(
                                        mainAxisSize: MainAxisSize.min,
                                        children: [
                                          Icon(
                                            isCompleted ? Icons.check_circle : Icons.touch_app,
                                            size: 16,
                                            color: isCompleted ? Colors.white : const Color(0xFF1B5E20),
                                          ),
                                          const SizedBox(width: 6),
                                          Text(
                                            "$currentCount / $targetCount",
                                            style: TextStyle(
                                              fontSize: 12,
                                              fontWeight: FontWeight.bold,
                                              color: isCompleted ? Colors.white : const Color(0xFF1B5E20),
                                            ),
                                          ),
                                        ],
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}

extension ListFilter<E> on List<E> {
  List<E> filter(bool Function(E element) test) {
    return where(test).toList();
  }
}
