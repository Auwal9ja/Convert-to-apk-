import 'dart:convert';
import 'dart:io';
import 'package:adhan/adhan.dart';
import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';
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
  static const String defaultCity = "Wurin Da Kake";
  static const String defaultCountry = "";
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
    
    // Egyptian General Authority of Survey (standard for West Africa)
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

  static Future<Map<String, dynamic>?> autoDetectLocation() async {
    try {
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
      }

      Position? position;
      if (serviceEnabled && (permission == LocationPermission.always || permission == LocationPermission.whileInUse)) {
        // Fast last known position first
        position = await Geolocator.getLastKnownPosition();
        if (position == null) {
          try {
            position = await Geolocator.getCurrentPosition(
              desiredAccuracy: LocationAccuracy.medium,
              timeLimit: const Duration(seconds: 7),
            );
          } catch (_) {}
        }
      }

      if (position != null) {
        final lat = position.latitude;
        final lng = position.longitude;
        final nameInfo = await _reverseGeocode(lat, lng);
        final city = nameInfo['city'] ?? "Wurin Da Kake";
        final country = nameInfo['country'] ?? "";

        await saveLocation(city, country, lat, lng);
        return {
          'city': city,
          'country': country,
          'lat': lat,
          'lng': lng,
        };
      }

      // Fallback to IP geolocation if GPS is unavailable
      final ipLocation = await _detectByIp();
      if (ipLocation != null) {
        await saveLocation(
          ipLocation['city'] ?? "Wurin Da Kake",
          ipLocation['country'] ?? "",
          ipLocation['lat'] ?? defaultLat,
          ipLocation['lng'] ?? defaultLng,
        );
        return ipLocation;
      }
    } catch (_) {}
    return null;
  }

  static Future<Map<String, String>> _reverseGeocode(double lat, double lng) async {
    // 1. Try standard Geocoding plugin
    try {
      final placemarks = await placemarkFromCoordinates(lat, lng);
      if (placemarks.isNotEmpty) {
        final place = placemarks.first;
        final city = place.locality?.isNotEmpty == true
            ? place.locality!
            : (place.subAdministrativeArea?.isNotEmpty == true
                ? place.subAdministrativeArea!
                : (place.administrativeArea ?? "Wurin Da Kake"));
        final country = place.country ?? "";
        return {'city': city, 'country': country};
      }
    } catch (_) {}

    // 2. HTTP Reverse Geocode fallback for Huawei devices without GMS
    try {
      final client = HttpClient()..connectionTimeout = const Duration(seconds: 5);
      final request = await client.getUrl(
        Uri.parse('https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=$lat&longitude=$lng&localityLanguage=en')
      );
      final response = await request.close();
      if (response.statusCode == 200) {
        final responseBody = await response.transform(utf8.decoder).join();
        final json = jsonDecode(responseBody) as Map<String, dynamic>;
        final city = json['city'] ?? json['locality'] ?? json['principalSubdivision'] ?? "Wurin Da Kake";
        final country = json['countryName'] ?? "";
        return {'city': city.toString(), 'country': country.toString()};
      }
    } catch (_) {}

    return {'city': "Wurin Da Kake", 'country': ""};
  }

  static Future<Map<String, dynamic>?> _detectByIp() async {
    try {
      final client = HttpClient()..connectionTimeout = const Duration(seconds: 5);
      final request = await client.getUrl(Uri.parse('http://ip-api.com/json'));
      final response = await request.close();
      if (response.statusCode == 200) {
        final responseBody = await response.transform(utf8.decoder).join();
        final json = jsonDecode(responseBody) as Map<String, dynamic>;
        if (json['status'] == 'success') {
          return {
            'city': json['city']?.toString() ?? "Wurin Da Kake",
            'country': json['country']?.toString() ?? "",
            'lat': (json['lat'] as num?)?.toDouble() ?? defaultLat,
            'lng': (json['lon'] as num?)?.toDouble() ?? defaultLng,
          };
        }
      }
    } catch (_) {}
    return null;
  }
}
