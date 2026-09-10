import 'package:flutter/material.dart';
import 'package:adhan/adhan.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/prayer_service.dart';
import '../services/billing_service.dart';
import '../services/ad_service.dart';
import '../components/subscription_dialog.dart';
import 'quran_screen.dart';
import 'qibla_screen.dart';
import 'azkar_screen.dart';
import 'settings_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;
  PrayerTimes? _prayerTimes;
  String _cityName = "Wurin Da Kake";
  String _countryName = "";
  String _selectedLanguage = "Hausa";
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadPreferencesAndPrayers();
    _autoDetectLocation();
  }

  Future<void> _loadPreferencesAndPrayers() async {
    final prefs = await SharedPreferences.getInstance();
    final lang = prefs.getString('selected_language') ?? "Hausa";
    final city = prefs.getString('city_name') ?? "Wurin Da Kake";
    final country = prefs.getString('country_name') ?? "";

    setState(() {
      _selectedLanguage = lang;
      _cityName = city;
      _countryName = country;
    });

    final times = await PrayerService.getPrayerTimes();
    setState(() {
      _prayerTimes = times;
      _isLoading = false;
    });
  }

  Future<void> _autoDetectLocation() async {
    final loc = await PrayerService.autoDetectLocation();
    if (loc != null && mounted) {
      final times = await PrayerService.getPrayerTimes();
      setState(() {
        _cityName = loc['city'] ?? _cityName;
        _countryName = loc['country'] ?? _countryName;
        _prayerTimes = times;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      _buildHomeView(),
      const QuranScreen(),
      const QiblaScreen(),
      const AzkarScreen(),
      const SettingsScreen(),
    ];

    return Scaffold(
      body: pages[_currentIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          final prevIndex = _currentIndex;
          setState(() {
            _currentIndex = index;
          });
          if (index == 0) {
            _loadPreferencesAndPrayers();
          }
          // Set ad suppression state: Suppress ads while in Azkar (index 3) or Quran (index 1)
          final isEnteringReading = (index == 3 || index == 1);
          final wasInReading = (prevIndex == 3 || prevIndex == 1);

          FlutterAdService().setUserReadingAdhkar(isEnteringReading);

          // If the user has just exited Azkar/Quran reading page to another tab,
          // now is the respectful time to present an interstitial ad if eligible.
          if (wasInReading && !isEnteringReading && FlutterAdService().canShowAd) {
            FlutterAdService().showInterstitialAd();
          } else if (prevIndex != index && !isEnteringReading && FlutterAdService().canShowAd) {
            FlutterAdService().showInterstitialAd();
          }
        },
        destinations: [
          NavigationDestination(
            icon: const Icon(Icons.access_time),
            selectedIcon: const Icon(Icons.access_time_filled, color: Color(0xFF1B5E20)),
            label: _getNavLabel('saloli'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.menu_book_outlined),
            selectedIcon: const Icon(Icons.menu_book, color: Color(0xFF1B5E20)),
            label: _getNavLabel('quran'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.explore_outlined),
            selectedIcon: const Icon(Icons.explore, color: Color(0xFF1B5E20)),
            label: _getNavLabel('qibla'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.format_quote_outlined),
            selectedIcon: const Icon(Icons.format_quote, color: Color(0xFF1B5E20)),
            label: _getNavLabel('azkar'),
          ),
          NavigationDestination(
            icon: const Icon(Icons.settings_outlined),
            selectedIcon: const Icon(Icons.settings, color: Color(0xFF1B5E20)),
            label: _getNavLabel('settings'),
          ),
        ],
      ),
    );
  }

  String _getNavLabel(String key) {
    if (_selectedLanguage == "Hausa") {
      switch (key) {
        case 'saloli': return 'Saloli';
        case 'quran': return 'Al-Qur\'ani';
        case 'qibla': return 'Alƙibla';
        case 'azkar': return 'Addu\'o\'i';
        case 'settings': return 'Saituna';
      }
    } else if (_selectedLanguage == "Yoruba") {
      switch (key) {
        case 'saloli': return 'Àkókò';
        case 'quran': return 'Al-Ƙurani';
        case 'qibla': return 'Kiblah';
        case 'azkar': return 'Àdúà';
        case 'settings': return 'Ètò';
      }
    } else if (_selectedLanguage == "Igbo") {
      switch (key) {
        case 'saloli': return 'Oge Ekpere';
        case 'quran': return 'Quran';
        case 'qibla': return 'Qibla';
        case 'azkar': return 'Ekpere';
        case 'settings': return 'Ntọala';
      }
    } else if (_selectedLanguage == "Arabic") {
      switch (key) {
        case 'saloli': return 'الصلاة';
        case 'quran': return 'القرآن';
        case 'qibla': return 'القبلة';
        case 'azkar': return 'الأذكار';
        case 'settings': return 'الإعدادات';
      }
    }
    switch (key) {
      case 'saloli': return 'Prayers';
      case 'quran': return 'Quran';
      case 'qibla': return 'Qibla';
      case 'azkar': return 'Adhkar';
      case 'settings': return 'Settings';
      default: return key;
    }
  }

  Widget _buildHomeView() {
    return CustomScrollView(
      slivers: [
        SliverAppBar(
          expandedHeight: 230,
          floating: false,
          pinned: true,
          flexibleSpace: FlexibleSpaceBar(
            background: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF042F24), Color(0xFF064E3B), Color(0xFF006C4E)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
              ),
              child: SafeArea(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Location and Calligraphy
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                            decoration: BoxDecoration(
                              color: Colors.white.withOpacity(0.18),
                              borderRadius: BorderRadius.circular(16),
                              border: Border.all(color: Colors.white.withOpacity(0.3), width: 0.8),
                            ),
                            child: Row(
                              children: [
                                const Icon(Icons.location_on, color: Color(0xFFFDE68A), size: 16),
                                const SizedBox(width: 4),
                                Text(
                                  _countryName.isNotEmpty ? '$_cityName, $_countryName' : _cityName,
                                  style: const TextStyle(
                                    color: Colors.white,
                                    fontWeight: FontWeight.bold,
                                    fontSize: 13,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Row(
                            children: [
                              InkWell(
                                onTap: () {
                                  SubscriptionDialog.show(context, selectedLanguage: _selectedLanguage);
                                },
                                borderRadius: BorderRadius.circular(16),
                                child: Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFD4AF37).withOpacity(0.25),
                                    borderRadius: BorderRadius.circular(16),
                                    border: Border.all(color: const Color(0xFFFFD700), width: 1.2),
                                  ),
                                  child: Row(
                                    children: [
                                      const Icon(Icons.workspace_premium, color: Color(0xFFFFD700), size: 16),
                                      const SizedBox(width: 4),
                                      Text(
                                        _selectedLanguage == 'Hausa' ? 'Cire Talla' : 'VIP',
                                        style: const TextStyle(
                                          color: Color(0xFFFFD700),
                                          fontWeight: FontWeight.bold,
                                          fontSize: 11,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                              const SizedBox(width: 4),
                              IconButton(
                                icon: const Icon(Icons.my_location, color: Colors.white, size: 20),
                                onPressed: () async {
                                  final pos = await PrayerService.getCurrentLocation();
                                  if (pos != null) {
                                    await PrayerService.saveLocation("GPS Location", "", pos.latitude, pos.longitude);
                                    _loadPreferencesAndPrayers();
                                  }
                                },
                              ),
                            ],
                          ),
                        ],
                      ),
                      const Spacer(),
                      if (_prayerTimes != null) ...[
                        Text(
                          '${_getNextPrefix()}: ${_getNextPrayerName(_prayerTimes!.nextPrayer())}',
                          style: const TextStyle(color: Color(0xFFFDE68A), fontSize: 14, fontWeight: FontWeight.bold),
                        ),
                        const SizedBox(height: 2),
                        Text(
                          DateFormat('hh:mm a').format(_prayerTimes!.timeForPrayer(_prayerTimes!.nextPrayer()) ?? DateTime.now()),
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 34,
                            fontWeight: FontWeight.w900,
                            letterSpacing: -0.5,
                          ),
                        ),
                      ],
                      const SizedBox(height: 8),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),

        // 6 Columns Horizontal Prayer Times Bar (Bold & Sharp)
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(14, 14, 14, 6),
            child: _buildHorizontalPrayerBar(),
          ),
        ),

        // Full Detailed Prayer Times List
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(14.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  _getSectionTitle(),
                  style: const TextStyle(
                    fontSize: 17,
                    fontWeight: FontWeight.w900,
                    color: Color(0xFF064E3B),
                    letterSpacing: -0.3,
                  ),
                ),
                const SizedBox(height: 10),
                if (_isLoading)
                  const Center(child: Padding(
                    padding: EdgeInsets.all(24.0),
                    child: CircularProgressIndicator(),
                  ))
                else if (_prayerTimes != null) ...[
                  _prayerCard('FAJR', _getPrayerName('FAJR'), _prayerTimes!.fajr, '🌅', _prayerTimes!.nextPrayer() == Prayer.fajr),
                  _prayerCard('SUNRISE', _getPrayerName('SUNRISE'), _prayerTimes!.sunrise, '☀️', _prayerTimes!.nextPrayer() == Prayer.sunrise),
                  _prayerCard('DHUHR', _getPrayerName('DHUHR'), _prayerTimes!.dhuhr, '🌞', _prayerTimes!.nextPrayer() == Prayer.dhuhr),
                  _prayerCard('ASR', _getPrayerName('ASR'), _prayerTimes!.asr, '⛅', _prayerTimes!.nextPrayer() == Prayer.asr),
                  _prayerCard('MAGHRIB', _getPrayerName('MAGHRIB'), _prayerTimes!.maghrib, '🌇', _prayerTimes!.nextPrayer() == Prayer.maghrib),
                  _prayerCard('ISHA', _getPrayerName('ISHA'), _prayerTimes!.isha, '🌙', _prayerTimes!.nextPrayer() == Prayer.isha),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildHorizontalPrayerBar() {
    if (_prayerTimes == null) return const SizedBox.shrink();

    final next = _prayerTimes!.nextPrayer();
    final prayers = [
      {'id': 'FAJR', 'emoji': '🌅', 'time': _prayerTimes!.fajr, 'isNext': next == Prayer.fajr},
      {'id': 'SUNRISE', 'emoji': '☀️', 'time': _prayerTimes!.sunrise, 'isNext': next == Prayer.sunrise},
      {'id': 'DHUHR', 'emoji': '🌞', 'time': _prayerTimes!.dhuhr, 'isNext': next == Prayer.dhuhr},
      {'id': 'ASR', 'emoji': '⛅', 'time': _prayerTimes!.asr, 'isNext': next == Prayer.asr},
      {'id': 'MAGHRIB', 'emoji': '🌇', 'time': _prayerTimes!.maghrib, 'isNext': next == Prayer.maghrib},
      {'id': 'ISHA', 'emoji': '🌙', 'time': _prayerTimes!.isha, 'isNext': next == Prayer.isha},
    ];

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 10),
      decoration: BoxDecoration(
        color: const Color(0xFF092B22),
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: const Color(0xFF144D3E), width: 1.2),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 8,
            offset: const Offset(0, 3),
          ),
        ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: prayers.map((p) {
          final isNext = p['isNext'] as bool;
          final id = p['id'] as String;
          final name = _getPrayerName(id);
          final formattedTime = DateFormat('h:mm a').format(p['time'] as DateTime);
          final emoji = p['emoji'] as String;

          return Expanded(
            child: Container(
              margin: const EdgeInsets.symmetric(horizontal: 2),
              padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 2),
              decoration: BoxDecoration(
                color: isNext ? const Color(0xFF0D3B31) : Colors.transparent,
                borderRadius: BorderRadius.circular(14),
                border: isNext
                    ? Border.all(color: const Color(0xFF34D399), width: 1.8)
                    : Border.all(color: Colors.white.withOpacity(0.08), width: 0.6),
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    emoji,
                    style: const TextStyle(fontSize: 19),
                  ),
                  const SizedBox(height: 5),
                  Text(
                    name,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.w900,
                      color: isNext ? const Color(0xFF34D399) : Colors.white,
                    ),
                  ),
                  const SizedBox(height: 3),
                  Text(
                    formattedTime,
                    maxLines: 1,
                    style: TextStyle(
                      fontSize: 10.5,
                      fontWeight: FontWeight.w900,
                      color: isNext ? const Color(0xFF34D399) : const Color(0xFFF1F5F9),
                    ),
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }

  Widget _prayerCard(String id, String name, DateTime time, String emoji, bool isNext) {
    final formattedTime = DateFormat('hh:mm a').format(time);
    return Card(
      margin: const EdgeInsets.only(bottom: 10),
      elevation: isNext ? 2.5 : 0.8,
      shape: RoundedCornerShape(14),
      color: isNext ? const Color(0xFFE8F5E9) : Colors.white,
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(14),
          border: Border.all(
            color: isNext ? const Color(0xFF2E7D32) : Colors.grey.shade200,
            width: isNext ? 1.8 : 1.0,
          ),
        ),
        child: ListTile(
          contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 4),
          leading: Container(
            width: 44,
            height: 44,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: isNext ? const Color(0xFF2E7D32) : const Color(0xFFF1F8E9),
              shape: BoxShape.circle,
            ),
            child: Text(emoji, style: const TextStyle(fontSize: 22)),
          ),
          title: Text(
            name,
            style: TextStyle(
              fontWeight: FontWeight.w900,
              fontSize: 15,
              color: isNext ? const Color(0xFF1B5E20) : const Color(0xFF132D27),
            ),
          ),
          subtitle: isNext
              ? Text(
                  _selectedLanguage == "Hausa" ? "Sallah Mai Zuwa" : "Upcoming Prayer",
                  style: const TextStyle(color: Color(0xFF2E7D32), fontWeight: FontWeight.bold, fontSize: 12),
                )
              : null,
          trailing: Text(
            formattedTime,
            style: TextStyle(
              fontWeight: FontWeight.w900,
              fontSize: 16.5,
              color: isNext ? const Color(0xFF1B5E20) : const Color(0xFF2D3748),
            ),
          ),
        ),
      ),
    );
  }

  String _getSectionTitle() {
    switch (_selectedLanguage) {
      case "Hausa":
        return 'Lokutan Salloli Na Yau';
      case "Yoruba":
        return 'Àkókò Àdúrà Òní';
      case "Igbo":
        return 'Oge Ekpere Taa';
      case "Arabic":
        return 'مواقيت الصلاة اليوم';
      default:
        return 'Today\'s Prayer Times';
    }
  }

  String _getNextPrefix() {
    switch (_selectedLanguage) {
      case "Hausa":
        return 'Salla Mai Zuwa';
      case "Yoruba":
        return 'Àdúrà Tókàn';
      case "Igbo":
        return 'Ekpere Na-esote';
      case "Arabic":
        return 'الصلاة القادمة';
      default:
        return 'Next Prayer';
    }
  }

  String _getPrayerName(String id) {
    switch (id) {
      case 'FAJR':
        return _selectedLanguage == "Hausa" ? "Asuba" : _selectedLanguage == "Arabic" ? "الفجر" : "Fajr";
      case 'SUNRISE':
        return _selectedLanguage == "Hausa" ? "Fitowar Rana" : _selectedLanguage == "Arabic" ? "الشروق" : "Sunrise";
      case 'DHUHR':
        return _selectedLanguage == "Hausa" ? "Azahar" : _selectedLanguage == "Arabic" ? "الظهر" : "Dhuhr";
      case 'ASR':
        return _selectedLanguage == "Hausa" ? "La'asar" : _selectedLanguage == "Arabic" ? "العصر" : "Asr";
      case 'MAGHRIB':
        return _selectedLanguage == "Hausa" ? "Magariba" : _selectedLanguage == "Arabic" ? "المغرب" : "Maghrib";
      case 'ISHA':
        return _selectedLanguage == "Hausa" ? "Isha'i" : _selectedLanguage == "Arabic" ? "العشاء" : "Isha";
      default:
        return id;
    }
  }

  String _getNextPrayerName(Prayer prayer) {
    switch (prayer) {
      case Prayer.fajr:
        return _getPrayerName('FAJR');
      case Prayer.sunrise:
        return _getPrayerName('SUNRISE');
      case Prayer.dhuhr:
        return _getPrayerName('DHUHR');
      case Prayer.asr:
        return _getPrayerName('ASR');
      case Prayer.maghrib:
        return _getPrayerName('MAGHRIB');
      case Prayer.isha:
        return _getPrayerName('ISHA');
      case Prayer.none:
        return _selectedLanguage == "Hausa" ? "Babu" : "None";
    }
  }
}
