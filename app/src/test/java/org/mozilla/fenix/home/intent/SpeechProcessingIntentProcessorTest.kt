/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.home.intent

import android.content.Intent
import androidx.navigation.NavController
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.fenix.BrowserDirection
import org.mozilla.fenix.HomeActivity
import org.mozilla.fenix.TestApplication
import org.mozilla.fenix.widget.VoiceSearchActivity.Companion.SPEECH_PROCESSING
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class SpeechProcessingIntentProcessorTest {

    private val activity: HomeActivity = mockk(relaxed = true)
    private val navController: NavController = mockk(relaxed = true)
    private val out: Intent = mockk(relaxed = true)

    @Test
    fun `do not process blank intents`() {
        val processor = SpeechProcessingIntentProcessor(activity)
        processor.process(Intent(), navController, out)

        verify { activity wasNot Called }
        verify { navController wasNot Called }
        verify { out wasNot Called }
    }

    @Test
    fun `do not process when open extra is false`() {
        val intent = Intent().apply {
            putExtra(HomeActivity.OPEN_TO_BROWSER_AND_LOAD, false)
        }
        val processor = SpeechProcessingIntentProcessor(activity)
        processor.process(intent, navController, out)

        verify { activity wasNot Called }
        verify { navController wasNot Called }
        verify { out wasNot Called }
    }

    @Test
    fun `process when open extra is true`() {
        val intent = Intent().apply {
            putExtra(HomeActivity.OPEN_TO_BROWSER_AND_LOAD, true)
        }
        val processor = SpeechProcessingIntentProcessor(activity)
        processor.process(intent, navController, out)

        verify {
            activity.openToBrowserAndLoad(
                searchTermOrURL = "",
                newTab = true,
                from = BrowserDirection.FromGlobal,
                forceSearch = true
            )
        }
        verify { navController wasNot Called }
        verify { out.putExtra(HomeActivity.OPEN_TO_BROWSER_AND_LOAD, false) }
    }

    @Test
    fun `reads the speech processing extra`() {
        val intent = Intent().apply {
            putExtra(HomeActivity.OPEN_TO_BROWSER_AND_LOAD, true)
            putExtra(SPEECH_PROCESSING, "hello world")
        }
        val processor = SpeechProcessingIntentProcessor(activity)
        processor.process(intent, mockk(), mockk(relaxed = true))

        verify {
            activity.openToBrowserAndLoad(
                searchTermOrURL = "hello world",
                newTab = true,
                from = BrowserDirection.FromGlobal,
                forceSearch = true
            )
        }
    }
}
