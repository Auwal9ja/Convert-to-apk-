import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:onesignal_flutter/onesignal_flutter.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'screens/main_screen.dart';
import 'services/prayer_service.dart';
import 'services/billing_service.dart';
import 'services/ad_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);

  // Initialize In-App Purchases & Subscriptions
  try {
    await BillingService().initialize();
  } catch (e) {
    debugPrint("BillingService init error: $e");
  }

  // Initialize Mobile Ads SDK and start scheduled Ad Service (1 min delay, max 2 ads)
  try {
    await MobileAds.instance.initialize();
    FlutterAdService().initialize();
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

  runApp(const ZakiruApp());
}

class ZakiruApp extends StatelessWidget {
  const ZakiruApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Zakiru',
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
