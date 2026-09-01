import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:shared_preferences/shared_preferences.dart';

class BillingConstants {
  static const String keyIsAdsRemoved = 'is_ads_removed';
  static const String keyPurchaseToken = 'purchase_token';

  // Subscriptions: Weekly, Monthly, Yearly
  static const String subWeekly = 'noor_zikir_remove_ads_weekly';
  static const String subMonthly = 'noor_zikir_remove_ads_monthly';
  static const String subYearly = 'noor_zikir_remove_ads_yearly';

  static const Set<String> productIds = {
    subWeekly,
    subMonthly,
    subYearly,
    'remove_ads_weekly',
    'remove_ads_monthly',
    'remove_ads_yearly',
  };
}

class BillingService extends ChangeNotifier {
  static final BillingService _instance = BillingService._internal();
  factory BillingService() => _instance;

  BillingService._internal();

  final InAppPurchase _iap = InAppPurchase.instance;
  late StreamSubscription<List<PurchaseDetails>> _subscription;

  bool _isAdsRemoved = false;
  bool get isAdsRemoved => _isAdsRemoved;

  bool _isAvailable = false;
  bool get isAvailable => _isAvailable;

  List<ProductDetails> _products = [];
  List<ProductDetails> get products => _products;

  String? _statusMessage;
  String? get statusMessage => _statusMessage;

  Future<void> initialize() async {
    final prefs = await SharedPreferences.getInstance();
    _isAdsRemoved = prefs.getBool(BillingConstants.keyIsAdsRemoved) ?? false;
    notifyListeners();

    _isAvailable = await _iap.isAvailable();
    if (!_isAvailable) {
      return;
    }

    _subscription = _iap.purchaseStream.listen(
      _handlePurchaseUpdates,
      onDone: () => _subscription.cancel(),
      onError: (error) {
        debugPrint('Billing purchase stream error: $error');
      },
    );

    await loadProducts();
    await restorePurchases();
  }

  Future<void> loadProducts() async {
    if (!_isAvailable) return;
    try {
      final response = await _iap.queryProductDetails(BillingConstants.productIds);
      if (response.notFoundIDs.isNotEmpty) {
        debugPrint('Some subscription IDs not found on Play Store: ${response.notFoundIDs}');
      }
      _products = response.productDetails;
      notifyListeners();
    } catch (e) {
      debugPrint('Error querying product details: $e');
    }
  }

  Future<void> buySubscription(ProductDetails product) async {
    final purchaseParam = PurchaseParam(productDetails: product);
    await _iap.buyNonConsumable(purchaseParam: purchaseParam);
  }

  Future<void> restorePurchases() async {
    try {
      await _iap.restorePurchases();
    } catch (e) {
      debugPrint('Error restoring subscriptions: $e');
    }
  }

  Future<void> _handlePurchaseUpdates(List<PurchaseDetails> purchaseDetailsList) async {
    for (final purchaseDetails in purchaseDetailsList) {
      if (purchaseDetails.status == PurchaseStatus.pending) {
        // Pending purchase transaction
      } else if (purchaseDetails.status == PurchaseStatus.error) {
        _statusMessage = purchaseDetails.error?.message;
        notifyListeners();
      } else if (purchaseDetails.status == PurchaseStatus.purchased ||
          purchaseDetails.status == PurchaseStatus.restored) {
        await _setAdsRemoved(true);
        if (purchaseDetails.pendingCompletePurchase) {
          await _iap.completePurchase(purchaseDetails);
        }
      }
    }
  }

  Future<void> _setAdsRemoved(bool removed) async {
    _isAdsRemoved = removed;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(BillingConstants.keyIsAdsRemoved, removed);
    notifyListeners();
  }

  @override
  void dispose() {
    _subscription.cancel();
    super.dispose();
  }
}
