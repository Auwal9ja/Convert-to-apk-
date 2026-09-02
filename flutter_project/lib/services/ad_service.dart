import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'billing_service.dart';

/// Helper to load and show Interstitial Ads in the Flutter project
/// according to strict user-experience rules:
/// 1. Initial delay: Wait at least 1 minute after app start before showing first ad
/// 2. Interval: At least 1 minute cooldown between consecutive ads
/// 3. Limit: Maximum 2 ads shown per app session/open
class FlutterAdService {
  static final FlutterAdService _instance = FlutterAdService._internal();
  factory FlutterAdService() => _instance;
  FlutterAdService._internal();

  // Test Ad Units (Sample IDs for testing / fallback)
  static const String interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712";

  final DateTime _appStartTime = DateTime.now();
  static const Duration _initialDelay = Duration(minutes: 1);
  static const Duration _minIntervalBetweenAds = Duration(minutes: 1);
  static const int _maxAdsPerSession = 2;

  int _adsShownThisSession = 0;
  DateTime? _lastAdShownTime;

  InterstitialAd? _interstitialAd;
  bool _isLoading = false;
  Timer? _initialScheduleTimer;

  bool get canShowAd {
    if (BillingService().isSubscribed) return false;
    if (_adsShownThisSession >= _maxAdsPerSession) {
      debugPrint("FlutterAdService: Max ads per session ($_maxAdsPerSession) reached.");
      return false;
    }

    final now = DateTime.now();
    if (now.difference(_appStartTime) < _initialDelay) {
      final remSec = _initialDelay.inSeconds - now.difference(_appStartTime).inSeconds;
      debugPrint("FlutterAdService: Initial 1-minute delay active ($remSec seconds remaining).");
      return false;
    }

    if (_lastAdShownTime != null && now.difference(_lastAdShownTime!) < _minIntervalBetweenAds) {
      final remSec = _minIntervalBetweenAds.inSeconds - now.difference(_lastAdShownTime!).inSeconds;
      debugPrint("FlutterAdService: Cooldown active ($remSec seconds remaining).");
      return false;
    }

    return true;
  }

  /// Starts the app session and schedules the 1-minute delayed initial ad
  void initialize() {
    _loadInterstitialAd();

    // Schedule the first ad after 1 minute (60 seconds)
    _initialScheduleTimer?.cancel();
    _initialScheduleTimer = Timer(_initialDelay, () {
      if (canShowAd) {
        debugPrint("FlutterAdService: 1-minute initial delay reached. Presenting launch ad.");
        showInterstitialAd();
      }
    });
  }

  void _loadInterstitialAd() {
    if (BillingService().isSubscribed || _adsShownThisSession >= _maxAdsPerSession) {
      return;
    }
    if (_interstitialAd != null || _isLoading) return;

    _isLoading = true;
    InterstitialAd.load(
      adUnitId: interstitialAdUnitId,
      request: const AdRequest(),
      adLoadCallback: InterstitialAdLoadCallback(
        onAdLoaded: (ad) {
          debugPrint("FlutterAdService: Interstitial loaded successfully.");
          _interstitialAd = ad;
          _isLoading = false;
        },
        onAdFailedToLoad: (error) {
          debugPrint("FlutterAdService: Interstitial failed to load: ${error.message}");
          _interstitialAd = null;
          _isLoading = false;
        },
      ),
    );
  }

  void showInterstitialAd({VoidCallback? onDismissed}) {
    if (!canShowAd) {
      onDismissed?.call();
      return;
    }

    if (_interstitialAd == null) {
      _loadInterstitialAd();
      onDismissed?.call();
      return;
    }

    _interstitialAd!.fullScreenContentCallback = FullScreenContentCallback(
      onAdShowedFullScreenContent: (ad) {
        debugPrint("FlutterAdService: Ad shown.");
        _adsShownThisSession++;
        _lastAdShownTime = DateTime.now();
        _interstitialAd = null;
      },
      onAdDismissedFullScreenContent: (ad) {
        debugPrint("FlutterAdService: Ad dismissed.");
        ad.dispose();
        _interstitialAd = null;
        if (_adsShownThisSession < _maxAdsPerSession) {
          _loadInterstitialAd();
        }
        onDismissed?.call();
      },
      onAdFailedToShowFullScreenContent: (ad, error) {
        debugPrint("FlutterAdService: Failed to show ad: ${error.message}");
        ad.dispose();
        _interstitialAd = null;
        if (_adsShownThisSession < _maxAdsPerSession) {
          _loadInterstitialAd();
        }
        onDismissed?.call();
      },
    );

    _interstitialAd!.show();
  }

  void dispose() {
    _initialScheduleTimer?.cancel();
    _interstitialAd?.dispose();
  }
}
