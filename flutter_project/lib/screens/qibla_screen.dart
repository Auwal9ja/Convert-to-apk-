import 'dart:math' as math;
import 'package:flutter/material.dart';
import 'package:flutter_compass/flutter_compass.dart';
import 'package:adhan/adhan.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/prayer_service.dart';

class QiblaScreen extends StatefulWidget {
  const QiblaScreen({super.key});

  @override
  State<QiblaScreen> createState() => _QiblaScreenState();
}

class _QiblaScreenState extends State<QiblaScreen> {
  double _qiblaDirection = 65.0; // Default for Nigeria
  double _userHeading = 0.0;
  bool _hasCompass = true;

  @override
  void initState() {
    super.initState();
    _initQibla();
    _listenToCompass();
  }

  Future<void> _initQibla() async {
    final prefs = await SharedPreferences.getInstance();
    final lat = prefs.getDouble('latitude') ?? PrayerService.defaultLat;
    final lng = prefs.getDouble('longitude') ?? PrayerService.defaultLng;

    final coordinates = Coordinates(lat, lng);
    final qibla = Qibla(coordinates);

    setState(() {
      _qiblaDirection = qibla.direction;
    });
  }

  void _listenToCompass() {
    FlutterCompass.events?.listen((event) {
      if (mounted && event.heading != null) {
        setState(() {
          _userHeading = event.heading!;
        });
      }
    }, onError: (err) {
      setState(() {
        _hasCompass = false;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    // Relative angle to point towards Qibla
    final relativeQiblaAngle = (_qiblaDirection - _userHeading) * (math.pi / 180);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Neman Alƙibla (Qibla Compass)'),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Alƙibla tana: ${_qiblaDirection.toStringAsFixed(1)}° daga Arewa',
                style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF132D27)),
              ),
              const SizedBox(height: 8),
              Text(
                'Fuskarku tana: ${_userHeading.toStringAsFixed(1)}°',
                style: const TextStyle(fontSize: 14, color: Colors.grey),
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: 280,
                height: 280,
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    // Dial Background
                    Container(
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        color: Colors.white,
                        border: Border.all(color: const Color(0xFF1B5E20), width: 4),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.08),
                            blurRadius: 20,
                            spreadRadius: 4,
                          )
                        ],
                      ),
                    ),
                    // Compass needle
                    Transform.rotate(
                      angle: relativeQiblaAngle,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.navigation, size: 80, color: Color(0xFF1B5E20)),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                            decoration: BoxDecoration(
                              color: const Color(0xFF1B5E20),
                              borderRadius: BorderRadius.circular(8),
                            ),
                            child: const Text('KA\'ABAH', style: TextStyle(color: Colors.white, fontSize: 10, fontWeight: FontWeight.bold)),
                          ),
                          const SizedBox(height: 40),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 40),
              const Text(
                'Rike wayar a kwance (Flat) don samun daidaito',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 13, color: Colors.black54),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
