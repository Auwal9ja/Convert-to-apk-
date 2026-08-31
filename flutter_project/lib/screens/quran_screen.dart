import 'package:flutter/material.dart';
import 'package:just_audio/just_audio.dart';

class QuranScreen extends StatefulWidget {
  const QuranScreen({super.key});

  @override
  State<QuranScreen> createState() => _QuranScreenState();
}

class _QuranScreenState extends State<QuranScreen> {
  final AudioPlayer _audioPlayer = AudioPlayer();
  int? _currentlyPlayingIndex;
  bool _isPlaying = false;

  final List<Map<String, dynamic>> _surahs = [
    {"number": 1, "name": "Al-Fatihah", "arabic": "الفاتحة", "verses": 7, "audio": "https://server8.mp3quran.net/afs/001.mp3"},
    {"number": 2, "name": "Al-Baqarah", "arabic": "البقرة", "verses": 286, "audio": "https://server8.mp3quran.net/afs/002.mp3"},
    {"number": 3, "name": "Ali 'Imran", "arabic": "آل عمران", "verses": 200, "audio": "https://server8.mp3quran.net/afs/003.mp3"},
    {"number": 4, "name": "An-Nisa", "arabic": "النساء", "verses": 176, "audio": "https://server8.mp3quran.net/afs/004.mp3"},
    {"number": 36, "name": "Ya-Sin", "arabic": "يس", "verses": 83, "audio": "https://server8.mp3quran.net/afs/036.mp3"},
    {"number": 55, "name": "Ar-Rahman", "arabic": "الرحمن", "verses": 78, "audio": "https://server8.mp3quran.net/afs/055.mp3"},
    {"number": 67, "name": "Al-Mulk", "arabic": "الملك", "verses": 30, "audio": "https://server8.mp3quran.net/afs/067.mp3"},
    {"number": 112, "name": "Al-Ikhlas", "arabic": "الإخلاص", "verses": 4, "audio": "https://server8.mp3quran.net/afs/112.mp3"},
    {"number": 113, "name": "Al-Falaq", "arabic": "الفلق", "verses": 5, "audio": "https://server8.mp3quran.net/afs/113.mp3"},
    {"number": 114, "name": "An-Nas", "arabic": "الناس", "verses": 6, "audio": "https://server8.mp3quran.net/afs/114.mp3"},
  ];

  @override
  void dispose() {
    _audioPlayer.dispose();
    super.dispose();
  }

  Future<void> _togglePlay(int index, String url) async {
    if (_currentlyPlayingIndex == index && _isPlaying) {
      await _audioPlayer.pause();
      setState(() {
        _isPlaying = false;
      });
    } else {
      await _audioPlayer.setUrl(url);
      _audioPlayer.play();
      setState(() {
        _currentlyPlayingIndex = index;
        _isPlaying = true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Al-Qur\'ani Mai Girma'),
      ),
      body: ListView.separated(
        padding: const EdgeInsets.all(12),
        itemCount: _surahs.length,
        separatorBuilder: (_, __) => const Divider(height: 1),
        itemBuilder: (context, index) {
          final surah = _surahs[index];
          final isCurrent = _currentlyPlayingIndex == index && _isPlaying;

          return ListTile(
            leading: CircleAvatar(
              backgroundColor: isCurrent ? const Color(0xFF1B5E20) : const Color(0xFFE8F5E9),
              foregroundColor: isCurrent ? Colors.white : const Color(0xFF1B5E20),
              child: Text('${surah["number"]}'),
            ),
            title: Text(
              surah["name"],
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            subtitle: Text('Ayoyi: ${surah["verses"]}'),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  surah["arabic"],
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20)),
                ),
                const SizedBox(width: 12),
                IconButton(
                  icon: Icon(
                    isCurrent ? Icons.pause_circle_filled : Icons.play_circle_filled,
                    color: const Color(0xFF1B5E20),
                    size: 32,
                  ),
                  onPressed: () => _togglePlay(index, surah["audio"]),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
