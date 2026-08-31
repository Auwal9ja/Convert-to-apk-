import 'package:adhan/adhan.dart';
import 'package:geolocator/geolocator.dart';
import 'package:shared_preferences/shared_preferences.dart';

class PrayerTimeModel {
  final String nameHausa;
  final String nameEnglish;
  final String nameArabic;
  final DateTime time;

  PrayerTimeModel({
    required this.nameHausa,
    required this.nameEnglish,
    required this.nameArabic,
    required this.time,
  });
}

class PrayerService {
  static const String defaultCity = "Kano";
  static const String defaultCountry = "Nigeria";
  static const double defaultLat = 12.0022;
  static const double defaultLng = 8.5920;

  static Future<PrayerTimes> getPrayerTimes({
    double? lat,
    double? lng,
    DateTime? date,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    final latitude = lat ?? prefs.getDouble('latitude') ?? defaultLat;
    final longitude = lng ?? prefs.getDouble('longitude') ?? defaultLng;
    final coordinates = Coordinates(latitude, longitude);

    final calculationDate = DateComponents.from(date ?? DateTime.now());
    
    // Egyptian General Authority of Survey (standard for West Africa) or Muslim World League
    final params = CalculationMethod.egyptian.getParameters();
    params.madhab = Madhab.shafi;

    return PrayerTimes(coordinates, calculationDate, params);
  }

  static Future<void> saveLocation(String city, String country, double lat, double lng) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('city_name', city);
    await prefs.setString('country_name', country);
    await prefs.setDouble('latitude', lat);
    await prefs.setDouble('longitude', lng);
  }

  static Future<Position?> getCurrentLocation() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return null;
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return null;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return null;
    }

    return await Geolocator.getCurrentPosition(
      desiredAccuracy: LocationAccuracy.medium,
    );
  }
}
