import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../data/asmaul_and_ijaba_data.dart';

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

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _loadLanguagePreference();
  }

  Future<void> _loadLanguagePreference() async {
    final prefs = await SharedPreferences.getInstance();
    final savedLang = prefs.getString('selected_language') ?? "Hausa";
    if (savedLang != _selectedLanguage) {
      setState(() {
        _selectedLanguage = savedLang;
        _selectedCategory = _getAllCategoryLabel(savedLang);
      });
    }
  }

  String _getAllCategoryLabel(String lang) {
    switch (lang) {
      case "Hausa":
        return "Duka";
      case "Yoruba":
        return "Gbogbo rẹ";
      case "Igbo":
        return "Niile";
      case "Arabic":
        return "الكل";
      default:
        return "All";
    }
  }

  final List<Map<String, dynamic>> _azkarList = const [
    // --- MORNING ADHKAR (ZIKIRIN SAFE) ---
    {
      "id": 1,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "categoryYo": "Azkar Owurọ̀",
      "categoryIg": "Azkar Ụtụtụ",
      "title": "Supplication for the Morning (Asbahna wa Asbahal-Mulk)",
      "titleHa": "Addu'ar Safiya (Asbahna wa Asbahal Mulk)",
      "titleYo": "Àdúà Owurọ̀ (Asbahna wa Asbahal-Mulk)",
      "titleIg": "Ekpere Ụtụtụ (Asbahna wa Asbahal-Mulk)",
      "arabic": "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
      "transliteration": "Asbahna wa asbahal-mulku lillahi, wal-hamdu lillahi, la ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadir.",
      "translationEn": "We have reached the morning and all sovereignty belongs to Allah, and all praise is for Allah. None has the right to be worshipped except Allah alone, without partner; to Him belongs all sovereignty and praise, and He is over all things omnipotent.",
      "translationHa": "Mun wayi gari kuma mulki ya wayi gari yana mai tabbata ga Allah, godiya ta tabbata ga Allah. Babu abin bautawa da gaskiya sai Allah shi kaɗai, bashi da abokin tarayya. Mulki da godiya nasa ne, kuma shi mai ikon yi ne a kan komai.",
      "translationYo": "A ti bọ si owurọ ati pe ijọba bọ si ti Allāhu, gbogbo ọpẹ si jẹ ti Allāhu. Ko si ọlọhun miiran bikoṣe Allāhu nikan lai si orogun; tiRẹ ni gbogbo ijọba ati ọpẹ, Oun si ni Alagbara lori ohun gbogbo.",
      "translationIg": "Anyị erutela ụtụtụ ma ọchịchị niile bụ nke Chineke, otuto niile bụkwa nke Chineke. Ọ dịghị onye kwesịrị ofufe ma ọ bụghị Chineke naanị Ya, na-enweghị onye mmekọ; nke Ya bụ ọchịchị na otuto niile, Ọ nwekwara ike n'elu ihe niile.",
      "targetCount": 1,
      "reference": "Sahih Muslim 4/2088 (Hisnul Muslim)"
    },
    {
      "id": 2,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "categoryYo": "Azkar Owurọ̀",
      "categoryIg": "Azkar Ụtụtụ",
      "title": "Sayyidul Istighfar in the Morning (Master Supplication for Forgiveness)",
      "titleHa": "Sayyidul Istighfar da Safe (Shugaban Neman Gafara)",
      "titleYo": "Sayyidul Istighfar ni Owurọ̀ (Olórí Ẹ̀bẹ̀ Ìdáríjì)",
      "titleIg": "Sayyidul Istighfar n'Ụtụtụ (Isi Ekpere Mgbaghara)",
      "arabic": "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
      "transliteration": "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika ma-stata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfiru-dhunuba illa Anta.",
      "translationEn": "O Allah, You are my Lord, there is none worthy of worship but You. You created me and I am Your slave. I keep Your covenant and my pledge to You so far as I am able. I seek refuge in You from the evil of what I have done. I admit to Your grace upon me and I admit to my sin. So forgive me, for none forgives sins but You.",
      "translationHa": "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halitta ni kuma ni bawanKa ne. Ina kan alkawarinKa da wa'adinKa gwargwadon ikona. Ina neman tsari da Kai daga sharrin abin da na aikata. Ina amsa muku ni'imarKa a kaina, kuma ina amsa zunubina. Don haka Ka gafarta mini, domin babu mai gafarta zunubai sai Kai.",
      "translationYo": "Allāhu n bẹ, Iwọ ni Ọlọrun mi, ko si ọba miran ti a gbọdọ jọsin fun afi Iwọ. Iwọ lo da mi, emi si ni ẹru Rẹ. Mo duro lori adehun Rẹ ati ileri Rẹ bi agbara mi ti mọ. Mo wa aabo pẹlu Rẹ kuro ninu aburu ohun ti mo ṣe. Mo jẹwọ ibukun Rẹ lori mi, mo si jẹwọ ẹṣẹ mi. Nitori naa dariji mi, ko si ẹnikan ti o le dari ẹṣẹ ji afi Iwọ.",
      "translationIg": "Chineke, Gị bụ Onyenwe m, ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị. Ị kere m ma abụ m ohu Gị. Ana m edebanye n'ọgbụgba ndụ Gị na nkwa Gị dịka ike m hà. Ana m achọ mgbaba n'ime Gị site n'ihe ọjọọ m mere. Ana m anabata amara Gị n'ebe m nọ ma na-anabata mmehie m. Ya mere gbaghara m, n'ihi na ọ dịghị onye na-agbaghara mmehie ma ọ bụghị Gị.",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 7/150"
    },
    {
      "id": 3,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "categoryYo": "Azkar Owurọ̀",
      "categoryIg": "Azkar Ụtụtụ",
      "title": "Morning Protection from All Harm (Recite 3 times)",
      "titleHa": "Neman Kariyar Safe Daga Dukkan Cutarwa (Sau 3)",
      "titleYo": "Ààbò Owurọ̀ Kúrò Nínú Ipalara Gbogbo (Igba 3)",
      "titleIg": "Nchedo Ụtụtụ Site n'Ihe Mmerụ Ahụ Niile (Ugboro 3)",
      "arabic": "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
      "transliteration": "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
      "translationEn": "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing (Recited 3 times in morning).",
      "translationHa": "Da sunan Allah wanda babu abin da ke cutarwa tare da sunanSa a kasa ko a sama, kuma Shi ne Mai ji, Masani. (Sau 3 da safe).",
      "translationYo": "Pẹlu orukọ Allāhu ti nkan kankan ko le pa lara pẹlu orukọ Rẹ ni ilẹ ati ni oju sanma, Oun si ni Olugbọ, Onimọ. (Igba mẹta ni owurọ).",
      "translationIg": "N'aha Chineke, Onye na-enweghị ihe ọ bụla nwere ike imerụ ahụ n'ụwa ma ọ bụ n'eluigwe n'aha Ya, Ọ bụkwa Onye Na-anụ Ihe, Onye Mara Ihe Niile. (Ugboro 3 n'ụtụtụ).",
      "targetCount": 3,
      "reference": "Sunan Abi Dawud 4/323, Jami' At-Tirmidhi 5/465"
    },
    {
      "id": 4,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "categoryYo": "Azkar Owurọ̀",
      "categoryIg": "Azkar Ụtụtụ",
      "title": "Beneficial Knowledge, Good Sustenance & Accepted Deeds",
      "titleHa": "Neman Ilimi Mai Amfani, Arziki Halal & Aiki Karɓaɓɓe",
      "titleYo": "Títọrọ Ìmọ̀ Tó Wúlò, Ọrọ̀ Rere & Iṣẹ́ Tó Gbà",
      "titleIg": "Ịrịọ Ihe Ọmụma Bara Uru, Nri Dị Mma & Ọrụ A Nabata",
      "arabic": "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
      "transliteration": "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan tayyiban, wa 'amalan mutaqabbalan.",
      "translationEn": "O Allah, I ask You for beneficial knowledge, good and lawful sustenance, and accepted deeds.",
      "translationHa": "Ya Allah! Ina roƙonKa ilimi mai amfani, da arziki na halal mai daɗi, da aiki karɓaɓɓe. (Bayan sallan Asuba).",
      "translationYo": "Allāhu, mo n tọrọ lọwọ Rẹ imọ ti o wulo, ati ohun elo igbesi aye to dara ti o tọ, ati iṣẹ ti a tẹwọgba.",
      "translationIg": "Chineke, ana m arịọ Gị maka ihe ọmụma bara uru, na ihe e ji ebi ndụ dị mma ma dị ọcha, na ọrụ a nabatara.",
      "targetCount": 1,
      "reference": "Sunan Ibn Majah 1/298, Sahih Ibn Majah 1/152"
    },
    {
      "id": 5,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "categoryYo": "Azkar Owurọ̀",
      "categoryIg": "Azkar Ụtụtụ",
      "title": "Glorification of Allah (Subhanallahi wa Bihamdihi 100 times)",
      "titleHa": "Tasbihi ga Allah da Safiya (Sau 100)",
      "titleYo": "Afọmọ fun Allāhu (Subhanallahi wa Bihamdihi Igba 100)",
      "titleIg": "Otuto Dịrị Chineke (Subhanallahi wa Bihamdihi Ugboro 100)",
      "arabic": "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
      "transliteration": "Subhanallahi wa bihamdihi.",
      "translationEn": "Glory is to Allah and praise is to Him (Recite 100 times in the morning).",
      "translationHa": "Tsarki ya tabbata ga Allah tare da godiyarSa (sau 100 da safe).",
      "translationYo": "Mimo ni fun Allāhu ati gbogbo ọpẹ jẹ Tirẹ (igba 100 ni owurọ).",
      "translationIg": "Otuto dịrị Chineke na ekele dịrị Ya (ugboro 100 n'ụtụtụ).",
      "targetCount": 100,
      "reference": "Sahih Muslim 4/2071"
    },
    {
      "id": 6,
      "category": "Morning Adhkar",
      "categoryHa": "Zikirin Safe",
      "categoryYo": "Azkar Owurọ̀",
      "categoryIg": "Azkar Ụtụtụ",
      "title": "Seeking Refuge & Well-Being in All Directions",
      "titleHa": "Neman Lafiya da Kariya ta Kowace Fuska",
      "titleYo": "Títọrọ Àlàáfíà àti Ààbò ní Gbogbo Ọ̀nà",
      "titleIg": "Ịrịọ Ezi Ahụike na Nchebe n'Ụzọ Niile",
      "arabic": "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي، اللَّهُمَّ اسْتُرْ عَوْرَاتِي وَآمِنْ رَوْعَاتِي",
      "transliteration": "Allahumma inni as'alukal-'afwa wal-'afiyah fid-dunya wal-akhirah. Allahumma inni as'alukal-'afwa wal-'afiyah fi dini wa dunyaya wa ahli wa mali...",
      "translationEn": "O Allah, I ask You for forgiveness and well-being in this world and the Hereafter. O Allah, I ask You for well-being in my religion, worldly affairs, family and wealth. O Allah, conceal my faults and calm my fears.",
      "translationHa": "Ya Allah! Ina roƙonKa afuwa da lafiya a duniya da lahira. Ya Allah! Ina roƙonKa afuwa da lafiya a addinina, duniyata, iyalina, da dukiyata. Ya Allah Ka suturta al'aurata kuma Ka kwantar mini da hankali.",
      "translationYo": "Allāhu, mo n tọrọ aforijin ati alaafia lọwọ Rẹ ni aiye ati ọrun. Allāhu, mo n tọrọ alaafia ninu ẹsin mi, ayé mi, ẹbi mi, ati dukia mi. Allāhu, bo aṣiri mi ki o si fi ọkan mi balẹ kuro ninu ẹ̀rù.",
      "translationIg": "Chineke, ana m arịọ Gị mgbaghara na ezi ahụike n'ụwa a na n'eluigwe. Chineke, ana m arịọ Gị ahụike n'okpukpe m, n'ihe omume ụwa m, ezinụlọ m na akụ na ụba m. Chineke, kpuchie mmejọ m ma mee ka obi dajụọ m.",
      "targetCount": 1,
      "reference": "Sunan Abi Dawud 5074"
    },

    // --- EVENING ADHKAR (ZIKIRIN YAMMA) ---
    {
      "id": 7,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "categoryYo": "Azkar Irọlẹ́",
      "categoryIg": "Azkar Anyasị",
      "title": "Virtue of Evening Dhikr",
      "titleHa": "Falalar Zikirin Yamma (Ku Ambace Ni Zan Ambace Ku)",
      "titleYo": "Ànfàní Azkar Irọlẹ́ (Ẹ Ranti Mi, Emi Yoo Ranti Yin)",
      "titleIg": "Uru Azkar Anyasị (Chetanụ M, M Ga-echeta Unu)",
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
      "categoryYo": "Azkar Irọlẹ́",
      "categoryIg": "Azkar Anyasị",
      "title": "Supplication for the Evening (Amsayna wa Amsal-Mulk)",
      "titleHa": "Addu'ar Maraice (Amsayna wa Amsal Mulk)",
      "titleYo": "Àdúà Irọlẹ́ (Amsayna wa Amsal-Mulk)",
      "titleIg": "Ekpere Anyasị (Amsayna wa Amsal-Mulk)",
      "arabic": "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
      "transliteration": "Amsayna wa amsal-mulku lillahi, wal-hamdu lillahi, la ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadir.",
      "translationEn": "We have reached the evening and all sovereignty belongs to Allah, and all praise is for Allah. None has the right to be worshipped except Allah alone, without partner; to Him belongs all sovereignty and praise, and He is over all things omnipotent.",
      "translationHa": "Mun shiga maraice kuma mulki ya shiga yana mai tabbata ga Allah, godiya ta tabbata ga Allah. Babu abin bautawa da gaskiya sai Allah shi kaɗai, bashi da abokin tarayya. Mulki da godiya nasa ne, kuma shi mai ikon yi ne a kan komai.",
      "translationYo": "A ti bọ si irọlẹ ati pe ijọba bọ si ti Allāhu Oluwa gbogbo agbaye, gbogbo ọpẹ si jẹ ti Allāhu. Ko si ọlọhun miiran bikoṣe Allāhu nikan lai si orogun; tiRẹ ni gbogbo ijọba ati ọpẹ, Oun si ni Alagbara lori ohun gbogbo.",
      "translationIg": "Anyị erutela anyasị ma ọchịchị niile bụ nke Chineke, otuto niile bụkwa nke Chineke. Ọ dịghị onye kwesịrị ofufe ma ọ bụghị Chineke naanị Ya, na-enweghị onye mmekọ; nke Ya bụ ọchịchị na otuto niile, Ọ nwekwara ike n'elu ihe niile.",
      "targetCount": 1,
      "reference": "Sahih Muslim 4/2088 (Hisnul Muslim)"
    },
    {
      "id": 9,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "categoryYo": "Azkar Irọlẹ́",
      "categoryIg": "Azkar Anyasị",
      "title": "Evening Protection Against Harm (Recite 3 times)",
      "titleHa": "Kariyar Yamma Daga Dukkan Sharri (Sau 3)",
      "titleYo": "Ààbò Irọlẹ́ Kúrò Nínú Ipalara Gbogbo (Igba 3)",
      "titleIg": "Nchedo Anyasị Site n'Ihe Mmerụ Ahụ Niile (Ugboro 3)",
      "arabic": "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
      "transliteration": "Bismillahil-ladhi la yadurru ma'as-mihi shay'un fil-ardi wa la fis-sama'i wa Huwas-Sami'ul-'Alim.",
      "translationEn": "In the Name of Allah, with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing (3 times in evening).",
      "translationHa": "Da sunan Allah wanda babu abin da ke cutarwa tare da sunanSa a kasa ko a sama, kuma Shi ne Mai ji, Masani. (Sau 3 da yamma).",
      "translationYo": "Pẹlu orukọ Allāhu ti nkan kankan ko le pa lara pẹlu orukọ Rẹ ni ilẹ ati ni oju sanma, Oun si ni Olugbọ, Onimọ. (Igba mẹta ni irọlẹ).",
      "translationIg": "N'aha Chineke, Onye na-enweghị ihe ọ bụla nwere ike imerụ ahụ n'ụwa ma ọ bụ n'eluigwe n'aha Ya, Ọ bụkwa Onye Na-anụ Ihe, Onye Mara Ihe Niile. (Ugboro 3 n'anyasị).",
      "targetCount": 3,
      "reference": "Sunan Abi Dawud 4/323, Jami' At-Tirmidhi 5/465"
    },
    {
      "id": 10,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "categoryYo": "Azkar Irọlẹ́",
      "categoryIg": "Azkar Anyasị",
      "title": "Sayyidul Istighfar in the Evening (Master Forgiveness)",
      "titleHa": "Sayyidul Istighfar da Yamma (Shugaban Neman Gafara)",
      "titleYo": "Sayyidul Istighfar ni Irọlẹ́ (Olórí Ẹ̀bẹ̀ Ìdáríjì)",
      "titleIg": "Sayyidul Istighfar n'Anyasị (Isi Ekpere Mgbaghara)",
      "arabic": "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
      "transliteration": "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u bidhanbi faghfir li fa'innahu la yaghfiru-dhunuba illa Anta.",
      "translationEn": "O Allah, You are my Lord, there is none worthy of worship but You. You created me and I am Your slave. I keep Your covenant and my pledge to You as far as I am able. I seek refuge in You from the evil of what I have done. I acknowledge Your grace upon me and I acknowledge my sin. So forgive me, for none forgives sins but You.",
      "translationHa": "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halitta ni kuma ni bawanKa ne. Ina kan alkawarinKa da wa'adinKa gwargwadon ikona. Ina neman tsari da Kai daga sharrin abin da na aikata. Ina amsa muku ni'imarKa a kaina, kuma ina amsa zunubina. Don haka Ka gafarta mini, domin babu mai gafarta zunubai sai Kai.",
      "translationYo": "Allāhu n bẹ, Iwọ ni Ọlọrun mi, ko si ọba miran ti a gbọdọ jọsin fun afi Iwọ. Iwọ lo da mi, emi si ni ẹru Rẹ. Mo duro lori adehun Rẹ ati ileri Rẹ bi agbara mi ti mọ. Mo wa aabo pẹlu Rẹ kuro ninu aburu ohun ti mo ṣe. Mo jẹwọ ibukun Rẹ lori mi, mo si jẹwọ ẹṣẹ mi. Nitori naa dariji mi, ko si ẹnikan ti o le dari ẹṣẹ ji afi Iwọ.",
      "translationIg": "Chineke, Gị bụ Onyenwe m, ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị. Ị kere m ma abụ m ohu Gị. Ana m edebanye n'ọgbụgba ndụ Gị na nkwa Gị dịka ike m hà. Ana m achọ mgbaba n'ime Gị site n'ihe ọjọọ m mere. Ana m anabata amara Gị n'ebe m nọ ma na-anabata mmehie m. Ya mere gbaghara m, n'ihi na ọ dịghị onye na-agbaghara mmehie ma ọ bụghị Gị.",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 7/150"
    },
    {
      "id": 11,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "categoryYo": "Azkar Irọlẹ́",
      "categoryIg": "Azkar Anyasị",
      "title": "Sufficient is Allah for Me (Recite 7 times in evening)",
      "titleHa": "Hasbiyallahu da Maraice (Sau 7)",
      "titleYo": "Allāhu Tó Fún Mi (Hasbiyallahu Igba 7)",
      "titleIg": "Chineke Ezutewo M (Hasbiyallahu Ugboro 7)",
      "arabic": "حَسْبِيَ اللَّهُ لَا إِلَهَ إِلَّا هُوَ عَلَيْهِ تَوَكَّلْتُ وَهُوَ رَبُّ الْعَرْشِ الْعَظِيمِ",
      "transliteration": "Hasbiyallahu la ilaha illa Huwa 'alayhi tawakkaltu wa Huwa Rabbul-'Arshil-'Azim.",
      "translationEn": "Allah is sufficient for me. There is no deity except Him. On Him I rely, and He is the Lord of the Mighty Throne (Recite 7 times).",
      "translationHa": "Allah Ya wadatar mini, babu abin bautawa da gaskiya sai Shi, gare Shi na dogara kuma Shi ne Ubangijin Al'arshi mai girma. (Sau 7 da yamma).",
      "translationYo": "Allāhu ti to fun mi. Ko si ọlọhun miiran bikoṣe Oun. Lọdọ Rẹ ni mo gbẹkẹle, Oun si ni Oluwa Itẹ Ọla Alaponle. (Igba 7).",
      "translationIg": "Chineke ezutewo m. Ọ dịghị chi ọzọ ma e wezụga Ya. N'ime Ya ka m tụkwasịrị obi, Ọ bụkwa Onyenwe Ocheeze Dị Ebube. (Ugboro 7).",
      "targetCount": 7,
      "reference": "Sunan Abi Dawud 4/321"
    },
    {
      "id": 12,
      "category": "Evening Adhkar",
      "categoryHa": "Zikirin Yamma",
      "categoryYo": "Azkar Irọlẹ́",
      "categoryIg": "Azkar Anyasị",
      "title": "Evening Invocation for Relief (Ya Hayyu Ya Qayyum)",
      "titleHa": "Neman Agaji da Yamma (Ya Hayyu Ya Qayyum)",
      "titleYo": "Ẹ̀bẹ̀ Ìrànwọ́ ni Irọlẹ́ (Ya Hayyu Ya Qayyum)",
      "titleIg": "Arịrịọ Enyemaka n'Anyasị (Ya Hayyu Ya Qayyum)",
      "arabic": "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
      "transliteration": "Ya Hayyu Ya Qayyum bi-rahmatika astagheeth, aslih li sha'ni kullahu wa la takilni ila nafsi tarfata 'ayn.",
      "translationEn": "O Ever-Living One, O Sustainer, by Your mercy I seek help. Rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
      "translationHa": "Ya Mai Rai Ya Tsayayye, da rahamarKa nake neman agaji, Ka gyara mini al'amurana baki ɗaya kuma kada Ka ragamar da ni zuwa ga kaina ko da ƙiftawar ido guda ne.",
      "translationYo": "Iwọ Alaye, Olugbe gbogbo nkan duro, pẹlu aanu Rẹ ni mo n wa iranlọwọ, ṣe atunṣe gbogbo ọrọ mi fun mi, ki O ma si fi mi silẹ fun ara mi paapaa fun bi iṣẹju kan.",
      "translationIg": "Onye Dị Ndụ, Onye Na-elekọta ihe niile, site n'ebere Gị ka m na-achọ enyemaka. Mezie ihe omume m niile ma ahapụla m n'onwe m ọbụlagodi maka ntugharị anya.",
      "targetCount": 1,
      "reference": "Mustadrak Al-Hakim 1/545, Sahih At-Targhib 1/273"
    },

    // --- SLEEPING & WAKING UP ---
    {
      "id": 13,
      "category": "Sleeping & Waking Up",
      "categoryHa": "Barci & Tashi",
      "categoryYo": "Isún & Jíjìde",
      "categoryIg": "Ụra & Mbilite",
      "title": "Supplication Before Sleeping",
      "titleHa": "Addu'ar Kafin Kwanciya Barci",
      "titleYo": "Àdúà Ṣáájú Kí Á Tó Sùn",
      "titleIg": "Ekpere Tupu Ụra",
      "arabic": "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
      "transliteration": "Bismika Rabbi wada'tu janbi, wa bika arfa'uhu, fa'in amsakta nafsi farhamha, wa in arsaltaha fahfazha bima tahfazu bihi 'ibadakas-salihin.",
      "translationEn": "In Your Name my Lord, I lie down, and in Your Name I rise. If You take my soul, have mercy upon it; and if You return it, protect it as You protect Your righteous slaves.",
      "translationHa": "Da sunanKa Ya Ubangijina na kwantar da gefena, kuma da ikonKa nake ɗaga shi. Idan Ka riƙe raina to Ka yi masa rahama, idan kuma Ka sako shi to Ka kiyaye shi da abin da kake kiyaye bayinKa salihan bayi.",
      "translationYo": "Pẹlu orukọ Rẹ Oluwa mi ni mo fi dubulẹ, pẹlu orukọ Rẹ si ni mo fi dide. Ti O ba di ẹmi mi mu, ṣe aanu fun un; ti O ba si da a pada, daabobo o gẹgẹ bi O ṣe n daabobo awọn ẹru Rẹ olododo.",
      "translationIg": "N'aha Gị Onyenwe m ka m ji dina ala, n'aha Gị ka m ji ebili. Ọ bụrụ na Ị nara mkpụrụ obi m, mee ya ebere; ọ bụrụkwa na Ị nọgide ya, chebe ya dịka Ị na-echebe ndị ohu Gị ezi omume.",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 11/126, Sahih Muslim 4/2084"
    },
    {
      "id": 14,
      "category": "Sleeping & Waking Up",
      "categoryHa": "Barci & Tashi",
      "categoryYo": "Isún & Jíjìde",
      "categoryIg": "Ụra & Mbilite",
      "title": "Supplication Upon Waking Up",
      "titleHa": "Addu'ar Tashi Daga Barci",
      "titleYo": "Àdúà Nígbà Tí Á Bá Jí Lójú Oorun",
      "titleIg": "Ekpere Mgbe A Tetara n'Ụra",
      "arabic": "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
      "transliteration": "Alhamdu lillahil-ladhi ahyana ba'da ma amatana wa ilayhin-nushur.",
      "translationEn": "All praise is for Allah Who gave us life after having taken it from us and unto Him is the resurrection.",
      "translationHa": "Godiya ta tabbata ga Allah wanda Ya raya mu bayan Ya kashe mu kuma zuwa gare Shi tashi yake.",
      "translationYo": "Gbogbo ọpẹ jẹ ti Allāhu ti O sọ wa di alaye lẹhin ti O ti mu wa ku, ati ọdọ Rẹ ni ijinde n bẹ.",
      "translationIg": "Otuto niile diri Chineke Onye nyere anyị ndụ mgbe O mechara ka anyị nwụọ ma n'ebe Ọ nọ ka mbilite n'ọnwụ dị.",
      "targetCount": 1,
      "reference": "Sahih Al-Bukhari 11/113, Sahih Muslim 4/2083"
    },

    // --- FOOD & DRINKING ---
    {
      "id": 15,
      "category": "Food & Eating",
      "categoryHa": "Cin Abinci",
      "categoryYo": "Oúnjẹ & Imutí",
      "categoryIg": "Iri Nri & Inụ Ihe",
      "title": "Supplication Before Eating",
      "titleHa": "Addu'ar Kafin Fara Cin Abinci",
      "titleYo": "Àdúà Ṣáájú Kí Á Tó Jẹun",
      "titleIg": "Ekpere Tupu Eriri Nri",
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
      "categoryYo": "Oúnjẹ & Imutí",
      "categoryIg": "Iri Nri & Inụ Ihe",
      "title": "Supplication After Finishing Meal",
      "titleHa": "Addu'ar Bayan Gama Cin Abinci",
      "titleYo": "Àdúà Lẹ́yìn Tí Á Bá Jẹun Tán",
      "titleIg": "Ekpere Mgbe E Richara Nri",
      "arabic": "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
      "transliteration": "Alhamdu lillahil-ladhi at'amani hadha wa razaqanihi min ghayri hawlin minni wa la quwwatin.",
      "translationEn": "All praise is for Allah Who has fed me this and provided it for me without any might or power from myself.",
      "translationHa": "Godiya ta tabbata ga Allah wanda Ya ciyar da ni wannan kuma Ya azurta ni da shi ba tare da wata dabara ko ƙarfi daga gare ni ba.",
      "translationYo": "Gbogbo ọpẹ jẹ ti Allāhu ti O bọ mi pẹlu eyi ti O si pese rẹ fun mi lai si agbara tabi agbara kankan lati ọdọ mi.",
      "translationIg": "Otuto diri Chineke Onye nyere m nri a ma wetara m ya na enweghi ike ma ọ bụ ikike n'onwe m.",
      "targetCount": 1,
      "reference": "Jami' At-Tirmidhi, Sunan Ibn Majah"
    }
  ];

  List<String> _getCategoriesForLanguage(String lang) {
    switch (lang) {
      case "Hausa":
        return ["Duka", "Zikirin Safe", "Zikirin Yamma", "Barci & Tashi", "Cin Abinci", "Asma'ul Husna", "Addu'o'i na Ijaba"];
      case "Yoruba":
        return ["Gbogbo rẹ", "Azkar Owurọ̀", "Azkar Irọlẹ́", "Isún & Jíjìde", "Oúnjẹ & Imutí", "Asma'ul Husna", "Àwọn Àdúà Ìtẹ́wọ́gbà"];
      case "Igbo":
        return ["Niile", "Azkar Ụtụtụ", "Azkar Anyasị", "Ụra & Mbilite", "Iri Nri & Inụ Ihe", "Asma'ul Husna", "Ekpere A Na-aza Ngwa Ngwa"];
      case "Arabic":
        return ["الكل", "أذكار الصباح", "أذكار المساء", "النوم والاستيقاظ", "طعام وشراب", "أسماء الله الحسنى", "أدعية الإجابة والاسم الأعظم"];
      default:
        return ["All", "Morning Adhkar", "Evening Adhkar", "Sleeping & Waking Up", "Food & Eating", "Asma'ul Husna", "Answered Prayers (Ijaba)"];
    }
  }

  String _getCategoryText(Map<String, dynamic> item, String lang) {
    if (lang == "Hausa" && item["categoryHa"] != null) return item["categoryHa"]!;
    if (lang == "Yoruba" && item["categoryYo"] != null) return item["categoryYo"]!;
    if (lang == "Igbo" && item["categoryIg"] != null) return item["categoryIg"]!;
    if (lang == "English" && item["categoryEn"] != null) return item["categoryEn"]!;
    return item["categoryEn"] ?? item["category"] ?? "";
  }

  String _getTitleText(Map<String, dynamic> item, String lang) {
    if (lang == "Hausa" && item["titleHa"] != null) return item["titleHa"]!;
    if (lang == "Yoruba" && item["titleYo"] != null) return item["titleYo"]!;
    if (lang == "Igbo" && item["titleIg"] != null) return item["titleIg"]!;
    return item["titleEn"] ?? item["title"] ?? "";
  }

  String _getTranslationText(Map<String, dynamic> item, String lang) {
    if (lang == "Hausa" && item["translationHa"] != null) return item["translationHa"]!;
    if (lang == "Yoruba" && item["translationYo"] != null) return item["translationYo"]!;
    if (lang == "Igbo" && item["translationIg"] != null) return item["translationIg"]!;
    return item["translationEn"] ?? "";
  }

  String _getAppTitle(String lang) {
    switch (lang) {
      case "Hausa":
        return 'Hisnul Muslim (Addu\'o\'i & Azkar)';
      case "Yoruba":
        return 'Hisnul Muslim (Àdúà & Azkar)';
      case "Igbo":
        return 'Hisnul Muslim (Ekpere & Azkar)';
      case "Arabic":
        return 'حصن المسلم (الأدعية والأذكار)';
      default:
        return 'Hisnul Muslim (Duas & Adhkar)';
    }
  }

  String _getSearchHint(String lang) {
    switch (lang) {
      case "Hausa":
        return 'Nemi addu\'a ko zikiri...';
      case "Yoruba":
        return 'Wa àdúà tabi azkar...';
      case "Igbo":
        return 'Chọọ ekpere ma ọ bụ azkar...';
      case "Arabic":
        return 'ابحث عن دعاء أو ذكر...';
      default:
        return 'Search supplication or dhikr...';
    }
  }

  String _getEmptyMessage(String lang) {
    switch (lang) {
      case "Hausa":
        return "Ba a sami addu'ar da kake nema ba";
      case "Yoruba":
        return "Ko si àdúà kankan ti a ri";
      case "Igbo":
        return "Ahụghị ekpere ọ bụla";
      case "Arabic":
        return "لم يتم العثور على أذكار";
      default:
        return "No supplication found";
    }
  }

  String _getRepeatLabel(int count, String lang) {
    switch (lang) {
      case "Hausa":
        return "Maimaita Sau $count";
      case "Yoruba":
        return "Tun ṣe lẹẹ $count";
      case "Igbo":
        return "Megharịa ugboro $count";
      case "Arabic":
        return "تكرار $count مرات";
      default:
        return "Recite $count times";
    }
  }

  @override
  Widget build(BuildContext context) {
    final categories = _getCategoriesForLanguage(_selectedLanguage);
    final allCategoryLabel = _getAllCategoryLabel(_selectedLanguage);
    final allCombinedAzkar = [..._azkarList, ...extraAzkarData];

    final filteredAzkar = allCombinedAzkar.filter((item) {
      // Category filter
      if (_selectedCategory != "All" &&
          _selectedCategory != "Duka" &&
          _selectedCategory != "Gbogbo rẹ" &&
          _selectedCategory != "Niile" &&
          _selectedCategory != "الكل") {
        final catEn = item["categoryEn"] ?? item["category"];
        final catHa = item["categoryHa"];
        final catYo = item["categoryYo"];
        final catIg = item["categoryIg"];
        final catRaw = item["category"];
        if (_selectedCategory != catEn &&
            _selectedCategory != catRaw &&
            _selectedCategory != catHa &&
            _selectedCategory != catYo &&
            _selectedCategory != catIg) {
          return false;
        }
      }

      // Search filter
      if (_searchQuery.isNotEmpty) {
        final query = _searchQuery.toLowerCase();
        final title = (item["title"] ?? "").toString().toLowerCase();
        final titleHa = (item["titleHa"] ?? "").toString().toLowerCase();
        final titleYo = (item["titleYo"] ?? "").toString().toLowerCase();
        final titleIg = (item["titleIg"] ?? "").toString().toLowerCase();
        final trans = (item["transliteration"] ?? "").toString().toLowerCase();
        final arabic = (item["arabic"] ?? "").toString();
        final transEn = (item["translationEn"] ?? "").toString().toLowerCase();
        final transHa = (item["translationHa"] ?? "").toString().toLowerCase();
        final transYo = (item["translationYo"] ?? "").toString().toLowerCase();
        final transIg = (item["translationIg"] ?? "").toString().toLowerCase();

        return title.contains(query) ||
            titleHa.contains(query) ||
            titleYo.contains(query) ||
            titleIg.contains(query) ||
            trans.contains(query) ||
            arabic.contains(query) ||
            transEn.contains(query) ||
            transHa.contains(query) ||
            transYo.contains(query) ||
            transIg.contains(query);
      }

      return true;
    }).toList();

    return Scaffold(
      appBar: AppBar(
        title: Text(_getAppTitle(_selectedLanguage)),
        elevation: 0,
        actions: [
          PopupMenuButton<String>(
            icon: const Icon(Icons.language),
            tooltip: _selectedLanguage == "Hausa" ? "Zaɓi Harshe" : "Select Language",
            onSelected: (lang) async {
              setState(() {
                _selectedLanguage = lang;
                _selectedCategory = _getAllCategoryLabel(lang);
              });
              final prefs = await SharedPreferences.getInstance();
              await prefs.setString('selected_language', lang);
            },
            itemBuilder: (context) => [
              const PopupMenuItem(value: "Hausa", child: Text("Hausa")),
              const PopupMenuItem(value: "English", child: Text("English")),
              const PopupMenuItem(value: "Yoruba", child: Text("Yorùbá")),
              const PopupMenuItem(value: "Igbo", child: Text("Igbo")),
              const PopupMenuItem(value: "Arabic", child: Text("العربية")),
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
                hintText: _getSearchHint(_selectedLanguage),
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
                final isSelected = _selectedCategory == cat || (_selectedCategory == allCategoryLabel && cat == allCategoryLabel);
                String emoji = "📖";
                if (cat.contains("Safe") || cat.contains("Morning") || cat.contains("Owurọ̀") || cat.contains("Ụtụtụ") || cat.contains("الصباح")) emoji = "🌅";
                if (cat.contains("Yamma") || cat.contains("Evening") || cat.contains("Irọlẹ́") || cat.contains("Anyasị") || cat.contains("المساء")) emoji = "🌆";
                if (cat.contains("Barci") || cat.contains("Sleeping") || cat.contains("Isún") || cat.contains("Ụra") || cat.contains("النوم")) emoji = "🌙";
                if (cat.contains("Cin") || cat.contains("Food") || cat.contains("Oúnjẹ") || cat.contains("Nri") || cat.contains("طعام")) emoji = "🍽️";
                if (cat.contains("Asma") || cat.contains("Husna") || cat.contains("أسماء")) emoji = "🌟";
                if (cat.contains("Ijaba") || cat.contains("Ìtẹ́wọ́gbà") || cat.contains("Ekpere A Na-aza") || cat.contains("الإجابة") || cat.contains("Answered")) emoji = "⚡";

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
                          _getEmptyMessage(_selectedLanguage),
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

                      final titleText = _getTitleText(item, _selectedLanguage);
                      final categoryText = _getCategoryText(item, _selectedLanguage);
                      final translationText = _getTranslationText(item, _selectedLanguage);

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
                                      categoryText,
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
                                        _getRepeatLabel(targetCount, _selectedLanguage),
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
