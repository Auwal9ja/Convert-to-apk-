import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/prayer_service.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  String _selectedCity = "Kano";
  bool _athanNotifications = true;
  String _calcMethod = "Egyptian";

  final List<Map<String, dynamic>> _presetCities = [
    {"name": "Kano", "country": "Nigeria", "lat": 12.0022, "lng": 8.5920},
    {"name": "Abuja", "country": "Nigeria", "lat": 9.0765, "lng": 7.3986},
    {"name": "Lagos", "country": "Nigeria", "lat": 6.5244, "lng": 3.3792},
    {"name": "Kaduna", "country": "Nigeria", "lat": 10.5105, "lng": 7.4165},
    {"name": "Sokoto", "country": "Nigeria", "lat": 13.0609, "lng": 5.2343},
    {"name": "Maiduguri", "country": "Nigeria", "lat": 11.8333, "lng": 13.1500},
    {"name": "Makkah", "country": "Saudi Arabia", "lat": 21.4225, "lng": 39.8262},
    {"name": "Madinah", "country": "Saudi Arabia", "lat": 24.4672, "lng": 39.6024},
    {"name": "Cairo", "country": "Egypt", "lat": 30.0444, "lng": 31.2357},
    {"name": "London", "country": "United Kingdom", "lat": 51.5074, "lng": -0.1278},
    {"name": "New York", "country": "United States", "lat": 40.7128, "lng": -74.0060},
  ];

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _selectedCity = prefs.getString('city_name') ?? "Kano";
      _athanNotifications = prefs.getBool('athan_enabled') ?? true;
      _calcMethod = prefs.getString('calc_method') ?? "Egyptian";
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Saitunan Manhaja (Settings)'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text('WURI & KASASHEN SALLA', style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12)),
          const SizedBox(height: 8),
          Card(
            shape: RoundedCornerShape(12),
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.location_city, color: Color(0xFF1B5E20)),
                  title: const Text('Zaɓi Gari / Wuri'),
                  subtitle: Text(_selectedCity),
                  trailing: const Icon(Icons.arrow_forward_ios, size: 14),
                  onTap: () => _showCityPicker(),
                ),
              ],
            ),
          ),
          const SizedBox(height: 24),
          const Text('SANARWAR LADAN & KIRA', style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12)),
          const SizedBox(height: 8),
          Card(
            shape: RoundedCornerShape(12),
            child: SwitchListTile(
              secondary: const Icon(Icons.notifications_active, color: Color(0xFF1B5E20)),
              title: const Text('Sanarwar Lokacin Sallah'),
              subtitle: const Text('Kiran sallah da sanarwa a kowane lokaci'),
              value: _athanNotifications,
              onChanged: (val) async {
                final prefs = await SharedPreferences.getInstance();
                await prefs.setBool('athan_enabled', val);
                setState(() {
                  _athanNotifications = val;
                });
              },
            ),
          ),
          const SizedBox(height: 24),
          const Text('GAME DA MANHAJA', style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12)),
          const SizedBox(height: 8),
          Card(
            shape: RoundedCornerShape(12),
            child: const Column(
              children: [
                ListTile(
                  leading: Icon(Icons.info_outline, color: Color(0xFF1B5E20)),
                  title: Text('Shafi & Sigar Manhaja'),
                  subtitle: Text('Version 1.0.0 (iOS & Android Compatible)'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _showCityPicker() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedCornerShape(top: Radius.circular(20)),
      builder: (context) {
        return ListView.builder(
          itemCount: _presetCities.length,
          itemBuilder: (context, index) {
            final city = _presetCities[index];
            return ListTile(
              title: Text(city["name"]),
              subtitle: Text(city["country"]),
              trailing: _selectedCity == city["name"] ? const Icon(Icons.check, color: Color(0xFF1B5E20)) : null,
              onTap: () async {
                await PrayerService.saveLocation(
                  city["name"],
                  city["country"],
                  city["lat"],
                  city["lng"],
                );
                setState(() {
                  _selectedCity = city["name"];
                });
                Navigator.pop(context);
              },
            );
          },
        );
      },
    );
  }
}
