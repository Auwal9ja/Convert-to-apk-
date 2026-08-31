import 'package:flutter/material.dart';

class AzkarScreen extends StatelessWidget {
  const AzkarScreen({super.key});

  final List<Map<String, String>> _azkar = const [
    {
      "title": "Addu'ar Safiya",
      "arabic": "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ",
      "trans": "Asbahna wa asbahal mulku lillah, walhamdu lillah, la ilaha illallahu wahdahu la shareeka lah.",
      "meaning": "Mun wayi gari mulki ya tabbata ga Allah, godiya ta tabbata ga Allah..."
    },
    {
      "title": "Addu'ar Maraice",
      "arabic": "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ",
      "trans": "Amsayna wa amsal mulku lillah, walhamdu lillah.",
      "meaning": "Mun shiga maraice mulki ya tabbata ga Allah, godiya ta tabbata ga Allah..."
    },
    {
      "title": "Kafin Shiga Barci",
      "arabic": "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ",
      "trans": "Bismika Rabbi wada'tu janbi wa bika arfa'uh.",
      "meaning": "Da sunanka Ya Ubangijina na kwantar da gefena, kuma da ikonka nake daukaka shi..."
    },
    {
      "title": "Idan An Tashi Daga Barci",
      "arabic": "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
      "trans": "Alhamdu lillahilladhi ahyana ba'da ma amatana wa ilaihin nushoor.",
      "meaning": "Godiya ta tabbata ga Allah wanda ya raya mu bayan ya kashe mu kuma zuwa gare shi tashi yake."
    },
    {
      "title": "Kafin Cin Abinci",
      "arabic": "بِسْمِ اللَّهِ",
      "trans": "Bismillah",
      "meaning": "Da sunan Allah."
    },
    {
      "title": "Bayan Cin Abinci",
      "arabic": "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنَا وَسَقَانَا وَجَعَلَنَا مُسْلِمِينَ",
      "trans": "Alhamdu lillahilladhi at'amana wa saqana wa ja'alana muslimeen.",
      "meaning": "Godiya ta tabbata ga Allah wanda ya ciyar da mu, ya shayar da mu, ya sanya mu musulmai."
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Hisnul Muslim (Addu\'o\'i & Azkar)'),
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(12),
        itemCount: _azkar.length,
        itemBuilder: (context, index) {
          final item = _azkar[index];
          return Card(
            margin: const EdgeInsets.only(bottom: 12),
            shape: RoundedCornerShape(12),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  Text(
                    item["title"]!,
                    style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16, color: Color(0xFF1B5E20)),
                  ),
                  const SizedBox(height: 12),
                  Text(
                    item["arabic"]!,
                    textAlign: TextAlign.right,
                    style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, height: 1.6, color: Color(0xFF132D27)),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    item["trans"]!,
                    style: const TextStyle(fontStyle: FontStyle.italic, color: Colors.black87),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    item["meaning"]!,
                    style: const TextStyle(color: Colors.black54, fontSize: 13),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
