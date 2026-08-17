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
    assertEquals("Noor zikir", appName)
  }

  @Test
  fun `verify database seeder contains key prayers`() {
    val seedDuas = DuaDatabaseSeeder.getSeedDuas()
    assertTrue(seedDuas.isNotEmpty())
    
    // Check that we have categories like Morning & Evening
    val categories = seedDuas.map { it.category }.toSet()
    assertTrue(categories.contains("Morning & Evening"))
    assertTrue(categories.contains("Sleeping & Waking Up"))
    assertTrue(categories.contains("Travel & Home"))
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
}

