import 'package:flutter/material.dart';
import 'package:in_app_purchase/in_app_purchase.dart';
import 'package:url_launcher/url_launcher.dart';
import '../services/billing_service.dart';

class SubscriptionDialog extends StatefulWidget {
  final String selectedLanguage;

  const SubscriptionDialog({
    super.key,
    required this.selectedLanguage,
  });

  static Future<void> show(BuildContext context, {String selectedLanguage = "Hausa"}) {
    return showDialog(
      context: context,
      barrierDismissible: true,
      builder: (ctx) => SubscriptionDialog(selectedLanguage: selectedLanguage),
    );
  }

  @override
  State<SubscriptionDialog> createState() => _SubscriptionDialogState();
}

class _SubscriptionDialogState extends State<SubscriptionDialog> {
  final BillingService _billingService = BillingService();
  String? _selectedProductId;
  bool _isRestoring = false;

  @override
  void initState() {
    super.initState();
    _billingService.addListener(_onBillingChanged);
    _billingService.initialize();
  }

  void _onBillingChanged() {
    if (mounted) {
      setState(() {
        if (_selectedProductId == null && _billingService.products.isNotEmpty) {
          // Default to yearly subscription
          final yearly = _billingService.products.firstWhere(
            (p) => p.id.contains('yearly'),
            orElse: () => _billingService.products.first,
          );
          _selectedProductId = yearly.id;
        }
      });
    }
  }

