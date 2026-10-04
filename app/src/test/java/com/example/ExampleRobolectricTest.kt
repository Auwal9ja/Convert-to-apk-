package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaReferenceLocalization
import com.example.receiver.OneSignalHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Zakiru", appName)
  }

  @Test
  fun `verify database seeder contains key prayers`() {
    val seedDuas = DuaDatabaseSeeder.getSeedDuas()
    assertTrue(seedDuas.isNotEmpty())
    
    // Check that we have categories like Morning Adhkar, Evening Adhkar, Ruqyah, and 40 Rabbana Duas
    val categories = seedDuas.map { it.category }.toSet()
    assertTrue(categories.contains("Morning Adhkar"))
    assertTrue(categories.contains("Evening Adhkar"))
    assertTrue(categories.contains("Sleeping & Waking Up"))
    assertTrue(categories.contains("Ruqyah"))
    assertTrue(categories.contains("40 Rabbana Duas"))
  }

  @Test
  fun `verify OneSignal helper initialization`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    OneSignalHelper.initialize(context)
    val appId = OneSignalHelper.getEffectiveAppId(context)
    assertNotNull(appId)
  }

  @Test
  fun `verify Hausa reference localization`() {
    val ref1 = DuaReferenceLocalization.getLocalizedReference(1, "Hausa")
    assertNotNull(ref1)
    assertTrue(ref1!!.contains("Al-Baqarah") || ref1.contains("Zikiri"))

    val ref2 = DuaReferenceLocalization.getLocalizedReference(2, "Hausa")
    assertNotNull(ref2)
    assertTrue(ref2!!.contains("Al-Bukhari"))
  }

  @Test
  fun `verify Surah Al-Mulk and sleep category data`() {
    val seedDuas = DuaDatabaseSeeder.getSeedDuas()
    val sleepDuas = seedDuas.filter { it.category == "Sleeping & Waking Up" }
    assertTrue(sleepDuas.isNotEmpty())

    // Verify Surah Al-Mulk is first
    val firstSleepDua = sleepDuas.first()
    assertEquals(5, firstSleepDua.id)
    assertTrue(firstSleepDua.title.contains("Mulk"))
    assertTrue(firstSleepDua.arabic.contains("تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ"))

    // Verify all 30 ayahs in SleepingAndWakingData
    assertEquals(30, com.example.data.local.SleepingAndWakingData.surahAlMulkAyahs.size)

    // Verify Falalar Suratul Mulk
    val virtuesDua = sleepDuas.find { it.id == 6 }
    assertNotNull(virtuesDua)
    assertTrue(virtuesDua!!.title.contains("Falalar Suratul Mulk"))
    assertTrue(virtuesDua.translationHausa.contains("ceto") || virtuesDua.translationHausa.contains("kabari"))

    // Verify Ladubban Kwanciya Barci
    val etiquettesDua = sleepDuas.find { it.id == 57 }
    assertNotNull(etiquettesDua)
    assertTrue(etiquettesDua!!.title.contains("Ladubban"))

    // Verify Tasbihin Kwanciya Barci (Subhanallah 33, Alhamdulillah 33, Allahu Akbar 34)
    val tasbihDua = sleepDuas.find { it.id == 58 }
    assertNotNull(tasbihDua)
    assertTrue(tasbihDua!!.title.contains("Tasbihin"))
    assertTrue(tasbihDua.arabic.contains("سُبْحَانَ اللَّهِ"))
  }
}

