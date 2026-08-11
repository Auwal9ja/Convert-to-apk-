package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DuaDatabaseSeeder
import org.junit.Assert.assertEquals
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
    assertEquals("Hisnul Muslim", appName)
  }

  @Test
  fun `verify database seeder contains key prayers`() {
    val seedDuas = DuaDatabaseSeeder.getSeedDuas()
    assertEquals(40, seedDuas.size) // 40 preloaded duas
    
    // Check that we have categories like Morning & Evening
    val categories = seedDuas.map { it.category }.toSet()
    assertTrue(categories.contains("Morning & Evening"))
    assertTrue(categories.contains("Sleeping & Waking Up"))
    assertTrue(categories.contains("Travel & Home"))
  }
}