  @override
  void dispose() {
    _billingService.removeListener(_onBillingChanged);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final isHausa = widget.selectedLanguage == "Hausa";
    final isYoruba = widget.selectedLanguage == "Yoruba";
    final isIgbo = widget.selectedLanguage == "Igbo";
    final isAdsRemoved = _billingService.isAdsRemoved;

    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      insetPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(24),
        child: Container(
          color: Colors.white,
          child: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // 1. Header with Islamic Emerald & Gold Gradient
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 24),
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      colors: [
                        Color(0xFF0F5132),
                        Color(0xFF198754),
                        Color(0xFFB8860B),
                      ],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                  ),
                  child: Column(
                    children: [
                      Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          color: const Color(0xFFD4AF37).withOpacity(0.25),
                        ),
                        child: Icon(
                          isAdsRemoved ? Icons.verified : Icons.workspace_premium,
                          color: const Color(0xFFFFD700),
                          size: 40,
                        ),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        isAdsRemoved
                            ? (isHausa ? "🎉 Kana da Noor Premium!" : "🎉 Noor Premium Active!")
                            : (isHausa ? "Cire Tallace-tallace (VIP)" : "Remove All Ads & Support"),
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                        textAlign: TextAlign.Center,
                      ),
                      const SizedBox(height: 6),
                      Text(
                        isAdsRemoved
                            ? (isHausa
                                ? "An cire dukkan tallace-tallace a manhajarka. Muna godiya da gudunmawarka!"
                                : "All ads are completely removed. Thank you for your support!")
                            : (isHausa
                                ? "Kariyar zikiri cikin cikakkiyar natsuwa ba tare da wani katsewa na talla ba"
                                : "Enjoy an uninterrupted spiritual journey with 100% ad-free experience"),
                        style: TextStyle(
                          color: Colors.white.withOpacity(0.9),
                          fontSize: 12,
                        ),
                        textAlign: TextAlign.Center,
                      ),
                    ],
                  ),
                ),

                // 2. Body
                Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      if (isAdsRemoved) ...[
                        Container(
                          padding: const EdgeInsets.all(16),
                          decoration: BoxDecoration(
                            color: const Color(0xFFE8F5E9),
                            borderRadius: BorderRadius.circular(16),
                            border: Border.all(color: const Color(0xFF2E7D32)),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  const Icon(Icons.check_circle, color: Color(0xFF2E7D32)),
                                  const SizedBox(width: 8),
                                  Text(
                                    isHausa ? "Manhaja Ad-Free ce 100%" : "100% Ad-Free Experience",
                                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Text(
                                isHausa
                                    ? "Banners, bidiyoyi da hotunan talla ba za su sake fitowa ba a ko ina a cikin wannan manhaja."
                                    : "Banners, interstitial, and popup ads are permanently disabled.",
                                style: const TextStyle(fontSize: 13, color: Colors.black87),
                              ),
                            ],
                          ),
                        ),
                      ] else ...[
                        // Benefits
                        _buildBenefitRow(
                          icon: Icons.block,
                          title: isHausa ? "Babu Wani Talla (100% Ad-Free)" : "100% Ad-Free Experience",
                          subtitle: isHausa
                              ? "Karatun Azkar, Salloli da Addu'o'i cikin natsuwa ba tare da talla ba."
                              : "Zero popups, banners, or video interruptions.",
                        ),
                        const SizedBox(height: 12),
                        _buildBenefitRow(
                          icon: Icons.speed,
                          title: isHausa ? "Sauri & Sauƙin Aiki" : "Faster Loading & Battery Saver",
                          subtitle: isHausa
                              ? "Babu jinkirin saukar talla, yana kiyaye caji da data."
                              : "Smoother UI navigation, saves mobile internet data.",
                        ),
                        const SizedBox(height: 12),
                        _buildBenefitRow(
                          icon: Icons.volunteer_activism,
                          title: isHausa ? "Sadaqah Jariyah & Bunkasawa" : "Support Islamic Development",
                          subtitle: isHausa
                              ? "Gudunmawarka na taimakawa wajen ci gaba da kula da wannan manhaja."
                              : "Empower continuous updates, translations and features.",
                        ),
                        const SizedBox(height: 20),

                        // Plan Choices (Weekly, Monthly, Yearly)
                        Text(
                          isHausa ? "Zaɓi Tsarin Subscriptions:" : "Choose Subscription Plan:",
                          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                        ),
                        const SizedBox(height: 10),

                        _buildSubscriptionTiers(isHausa, isYoruba, isIgbo),
                        const SizedBox(height: 16),

                        // Action Button
                        SizedBox(
                          width: double.infinity,
                          height: 50,
                          child: ElevatedButton.icon(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: const Color(0xFF0F5132),
                              foregroundColor: Colors.white,
                              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                            ),
                            icon: const Icon(Icons.lock_open),
                            label: Text(
                              isHausa ? "Ci gaba da Biya / Subscribe" : "Continue to Subscribe",
                              style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
                            ),
                            onPressed: () => _handleSubscribe(context),
                          ),
                        ),
                      ],

                      const SizedBox(height: 14),

                      // Restore Purchases
                      Center(
                        child: TextButton.icon(
                          icon: const Icon(Icons.restore, size: 18),
                          label: Text(
                            _isRestoring
                                ? (isHausa ? "Ana duba Play Store..." : "Checking Play Store...")
                                : (isHausa ? "Dawo da Tsohon Biyan Kuɗi (Restore Purchases)" : "Restore Existing Purchases"),
                            style: const TextStyle(fontSize: 13),
                          ),
                          onPressed: _isRestoring ? null : () => _handleRestore(context),
                        ),
                      ),

                      // Legal & Privacy
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          GestureDetector(
                            onTap: () => launchUrl(Uri.parse("https://play.google.com/about/play-terms/")),
                            child: const Text(
                              "Google Play Terms",
                              style: TextStyle(fontSize: 11, color: Colors.blue, decoration: TextDecoration.underline),
                            ),
                          ),
                          const Text(" • ", style: TextStyle(fontSize: 11, color: Colors.grey)),
                          GestureDetector(
                            onTap: () => Navigator.pop(context),
                            child: Text(
                              isHausa ? "Rufe (Close)" : "Close",
                              style: const TextStyle(fontSize: 11, color: Colors.grey),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildBenefitRow({required IconData icon, required String title, required String subtitle}) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: const Color(0xFFE8F5E9),
            borderRadius: BorderRadius.circular(10),
          ),
          child: Icon(icon, color: const Color(0xFF1B5E20), size: 20),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
              const SizedBox(height: 2),
              Text(subtitle, style: TextStyle(color: Colors.grey[700], fontSize: 11, height: 1.3)),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildSubscriptionTiers(bool isHausa, bool isYoruba, bool isIgbo) {
    final products = _billingService.products;

    final weeklyProduct = products.where((p) => p.id.contains('weekly')).firstOrNull;
    final monthlyProduct = products.where((p) => p.id.contains('monthly')).firstOrNull;
    final yearlyProduct = products.where((p) => p.id.contains('yearly')).firstOrNull;

    return Column(
      children: [
        _buildTierCard(
          id: BillingConstants.subWeekly,
          title: isHausa ? "Biyan Mako (Weekly)" : "Weekly Subscription",
          subtitle: isHausa ? "Biyan kuɗi kowane mako • Gwaji mai sauƙi" : "Billed weekly • Flexible short-term plan",
          badge: "Flexible",
          price: weeklyProduct?.price ?? "Play Store",
          isSelected: _selectedProductId == null ? false : _selectedProductId!.contains('weekly'),
          onTap: () {
            setState(() {
              _selectedProductId = weeklyProduct?.id ?? BillingConstants.subWeekly;
            });
          },
        ),
        const SizedBox(height: 8),
        _buildTierCard(
          id: BillingConstants.subMonthly,
          title: isHausa ? "Biyan Wata-wata (Monthly)" : "Monthly Subscription",
          subtitle: isHausa ? "Biyan kuɗi kowane wata • Soke a ko yaushe" : "Billed monthly • Cancel anytime",
          badge: "Popular",
          price: monthlyProduct?.price ?? "Play Store",
          isSelected: _selectedProductId == null ? false : _selectedProductId!.contains('monthly'),
          onTap: () {
            setState(() {
              _selectedProductId = monthlyProduct?.id ?? BillingConstants.subMonthly;
            });
          },
        ),
        const SizedBox(height: 8),
        _buildTierCard(
          id: BillingConstants.subYearly,
          title: isHausa ? "Biyan Shekara (Yearly)" : "Yearly Subscription",
          subtitle: isHausa ? "Mafi arha • Biyan shekara guda (Rage 45%)" : "Best value • Full year access (Save 45%)",
          badge: "★ SAVE 45%",
          price: yearlyProduct?.price ?? "Play Store",
          isSelected: _selectedProductId == null ? true : _selectedProductId!.contains('yearly'),
          onTap: () {
            setState(() {
              _selectedProductId = yearlyProduct?.id ?? BillingConstants.subYearly;
            });
          },
        ),
      ],
    );
  }

  Widget _buildTierCard({
    required String id,
    required String title,
    required String subtitle,
    required String badge,
    required String price,
    required bool isSelected,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? const Color(0xFFE8F5E9) : const Color(0xFFFAFAFA),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isSelected ? const Color(0xFF1B5E20) : Colors.grey.shade300,
            width: isSelected ? 2 : 1,
          ),
        ),
        child: Row(
          children: [
            Radio<bool>(
              value: true,
              groupValue: isSelected,
              activeColor: const Color(0xFF1B5E20),
              onChanged: (_) => onTap(),
            ),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(title, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13)),
                      const SizedBox(width: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: const Color(0xFFD4AF37),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(
                          badge,
                          style: const TextStyle(fontSize: 9, fontWeight: FontWeight.bold, color: Colors.black),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Text(subtitle, style: TextStyle(fontSize: 11, color: Colors.grey[700])),
                ],
              ),
            ),
            Text(
              price,
              style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF1B5E20), fontSize: 13),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _handleSubscribe(BuildContext context) async {
    final products = _billingService.products;
    ProductDetails? targetProduct;

    if (_selectedProductId != null) {
      targetProduct = products.where((p) => p.id == _selectedProductId || p.id.contains(_selectedProductId!)).firstOrNull;
    }

    targetProduct ??= products.firstOrNull;

    if (targetProduct != null) {
      await _billingService.buySubscription(targetProduct);
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Connecting to Google Play Store... Please ensure app is published.")),
      );
    }
  }

  Future<void> _handleRestore(BuildContext context) async {
    setState(() => _isRestoring = true);
    await _billingService.restorePurchases();
    if (mounted) {
      setState(() => _isRestoring = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            _billingService.isAdsRemoved
                ? "Alhamdulillah! Purchases restored successfully."
                : "No active subscriptions found for your account.",
          ),
        ),
      );
    }
  }
}
