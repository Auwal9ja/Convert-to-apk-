import 'package:flutter/material.dart';
import 'package:adhan/adhan.dart';
import 'package:intl/intl.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/prayer_service.dart';
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
  String _cityName = "Kano";
  String _countryName = "Nigeria";
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadPrayerTimes();
  }

  Future<void> _loadPrayerTimes() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _cityName = prefs.getString('city_name') ?? "Kano";
      _countryName = prefs.getString('country_name') ?? "Nigeria";
    });

    final times = await PrayerService.getPrayerTimes();
    setState(() {
      _prayerTimes = times;
      _isLoading = false;
    });
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
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.access_time),
            selectedIcon: Icon(Icons.access_time_filled, color: Color(0xFF1B5E20)),
            label: 'Saloli',
          ),
          NavigationDestination(
            icon: Icon(Icons.menu_book_outlined),
            selectedIcon: Icon(Icons.menu_book, color: Color(0xFF1B5E20)),
            label: 'Al-Qur\'ani',
          ),
          NavigationDestination(
            icon: Icon(Icons.explore_outlined),
            selectedIcon: Icon(Icons.explore, color: Color(0xFF1B5E20)),
            label: 'Alƙibla',
          ),
          NavigationDestination(
            icon: Icon(Icons.format_quote_outlined),
            selectedIcon: Icon(Icons.format_quote, color: Color(0xFF1B5E20)),
            label: 'Addu\'o\'i',
          ),
          NavigationDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings, color: Color(0xFF1B5E20)),
            label: 'Saituna',
          ),
        ],
      ),
    );
  }

  Widget _buildHomeView() {
    return CustomScrollView(
      slivers: [
        SliverAppBar(
          expandedHeight: 220,
          floating: false,
          pinned: true,
          flexibleSpace: FlexibleSpaceBar(
            background: Container(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF1B5E20), Color(0xFF2E7D32)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
              ),
              child: SafeArea(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Row(
                            children: [
                              const Icon(Icons.location_on, color: Colors.amber, size: 20),
                              const SizedBox(width: 4),
                              Text(
                                '$_cityName, $_countryName',
                                style: const TextStyle(
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                  fontSize: 16,
                                ),
                              ),
                            ],
                          ),
                          IconButton(
                            icon: const Icon(Icons.my_location, color: Colors.white),
                            onPressed: () async {
                              final pos = await PrayerService.getCurrentLocation();
                              if (pos != null) {
                                await PrayerService.saveLocation("GPS Location", "", pos.latitude, pos.longitude);
                                _loadPrayerTimes();
                              }
                            },
                          ),
                        ],
                      ),
                      const Spacer(),
                      if (_prayerTimes != null) ...[
                        Text(
                          'Sallar Gaba: ${_getNextPrayerName(_prayerTimes!.nextPrayer())}',
                          style: const TextStyle(color: Colors.white70, fontSize: 14),
                        ),
                        Text(
                          DateFormat('hh:mm a').format(_prayerTimes!.timeForPrayer(_prayerTimes!.nextPrayer()) ?? DateTime.now()),
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 32,
                            fontWeight: FontWeight.bold,
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
        SliverToBoxAdapter(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'Lokutan Salloli Na Yau',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF132D27)),
                ),
                const SizedBox(height: 12),
                if (_isLoading)
                  const Center(child: CircularProgressIndicator())
                else if (_prayerTimes != null) ...[
                  _prayerCard('Asubahi (Fajr)', _prayerTimes!.fajr, Icons.wb_twilight),
                  _prayerCard('Hantsi (Sunrise)', _prayerTimes!.sunrise, Icons.wb_sunny_outlined),
                  _prayerCard('Azahar (Dhuhr)', _prayerTimes!.dhuhr, Icons.wb_sunny),
                  _prayerCard('La\'asar (Asr)', _prayerTimes!.asr, Icons.wb_cloudy),
                  _prayerCard('Magriba (Maghrib)', _prayerTimes!.maghrib, Icons.nights_stay_outlined),
                  _prayerCard('Isha\'i (Isha)', _prayerTimes!.isha, Icons.nights_stay),
                ],
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _prayerCard(String name, DateTime time, IconData icon) {
    final formattedTime = DateFormat('hh:mm a').format(time);
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      elevation: 0.5,
      shape: RoundedCornerShape(12),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: const Color(0xFFE8F5E9),
          child: Icon(icon, color: const Color(0xFF1B5E20), size: 20),
        ),
        title: Text(name, style: const TextStyle(fontWeight: FontWeight.w600)),
        trailing: Text(
          formattedTime,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF1B5E20)),
        ),
      ),
    );
  }

  String _getNextPrayerName(Prayer prayer) {
    switch (prayer) {
      case Prayer.fajr:
        return 'Asubahi';
      case Prayer.sunrise:
        return 'Fitowar Rana';
      case Prayer.dhuhr:
        return 'Azahar';
      case Prayer.asr:
        return 'La\'asar';
      case Prayer.maghrib:
        return 'Magriba';
      case Prayer.isha:
        return 'Isha\'i';
      case Prayer.none:
        return 'Babu';
    }
  }
}
