import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/prayer_service.dart';
import '../services/billing_service.dart';
import '../components/subscription_dialog.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  String _selectedCity = "Kano";
  String _selectedLanguage = "Hausa";
  bool _athanNotifications = true;
  bool _morningReminder = true;
  bool _eveningReminder = true;
  String _calcMethod = "Egyptian";

  final List<Map<String, dynamic>> _presetCities = [
    {"name": "Kano", "country": "Nigeria", "lat": 12.0022, "lng": 8.5920},
    {"name": "Abuja", "country": "Nigeria", "lat": 9.0765, "lng": 7.3986},
    {"name": "Lagos", "country": "Nigeria", "lat": 6.5244, "lng": 3.3792},
    {"name": "Kaduna", "country": "Nigeria", "lat": 10.5105, "lng": 7.4165},
    {"name": "Sokoto", "country": "Nigeria", "lat": 13.0609, "lng": 5.2343},
    {"name": "Maiduguri", "country": "Nigeria", "lat": 11.8333, "lng": 13.1500},
    {"name": "Ibadan", "country": "Nigeria", "lat": 7.3775, "lng": 3.9470},
    {"name": "Enugu", "country": "Nigeria", "lat": 6.4584, "lng": 7.5464},
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
      _selectedLanguage = prefs.getString('selected_language') ?? "Hausa";
      _athanNotifications = prefs.getBool('athan_enabled') ?? true;
      _morningReminder = prefs.getBool('morning_adhkar_enabled') ?? true;
      _eveningReminder = prefs.getBool('evening_adhkar_enabled') ?? true;
      _calcMethod = prefs.getString('calc_method') ?? "Egyptian";
    });
  }

  @override
  Widget build(BuildContext context) {
    final isHausa = _selectedLanguage == "Hausa";

    return Scaffold(
      appBar: AppBar(
        title: Text(isHausa ? 'Saitunan Manhaja (Settings)' : 'App Settings'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // 0. Premium / Remove Ads Card
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            elevation: 2,
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                gradient: const LinearGradient(
                  colors: [Color(0xFF0F5132), Color(0xFF198754)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
              ),
              child: ListTile(
                contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                leading: Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: const Color(0xFFD4AF37).withOpacity(0.25),
                    shape: BoxShape.circle,
                  ),
                  child: const Icon(Icons.workspace_premium, color: Color(0xFFFFD700), size: 28),
                ),
                title: Text(
                  isHausa ? 'Cire Tallace-tallace (Noor Premium)' : 'Noor Premium (Remove Ads)',
                  style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 15),
                ),
                subtitle: Text(
                  isHausa ? 'Weekly, Monthly da Yearly Subscriptions' : 'Weekly, Monthly & Yearly Subscriptions',
                  style: TextStyle(color: Colors.white.withOpacity(0.85), fontSize: 12),
                ),
                trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white, size: 14),
                onTap: () {
                  SubscriptionDialog.show(context, selectedLanguage: _selectedLanguage);
                },
              ),
            ),
          ),
          const SizedBox(height: 20),

          // 1. Language Setting
          Text(
            isHausa ? 'HARSHE / LANGUAGE' : 'LANGUAGE SELECTION',
            style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12),
          ),
          const SizedBox(height: 8),
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: ListTile(
              leading: const Icon(Icons.language, color: Color(0xFF1B5E20)),
              title: Text(isHausa ? 'Zaɓi Harshe' : 'Choose Language'),
              subtitle: Text(_selectedLanguage),
              trailing: const Icon(Icons.arrow_forward_ios, size: 14),
              onTap: () => _showLanguagePicker(),
            ),
          ),
          const SizedBox(height: 20),

          // 2. Location Setting
          Text(
            isHausa ? 'WURI & KASASHEN SALLA' : 'PRAYER LOCATION & CITY',
            style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12),
          ),
          const SizedBox(height: 8),
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: ListTile(
              leading: const Icon(Icons.location_city, color: Color(0xFF1B5E20)),
              title: Text(isHausa ? 'Zaɓi Gari / Wuri' : 'Select City / Location'),
              subtitle: Text(_selectedCity),
              trailing: const Icon(Icons.arrow_forward_ios, size: 14),
              onTap: () => _showCityPicker(),
            ),
          ),
          const SizedBox(height: 20),

          // 3. Adhkar & Reminders
          Text(
            isHausa ? 'JADAWALIN ZIKIRIN SAFE DA NA YAMMA' : 'ADHKAR SCHEDULER & NOTIFICATIONS',
            style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12),
          ),
          const SizedBox(height: 8),
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: Column(
              children: [
                SwitchListTile(
                  secondary: const Icon(Icons.wb_sunny, color: Color(0xFFFFA000)),
                  title: Text(isHausa ? 'Zikirin Safe (Morning Adhkar)' : 'Morning Adhkar Reminder'),
                  subtitle: Text(isHausa ? 'Sanarwa da safe bayan Asuba' : 'Daily morning session notification'),
                  value: _morningReminder,
                  onChanged: (val) async {
                    final prefs = await SharedPreferences.getInstance();
                    await prefs.setBool('morning_adhkar_enabled', val);
                    setState(() => _morningReminder = val);
                  },
                ),
                const Divider(height: 1),
                SwitchListTile(
                  secondary: const Icon(Icons.nights_stay, color: Color(0xFF512DA8)),
                  title: Text(isHausa ? 'Zikirin Yamma (Evening Adhkar)' : 'Evening Adhkar Reminder'),
                  subtitle: Text(isHausa ? 'Sanarwa da yamma bayan La\'asar/Magariba' : 'Daily evening session notification'),
                  value: _eveningReminder,
                  onChanged: (val) async {
                    final prefs = await SharedPreferences.getInstance();
                    await prefs.setBool('evening_adhkar_enabled', val);
                    setState(() => _eveningReminder = val);
                  },
                ),
                const Divider(height: 1),
                SwitchListTile(
                  secondary: const Icon(Icons.notifications_active, color: Color(0xFF1B5E20)),
                  title: Text(isHausa ? 'Sanarwar Lokacin Sallah' : 'Athan / Prayer Times Alarm'),
                  subtitle: Text(isHausa ? 'Kiran sallah a kowane lokaci' : 'Prayer notifications on time'),
                  value: _athanNotifications,
                  onChanged: (val) async {
                    final prefs = await SharedPreferences.getInstance();
                    await prefs.setBool('athan_enabled', val);
                    setState(() => _athanNotifications = val);
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 20),

          // 4. About
          Text(
            isHausa ? 'GAME DA MANHAJA' : 'ABOUT APPLICATION',
            style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12),
          ),
          const SizedBox(height: 8),
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.info_outline, color: Color(0xFF1B5E20)),
                  title: Text(isHausa ? 'Shafi & Sigar Manhaja' : 'App Version'),
                  subtitle: const Text('Zakiru v1.0.0 (iOS & Android)'),
                ),
                const Divider(height: 1),
                ListTile(
                  leading: const Icon(Icons.verified_user, color: Color(0xFF1B5E20)),
                  title: Text(isHausa ? 'Tushen Addu\'o\'i' : 'Authentic Supplications'),
                  subtitle: const Text('Hisnul Muslim & Sahih Hadiths'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _showLanguagePicker() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
      builder: (ctx) {
        return SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const SizedBox(height: 12),
              const Text('Zaɓi Harshe (Select Language)', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              const Divider(),
              ListTile(
                title: const Text('Hausa'),
                trailing: _selectedLanguage == 'Hausa' ? const Icon(Icons.check, color: Color(0xFF1B5E20)) : null,
                onTap: () => _updateLanguage('Hausa'),
              ),
              ListTile(
                title: const Text('English'),
                trailing: _selectedLanguage == 'English' ? const Icon(Icons.check, color: Color(0xFF1B5E20)) : null,
                onTap: () => _updateLanguage('English'),
              ),
              ListTile(
                title: const Text('Yorùbá'),
                trailing: _selectedLanguage == 'Yoruba' ? const Icon(Icons.check, color: Color(0xFF1B5E20)) : null,
                onTap: () => _updateLanguage('Yoruba'),
              ),
              ListTile(
                title: const Text('Igbo'),
                trailing: _selectedLanguage == 'Igbo' ? const Icon(Icons.check, color: Color(0xFF1B5E20)) : null,
                onTap: () => _updateLanguage('Igbo'),
              ),
            ],
          ),
        );
      },
    );
  }

  Future<void> _updateLanguage(String lang) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('selected_language', lang);
    setState(() => _selectedLanguage = lang);
    Navigator.pop(context);
  }

  void _showCityPicker() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(16))),
      builder: (ctx) {
        return DraggableScrollableSheet(
          initialChildSize: 0.7,
          minChildSize: 0.4,
          maxChildSize: 0.9,
          expand: false,
          builder: (context, scrollController) {
            return Column(
              children: [
                const SizedBox(height: 12),
                const Text('Zaɓi Garinku don Lokutan Sallah', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const Divider(),
                Expanded(
                  child: ListView.builder(
                    controller: scrollController,
                    itemCount: _presetCities.length,
                    itemBuilder: (context, index) {
                      final city = _presetCities[index];
                      return ListTile(
                        title: Text(city["name"]),
                        subtitle: Text(city["country"]),
                        trailing: _selectedCity == city["name"]
                            ? const Icon(Icons.check_circle, color: Color(0xFF1B5E20))
                            : null,
                        onTap: () async {
                          final prefs = await SharedPreferences.getInstance();
                          await prefs.setString('city_name', city["name"]);
                          await prefs.setString('country_name', city["country"]);
                          await prefs.setDouble('latitude', city["lat"]);
                          await prefs.setDouble('longitude', city["lng"]);

                          setState(() {
                            _selectedCity = city["name"];
                          });

                          Navigator.pop(context);
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('An sabunta gari zuwa: ${city["name"]}')),
                          );
                        },
                      );
                    },
                  ),
                ),
              ],
            );
          },
        );
      },
    );
  }
}
