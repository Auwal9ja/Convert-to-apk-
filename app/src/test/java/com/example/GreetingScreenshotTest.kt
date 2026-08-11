package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.DuaEntity
import com.example.ui.screens.DuaItemCard
import com.example.ui.audio.DuaSpeaker
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val context = RuntimeEnvironment.getApplication()
    val speaker = DuaSpeaker(context)
    val mockDua = DuaEntity(
      id = 99,
      category = "Morning & Evening",
      title = "Sayyidul Istighfar",
      arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ",
      translation = "O Allah, You are my Lord, there is none worthy of worship but You.",
      transliteration = "Allahumma anta Rabbi la ilaha illa Anta.",
      reference = "Al-Bukhari"
    )

    composeTestRule.setContent {
      MyApplicationTheme(darkTheme = true) {
        DuaItemCard(
          dua = mockDua,
          speaker = speaker,
          getTranslation = { d, _ -> Pair(d.translation, d.reference) },
          onFavoriteToggle = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
