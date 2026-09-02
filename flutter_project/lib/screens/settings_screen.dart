import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:url_launcher/url_launcher.dart';
import '../services/prayer_service.dart';
import '../services/billing_service.dart';
import '../components/subscription_dialog.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  String _selectedCity = "Wurin Da Kake";
  String _countryName = "";
  String _selectedLanguage = "Hausa";
  bool _athanNotifications = true;
  bool _morningReminder = true;
  bool _eveningReminder = true;
  bool _isDetecting = false;
  String _calcMethod = "Egyptian";

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _selectedCity = prefs.getString('city_name') ?? "Wurin Da Kake";
      _countryName = prefs.getString('country_name') ?? "";
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

          // 2. Location Setting (Automatic GPS)
          Text(
            isHausa ? 'WURIN DA KAKE (GPS LOCATION)' : 'CURRENT LOCATION (GPS)',
            style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 12),
          ),
          const SizedBox(height: 8),
          Card(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      const Icon(Icons.my_location, color: Color(0xFF1B5E20)),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              isHausa ? 'Ainihin Wurin Da Kake:' : 'Detected Location:',
                              style: const TextStyle(fontSize: 12, color: Colors.grey),
                            ),
                            Text(
                              _countryName.isNotEmpty ? '$_selectedCity, $_countryName' : _selectedCity,
                              style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: _isDetecting ? null : _detectLocationGps,
                      icon: _isDetecting
                          ? const SizedBox(
                              width: 16,
                              height: 16,
                              child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                            )
                          : const Icon(Icons.refresh, size: 18),
                      label: Text(
                        _isDetecting
                            ? (isHausa ? 'Ana neman wuri ta GPS...' : 'Detecting GPS...')
                            : (isHausa ? 'Sabunta Wurin Da Kake Ta GPS' : 'Refresh Location via GPS'),
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF1B5E20),
                        foregroundColor: Colors.white,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    isHausa
                        ? 'Manhajar tana amfani da GPS kai tsaye don gano ainihin wurin da kake ba tare da zaɓen gari ba.'
                        : 'The app uses real-time GPS to accurately determine prayer times without manual city selection.',
                    style: const TextStyle(fontSize: 11, color: Colors.grey),
                  ),
                ],
              ),
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

  Future<void> _detectLocationGps() async {
    setState(() => _isDetecting = true);
    final loc = await PrayerService.autoDetectLocation();
    setState(() => _isDetecting = false);
    if (loc != null && mounted) {
      setState(() {
        _selectedCity = loc['city'] ?? _selectedCity;
        _countryName = loc['country'] ?? _countryName;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            _selectedLanguage == "Hausa"
                ? 'An sabunta wurin ku: $_selectedCity'
                : 'Updated location: $_selectedCity',
          ),
        ),
      );
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            _selectedLanguage == "Hausa"
                ? 'Ba a iya samun GPS ba. Duba izinin wuri (Location Permission).'
                : 'Could not acquire GPS. Check location permission.',
          ),
        ),
      );
    }
  }
}
