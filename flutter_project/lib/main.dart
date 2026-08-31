import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:onesignal_flutter/onesignal_flutter.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'screens/main_screen.dart';
import 'services/prayer_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);

  // Initialize Mobile Ads SDK
  try {
    await MobileAds.instance.initialize();
  } catch (e) {
    debugPrint("MobileAds init error: $e");
  }

  // Initialize OneSignal Push Notifications (Replace with your OneSignal App ID)
  try {
    OneSignal.Debug.setLogLevel(OSLogLevel.verbose);
    OneSignal.initialize("YOUR_ONESIGNAL_APP_ID_HERE");
    OneSignal.Notifications.requestPermission(true);
  } catch (e) {
    debugPrint("OneSignal init error: $e");
  }

  runApp(const IslamicApp());
}

class IslamicApp extends StatelessWidget {
  const IslamicApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Islamic & Prayer App',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF1B5E20),
          primary: const Color(0xFF1B5E20),
          secondary: const Color(0xFF2E7D32),
          surface: Colors.white,
        ),
        scaffoldBackgroundColor: const Color(0xFFF6F8F6),
        appBarTheme: const AppBarTheme(
          backgroundColor: Color(0xFF1B5E20),
          foregroundColor: Colors.white,
          elevation: 0,
        ),
      ),
      home: const MainScreen(),
    );
  }
}
